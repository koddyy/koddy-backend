package com.koddy.server.global.config.etc

import com.koddy.server.auth.domain.model.AuthToken
import com.koddy.server.global.annotation.Auth
import com.koddy.server.global.annotation.ExtractToken
import io.swagger.v3.oas.models.Components
import io.swagger.v3.oas.models.OpenAPI
import io.swagger.v3.oas.models.info.Contact
import io.swagger.v3.oas.models.info.Info
import io.swagger.v3.oas.models.security.SecurityRequirement
import io.swagger.v3.oas.models.security.SecurityScheme
import io.swagger.v3.oas.models.servers.Server
import org.springdoc.core.utils.SpringDocUtils
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
class SwaggerConfig(
    @Value("\${springdoc.server.url}")
    private val url: String,
) {
    @Bean
    @Profile("!prod")
    fun openAPI(): OpenAPI =
        OpenAPI()
            .info(createInfo())
            .addSecurityItem(createSecurityItems())
            .components(createComponents())
            .servers(listOf(Server().url(url)))

    private fun createInfo(): Info =
        Info()
            .title("Koddy API 명세서")
            .description("Koddy API 명세서")
            .contact(
                Contact()
                    .name("서지원")
                    .email("sjiwon4491@gmail.com")
                    .url("https://github.com/koddyy/koddy-backend"),
            )
            .version("v1")

    private fun createSecurityItems(): SecurityRequirement =
        SecurityRequirement()
            .addList(AuthToken.ACCESS_TOKEN_HEADER)
            .addList(AuthToken.REFRESH_TOKEN_HEADER)

    private fun createComponents(): Components =
        Components()
            .addSecuritySchemes(AuthToken.ACCESS_TOKEN_HEADER, createAccessTokenSecurityScheme())
            .addSecuritySchemes(AuthToken.REFRESH_TOKEN_HEADER, createRefreshTokenSecurityScheme())

    private fun createAccessTokenSecurityScheme(): SecurityScheme =
        SecurityScheme()
            .name(AuthToken.ACCESS_TOKEN_HEADER)
            .type(SecurityScheme.Type.HTTP)
            .`in`(SecurityScheme.In.HEADER)
            .scheme(AuthToken.TOKEN_TYPE)
            .bearerFormat("JWT")

    private fun createRefreshTokenSecurityScheme(): SecurityScheme =
        SecurityScheme()
            .name(AuthToken.REFRESH_TOKEN_HEADER)
            .type(SecurityScheme.Type.APIKEY)
            .`in`(SecurityScheme.In.HEADER)

    companion object {
        init {
            SpringDocUtils
                .getConfig()
                .addAnnotationsToIgnore(
                    Auth::class.java,
                    ExtractToken::class.java,
                )
        }
    }
}
