package com.koddy.server.global.config.web;

import com.koddy.server.global.filter.MdcLoggingFilter;
import com.koddy.server.global.filter.RequestLoggingFilter;
import com.koddy.server.global.filter.RequestResponseCachingFilter;
import com.koddy.server.global.log.LoggingStatusManager;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class WebLogConfig {
    @Bean
    public FilterRegistrationBean<MdcLoggingFilter> firstFilter() {
        final FilterRegistrationBean<MdcLoggingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new MdcLoggingFilter());
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(1);
        registrationBean.setName("mdcLoggingFilter");
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<RequestResponseCachingFilter> secondFilter() {
        final FilterRegistrationBean<RequestResponseCachingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new RequestResponseCachingFilter());
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(2);
        registrationBean.setName("requestResponseCachingFilter");
        return registrationBean;
    }

    @Bean
    public FilterRegistrationBean<RequestLoggingFilter> thirdFilter(final LoggingStatusManager loggingStatusManager) {
        final FilterRegistrationBean<RequestLoggingFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new RequestLoggingFilter(
                loggingStatusManager,
                "/favicon.ico",
                "/error*",
                "/api/swagger*",
                "/api-docs*",
                "/actuator*"
        ));
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(3);
        registrationBean.setName("requestLoggingFilter");
        return registrationBean;
    }
}
