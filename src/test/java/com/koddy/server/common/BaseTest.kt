package com.koddy.server.common

import com.koddy.server.common.containers.MySqlTestContainers
import com.koddy.server.common.containers.RedisTestContainers
import com.koddy.server.common.containers.callback.DatabaseCleanerEachCallbackExtension
import com.koddy.server.common.containers.callback.RedisCleanerEachCallbackExtension
import com.koddy.server.global.config.etc.P6SpyConfig
import com.koddy.server.global.config.infra.QueryDslConfig
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
@DataJpaTest(showSql = false)
@ContextConfiguration(initializers = [MySqlTestContainers.Initializer::class])
@Import(
    QueryDslConfig::class,
    P6SpyConfig::class,
)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
annotation class RepositoryTestKt

@Tag("Redis")
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@DataRedisTest
@ContextConfiguration(initializers = [RedisTestContainers.Initializer::class])
annotation class RedisTestKt

@Tag("Integrate")
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@SpringBootTest
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
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
annotation class IntegrateTestKt
