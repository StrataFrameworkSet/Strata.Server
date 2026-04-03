//////////////////////////////////////////////////////////////////////////////
// EntityManagerProvider.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.spring.repository;

import com.google.inject.Provider;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceUnit;
import org.hibernate.dialect.H2Dialect;
import org.hibernate.jpa.HibernatePersistenceProvider;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.persistenceunit.DefaultPersistenceUnitManager;
import org.springframework.orm.jpa.persistenceunit.PersistenceManagedTypes;
import org.springframework.orm.jpa.vendor.Database;
import org.springframework.orm.jpa.vendor.HibernateJpaDialect;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;
import strata.foundation.core.configuration.IConfiguration;

import javax.sql.DataSource;
import java.util.Properties;

public
class EntityManagerFactoryProvider
    implements Provider<EntityManagerFactory>
{
    private final LocalContainerEntityManagerFactoryBean factory;

    @Inject
    public
    EntityManagerFactoryProvider(LocalContainerEntityManagerFactoryBean f)
    {
        factory = f;
    }

    @Override
    public EntityManagerFactory
    get()
    {
        return factory.getObject();
    }
}

//////////////////////////////////////////////////////////////////////////////
