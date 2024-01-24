package com.koddy.server.global.aop;

import com.koddy.server.auth.domain.model.Authenticated;
import com.koddy.server.auth.exception.AuthException;
import com.koddy.server.global.utils.redis.RedisOperator;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.lang.reflect.Method;
import java.time.Duration;

import static com.koddy.server.auth.exception.AuthExceptionCode.TOO_MANY_MAIL_AUTH_ATTEMPTS;

@Aspect
@Component
@RequiredArgsConstructor
public class DailyMailAuthLimitAop {
    private static final String AUTH_BAN_KEY = "MAIL-AUTH-BAN:%s";
    private static final String AUTH_TRY_COUNT_KEY = "MAIL-AUTH-TRY:%s";

    private final RedisOperator<String, String> redisOperator;

    @Before("@annotation(com.koddy.server.global.aop.DailyMailAuthLimit) && args(authenticated, ..)")
    public void applyDailyAuthLimit(final JoinPoint joinPoint, final Authenticated authenticated) {
        checkUserAlreadyBanned(authenticated.id());

        final DailyMailAuthLimit dailyMailAuthLimit = getDailyMailAuthLimitAnnotation(joinPoint);
        final long dailyTryCount = applyDailyTryCount(authenticated.id());
        applyBanIfExceededDailyLimit(dailyMailAuthLimit, dailyTryCount, authenticated.id());
    }

    private void checkUserAlreadyBanned(final long id) {
        final String authBanKey = createKey(AUTH_BAN_KEY, id);
        final String value = redisOperator.get(authBanKey);

        if (StringUtils.hasText(value)) {
            throw new AuthException(TOO_MANY_MAIL_AUTH_ATTEMPTS);
        }
    }

    private DailyMailAuthLimit getDailyMailAuthLimitAnnotation(final JoinPoint joinPoint) {
        final MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        final Method method = signature.getMethod();
        return method.getAnnotation(DailyMailAuthLimit.class);
    }

    private long applyDailyTryCount(final long id) {
        final String authTryCountKey = createKey(AUTH_TRY_COUNT_KEY, id);
        final String authTryCount = redisOperator.get(authTryCountKey);

        if (authTryCount == null) {
            redisOperator.save(authTryCountKey, String.valueOf(1), Duration.ofMinutes(10));
            return 1;
        }

        final int currentTryCount = Integer.parseInt(authTryCount) + 1;
        redisOperator.save(authTryCountKey, String.valueOf(currentTryCount), Duration.ofMinutes(10));
        return currentTryCount;
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
        redisOperator.save(authBanKey, "ban", dailyMailAuthLimit.banTime(), dailyMailAuthLimit.banTimeUnit());
    }

    private void deleteUserAuthTryCount(final long id) {
        final String authTryCountKey = createKey(AUTH_TRY_COUNT_KEY, id);
        redisOperator.delete(authTryCountKey);
    }

    private String createKey(final String prefix, final Object suffix) {
        return String.format(prefix, suffix);
    }
}
