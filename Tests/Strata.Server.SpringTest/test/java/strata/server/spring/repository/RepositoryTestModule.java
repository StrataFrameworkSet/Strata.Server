//////////////////////////////////////////////////////////////////////////////
// RepositoryTestModule.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.spring.repository;

import com.google.inject.internal.SingletonScope;
import strata.foundation.core.configuration.IConfiguration;
import strata.foundation.guice.inject.AbstractModule;
import strata.foundation.core.inject.ApplicationConfigurationProvider;
import strata.foundation.guice.inject.ThreadScope;
import strata.server.core.repository.FooBarRepository;
import strata.server.core.repository.IFooBarRepository;
import strata.server.core.repository.IFooRepository;

public
class RepositoryTestModule
    extends AbstractModule
{
    public
    RepositoryTestModule()
    {
        setDefaultScope(new ThreadScope());
    }

    @Override
    protected void
    configure()
    {
        super.configure();
        install(new RepositoryModule());

        bind(IConfiguration.class)
            .toProvider(new ApplicationConfigurationProvider("repositorytest"))
            .in(new SingletonScope());

        bind(IFooRepository.class)
            .toProvider(FooRepositoryProvider.class)
            .in(getDefaultScope());

        bind(IFooBarRepository.class)
            .to(FooBarRepository.class)
            .in(getDefaultScope());

        bind(IFooJpaRepository.class)
            .toProvider(FooJpaRepositoryProvider.class)
            .in(getDefaultScope());
    }
}

//////////////////////////////////////////////////////////////////////////////
