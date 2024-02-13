package com.koddy.server.global.config.etc;

import com.koddy.server.auth.domain.model.AuthToken;
import com.koddy.server.global.annotation.Auth;
import com.koddy.server.global.annotation.ExtractToken;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springdoc.core.utils.SpringDocUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.util.List;

@Configuration
public class SwaggerConfig {
    static {
        SpringDocUtils
                .getConfig()
                .addAnnotationsToIgnore(
                        Auth.class,
                        ExtractToken.class
                );
    }

    @Value("${springdoc.server.url}")
    private String url;

    @Bean
    @Profile("!prod")
    public OpenAPI openAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("Koddy API 명세서")
                        .description("Koddy API 명세서")
                        .contact(new Contact()
                                .name("서지원")
                                .email("sjiwon4491@gmail.com")
                                .url("https://github.com/koddyy/koddy-backend"))
                        .version("v1"))
                .addSecurityItem(new SecurityRequirement()
                        .addList(AuthToken.ACCESS_TOKEN_HEADER)
                        .addList(AuthToken.REFRESH_TOKEN_HEADER))
                .components(new Components()
                        .addSecuritySchemes(AuthToken.ACCESS_TOKEN_HEADER, createAccessTokenSecurityScheme())
                        .addSecuritySchemes(AuthToken.REFRESH_TOKEN_HEADER, createRefreshTokenSecurityScheme()))
                .servers(List.of(
                        new Server().url(url)
                ));
    }

    private SecurityScheme createAccessTokenSecurityScheme() {
        return new SecurityScheme()
                .name(AuthToken.ACCESS_TOKEN_HEADER)
                .type(SecurityScheme.Type.HTTP)
                .scheme(AuthToken.TOKEN_TYPE)
                .bearerFormat("JWT")
                .in(SecurityScheme.In.HEADER);
    }

    private SecurityScheme createRefreshTokenSecurityScheme() {
        return new SecurityScheme()
                .name(AuthToken.REFRESH_TOKEN_HEADER)
                .type(SecurityScheme.Type.APIKEY)
                .in(SecurityScheme.In.HEADER);
    }
}
