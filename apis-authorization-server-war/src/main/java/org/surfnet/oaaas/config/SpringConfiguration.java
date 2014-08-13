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

package org.surfnet.oaaas.config;

import com.googlecode.flyway.core.Flyway;
import com.sun.jersey.spi.spring.container.servlet.SpringServlet;
import org.apache.openjpa.persistence.PersistenceProviderImpl;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.boot.context.web.SpringBootServletInitializer;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.surfnet.oaaas.auth.*;
import org.surfnet.oaaas.repository.ExceptionTranslator;
import org.surfnet.oaaas.repository.OpenJPAExceptionTranslator;
import org.surfnet.oaaas.support.Cleaner;

import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.ServletException;
import javax.validation.Validator;

/**
 * The SpringConfiguration is a {@link Configuration} that can be overridden if
 * you want to plugin your own implementations. Note that the two most likely
 * candidates to change are the {@link AbstractAuthenticator} an
 * {@link AbstractUserConsentHandler}. You can change the implementation by
 * editing the application.apis.properties file where the implementations are
 * configured.
 */
@Configuration
@EnableAutoConfiguration
@PropertySource("classpath:application.properties")
@ComponentScan(basePackages = {"org.surfnet.oaaas.resource"})
@EnableTransactionManagement
@EnableScheduling
@EnableJpaRepositories(basePackages = "org.surfnet.oaaas.repository")
public class SpringConfiguration extends SpringBootServletInitializer {

  private static final String PERSISTENCE_UNIT_NAME = "oaaas";
  private static final Class<PersistenceProviderImpl> PERSISTENCE_PROVIDER_CLASS = PersistenceProviderImpl.class;

  @Value("${jdbc.driverClassName}")
  String driverClassName;

  @Value("${jdbc.url}")
  String url;

  @Value("${jdbc.username}")
  String username;

  @Value("${jdbc.password}")
  String password;

  @Value("${flyway.migrations.location}")
  String flywayMigrationsLocation;

  @Value("${authenticatorClass}")
  String authenticatorClassName;

  @Value("${userConsentHandlerClass}")
  String userConsentHandlerClassName;

  @Override
  protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
    return application.sources(SpringConfiguration.class);
  }

  @Bean
  public FilterRegistrationBean adminAuthorizationFilter() {
    FilterRegistrationBean bean = new FilterRegistrationBean();
    bean.setFilter(new AuthorizationServerFilter());
    bean.addInitParameter("apis-resource-server.properties.file", "application.properties");
    bean.addUrlPatterns("/admin/*");
    return bean;
  }

  @Bean
  public FilterRegistrationBean oauth2AuthenticationFilter() {
    FilterRegistrationBean bean = new FilterRegistrationBean();
    AuthenticationFilter filter = new AuthenticationFilter();
    filter.setAuthenticator(authenticator());
    bean.setFilter(filter);
    bean.addUrlPatterns("/oauth2/authorize");
    return bean;
  }

  @Bean
  public FilterRegistrationBean oauth2UserConsentFilter() {
    FilterRegistrationBean bean = new FilterRegistrationBean();
    UserConsentFilter filter = new UserConsentFilter();
    filter.setUserConsentHandler(userConsentHandler());
    bean.setFilter(filter);
    bean.addUrlPatterns("/oauth2/authorize", "/oauth2/consent");
    return bean;
  }

  @Bean
  public ServletRegistrationBean jersey() {
    ServletRegistrationBean bean = new ServletRegistrationBean();
    bean.setServlet(new SpringServlet());
    bean.addInitParameter("com.sun.jersey.api.json.POJOMappingFeature", "true");
    bean.setLoadOnStartup(1);
    bean.addUrlMappings("/oauth2/*", "/v1/*", "/admin/*");
    return bean;
  }

  @Bean
  public javax.sql.DataSource dataSource() {
    DriverManagerDataSource dataSource = new DriverManagerDataSource();
    dataSource.setDriverClassName(driverClassName);
    dataSource.setUrl(url);
    dataSource.setUsername(username);
    dataSource.setPassword(password);
    return dataSource;
  }

  @Bean
  public Flyway flyway() {
    final Flyway flyway = new Flyway();
    flyway.setInitOnMigrate(true);
    flyway.setDataSource(dataSource());
    String[] locations = flywayMigrationsLocation.split("\\s*,\\s*");
    flyway.setLocations(locations);
    flyway.migrate();
    return flyway;
  }

  @Bean
  public JpaTransactionManager transactionManager() {
    return new JpaTransactionManager();
  }

  @Bean
  public LocalContainerEntityManagerFactoryBean entityManagerFactory() {
    final LocalContainerEntityManagerFactoryBean emfBean = new LocalContainerEntityManagerFactoryBean();
    emfBean.setDataSource(dataSource());
    emfBean.setPersistenceUnitName(PERSISTENCE_UNIT_NAME);
    emfBean.setPersistenceProviderClass(PERSISTENCE_PROVIDER_CLASS);
    return emfBean;
  }

  @Bean
  public OAuth2Validator oAuth2Validator() {
    return new OAuth2ValidatorImpl();
  }

  /**
   * Returns the {@link AbstractAuthenticator} that is responsible for the
   * authentication of Resource Owners.
   *
   * @return an {@link AbstractAuthenticator}
   */
  @Bean
  public AbstractAuthenticator authenticator() {
    AbstractAuthenticator authenticatorClass = (AbstractAuthenticator) getConfiguredBean(authenticatorClassName);
    try {
      authenticatorClass.init(null);
    } catch (ServletException e) {
      throw new RuntimeException(e);
    }
    return authenticatorClass;
  }

  @Bean
  public AbstractUserConsentHandler userConsentHandler() {
    return (AbstractUserConsentHandler) getConfiguredBean(userConsentHandlerClassName);
  }

  @Bean
  public ExceptionTranslator exceptionTranslator() {
    return new OpenJPAExceptionTranslator();
  }

  private Object getConfiguredBean(String className) {
    try {
      return getClass().getClassLoader().loadClass(className).newInstance();
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Bean
  public Validator validator() {
    // This LocalValidatorFactoryBean already uses the SpringConstraintValidatorFactory by default,
    // so available validators will be wired automatically.
    return new org.springframework.validation.beanvalidation.LocalValidatorFactoryBean();
  }

  @Bean
  public Cleaner cleaner() {
    return new Cleaner();
  }
}
