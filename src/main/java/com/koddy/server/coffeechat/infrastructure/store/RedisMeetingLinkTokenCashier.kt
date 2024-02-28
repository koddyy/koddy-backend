package com.koddy.server.coffeechat.infrastructure.store

import com.koddy.server.coffeechat.application.adapter.MeetingLinkTokenCashier
import com.koddy.server.global.utils.redis.RedisOperator
import okhttp3.internal.format
import org.springframework.stereotype.Component
import java.time.Duration

@Component
class RedisMeetingLinkTokenCashier(
    private val redisOperator: RedisOperator<String, String>,
) : MeetingLinkTokenCashier {
    override fun storeViaPlatformId(
        platformId: Long,
        oAuthAccessToken: String,
        duration: Duration,
    ) = redisOperator.save(createKeyViaPlatformId(platformId), oAuthAccessToken, duration)

    override fun getViaPlatformId(platformId: Long): String {
        return redisOperator.get(createKeyViaPlatformId(platformId)) ?: throw RuntimeException()
    }

    override fun containsViaPlatformId(platformId: Long): Boolean {
        return redisOperator.contains(createKeyViaPlatformId(platformId))
    }

    private fun createKeyViaPlatformId(platformId: Long): String =
        format(USER_OAUTH_TOKEN_FROM_PLATFORM_ID, platformId)

    override fun storeViaMeetingId(
        meetingId: String,
        oAuthAccessToken: String,
        duration: Duration,
    ) = redisOperator.save(createKeyViaMeetingId(meetingId), oAuthAccessToken, duration)

    override fun getViaMeetingId(meetingId: String): String {
        return redisOperator.get(createKeyViaMeetingId(meetingId)) ?: throw RuntimeException()
    }

    override fun containsViaMeetingId(meetingId: String): Boolean {
        return redisOperator.contains(createKeyViaMeetingId(meetingId))
    }

    override fun deleteViaMeetingId(meetingId: String) {
        redisOperator.delete(createKeyViaMeetingId(meetingId))
    }

    private fun createKeyViaMeetingId(meetingId: String): String =
        format(USER_OAUTH_TOKEN_FROM_MEETING_ID, meetingId)

    companion object {
        /**
         * Key = User Platform ID <br></br>
         * Value = User OAuth AccessToken
         */
        private const val USER_OAUTH_TOKEN_FROM_PLATFORM_ID = "USER_OAUTH_TOKEN:PLATFORM:%s"

        /**
         * Key = User Created Meeting ID <br></br>
         * Value = User OAuth AccessToken
         */
        private const val USER_OAUTH_TOKEN_FROM_MEETING_ID = "USER_OAUTH_TOKEN:CREATE_MEETING:%s"
    }
}
