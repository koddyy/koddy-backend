package com.koddy.server.common.containers

import org.flywaydb.test.junit5.annotation.FlywayTestExtension
import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.testcontainers.containers.MySQLContainer
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
@FlywayTestExtension
abstract class MySqlTestContainers {
    companion object {
        private const val MYSQL_IMAGE = "mysql:8.0.33"
        private const val DATABASE_NAME = "koddy"
        private const val USERNAME = "root"
        private const val PASSWORD = "1234"

        private val container: MySQLContainer<*> =
            MySQLContainer<Nothing>(MYSQL_IMAGE)
                .apply {
                    withDatabaseName(DATABASE_NAME)
                    withUsername(USERNAME)
                    withPassword(PASSWORD)
                    start()
                }
    }

    class Initializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
        override fun initialize(applicationContext: ConfigurableApplicationContext) {
            TestPropertyValues.of(
                "spring.datasource.url=${container.jdbcUrl}",
                "spring.datasource.username=${container.username}",
                "spring.datasource.password=${container.password}",
                "spring.flyway.url=${container.jdbcUrl}",
                "spring.flyway.user=${container.username}",
                "spring.flyway.password=${container.password}"
            ).applyTo(applicationContext.environment)
        }
    }
}
