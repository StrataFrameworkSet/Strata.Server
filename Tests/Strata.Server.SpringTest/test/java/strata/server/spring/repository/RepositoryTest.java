//////////////////////////////////////////////////////////////////////////////
// EntityManagerProviderTest.java
//////////////////////////////////////////////////////////////////////////////

package strata.server.spring.repository;

import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;
import strata.server.core.entity.*;
import strata.server.core.repository.IFooBarRepository;
import strata.server.core.repository.IFooRepository;
import strata.server.core.unitofwork.IUnitOfWork;
import strata.server.core.unitofwork.IUnitOfWorkSynchronizationManager;

import java.time.Instant;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

@Tag("CommitStage")
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {TestSpringConfiguration.class})
public
class RepositoryTest
{
    @Autowired
    private IFooRepository target;

    @Autowired
    private IFooBarRepository repository;

    @Autowired
    private EntityManager manager;

    @Autowired
    private PlatformTransactionManager transactionManager;

    @Autowired
    private IUnitOfWork unitOfWork;

    @Autowired
    private IUnitOfWorkSynchronizationManager synchronizer;

    private TransactionTemplate transaction;

    @BeforeEach
    public void
    setUp() throws Exception
    {
        transaction = new TransactionTemplate(transactionManager);
    }

    @AfterEach
    public void
    tearDown()
    {
        target = null;
    }

    @Test
    @Transactional
    public void
    testSave() throws Exception
    {
        final AtomicReference<Foo> expected = new AtomicReference<>();
        final AtomicReference<Foo> actual = new AtomicReference<>();
        final AtomicReference<IFooBar> foobarA = new AtomicReference<>();
        final AtomicReference<IFooBar> foobarB = new AtomicReference<>();

        transaction.executeWithoutResult(
            status ->
            {
                expected.set(
                    new Foo()
                        .setName("Foo123"));
                foobarA.set(
                    new FooBarA()
                        .setCreated(Instant.now())
                        .setBaz("XXX"));
                foobarB.set(
                    new FooBarB()
                        .setIndicator(true)
                        .setCreated(Instant.now())
                        .setBaz("ZZZ"));
                actual.set(target.save(expected.get()));
                foobarA.set(repository.save(foobarA.get()));
                foobarB.set(repository.save(foobarB.get()));
                synchronizer
                    .executeAfterCommit(() -> System.out.println("After Commit 1"))
                    .executeAfterCommit(() -> System.out.println("After Commit 2"))
                    .executeAfterCommit(() -> System.out.println("After Commit 3"))
                    .executeAfterRollback(() -> System.out.println("After Rollback 1"));
            });
        transaction.executeWithoutResult(
            status ->
            {
                foobarA.set(repository.save(foobarA.get().setBaz("YYY")));
                foobarB.set(repository.save(foobarB.get().setBaz("YYY")));
                synchronizer
                    .executeAfterCommit(() -> System.out.println("After Commit 4"))
                    .executeAfterCommit(() -> System.out.println("After Commit 5"))
                    .executeAfterCommit(() -> System.out.println("After Commit 6"))
                    .executeAfterRollback(() -> System.out.println("After Rollback 2"));

            });
        target
            .findAll()
            .stream()
            .forEach(foo -> System.out.println("primaryId="+foo.getPrimaryId()));

        Stream<Foo> result = target.findByName(actual.get().getName());

        result.forEach(foo -> assertEquals(actual.get().getName(),foo.getName()));

        repository
            .findByBaz(foobarA.get().getBaz())
            .forEach(fb -> assertEquals(foobarA.get().getBaz(),fb.getBaz()));
        assertNotNull(actual.get());
        assertNotNull(actual.get().getPrimaryId());
        assertEquals(expected.get().getName(),actual.get().getName());
        assertTrue(target.findById(actual.get().getPrimaryId()).isPresent());
        assertTrue(target.existsById(actual.get().getPrimaryId()));

        assertNotNull(foobarA.get());
        assertTrue(repository.existsById(foobarA.get().getPrimaryId()));

    }
}

//////////////////////////////////////////////////////////////////////////////
