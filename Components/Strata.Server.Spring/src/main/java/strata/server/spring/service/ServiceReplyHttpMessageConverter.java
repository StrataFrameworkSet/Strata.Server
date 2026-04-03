//////////////////////////////////////////////////////////////////////////////
// ServiceReplyHttpMessageConverter.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.spring.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpInputMessage;
import org.springframework.http.HttpOutputMessage;
import org.springframework.http.MediaType;
import org.springframework.http.converter.AbstractHttpMessageConverter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import strata.foundation.core.mapper.ObjectMapperSupplier;
import strata.foundation.core.transfer.AbstractServiceReply;

import java.io.IOException;

public
class ServiceReplyHttpMessageConverter
    extends AbstractHttpMessageConverter<AbstractServiceReply>
{
    private ObjectMapper mapper;
    private Logger       logger;

    public
    ServiceReplyHttpMessageConverter()
    {
        super(MediaType.APPLICATION_JSON);
        mapper = new ObjectMapperSupplier().get();
        logger = LogManager.getLogger(ServiceReplyHttpMessageConverter.class);
        logger.info("ServiceReplyHttpMessageConverter registered");
    }

    @Override
    protected boolean
    supports(Class<?> type)
    {
        if (
            AbstractServiceReply
                .class
                .isAssignableFrom(type))
        {
            logger.debug(type.getCanonicalName() + " is supported");
            return true;
        }

        logger.debug(type.getCanonicalName() + " is not supported");
        return false;
    }

    @Override
    protected AbstractServiceReply
    readInternal(Class<? extends AbstractServiceReply> type,HttpInputMessage message)
        throws IOException, HttpMessageNotReadableException
    {
        logger.debug("Getting entity from response");
        return mapper.readValue(message.getBody(),type);
    }

    @Override
    protected void
    writeInternal(AbstractServiceReply reply,HttpOutputMessage message)
        throws IOException, HttpMessageNotWritableException
    {
        logger.debug("Analyzing response...");

        if (reply != null)
        {
            if (
                !reply.isSuccess() &&
                message instanceof HttpServletResponse response)
            {
                logger.debug("Setting response.status to " + HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            }

            mapper.writeValue(message.getBody(),reply);
        }
    }
}

//////////////////////////////////////////////////////////////////////////////
