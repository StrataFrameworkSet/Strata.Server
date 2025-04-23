//////////////////////////////////////////////////////////////////////////////
// OutboxProcessorTest.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.spring.outbox;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.support.TransactionTemplate;
import strata.foundation.core.value.EmailAddress;
import strata.server.core.notification.EmailMessageBuilder;
import strata.server.core.notification.IEmailMessage;
import strata.server.core.notification.IEmailMessageBuilder;
import strata.server.core.notification.StandardTemplateRepository;
import strata.server.core.outbox.*;
import strata.server.core.unitofwork.IUnitOfWork;
import strata.server.core.unitofwork.IUnitOfWorkSynchronizationManager;
import strata.server.spring.unitofwork.JpaUnitOfWorkManager;

import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("CommitStage")
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfiguration.class})
public
class OutboxProcessorTest
{
    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private IEmailMessageOutboxEventFactory factory;

    @Autowired
    private IOutboxEventRepository repository;

    @Autowired
    private EntityManager manager;

    @Autowired
    private JpaUnitOfWorkManager unitOfWorkManager;

    @Autowired
    private IUnitOfWork unitOfWork;

    @Autowired
    private IUnitOfWorkSynchronizationManager synchronizer;

    private IEmailMessage message;

    private OutboxProcessor processor;

    private TransactionTemplate transaction;

    @BeforeEach
    public void
    setUp()
    {
        IEmailMessageBuilder builder =
            new EmailMessageBuilder(new StandardTemplateRepository());
        Properties props = new Properties();

        props.setProperty("name", "engine");
        props.setProperty("connector.class", "io.debezium.connector.postgresql.PostgresConnector");
        props.setProperty("offset.storage", "org.apache.kafka.connect.storage.FileOffsetBackingStore");
        props.setProperty("offset.storage.file.filename", "C:/temp/debezium/offsets.dat");
        props.setProperty("offset.flush.interval.ms", "60000");
        props.setProperty("database.hostname", "localhost");
        props.setProperty("database.port", "5432");
        props.setProperty("database.user", "test-user");
        props.setProperty("database.password", "test-password");
        props.setProperty("database.server.name", "PostgreSQL-17");
        props.setProperty("database.dbname", "test");
        props.setProperty("topic.prefix", "my-app-connector");
        props.setProperty("schema.history.internal", "io.debezium.storage.file.history.FileSchemaHistory");
        props.setProperty("schema.history.internal.file.filename", "C:/temp/debezium/schemahistory.dat");
        props.setProperty("plugin.name", "pgoutput");
        props.setProperty("table.include.list", "public.outboxevent");
        props.setProperty("from.field", "after");


        message =
            builder
                .setSender(new EmailAddress("john.doe@xyz.com"))
                .setSubject("Test Email")
                .addRecipient(new EmailAddress("jane.doe@abc.com"))
                .setContent("This is a test.")
                .build();

        transaction = new TransactionTemplate(unitOfWorkManager);

        processor = new OutboxProcessor(
            Map.of(
                IEmailMessage.class.getSimpleName(),
                new EmailMessageOutboxEventConsumer(objectMapper,new MockEmailMessageSender())),
            props,
            objectMapper);
    }

    @Test
    public void
    testProcess() throws Exception
    {
        AtomicReference<OutboxEvent> expected =
            new AtomicReference<>(factory.create(message));
        AtomicReference<OutboxEvent> actual = new AtomicReference<>();

        processor.start();
        transaction.executeWithoutResult(
            status ->
            {
                for (int i = 0;i < 10;i++)
                    actual.set(repository.save(expected.get()));
            });

        Thread.sleep(5000);
        processor.stop();
    }
}

//////////////////////////////////////////////////////////////////////////////
