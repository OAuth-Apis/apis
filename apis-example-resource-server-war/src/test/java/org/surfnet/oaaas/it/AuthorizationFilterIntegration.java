package org.surfnet.oaaas.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

/**
 * 
 * See http://maven.apache.org/plugins/maven-failsafe-plugin/examples/inclusion-exclusion.html
 *
 */
public class AuthorizationFilterIntegration {
  private String baseUrl;

  @Before
  public void setUp() throws Exception {
    String port = System.getProperty("servlet.port");
    this.baseUrl = "http://localhost:" + port + "/oaaas-example-resource-server-war";
  }

  @Test
  public void testCallIndexPage() throws Exception {
    URL url = new URL(this.baseUrl);
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestProperty("Authorization", "bearer 74eccf5f-0995-4e1c-b08c-d05dd5a0f89b");
    connection.connect();
    assertEquals(200, connection.getResponseCode());
    assertTrue(IOUtils.toString(connection.getInputStream()).contains("emma.blunt"));
  }
  

}
