//////////////////////////////////////////////////////////////////////////////
// RequestScope.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.spring.inject;

import com.google.inject.Key;
import com.google.inject.Provider;
import com.google.inject.Scope;
import reactor.core.publisher.Mono;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

@SuppressWarnings("unchecked")
public
class ReactiveRequestScope
    implements Scope
{
    @Override
    public <T> Provider<T>
    scope(Key<T> key,Provider<T> creator)
    {
        return
            new Provider<>()
            {
                @SuppressWarnings("unchecked")
                @Override
                public T
                get()
                {
                    Request request = null;
                    Class<T> instanceClass = getInstanceType(key);
                    T instance = getContextData(instanceClass);

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
                    return String.format("%s[%s]",creator,super.toString());
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
        Mono
            .deferContextual(Mono::just)
            .contextWrite(context -> context.put(key,instance));
    }

    private static <T> T
    getContextData(Class<T> key)
    {
        return
            Mono
                .deferContextual(contextView -> Mono.just(contextView.get(key)))
                .block();
    }
}

//////////////////////////////////////////////////////////////////////////////
