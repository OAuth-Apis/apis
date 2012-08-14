package org.surfnet.oaaas.it;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;

/**
 * 
 * See http://maven.apache.org/plugins/maven-failsafe-plugin/examples/inclusion-
 * exclusion.html
 * 
 */
public class AuthorizationFilterIntegration {
  private String baseUrl;

  @Before
  public void setUp() throws Exception {
    String port = System.getProperty("servlet.port");
    port = (StringUtils.isBlank(port) ? port = "8082" : port);
    this.baseUrl = "http://localhost:" + port ;
  }

  @Test
  public void testCallIndexPage() throws Exception {
    URL url = new URL(this.baseUrl);
    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
    connection.setRequestProperty("Authorization", "bearer 74eccf5f-0995-4e1c-b08c-d05dd5a0f89b");
    connection.connect();
    assertEquals(200, connection.getResponseCode());
    String output = IOUtils.toString(connection.getInputStream());
    assertTrue(output.contains("emma.blunt"));
  }

}
