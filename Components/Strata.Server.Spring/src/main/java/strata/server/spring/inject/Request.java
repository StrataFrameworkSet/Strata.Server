//////////////////////////////////////////////////////////////////////////////
// Operation.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.spring.inject;

import com.google.inject.Injector;
import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public
class Request
    implements AutoCloseable
{
    private final Injector             itsInjector;
    private final Queue<AutoCloseable> itsCloseables;

    public
    Request(Injector injector)
    {
        itsInjector = injector;
        itsCloseables = new ConcurrentLinkedQueue<>();
        setRequestContext(this);
    }

    public <T> T
    getInstance(Class<T> type)
    {
        T instance = itsInjector.getInstance(type);

        if (instance instanceof AutoCloseable)
            addCloseable((AutoCloseable)instance);

        return instance;
    }

    public <T> T
    getInstance(TypeLiteral<T> type)
    {
        T instance = itsInjector.getInstance(Key.get(type));

        if (instance instanceof AutoCloseable)
            addCloseable((AutoCloseable)instance);

        return instance;
    }

    public <T> T
    getInstance(Key<T> key)
    {
        T instance = itsInjector.getInstance(key);

        if (instance instanceof AutoCloseable)
            addCloseable((AutoCloseable)instance);

        return instance;
    }

    @Override
    public void
    close()
    {
        removeRequestContext();

        while (!itsCloseables.isEmpty())
        {
            AutoCloseable closeable = itsCloseables.remove();

            try
            {
                closeable.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    protected void
    addCloseable(AutoCloseable closeable)
    {
        if (!itsCloseables.contains(closeable))
            itsCloseables.add(closeable);
    }

    private static void
    setRequestContext(Request request)
    {
        RequestContextHolder
            .getRequestAttributes()
            .setAttribute(
                Request
                    .class
                    .getCanonicalName(),
                request,
                RequestAttributes.SCOPE_REQUEST);
    }

    private static void
    removeRequestContext()
    {
        RequestContextHolder
            .getRequestAttributes()
            .removeAttribute(
                Request
                    .class
                    .getCanonicalName(),
                RequestAttributes.SCOPE_REQUEST);
    }
}

//////////////////////////////////////////////////////////////////////////////
