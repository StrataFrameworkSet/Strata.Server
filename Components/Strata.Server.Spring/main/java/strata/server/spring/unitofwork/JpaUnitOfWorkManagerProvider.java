//////////////////////////////////////////////////////////////////////////////
// JpaUnitOfWorkManagerProvider.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.spring.unitofwork;

import com.google.inject.Inject;
import com.google.inject.Provider;
import strata.server.core.unitofwork.IUnitOfWork;

public
class JpaUnitOfWorkManagerProvider
    implements Provider<ISpringUnitOfWorkManager>
{
    private final JpaUnitOfWork unitOfWork;

    @Inject
    public
    JpaUnitOfWorkManagerProvider(IUnitOfWork uow)
    {
        unitOfWork = (JpaUnitOfWork)uow;
    }

    @Override
    public ISpringUnitOfWorkManager
    get()
    {
        return new JpaUnitOfWorkManager(unitOfWork);
    }
}

//////////////////////////////////////////////////////////////////////////////
