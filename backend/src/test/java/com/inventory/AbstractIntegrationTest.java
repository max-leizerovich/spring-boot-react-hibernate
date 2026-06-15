package com.inventory;

import org.springframework.boot.test.context.SpringBootTest;

/**
 * Base class for integration tests that require the docker-compose PostgreSQL instance.
 *
 * <p>Start the database before running tests: {@code docker compose up -d}
 */
@SpringBootTest
public abstract class AbstractIntegrationTest {
}
