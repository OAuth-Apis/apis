/*
 * Copyright 2012 SURFnet bv, The Netherlands
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.surfnet.oaaas.boot;

import javax.sql.DataSource;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.surfnet.oaaas.auth.SimpleAuthenticationHandler;
import org.surfnet.oaaas.resource.ClientResource;
import org.surfnet.oaaas.resource.ResourceServerResource;
import org.surfnet.oaaas.resource.TokenResource;

import com.googlecode.flyway.core.Flyway;
import com.yammer.dropwizard.Service;
import com.yammer.dropwizard.bundles.AssetsBundle;
import com.yammer.dropwizard.config.Environment;
import com.yammer.dropwizard.views.ViewBundle;

public class Application extends Service<ApplicationConfiguration> {

  public static void main(String[] args) throws Exception {
    if (args == null || args.length != 2) {
      args = new String[] { "server", "oaaas.yml" };
    }
    new Application("OAaaS").run(args);
  }

  public Application(String name) {
    super(name);
    
    /*
     * For serving static content
     */
    addBundle(new AssetsBundle());
    /*
     * For supporting freemarker views
     */
    addBundle(new ViewBundle());
  }

  @Override
  protected void initialize(ApplicationConfiguration configuration, Environment environment) throws Exception {

    ApplicationContext ctx = createSpringContext();

    initFlyway(ctx.getBean(DataSource.class));
    environment.addResource(ctx.getBean(ResourceServerResource.class));
    environment.addResource(ctx.getBean(ClientResource.class));
    environment.addResource(ctx.getBean(TokenResource.class));
    environment.addResource(ctx.getBean(SimpleAuthenticationHandler.class));
  }

  private void initFlyway(DataSource datasource) {
    Flyway flyway = new Flyway();
    flyway.setDataSource(datasource);
    //flyway.init();
    flyway.migrate();
  }

  private ApplicationContext createSpringContext() {
    AnnotationConfigApplicationContext ctx = new AnnotationConfigApplicationContext();
    ctx.register(SpringConfiguration.class);
    ctx.refresh();
    return ctx;
  }
}
