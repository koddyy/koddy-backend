package com.koddy.server.global.config

import com.koddy.server.global.decorator.MdcTaskDecorator
import com.koddy.server.global.log.logger
import org.slf4j.Logger
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
    private val log: Logger = logger()

    override fun getAsyncExecutor(): Executor {
        return ThreadPoolTaskExecutor().apply {
            corePoolSize = 10
            maxPoolSize = 100
            queueCapacity = 30
            setRejectedExecutionHandler(ThreadPoolExecutor.CallerRunsPolicy())
            setAwaitTerminationSeconds(60)
            setTaskDecorator(MdcTaskDecorator())
            setThreadNamePrefix("Asynchronous Thread-")
            initialize()
        }
    }

    @Bean(name = ["emailAsyncExecutor"])
    fun emailAsyncExecutor(): Executor {
        return ThreadPoolTaskExecutor().apply {
            corePoolSize = 5
            maxPoolSize = 50
            queueCapacity = 30
            setRejectedExecutionHandler(ThreadPoolExecutor.CallerRunsPolicy())
            setAwaitTerminationSeconds(60)
            setTaskDecorator(MdcTaskDecorator())
            setThreadNamePrefix("Asynchronous Mail Sender Thread-")
            initialize()
        }
    }

    @Bean(name = ["eventAsyncExecutor"])
    fun eventAsyncExecutor(): Executor {
        return ThreadPoolTaskExecutor().apply {
            corePoolSize = 10
            maxPoolSize = 100
            queueCapacity = 30
            setRejectedExecutionHandler(ThreadPoolExecutor.CallerRunsPolicy())
            setAwaitTerminationSeconds(60)
            setTaskDecorator(MdcTaskDecorator())
            setThreadNamePrefix("Asynchronous Event Publish Thread-")
            initialize()
        }
    }

    override fun getAsyncUncaughtExceptionHandler(): AsyncUncaughtExceptionHandler {
        return AsyncUncaughtExceptionHandler { ex: Throwable, method: Method, params: Array<Any?> ->
            log.error("Asynchronous method thrown exception... -> Method = {}, Params = {}", method, params, ex)
        }
    }
}
