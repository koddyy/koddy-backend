package com.koddy.server.global.config.web;

import com.koddy.server.auth.utils.TokenProvider;
import com.koddy.server.global.annotation.AuthArgumentResolver;
import com.koddy.server.global.annotation.ExtractTokenArgumentResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpMethod.DELETE;
import static org.springframework.http.HttpMethod.GET;
import static org.springframework.http.HttpMethod.HEAD;
import static org.springframework.http.HttpMethod.OPTIONS;
import static org.springframework.http.HttpMethod.PATCH;
import static org.springframework.http.HttpMethod.POST;
import static org.springframework.http.HttpMethod.PUT;

@Configuration
@RequiredArgsConstructor
public class AdditionalWebConfig implements WebMvcConfigurer {
    private final CorsProperties corsProperties;
    private final TokenProvider tokenProvider;

    @Override
    public void addCorsMappings(final CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOriginPatterns(corsProperties.allowedOriginPatterns().toArray(String[]::new))
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
                .exposedHeaders(AUTHORIZATION)
                .allowCredentials(true)
                .maxAge(3600);
    }

    @Override
    public void addArgumentResolvers(final List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new AuthArgumentResolver(tokenProvider));
        resolvers.add(new ExtractTokenArgumentResolver(tokenProvider));
    }
}
