package com.koddy.server.global.exception.alert

import com.koddy.server.global.log.RequestMetadataExtractor.getClientIP
import com.koddy.server.global.log.RequestMetadataExtractor.getRequestUriWithQueryString
import com.koddy.server.global.log.logger
import com.slack.api.Slack
import com.slack.api.model.Attachment
import com.slack.api.model.Field
import com.slack.api.webhook.WebhookPayloads.payload
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.Logger
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.io.IOException
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Component
class SlackAlertManager(
    @Value("\${slack.webhook.url}") private val slackWebhookUrl: String,
) {
    private val log: Logger = logger()

    fun sendErrorLog(
        request: HttpServletRequest,
        exception: Exception,
    ) {
        try {
            slackClient.send(
                slackWebhookUrl,
                payload {
                    it.text("서버 에러 발생!!")
                        .attachments(
                            listOf(generateSlackErrorAttachments(request, exception)),
                        )
                },
            )
        } catch (ex: IOException) {
            log.error("Slack API 통신 간 에러 발생", ex)
        }
    }

    private fun generateSlackErrorAttachments(
        request: HttpServletRequest,
        exception: Exception,
    ): Attachment {
        val requestTime: String = DateTimeFormatter.ofPattern(TIME_FORMAT).format(LocalDateTime.now())
        return Attachment.builder()
            .color(LOG_COLOR)
            .title("$requestTime 발생 에러 로그")
            .fields(
                listOf(
                    generateSlackField(
                        title = REQUEST_IP,
                        value = getClientIP(request),
                    ),
                    generateSlackField(
                        title = REQUEST_URL,
                        value = "[${request.method}] ${getRequestUriWithQueryString(request)}",
                    ),
                    generateSlackField(
                        title = ERROR_MESSAGE,
                        value = exception.toString(),
                    ),
                ),
            )
            .build()
    }

    private fun generateSlackField(
        title: String,
        value: String,
    ): Field {
        return Field.builder()
            .title(title)
            .value(value)
            .valueShortEnough(false)
            .build()
    }

    companion object {
        private const val TIME_FORMAT: String = "yyyy-MM-dd HH:mm:ss.SSS"
        private const val LOG_COLOR: String = "FF0000"
        private const val REQUEST_IP: String = "[Request IP]"
        private const val REQUEST_URL: String = "[Request URL]"
        private const val ERROR_MESSAGE: String = "[Error Message]"
        private val slackClient: Slack = Slack.getInstance()
    }
}
