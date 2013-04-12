/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.surfnet.oaaas.model;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.*;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.lang.StringUtils;
import org.codehaus.jettison.json.JSONArray;
import org.codehaus.jettison.json.JSONException;
import org.codehaus.jettison.json.JSONObject;
import org.codehaus.jettison.json.JSONStringer;
import org.springframework.util.Assert;
import org.surfnet.oaaas.auth.api.principal.AuthenticatedPrincipal;

/**
 * Representation of an <a
 * href="http://tools.ietf.org/html/draft-ietf-oauth-v2-30#section-1.4"
 * >AccessToken</a>
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "accesstoken")
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class AccessToken extends AbstractEntity {

  @Column(unique = true)
  @NotNull
  private String token;

  @Column(unique = true, nullable = true)
  private String refreshToken;

  @Transient
  @XmlTransient
  private AuthenticatedPrincipal principal;

  @Column(length = 1024)
  @NotNull
  @XmlTransient
  private String encodedPrincipal;

  @ManyToOne(optional = false)
  @JoinColumn(name = "client_id", nullable = false, updatable = false)
  @XmlTransient
  private Client client;

  @Column
  private long expires;

  @ElementCollection(fetch= FetchType.EAGER)
  private List<String> scopes;

  @Column
  @NotNull
  private String resourceOwnerId;

  public AccessToken() {
    super();
  }

  public AccessToken(String token, AuthenticatedPrincipal principal, Client client, long expires, List<String> scopes) {
    this(token, principal, client, expires, scopes, null);
  }

  public AccessToken(String token, AuthenticatedPrincipal principal, Client client, long expires, List<String> scopes,
      String refreshToken) {
    super();
    this.token = token;
    this.principal = principal;
    this.encodePrincipal();
    this.resourceOwnerId = principal.getName();
    this.client = client;
    this.expires = expires;
    this.scopes = scopes;
    this.refreshToken = refreshToken;
    invariant();
  }

  private void invariant() {
    Assert.notNull(token, "Token may not be null");
    Assert.notNull(client, "Client may not be null");
    Assert.notNull(principal, "AuthenticatedPrincipal may not be null");
    Assert.isTrue(StringUtils.isNotBlank(principal.getName()), "AuthenticatedPrincipal#name may not be null");
  }

  @PreUpdate
  @PrePersist
  public void encodePrincipal() {
    if (principal != null) {
//      byte[] binaryData = SerializationUtils.serialize(principal);
//      this.encodedPrincipal = new String(Base64.encodeBase64(binaryData));

		try {
			JSONStringer json=new JSONStringer();
			json.object();
			json.key("name").value(principal.getName());

			json.key("roles");
			json.array();
			if (principal.getRoles()!=null) {
				for (String role: principal.getRoles()) {
					json.value(role);
				}
			}
			json.endArray();

			json.key("attributes");
			json.array();
			if (principal.getAttributes()!=null) {
				json.object();
				for (Map.Entry<String,Object> entry:
						principal.getAttributes().entrySet()) {

					json.key("key").value(entry.getKey());

					Object value=entry.getValue();
					if (value!=null) {
						if (value instanceof String ||
							value instanceof Integer ||
							value instanceof Short ||
							value instanceof Byte ||
							value instanceof Long ||
							value instanceof Double ||
							value instanceof Float ||
							value instanceof Boolean) {

							json.key("value").value(entry.getValue());
						}
						else {
							// Be conservative and fail fast
							throw new PersistenceException("Could not "+
								"serialize attribute \""+entry.getKey()+"\" "+
								"of type "+value.getClass().getName()+" for "+
								"principal \""+principal.getName()+"\" "+
								"(attribute value = \""+entry.getValue()+"\")");
						}
					}
					else {
						json.key("value").value(null);
					}
				}
				json.endObject();
			}
			json.endArray();

			json.endObject();

			this.encodedPrincipal = json.toString();
		}
		catch (JSONException e) {
			throw new PersistenceException("Could not serialize principal",e);
		}
    }
  }

  @PostLoad
  @PostPersist
  @PostUpdate
  public void decodePrincipal() {
    if (StringUtils.isNotBlank(encodedPrincipal)) {

		Exception deserialziationException=null;

		// Try to decode as JSON
		try {
			AuthenticatedPrincipal tempPrincipal=new AuthenticatedPrincipal();

			// Name
			JSONObject json=new JSONObject(encodedPrincipal);
			tempPrincipal.setName(json.getString("name"));

			// Roles
			List<String> roles=new ArrayList<String>();
			JSONArray rolesArray=json.getJSONArray("roles");
			for (int i=0; i<rolesArray.length(); i++) {
				String role=rolesArray.getString(i);
				roles.add(role);
			}

			tempPrincipal.setRoles(roles);

			// Attributes
			Map<String,Object> attributes=new HashMap<String,Object>();
			JSONArray attributesArray=json.getJSONArray("attributes");
			for (int i=0; i<attributesArray.length(); i++) {
				JSONObject attribute=attributesArray.getJSONObject(i);
				String key=attribute.getString("key");
				Object value=attribute.get("value");

				attributes.put(key,value);
			}

			tempPrincipal.setAttributes(attributes);

			this.principal = tempPrincipal;
	
			return;
		}
		catch (JSONException e) {
			// Cache the exception and try to deserialize
			deserialziationException=e;
		}

		// Try to deserialize backward-compatible
		try {
			byte[] objectData = Base64.decodeBase64(encodedPrincipal);
			org.surfnet.oaaas.auth.principal.AuthenticatedPrincipal oldPrincipal = 
				(org.surfnet.oaaas.auth.principal.AuthenticatedPrincipal) SerializationUtils.deserialize(objectData);

			AuthenticatedPrincipal newPrincipal=new AuthenticatedPrincipal();
			newPrincipal.setName(oldPrincipal.getName());
			newPrincipal.setRoles(oldPrincipal.getRoles());
			newPrincipal.setAttributes(oldPrincipal.getAttributes());

			this.principal = newPrincipal;
		}
		catch (Exception e) {
			throw new PersistenceException("Could not deserialize principal",e);
		}
    }
  }

  /**
   * @return the token
   */
  public String getToken() {
    return token;
  }

  /**
   * @param token
   *          the token to set
   */
  public void setToken(String token) {
    this.token = token;
  }

  /**
   * @return the client
   */
  public Client getClient() {
    return client;
  }

  /**
   * @param client
   *          the client to set
   */
  public void setClient(Client client) {
    this.client = client;
  }

  /**
   * @return the expires
   */
  public long getExpires() {
    return expires;
  }

  /**
   * @param expires
   *          the expires to set
   */
  public void setExpires(long expires) {
    this.expires = expires;
  }

  /**
   * @return the scopes
   */
  public List<String> getScopes() {
    return scopes;
  }

  /**
   * @param scopes
   *          the scopes to set
   */
  public void setScopes(List<String> scopes) {
    this.scopes = scopes;
  }

  /**
   * @return the principal
   */
  public AuthenticatedPrincipal getPrincipal() {
    return principal;
  }

  /**
   * @return the encodedPrincipal
   */
  public String getEncodedPrincipal() {
    return encodedPrincipal;
  }

  /**
   * @return the refreshToken
   */
  public String getRefreshToken() {
    return refreshToken;
  }

  /**
   * @param refreshToken
   *          the refreshToken to set
   */
  public void setRefreshToken(String refreshToken) {
    this.refreshToken = refreshToken;
  }

  /**
   * @return the resourceOwnerId
   */
  public String getResourceOwnerId() {
    return resourceOwnerId;
  }

  @XmlElement
  public String getClientId() {
    return client.getClientId();
  }

}
