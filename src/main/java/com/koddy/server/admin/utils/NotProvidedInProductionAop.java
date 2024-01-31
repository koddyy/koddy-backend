package com.koddy.server.admin.utils;

import com.koddy.server.auth.exception.AuthException;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.util.Arrays;

import static com.koddy.server.auth.exception.AuthExceptionCode.INVALID_PERMISSION;

@Aspect
@Component
@RequiredArgsConstructor
public class NotProvidedInProductionAop {
    private final Environment environment;

    @Before("@annotation(com.koddy.server.admin.utils.NotProvidedInProduction)")
    public void checkProfile() {
        final String[] activeProfiles = environment.getActiveProfiles();
        if (Arrays.asList(activeProfiles).contains("prod")) {
            throw new AuthException(INVALID_PERMISSION);
        }
    }
}
