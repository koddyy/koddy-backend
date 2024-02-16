package com.koddy.server.global.config.web

import com.koddy.server.auth.domain.model.AuthToken.ACCESS_TOKEN_HEADER
import com.koddy.server.auth.domain.service.TokenProvider
import com.koddy.server.global.annotation.AuthArgumentResolver
import com.koddy.server.global.annotation.ExtractTokenArgumentResolver
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod.DELETE
import org.springframework.http.HttpMethod.GET
import org.springframework.http.HttpMethod.HEAD
import org.springframework.http.HttpMethod.OPTIONS
import org.springframework.http.HttpMethod.PATCH
import org.springframework.http.HttpMethod.POST
import org.springframework.http.HttpMethod.PUT
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.servlet.config.annotation.CorsRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class AdditionalWebConfig(
    private val corsProperties: CorsProperties,
    private val tokenProvider: TokenProvider,
) : WebMvcConfigurer {
    override fun addCorsMappings(registry: CorsRegistry) {
        registry.addMapping("/**")
            .allowedOriginPatterns(*corsProperties.allowedOriginPatterns.toTypedArray())
            .allowedMethods(
                GET.name(),
                POST.name(),
                PUT.name(),
                PATCH.name(),
                DELETE.name(),
                OPTIONS.name(),
                HEAD.name()
            )
            .allowedHeaders("*")
            .exposedHeaders(ACCESS_TOKEN_HEADER)
            .allowCredentials(true)
            .maxAge(3600)
    }

    override fun addArgumentResolvers(resolvers: MutableList<HandlerMethodArgumentResolver>) {
        resolvers.addAll(
            listOf(
                AuthArgumentResolver(tokenProvider),
                ExtractTokenArgumentResolver(tokenProvider)
            )
        )
    }
}
