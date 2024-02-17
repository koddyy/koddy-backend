package com.koddy.server.global.config.web

import com.koddy.server.global.filter.MdcLoggingFilter
import com.koddy.server.global.filter.RequestLoggingFilter
import com.koddy.server.global.filter.RequestResponseCachingFilter
import com.koddy.server.global.log.LoggingStatusManager
import org.springframework.boot.web.servlet.FilterRegistrationBean
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class WebLogConfig {
    @Bean
    fun firstFilter(): FilterRegistrationBean<MdcLoggingFilter> {
        return FilterRegistrationBean<MdcLoggingFilter>().apply {
            order = 1
            filter = MdcLoggingFilter()
            setName("mdcLoggingFilter")
            addUrlPatterns("/api/*")
        }
    }

    @Bean
    fun secondFilter(): FilterRegistrationBean<RequestResponseCachingFilter> {
        return FilterRegistrationBean<RequestResponseCachingFilter>().apply {
            order = 2
            filter = RequestResponseCachingFilter()
            setName("requestResponseCachingFilter")
            addUrlPatterns("/api/*")
        }
    }

    @Bean
    fun thirdFilter(loggingStatusManager: LoggingStatusManager): FilterRegistrationBean<RequestLoggingFilter> {
        val ignoredUrls: List<String> =
            listOf(
                "/favicon.ico",
                "/error*",
                "/api/swagger*",
                "/api-docs*",
                "/api/actuator*",
                "/api/health",
            )

        return FilterRegistrationBean<RequestLoggingFilter>().apply {
            order = 3
            filter = RequestLoggingFilter(loggingStatusManager, *ignoredUrls.toTypedArray())
            setName("requestLoggingFilter")
            addUrlPatterns("/api/*")
        }
    }
}
