//////////////////////////////////////////////////////////////////////////////
// OutboxEventRepositoryTest.java
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
import strata.server.core.outbox.IEmailMessageOutboxEventFactory;
import strata.server.core.outbox.IOutboxEventRepository;
import strata.server.core.outbox.OutboxEvent;
import strata.server.core.unitofwork.IUnitOfWork;
import strata.server.core.unitofwork.IUnitOfWorkSynchronizationManager;
import strata.server.spring.unitofwork.JpaUnitOfWorkManager;

import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.assertEquals;

@Tag("CommitStage")
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestConfiguration.class})
public
class OutboxEventRepositoryTest
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

    private TransactionTemplate transaction;

    @BeforeEach
    public void
    setUp()
    {
        IEmailMessageBuilder builder =
            new EmailMessageBuilder(new StandardTemplateRepository());

        message =
            builder
                .setSender(new EmailAddress("john.doe@xyz.com"))
                .setSubject("Test Email")
                .addRecipient(new EmailAddress("jane.doe@abc.com"))
                .setContent("This is a test.")
                .build();

        transaction = new TransactionTemplate(unitOfWorkManager);
    }

    @Test
    public void
    testSave()
    {
        AtomicReference<OutboxEvent> expected =
            new AtomicReference<>(factory.create(message));
        AtomicReference<OutboxEvent> actual = new AtomicReference<>();

        transaction.executeWithoutResult(
            status ->
            {
                actual.set(repository.save(expected.get()));
            });

        assertEquals(
            expected.get().getSourceType(),
            actual.get().getSourceType());

        assertEquals(
            expected.get().getEventType(),
            actual.get().getEventType());

        assertEquals(
            expected.get().getEventPayload(),
            actual.get().getEventPayload());
    }
}

//////////////////////////////////////////////////////////////////////////////
