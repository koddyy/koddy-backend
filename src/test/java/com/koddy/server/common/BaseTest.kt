package com.koddy.server.common

import com.koddy.server.common.containers.MySqlTestContainersExtension
import com.koddy.server.common.containers.RedisTestContainersExtension
import com.koddy.server.common.containers.callback.DatabaseCleanerEachCallbackExtension
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
@ExtendWith(MySqlTestContainersExtension::class)
@Import(QueryDslConfig::class, P6SpyConfig::class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
annotation class RepositoryTestKt

@Tag("Redis")
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@DataRedisTest
@ExtendWith(RedisTestContainersExtension::class)
annotation class RedisTestKt

@Tag("Integrate")
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
@SpringBootTest
@ExtendWith(
    DatabaseCleanerEachCallbackExtension::class,
    MySqlTestContainersExtension::class,
    RedisTestContainersExtension::class
)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
annotation class IntegrateTestKt
