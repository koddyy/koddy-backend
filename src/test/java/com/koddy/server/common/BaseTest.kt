package com.koddy.server.common

import com.koddy.server.common.containers.MySqlTestContainers
import com.koddy.server.common.containers.RedisTestContainers
import com.koddy.server.common.containers.callback.DatabaseCleanerEachCallbackExtension
import com.koddy.server.common.containers.callback.RedisCleanerEachCallbackExtension
import com.koddy.server.common.utils.RedisCleaner
import com.koddy.server.global.config.etc.P6SpyConfig
import com.koddy.server.global.config.infra.QueryDslConfig
import io.kotest.core.config.AbstractProjectConfig
import io.kotest.core.spec.IsolationMode
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.api.parallel.Execution
import org.junit.jupiter.api.parallel.ExecutionMode
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.TestConstructor
import java.time.LocalDate
import java.time.LocalDateTime

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
annotation class TestEnvironment

@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@Execution(ExecutionMode.CONCURRENT)
annotation class ExecuteParallel

@Tag("Unit")
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@ExecuteParallel
annotation class UnitTestKt

@Tag("Repository")
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(initializers = [MySqlTestContainers.Initializer::class])
@Import(
    QueryDslConfig::class,
    P6SpyConfig::class,
)
@TestEnvironment
@DataJpaTest(showSql = false)
annotation class RepositoryTestKt

@Tag("Redis")
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@ContextConfiguration(initializers = [RedisTestContainers.Initializer::class])
@ExtendWith(RedisCleanerEachCallbackExtension::class)
@Import(RedisCleaner::class)
@TestEnvironment
@DataRedisTest
annotation class RedisTestKt

@Tag("Integrate")
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ContextConfiguration(
    initializers = [
        MySqlTestContainers.Initializer::class,
        RedisTestContainers.Initializer::class,
    ],
)
@ExtendWith(
    DatabaseCleanerEachCallbackExtension::class,
    RedisCleanerEachCallbackExtension::class,
)
@TestEnvironment
@SpringBootTest
annotation class IntegrateTestKt

/**
 * Complete isolation between tests `with mocking`
 */
class ProjectConfig : AbstractProjectConfig() {
    override val isolationMode = IsolationMode.InstancePerLeaf
}

/**
 * String -> LocalDate & LocalDateTime `Delimiter`
 */
private const val DATE_DELIMITER: String = "/"
private const val DATE_TIME_DELIMITER: String = "-"
private const val TIME_DELIMITER: String = ":"

/**
 * yyyy/MM/dd -> LocalDate
 */
fun String.toLocalDate(): LocalDate {
    val split: List<Int> = this.split(DATE_DELIMITER.toRegex()).map { it.toInt() }
    return LocalDate.of(split[0], split[1], split[2])
}

/**
 * yyyy/MM/dd-HH:mm -> LocalDateTime
 */
fun String.toLocalDateTime(): LocalDateTime {
    val split: List<String> = this.split(DATE_TIME_DELIMITER.toRegex())
    val dateSplit: List<Int> = split[0].split(DATE_DELIMITER.toRegex()).map { it.toInt() }
    val timeSplit: List<Int> = split[1].split(TIME_DELIMITER.toRegex()).map { it.toInt() }
    return LocalDateTime.of(dateSplit[0], dateSplit[1], dateSplit[2], timeSplit[0], timeSplit[1])
}
