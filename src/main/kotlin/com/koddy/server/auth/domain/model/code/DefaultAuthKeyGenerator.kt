package com.koddy.server.auth.domain.model.code

import okhttp3.internal.format
import org.springframework.stereotype.Component

@Component
class DefaultAuthKeyGenerator : AuthKeyGenerator {
    override fun get(
        prefix: String,
        vararg suffix: Any,
    ): String = format(prefix, *suffix)
}
