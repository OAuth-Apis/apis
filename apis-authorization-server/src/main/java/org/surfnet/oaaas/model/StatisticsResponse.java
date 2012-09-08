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

import java.util.List;

import javax.xml.bind.annotation.XmlRootElement;

import org.codehaus.jackson.map.annotate.JsonSerialize;

/**
 * Response representing the statistics for the entire database
 * 
 */
@XmlRootElement
@JsonSerialize(include = JsonSerialize.Inclusion.NON_NULL)
public class StatisticsResponse {

  private List<ResourceServerStat> resourceServers;

  public StatisticsResponse() {}
  public StatisticsResponse(List<ResourceServerStat> resourceServers) {
    this.resourceServers = resourceServers;
  }
  
  public static class ResourceServerStat {
    private String name;
    private String description;
    private List<ClientStat> clients;
    public ResourceServerStat() {}
    public ResourceServerStat(String name, String description,List<ClientStat> clients) {
      this.name = name;
      this.description = description;
      this.clients = clients;
    }
    public String getName() {
      return name;
    }

    public String getDescription() {
      return description;
    }

    public List<ClientStat> getClients() {
      return clients;
    }
    public void setName(String name) {
      this.name = name;
    }
    public void setDescription(String description) {
      this.description = description;
    }
    public void setClients(List<ClientStat> clients) {
      this.clients = clients;
    }
    
  }

  public static class ClientStat {
    private String name;
    private String description;
    private long tokenCount;

    public ClientStat() {}
    public ClientStat(String name, String description, Number tokenCount) {
      this.name = name;
      this.description = description;
      this.tokenCount = tokenCount != null ? tokenCount.longValue() : 0L;
    }

    public String getName() {
      return name;
    }

    public String getDescription() {
      return description;
    }

    public long getTokenCount() {
      return tokenCount;
    }

    public void setName(String name) {
      this.name = name;
    }

    public void setDescription(String description) {
      this.description = description;
    }

    public void setTokenCount(long tokenCount) {
      this.tokenCount = tokenCount;
    }
    
    
  }

  public void setResourceServers(List<ResourceServerStat> resourceServers) {
    this.resourceServers = resourceServers;
  }

  public List<ResourceServerStat> getResourceServers() {
    return resourceServers;
  }

}
