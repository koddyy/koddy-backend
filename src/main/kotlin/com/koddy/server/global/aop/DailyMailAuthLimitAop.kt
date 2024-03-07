package com.koddy.server.global.aop

import com.koddy.server.auth.domain.model.Authenticated
import com.koddy.server.auth.exception.AuthException
import com.koddy.server.auth.exception.AuthExceptionCode.TOO_MANY_MAIL_AUTH_ATTEMPTS
import com.koddy.server.global.utils.redis.RedisOperator
import org.aspectj.lang.JoinPoint
import org.aspectj.lang.annotation.Aspect
import org.aspectj.lang.annotation.Before
import org.aspectj.lang.reflect.MethodSignature
import org.springframework.stereotype.Component

@Aspect
@Component
class DailyMailAuthLimitAop(
    private val redisOperator: RedisOperator<String, String>,
) {
    @Before("@annotation(com.koddy.server.global.aop.DailyMailAuthLimit) && args(authenticated, ..)")
    fun applyDailyAuthLimit(
        joinPoint: JoinPoint,
        authenticated: Authenticated,
    ) {
        val dailyMailAuthLimit: DailyMailAuthLimit = getDailyMailAuthLimitAnnotation(joinPoint)

        checkUserAlreadyBanned(authenticated)
        applyBanIfExceededDailyLimit(authenticated, dailyMailAuthLimit)
    }

    private fun getDailyMailAuthLimitAnnotation(joinPoint: JoinPoint): DailyMailAuthLimit {
        val signature = joinPoint.signature as MethodSignature
        val method = signature.method
        return method.getAnnotation(DailyMailAuthLimit::class.java)
    }

    private fun checkUserAlreadyBanned(authenticated: Authenticated) {
        val authBanKey: String = createKey(
            prefix = AUTH_BAN_KEY,
            suffix = authenticated.id.toString(),
        )
        val authBanValue: String? = redisOperator.get(authBanKey)

        if (!authBanValue.isNullOrBlank() && AUTH_BAN_VALUE == authBanValue) {
            throw AuthException(TOO_MANY_MAIL_AUTH_ATTEMPTS)
        }
    }

    private fun applyBanIfExceededDailyLimit(
        authenticated: Authenticated,
        dailyMailAuthLimit: DailyMailAuthLimit,
    ) {
        val dailyTryCount: Long = applyDailyTryCount(authenticated, dailyMailAuthLimit)
        if (dailyTryCount > dailyMailAuthLimit.maxTry) {
            applyUserBan(dailyMailAuthLimit, authenticated.id)
            deleteUserAuthTryCount(authenticated.id)
            throw AuthException(TOO_MANY_MAIL_AUTH_ATTEMPTS)
        }
    }

    private fun applyDailyTryCount(
        authenticated: Authenticated,
        dailyMailAuthLimit: DailyMailAuthLimit,
    ): Long {
        return redisOperator.incr(
            createKey(
                prefix = AUTH_TRY_COUNT_KEY,
                suffix = authenticated.id.toString(),
            ),
            dailyMailAuthLimit.banTime,
            dailyMailAuthLimit.banTimeUnit,
        )
    }

    private fun applyUserBan(
        dailyMailAuthLimit: DailyMailAuthLimit,
        id: Long,
    ) {
        redisOperator.save(
            createKey(
                prefix = AUTH_BAN_KEY,
                suffix = id.toString(),
            ),
            AUTH_BAN_VALUE,
            dailyMailAuthLimit.banTime,
            dailyMailAuthLimit.banTimeUnit,
        )
    }

    private fun deleteUserAuthTryCount(id: Long) {
        redisOperator.delete(
            createKey(
                prefix = AUTH_TRY_COUNT_KEY,
                suffix = id.toString(),
            ),
        )
    }

    private fun createKey(
        prefix: String,
        suffix: String,
    ): String = String.format(prefix, suffix)

    companion object {
        private const val AUTH_TRY_COUNT_KEY = "MAIL-AUTH-TRY:%s"
        private const val AUTH_BAN_KEY = "MAIL-AUTH-BAN:%s"
        private const val AUTH_BAN_VALUE = "ban"
    }
}
