package com.koddy.server.global.aop;

import com.koddy.server.auth.domain.model.Authenticated;
import com.koddy.server.auth.exception.AuthException;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;

import static com.koddy.server.auth.exception.AuthExceptionCode.TOO_MANY_MAIL_AUTH_ATTEMPTS;

@Aspect
@Component
@RequiredArgsConstructor
public class DailyMailAuthLimitAop {
    private static final String AUTH_BAN_KEY = "MAIL-AUTH-BAN:%s";
    private static final String AUTH_TRY_COUNT_KEY = "MAIL-AUTH-TRY:%s";

    private final StringRedisTemplate redisTemplate;

    @Before("@annotation(com.koddy.server.global.aop.DailyMailAuthLimit) && args(authenticated, ..)")
    public void applyDailyAuthLimit(final JoinPoint joinPoint, final Authenticated authenticated) {
        checkUserAlreadyBanned(authenticated.id());

        final DailyMailAuthLimit dailyMailAuthLimit = getDailyAuthLimitAnnotation(joinPoint);
        final long dailyTryCount = applyDailyTryCount(authenticated.id());
        applyBanIfExceededDailyLimit(dailyMailAuthLimit, dailyTryCount, authenticated.id());
    }

    private void checkUserAlreadyBanned(final long id) {
        final String authBanKey = createKey(AUTH_BAN_KEY, id);
        final String value = redisTemplate.opsForValue().get(authBanKey);

        if (StringUtils.hasText(value)) {
            throw new AuthException(TOO_MANY_MAIL_AUTH_ATTEMPTS);
        }
    }

    private DailyMailAuthLimit getDailyAuthLimitAnnotation(final JoinPoint joinPoint) {
        final MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        final Method method = signature.getMethod();
        return method.getAnnotation(DailyMailAuthLimit.class);
    }

    private long applyDailyTryCount(final long id) {
        final String authTryCountKey = createKey(AUTH_TRY_COUNT_KEY, id);
        return redisTemplate.opsForValue().increment(authTryCountKey);
    }

    private void applyBanIfExceededDailyLimit(
            final DailyMailAuthLimit dailyMailAuthLimit,
            final long dailyTryCount,
            final long id
    ) {
        if (dailyTryCount > dailyMailAuthLimit.maxTry()) {
            applyUserBan(dailyMailAuthLimit, id);
            deleteUserAuthTryCount(id);
            throw new AuthException(TOO_MANY_MAIL_AUTH_ATTEMPTS);
        }
    }

    private void applyUserBan(final DailyMailAuthLimit dailyMailAuthLimit, final long id) {
        final String authBanKey = createKey(AUTH_BAN_KEY, id);
        redisTemplate.opsForValue().set(authBanKey, "ban", dailyMailAuthLimit.banTime(), dailyMailAuthLimit.banTimeUnit());
    }

    private void deleteUserAuthTryCount(final long id) {
        final String authTryCountKey = createKey(AUTH_TRY_COUNT_KEY, id);
        redisTemplate.delete(authTryCountKey);
    }

    private String createKey(final String prefix, final Object suffix) {
        return String.format(prefix, suffix);
    }
}
