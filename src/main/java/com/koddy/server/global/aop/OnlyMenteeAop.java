package com.koddy.server.global.aop;

import com.koddy.server.auth.domain.model.Authenticated;
import com.koddy.server.auth.exception.AuthException;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import static com.koddy.server.auth.exception.AuthExceptionCode.INVALID_PERMISSION;
import static com.koddy.server.member.domain.model.Role.MENTEE;

@Aspect
@Component
public class OnlyMenteeAop {
    @Before("@annotation(com.koddy.server.global.aop.OnlyMentee) && args(authenticated, ..)")
    public void checkStudyHost(final Authenticated authenticated) {
        if (isNotMentee(authenticated)) {
            throw new AuthException(INVALID_PERMISSION);
        }
    }

    private boolean isNotMentee(final Authenticated authenticated) {
        return !MENTEE.getAuthority().equals(authenticated.authority());
    }
}
