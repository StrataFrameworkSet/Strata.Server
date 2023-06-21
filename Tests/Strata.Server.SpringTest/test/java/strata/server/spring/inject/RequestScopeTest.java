//////////////////////////////////////////////////////////////////////////////
// RequestScopeTest.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.spring.inject;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.TypeLiteral;
import jakarta.ws.rs.core.Application;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Tag("CurrentlyFailing")
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.MOCK,
    classes = Application.class)
public
class RequestScopeTest
{
    private IRequestProvider itsProvider;

    @BeforeEach
    public void
    setUp()
    {
        Injector injector = Guice.createInjector(new TestModule());

        itsProvider = injector.getInstance(IRequestProvider.class);
    }

    @AfterEach
    public void
    tearDown()
    {
        itsProvider = null;
    }

    @Test
    public void
    testRequestScope()
        throws Exception
    {
        Map<Integer,List<Integer>> lists = new TreeMap<>();

        for (int i=0;i < 5;i++)
        {
            try (Request request = itsProvider.get())
            {
                List<Integer> list =
                    request.getInstance(
                        new TypeLiteral<List<Integer>>(){});

                list.addAll(Arrays.asList(i,i,i,i,i));
                lists.put(i,list);
            }
        }

        assertEquals(5,lists.size());

        assertTrue(lists.containsKey(0));
        assertTrue(lists.containsKey(1));
        assertTrue(lists.containsKey(2));
        assertTrue(lists.containsKey(3));
        assertTrue(lists.containsKey(4));

        for (Integer i: lists.keySet())
        {
            List<Integer> list = lists.get(i);

            assertEquals(5,list.size());

            for (Integer j: list)
                assertEquals(i,j);
        }
    }
}

//////////////////////////////////////////////////////////////////////////////
