//////////////////////////////////////////////////////////////////////////////
// MissingPropertiesException.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.spring.repository;

import java.util.ArrayList;
import java.util.List;

public
class MissingPropertiesException
    extends RuntimeException
{
    private final List<String> missing;

    public
    MissingPropertiesException(List<String> m)
    {
        super(createMessage(m));
        missing = new ArrayList<>(m);
    }

    public List<String>
    getMissing() { return missing; }

    private static String
    createMessage(List<String> missing)
    {
        StringBuilder builder = new StringBuilder();

        builder.append(
            "Configuration is missing the following required properties: ");

        missing
            .stream()
            .forEach(property -> builder.append('\n').append(property));

        return builder.toString();
    }
}

//////////////////////////////////////////////////////////////////////////////
