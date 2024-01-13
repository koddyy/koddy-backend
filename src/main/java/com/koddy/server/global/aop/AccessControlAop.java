package com.koddy.server.global.aop;

import com.koddy.server.auth.domain.model.Authenticated;
import com.koddy.server.auth.exception.AuthException;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

import static com.koddy.server.auth.exception.AuthExceptionCode.INVALID_PERMISSION;
import static com.koddy.server.member.domain.model.Role.MENTEE;
import static com.koddy.server.member.domain.model.Role.MENTOR;

@Aspect
@Component
public class AccessControlAop {
    @Before("@annotation(com.koddy.server.global.aop.AccessControl) && args(authenticated, ..)")
    public void checkAccess(final JoinPoint joinPoint, final Authenticated authenticated) {
        final AccessControl accessControl = getAccessControlAnnotation(joinPoint);

        if (accessControl.role() == MENTOR) {
            verifyMentorRole(authenticated);
        }

        if (accessControl.role() == MENTEE) {
            verifyMenteeRole(authenticated);
        }
    }

    private AccessControl getAccessControlAnnotation(final JoinPoint joinPoint) {
        final MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        final Method method = signature.getMethod();
        return method.getAnnotation(AccessControl.class);
    }

    private void verifyMentorRole(final Authenticated authenticated) {
        if (!authenticated.isMentor()) {
            throw new AuthException(INVALID_PERMISSION);
        }
    }

    private void verifyMenteeRole(final Authenticated authenticated) {
        if (!authenticated.isMentee()) {
            throw new AuthException(INVALID_PERMISSION);
        }
    }
}
