//////////////////////////////////////////////////////////////////////////////
// RequestScope.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.spring.inject;

import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Scope;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.RequestContextListener;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

@SuppressWarnings("unchecked")
public
class RequestScope
    implements Scope
{
    @Override
    public <T> Provider<T>
    scope(Key<T> key,Provider<T> creator)
    {
        return
            new Provider<T>()
            {
                @SuppressWarnings("unchecked")
                @Override
                public T
                get()
                {
                    Request    request = null;
                    Class<T>   instanceClass = getInstanceType(key);
                    T          instance = getContextData(instanceClass);

                    if (instance == null)
                    {
                        instance = creator.get();
                        setContextData(instanceClass,instance);
                    }

                    request = getContextData(Request.class);

                    if (request != null && instance instanceof AutoCloseable)
                        request.addCloseable((AutoCloseable)instance);

                    return instance;
                }

                @Override
                public String
                toString()
                {
                    return String.format("%s[%s]", creator, super.toString());
                }
            };
    }


    private static <T> Class<T>
    getInstanceType(Key<T> key)
    {
        Type type = key.getTypeLiteral().getType();

        if (type instanceof Class)
            return (Class<T>)type;

        if (type instanceof ParameterizedType)
            return (Class<T>)((ParameterizedType)type).getRawType();

        throw new ClassCastException("Unsupported type: " + type.getTypeName());
    }

    private static <T> void
    setContextData(Class<T> key,T instance)
    {
        RequestContextHolder
            .getRequestAttributes()
            .setAttribute(
                key.getCanonicalName(),
                instance,
                RequestAttributes.SCOPE_REQUEST);
    }

    private static <T> T
    getContextData(Class<T> key)
    {
        return
            (T)RequestContextHolder
                .currentRequestAttributes()
                .getAttribute(
                    key.getCanonicalName(),
                    RequestAttributes.SCOPE_REQUEST);
    }
}

//////////////////////////////////////////////////////////////////////////////
