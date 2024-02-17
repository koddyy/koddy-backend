package com.koddy.server.common.containers

import org.springframework.boot.test.util.TestPropertyValues
import org.springframework.context.ApplicationContextInitializer
import org.springframework.context.ConfigurableApplicationContext
import org.testcontainers.containers.GenericContainer
import org.testcontainers.junit.jupiter.Testcontainers

@Testcontainers
abstract class RedisTestContainers {
    companion object {
        private const val REDIS_IMAGE = "redis:latest"
        private const val REDIS_PORT = 6379

        private val container: GenericContainer<*> =
            GenericContainer<Nothing>(REDIS_IMAGE)
                .apply {
                    withExposedPorts(REDIS_PORT)
                    start()
                }
    }

    class Initializer : ApplicationContextInitializer<ConfigurableApplicationContext> {
        override fun initialize(applicationContext: ConfigurableApplicationContext) {
            TestPropertyValues.of(
                "spring.data.redis.host=${container.host}",
                "spring.data.redis.port=${container.getMappedPort(REDIS_PORT)}"
            ).applyTo(applicationContext.environment)
        }
    }
}
