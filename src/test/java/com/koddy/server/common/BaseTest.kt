package com.koddy.server.common

import com.koddy.server.common.containers.MySqlTestContainers
import com.koddy.server.common.containers.RedisTestContainers
import com.koddy.server.common.containers.callback.DatabaseCleanerEachCallbackExtension
import com.koddy.server.common.containers.callback.RedisCleanerEachCallbackExtension
import com.koddy.server.common.utils.RedisCleaner
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
import org.springframework.test.context.TestConstructor

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
@DataJpaTest(showSql = false)
@TestEnvironment
annotation class RepositoryTestKt

@Tag("Redis")
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@ContextConfiguration(initializers = [RedisTestContainers.Initializer::class])
@ExtendWith(RedisCleanerEachCallbackExtension::class)
@Import(RedisCleaner::class)
@DataRedisTest
@TestEnvironment
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
@SpringBootTest
@TestEnvironment
annotation class IntegrateTestKt
