package com.koddy.server.common

import com.koddy.server.common.config.BlackboxLogicControlConfig
import com.koddy.server.common.config.ExternalApiConfig
import com.koddy.server.common.containers.LocalStackTestContainersConfig
import com.koddy.server.common.containers.MySqlTestContainers
import com.koddy.server.common.containers.RedisTestContainers
import io.kotest.core.spec.style.BehaviorSpec
import io.restassured.RestAssured
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.server.LocalServerPort
import org.springframework.context.annotation.Import
import org.springframework.test.context.ContextConfiguration

@Tag("Acceptance")
@SpringBootTest(
    webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    classes = [LocalStackTestContainersConfig::class],
)
@ContextConfiguration(
    initializers = [
        MySqlTestContainers.Initializer::class,
        RedisTestContainers.Initializer::class,
    ],
)
@Import(
    ExternalApiConfig::class,
    BlackboxLogicControlConfig::class,
)
abstract class AcceptanceTestKt : BehaviorSpec() {
    @LocalServerPort
    private val port: Int = 0

    @BeforeEach
    fun setUp() {
        RestAssured.port = port
    }
}
