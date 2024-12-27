package org.txn.control.fincore;

import org.junit.jupiter.api.BeforeEach;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

public abstract class BaseRepositoryTest<T> {

    protected T testEntity;
    protected final TestEntityManager entityManager;

    public BaseRepositoryTest(TestEntityManager entityManager) {
        this.entityManager = entityManager;
    }

    @BeforeEach
    public void setUp() {
        testEntity = createTestEntity();
    }

    protected abstract T createTestEntity();
}
