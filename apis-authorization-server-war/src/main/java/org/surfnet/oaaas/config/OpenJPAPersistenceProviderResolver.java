package org.surfnet.oaaas.config;

import java.util.Collections;
import java.util.List;

import javax.persistence.spi.PersistenceProvider;
import javax.persistence.spi.PersistenceProviderResolver;
import javax.persistence.spi.PersistenceProviderResolverHolder;

import org.apache.openjpa.persistence.PersistenceProviderImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This bean should be instantiated and register() should be called before using any JPA functions of Hibernate
 * validator. it sets the PersistenceProviderResolver to make sure Hibernate Validator will use the right
 * PersistentProvider, even if other providers are on the classpath (likt JPA 1 providers, got to love Weblogic).
 * 
 * @see https://hibernate.onjira.com/browse/JPA-4
 * @author Mike Noordermeer
 * 
 */
public class OpenJPAPersistenceProviderResolver implements PersistenceProviderResolver {
    static Logger logger = LoggerFactory.getLogger(OpenJPAPersistenceProviderResolver.class);

    private volatile PersistenceProvider persistenceProvider = new PersistenceProviderImpl();

    @Override
    public List<PersistenceProvider> getPersistenceProviders() {
        return Collections.singletonList(persistenceProvider);
    }

    @Override
    public void clearCachedProviders() {
        persistenceProvider = new PersistenceProviderImpl();
    }

    public void register() {
        logger.info("Registering OpenJPAPersistenceProviderResolver");
        PersistenceProviderResolverHolder.setPersistenceProviderResolver(this);
    }

}
