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

import static com.koddy.server.auth.exception.AuthExceptionCode.TOO_MANY_MAIL_AUTH_ATTEMPTS;

@Aspect
@Component
@RequiredArgsConstructor
public class DailyMailAuthLimitAop {
    private static final String AUTH_TRY_COUNT_KEY = "MAIL-AUTH-TRY:%s";
    private static final String AUTH_BAN_KEY = "MAIL-AUTH-BAN:%s";
    private static final String AUTH_BAN_VALUE = "ban";

    private final RedisOperator<String, String> redisOperator;

    @Before("@annotation(com.koddy.server.global.aop.DailyMailAuthLimit) && args(authenticated, ..)")
    public void applyDailyAuthLimit(final JoinPoint joinPoint, final Authenticated authenticated) {
        checkUserAlreadyBanned(authenticated);

        final DailyMailAuthLimit dailyMailAuthLimit = getDailyMailAuthLimitAnnotation(joinPoint);
        final long dailyTryCount = applyDailyTryCount(authenticated, dailyMailAuthLimit);
        applyBanIfExceededDailyLimit(authenticated, dailyMailAuthLimit, dailyTryCount);
    }

    private void checkUserAlreadyBanned(final Authenticated authenticated) {
        final String authBanKey = createKey(AUTH_BAN_KEY, authenticated.id());
        final String value = redisOperator.get(authBanKey);

        if (StringUtils.hasText(value) && AUTH_BAN_VALUE.equals(value)) {
            throw new AuthException(TOO_MANY_MAIL_AUTH_ATTEMPTS);
        }
    }

    private DailyMailAuthLimit getDailyMailAuthLimitAnnotation(final JoinPoint joinPoint) {
        final MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        final Method method = signature.getMethod();
        return method.getAnnotation(DailyMailAuthLimit.class);
    }

    private long applyDailyTryCount(final Authenticated authenticated, final DailyMailAuthLimit dailyMailAuthLimit) {
        return redisOperator.incr(
                createKey(AUTH_TRY_COUNT_KEY, authenticated.id()),
                dailyMailAuthLimit.banTime(),
                dailyMailAuthLimit.banTimeUnit()
        );
    }

    private void applyBanIfExceededDailyLimit(
            final Authenticated authenticated,
            final DailyMailAuthLimit dailyMailAuthLimit,
            final long dailyTryCount
    ) {
        if (dailyTryCount > dailyMailAuthLimit.maxTry()) {
            applyUserBan(dailyMailAuthLimit, authenticated.id());
            deleteUserAuthTryCount(authenticated.id());
            throw new AuthException(TOO_MANY_MAIL_AUTH_ATTEMPTS);
        }
    }

    private void applyUserBan(final DailyMailAuthLimit dailyMailAuthLimit, final long id) {
        redisOperator.save(
                createKey(AUTH_BAN_KEY, id),
                AUTH_BAN_VALUE,
                dailyMailAuthLimit.banTime(),
                dailyMailAuthLimit.banTimeUnit()
        );
    }

    private void deleteUserAuthTryCount(final long id) {
        redisOperator.delete(createKey(AUTH_TRY_COUNT_KEY, id));
    }

    private String createKey(final String prefix, final Object suffix) {
        return String.format(prefix, suffix);
    }
}
