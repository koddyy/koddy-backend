package com.koddy.server.global.aop

import com.koddy.server.auth.domain.model.Authenticated
import com.koddy.server.auth.exception.AuthException
import com.koddy.server.auth.exception.AuthExceptionCode
import com.koddy.server.member.domain.model.Role
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.stereotype.Component

@Aspect
@Component
class AccessControlAop {
    @Before("@annotation(com.koddy.server.global.aop.AccessControl) && args(authenticated, ..)")
    fun checkAccess(
        joinPoint: JoinPoint,
        authenticated: Authenticated,
    ) {
        val accessControl: AccessControl = getAccessControlAnnotation(joinPoint)
        when (accessControl.role) {
            Role.MENTOR -> if (authenticated.isMentor.not()) throw AuthException(AuthExceptionCode.INVALID_PERMISSION)
            Role.MENTEE -> if (authenticated.isMentee.not()) throw AuthException(AuthExceptionCode.INVALID_PERMISSION)
            Role.ADMIN -> {}
        }
    }

    private fun getAccessControlAnnotation(joinPoint: JoinPoint): AccessControl {
        val signature = joinPoint.signature as MethodSignature
        val method = signature.method
        return method.getAnnotation(AccessControl::class.java)
    }
}
