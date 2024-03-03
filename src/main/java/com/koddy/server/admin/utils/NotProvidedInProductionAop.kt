package com.koddy.server.admin.utils

import com.koddy.server.auth.exception.AuthException
import com.koddy.server.auth.exception.AuthExceptionCode.INVALID_PERMISSION
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.springframework.core.env.Environment
import org.springframework.stereotype.Component

@Aspect
@Component
class NotProvidedInProductionAop(
    private val environment: Environment,
) {
    @Before("@annotation(com.koddy.server.admin.utils.NotProvidedInProduction)")
    fun checkProfile() {
        if ("prod" in environment.activeProfiles) {
            throw AuthException(INVALID_PERMISSION)
        }
    }
}
