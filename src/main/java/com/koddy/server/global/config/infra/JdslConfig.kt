package com.koddy.server.global.config.infra

import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderContext
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class JdslConfig {
    @Bean
    fun jpqlRenderer(): JpqlRenderer {
        return JpqlRenderer()
    }

    @Bean
    fun jpqlRenderContext(): JpqlRenderContext {
        return JpqlRenderContext()
    }
}
