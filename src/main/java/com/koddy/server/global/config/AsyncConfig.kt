package com.koddy.server.global.config

import com.koddy.server.global.decorator.MdcTaskDecorator
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.scheduling.annotation.AsyncConfigurer
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor
import java.lang.reflect.Method
import java.util.concurrent.Executor
import java.util.concurrent.ThreadPoolExecutor

@Configuration
@EnableAsync
class AsyncConfig : AsyncConfigurer {
    private val logger: KLogger = KotlinLogging.logger { }

    @Bean(name = ["emailAsyncExecutor"])
    fun emailAsyncExecutor(): Executor {
        return ThreadPoolTaskExecutor().apply {
            corePoolSize = 10
            maxPoolSize = 90
            queueCapacity = 50
            setRejectedExecutionHandler(ThreadPoolExecutor.CallerRunsPolicy())
            setAwaitTerminationSeconds(60)
            setTaskDecorator(MdcTaskDecorator())
            setThreadNamePrefix("Asynchronous Mail Sender Thread-")
        }
    }

    override fun getAsyncUncaughtExceptionHandler(): AsyncUncaughtExceptionHandler? {
        return AsyncUncaughtExceptionHandler { ex: Throwable?, method: Method?, params: Array<Any?>? ->
            logger.error { "Asynchronous method thrown exception... -> Method = $method, Params = $params Trace = $ex" }
        }
    }
}
