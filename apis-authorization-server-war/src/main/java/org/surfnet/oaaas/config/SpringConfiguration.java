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
import org.apache.openjpa.persistence.PersistenceProviderImpl;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.springframework.context.annotation.*;
import org.springframework.core.env.Environment;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.surfnet.oaaas.auth.*;
import org.surfnet.oaaas.repository.ExceptionTranslator;
import org.surfnet.oaaas.repository.OpenJPAExceptionTranslator;

import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.ServletException;
import javax.validation.Validator;
import java.util.Arrays;
import java.util.List;

/**
 * The SpringConfiguration is a {@link Configuration} that can be overridden if
 * you want to plugin your own implementations. Note that the two most likely
 * candidates to change are the {@link AbstractAuthenticator} an
 * {@link AbstractUserConsentHandler}. You can change the implementation by
 * editing the application.apis.properties file where the implementations are
 * configured.
 */
@Configuration
@PropertySource("classpath:apis.application.properties")
/*
 * The component scan can be used to add packages and exclusions to the default
 * package
 */
@ComponentScan(basePackages = {"org.surfnet.oaaas.resource"})
@ImportResource("classpath:spring-repositories.xml")
@EnableTransactionManagement
public class SpringConfiguration {

  private static final String PERSISTENCE_UNIT_NAME = "oaaas";
  private static final Class<PersistenceProviderImpl> PERSISTENCE_PROVIDER_CLASS = PersistenceProviderImpl.class;

  @Inject
  Environment env;

  @Bean
  public javax.sql.DataSource dataSource() {
    DataSource dataSource = new DataSource();
    dataSource.setDriverClassName(env.getProperty("jdbc.driverClassName"));
    dataSource.setUrl(env.getProperty("jdbc.url"));
    dataSource.setUsername(env.getProperty("jdbc.username"));
    dataSource.setPassword(env.getProperty("jdbc.password"));
    dataSource.setTestOnBorrow(true);
    dataSource.setValidationQuery("SELECT 1");
    return dataSource;
  }

  @Bean
  public Flyway flyway() {
    final Flyway flyway = new Flyway();
    flyway.setInitOnMigrate(true);
    flyway.setDataSource(dataSource());
    String locationsValue = env.getProperty("flyway.migrations.location");
    String[] locations = locationsValue.split("\\s*,\\s*");
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
  public Filter oauth2AuthenticationFilter() {
    final AuthenticationFilter authenticationFilter = new AuthenticationFilter();
    authenticationFilter.setAuthenticator(authenticator());
    return authenticationFilter;
  }

  @Bean
  public Filter oauth2UserConsentFilter() {
    final UserConsentFilter userConsentFilter = new UserConsentFilter();
    userConsentFilter.setUserConsentHandler(userConsentHandler());
    return userConsentFilter;
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
    AbstractAuthenticator authenticatorClass = (AbstractAuthenticator) getConfiguredBean("authenticatorClass");
    try {
      authenticatorClass.init(null);
    } catch (ServletException e) {
      throw new RuntimeException(e);
    }
    return authenticatorClass;
  }

  @Bean
  public AbstractUserConsentHandler userConsentHandler() {
    return (AbstractUserConsentHandler) getConfiguredBean("userConsentHandlerClass");
  }

  @Bean
  public ExceptionTranslator exceptionTranslator() {
    return new OpenJPAExceptionTranslator();
  }

  private Object getConfiguredBean(String className) {
    try {
      return getClass().getClassLoader().loadClass(env.getProperty(className)).newInstance();
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

}
