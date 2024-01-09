package com.koddy.server.global.aop;

import com.koddy.server.auth.domain.model.Authenticated;
import com.koddy.server.auth.exception.AuthException;
import com.koddy.server.member.domain.model.Role;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

import static com.koddy.server.auth.exception.AuthExceptionCode.INVALID_PERMISSION;

@Aspect
@Component
public class OnlyMentorAop {
    @Before("@annotation(com.koddy.server.global.aop.OnlyMentor) && args(authenticated, ..)")
    public void checkStudyHost(final Authenticated authenticated) {
        if (isNotMentor(authenticated)) {
            throw new AuthException(INVALID_PERMISSION);
        }
    }

    private boolean isNotMentor(final Authenticated authenticated) {
        return !authenticated.authorities().contains(Role.Type.MENTOR.getAuthority());
    }
}
