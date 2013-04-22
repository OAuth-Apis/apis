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

package org.surfnet.oaaas.repository;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;

import com.googlecode.flyway.core.Flyway;

import org.apache.openjpa.persistence.PersistenceProviderImpl;
import org.apache.tomcat.jdbc.pool.DataSource;
import org.junit.BeforeClass;
import org.springframework.data.jpa.repository.support.JpaRepositoryFactory;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;

/**
 * Test the flyway migrations.
 * 
 */
public class AbstractTestRepository {

  private static final String PERSISTENCE_UNIT_NAME = "oaaas";
  private static final Class<PersistenceProviderImpl> PERSISTENCE_PROVIDER_CLASS = PersistenceProviderImpl.class;
  private static JpaRepositoryFactory factory;
  protected static EntityManager entityManager;

  @BeforeClass
  public static void beforeClass() {
    DataSource dataSource = dataSource();
    entityManager = entityManager(dataSource);
    initFlyway(dataSource);
    factory = new JpaRepositoryFactory(entityManager);
  }

  public <T> T getRepository(Class<T> repositoryInterface) {
    return factory.getRepository(repositoryInterface);
  }

  private static void initFlyway(DataSource dataSource) {
    final Flyway flyway = new Flyway();
    flyway.setInitOnMigrate(true);
    flyway.setDataSource(dataSource);
    flyway.setLocations("db/migration/hsqldb");
    flyway.migrate();
  }

  @SuppressWarnings({ "rawtypes", "unchecked" })
  private static EntityManager entityManager(DataSource dataSource) {
    LocalContainerEntityManagerFactoryBean emfBean = new LocalContainerEntityManagerFactoryBean();
    emfBean.setDataSource(dataSource);
    emfBean.setPersistenceUnitName(PERSISTENCE_UNIT_NAME);
    emfBean.setPersistenceProviderClass(PERSISTENCE_PROVIDER_CLASS);
    emfBean.afterPropertiesSet();
    Map map = new HashMap<String, String>();
    map.put("openjpa.ConnectionFactoryProperties", "PrintParameters=true");
    return emfBean.getObject().createEntityManager(map);
  }

  private static DataSource dataSource() {
    DataSource dataSource = new DataSource();
    dataSource.setDriverClassName("org.hsqldb.jdbcDriver");
    dataSource.setUrl("jdbc:hsqldb:file:target/db;shutdown=true");
    dataSource.setUsername("sa");
    dataSource.setPassword("");
    return dataSource;
  }

  /**
   * @return the entityManager
   */
  public static EntityManager getEntityManager() {
    return entityManager;
  }

}
