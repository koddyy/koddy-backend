package com.koddy.server.common.containers;

import org.flywaydb.test.junit5.annotation.FlywayTestExtension;
import org.junit.jupiter.api.extension.Extension;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;

@SuppressWarnings("rawtypes")
@Testcontainers
@FlywayTestExtension
public class MySqlTestContainersExtension implements Extension {
    private static final String MYSQL_IMAGE = "mysql:8.0.33";
    private static final String DATABASE_NAME = "koddy";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "1234";
    private static final MySQLContainer<?> CONTAINER;

    static {
        CONTAINER = new MySQLContainer(MYSQL_IMAGE)
                .withDatabaseName(DATABASE_NAME)
                .withUsername(USERNAME)
                .withPassword(PASSWORD);
        CONTAINER.start();

        System.setProperty("spring.datasource.url", CONTAINER.getJdbcUrl());
        System.setProperty("spring.datasource.username", CONTAINER.getUsername());
        System.setProperty("spring.datasource.password", CONTAINER.getPassword());
        System.setProperty("spring.flyway.url", CONTAINER.getJdbcUrl());
        System.setProperty("spring.flyway.user", CONTAINER.getUsername());
        System.setProperty("spring.flyway.password", CONTAINER.getPassword());
    }
}
