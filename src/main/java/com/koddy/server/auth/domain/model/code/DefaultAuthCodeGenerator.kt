package com.koddy.server.auth.domain.model.code

import org.springframework.stereotype.Component
import java.util.UUID

@Component
class DefaultAuthCodeGenerator : AuthCodeGenerator {
    override fun get(): String =
        UUID.randomUUID()
            .toString()
            .replace("-".toRegex(), "")
            .substring(0, 8)
}
