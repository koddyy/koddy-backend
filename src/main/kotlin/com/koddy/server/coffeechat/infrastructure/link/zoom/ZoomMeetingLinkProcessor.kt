package com.koddy.server.coffeechat.infrastructure.link.zoom

import com.koddy.server.auth.infrastructure.social.zoom.ZoomOAuthProperties
import com.koddy.server.coffeechat.application.adapter.MeetingLinkTokenCashier
import com.koddy.server.coffeechat.domain.model.link.MeetingLinkRequest
import com.koddy.server.coffeechat.domain.model.link.MeetingLinkResponse
import com.koddy.server.coffeechat.exception.CoffeeChatException
import com.koddy.server.coffeechat.exception.CoffeeChatExceptionCode.ANONYMOUS_MEETING_LINK
import com.koddy.server.coffeechat.infrastructure.link.MeetingLinkProcessor
import com.koddy.server.coffeechat.infrastructure.link.MeetingLinkProcessor.Companion.BEARER_TOKEN_TYPE
import com.koddy.server.coffeechat.infrastructure.link.MeetingLinkProcessor.Companion.OAUTH_CONTENT_TYPE
import com.koddy.server.coffeechat.infrastructure.link.zoom.spec.ZoomMeetingLinkResponse
import com.koddy.server.global.exception.GlobalException
import com.koddy.server.global.exception.GlobalExceptionCode.UNEXPECTED_SERVER_ERROR
import com.koddy.server.global.log.logger
import org.slf4j.Logger
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpHeaders.AUTHORIZATION
import org.springframework.http.HttpHeaders.CONTENT_TYPE
import org.springframework.http.HttpMethod.DELETE
import org.springframework.http.HttpMethod.POST
import org.springframework.stereotype.Component
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate
import java.time.Duration

@Component
class ZoomMeetingLinkProcessor(
    private val properties: ZoomOAuthProperties,
    private val restTemplate: RestTemplate,
    private val meetingLinkTokenCashier: MeetingLinkTokenCashier,
) : MeetingLinkProcessor {
    private val log: Logger = logger()

    override fun create(
        oAuthAccessToken: String,
        meetingLinkRequest: MeetingLinkRequest,
    ): MeetingLinkResponse {
        val response: ZoomMeetingLinkResponse = fetchMeetingLinkInfo(
            accessToken = oAuthAccessToken,
            meetingLinkRequest = meetingLinkRequest,
        )
        meetingLinkTokenCashier.storeViaMeetingId(
            meetingId = response.id(),
            oAuthAccessToken = oAuthAccessToken,
            duration = Duration.ofMinutes(10),
        )
        return response
    }

    private fun fetchMeetingLinkInfo(
        accessToken: String,
        meetingLinkRequest: MeetingLinkRequest,
    ): ZoomMeetingLinkResponse {
        try {
            return restTemplate.exchange(
                properties.other.createMeetingUrl,
                POST,
                HttpEntity(meetingLinkRequest, createMeetingLinkRequestHeader(accessToken)),
                ZoomMeetingLinkResponse::class.java,
            ).body!!
        } catch (ex: RestClientException) {
            log.error("OAuth Error... ", ex)
            throw GlobalException(UNEXPECTED_SERVER_ERROR)
        } catch (ex: Exception) {
            log.error("Undefined Error... ", ex)
            throw GlobalException(UNEXPECTED_SERVER_ERROR)
        }
    }

    private fun createMeetingLinkRequestHeader(accessToken: String): HttpHeaders {
        val headers = HttpHeaders()
        headers[CONTENT_TYPE] = OAUTH_CONTENT_TYPE
        headers[AUTHORIZATION] = "$BEARER_TOKEN_TYPE $accessToken"
        return headers
    }

    override fun delete(meetingId: String) {
        if (meetingLinkTokenCashier.containsViaMeetingId(meetingId).not()) {
            throw CoffeeChatException(ANONYMOUS_MEETING_LINK)
        }

        val oAuthAccessToken: String = meetingLinkTokenCashier.getViaMeetingId(meetingId)
        deleteMeetingLink(
            oAuthAccessToken = oAuthAccessToken,
            meetingId = meetingId,
        )
    }

    private fun deleteMeetingLink(
        oAuthAccessToken: String,
        meetingId: String,
    ) {
        try {
            restTemplate.exchange(
                properties.other.deleteMeetingUrl,
                DELETE,
                HttpEntity<Any>(createMeetingLinkRequestHeader(oAuthAccessToken)),
                Void::class.java,
                meetingId,
            )
            meetingLinkTokenCashier.deleteViaMeetingId(meetingId)
        } catch (ex: RestClientException) {
            log.error("OAuth Error... ", ex)
            throw GlobalException(UNEXPECTED_SERVER_ERROR)
        } catch (ex: Exception) {
            log.error("Undefined Error... ", ex)
            throw GlobalException(UNEXPECTED_SERVER_ERROR)
        }
    }
}
