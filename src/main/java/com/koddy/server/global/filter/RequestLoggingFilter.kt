package com.koddy.server.global.filter

import com.koddy.server.global.filter.MdcKey.REQUEST_ID
import com.koddy.server.global.filter.MdcKey.REQUEST_IP
import com.koddy.server.global.filter.MdcKey.REQUEST_METHOD
import com.koddy.server.global.filter.MdcKey.REQUEST_PARAMS
import com.koddy.server.global.filter.MdcKey.REQUEST_TIME
import com.koddy.server.global.filter.MdcKey.REQUEST_URI
import com.koddy.server.global.log.LoggingStatusManager
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import okhttp3.internal.format
import org.slf4j.MDC
import org.springframework.util.PatternMatchUtils
import org.springframework.util.StopWatch
import org.springframework.web.cors.CorsUtils
import org.springframework.web.util.ContentCachingResponseWrapper
import java.nio.charset.StandardCharsets

class RequestLoggingFilter(
    private val loggingStatusManager: LoggingStatusManager,
    vararg ignoredUrls: String,
) : Filter {
    private val logger: KLogger = KotlinLogging.logger {}

    private val ignoredUrls: MutableSet<String> = HashSet()

    init {
        this.ignoredUrls.addAll(listOf(*ignoredUrls))
    }

    override fun doFilter(
        request: ServletRequest,
        response: ServletResponse,
        chain: FilterChain,
    ) {
        val httpRequest = request as HttpServletRequest
        val httpResponse = response as HttpServletResponse

        if (CorsUtils.isPreFlightRequest(httpRequest) || isIgnoredUrl(httpRequest)) {
            chain.doFilter(request, response)
            return
        }

        val stopWatch = StopWatch()
        try {
            stopWatch.start()

            loggingStatusManager.syncStatus()
            loggingRequestInfo(httpRequest)

            chain.doFilter(request, response)
        } finally {
            stopWatch.stop()

            loggingResponseInfo(httpResponse, stopWatch)
            loggingStatusManager.clearResource()
        }
    }

    private fun isIgnoredUrl(request: HttpServletRequest): Boolean {
        return PatternMatchUtils.simpleMatch(ignoredUrls.toTypedArray(), request.requestURI)
    }

    private fun loggingRequestInfo(httpRequest: HttpServletRequest) {
        logger.info {
            format(
                "[Request START] = [Task ID = %s, IP = %s, HTTP Method = %s, Uri = %s, Params = %s, 요청 시작 시간 = %s]",
                MDC.get(REQUEST_ID.name),
                MDC.get(REQUEST_IP.name),
                MDC.get(REQUEST_METHOD.name),
                MDC.get(REQUEST_URI.name),
                MDC.get(REQUEST_PARAMS.name),
                MDC.get(REQUEST_TIME.name)
            )
        }
        logger.info { "Request Body = ${readRequestData(httpRequest)}" }
    }

    private fun readRequestData(request: HttpServletRequest): String {
        if (request is ReadableRequestWrapper) {
            val bodyContents: ByteArray = request.contentAsByteArray

            if (bodyContents.isEmpty()) {
                return EMPTY_RESULT
            }
            return String(bodyContents, StandardCharsets.UTF_8)
        }
        return EMPTY_RESULT
    }

    private fun loggingResponseInfo(
        httpResponse: HttpServletResponse,
        stopWatch: StopWatch,
    ) {
        logger.info { "Response Body = ${readResponseData(httpResponse)}" }
        logger.info {
            format(
                "[Request END] = [Task ID = %s, IP = %s, HTTP Method = %s, Uri = %s, HTTP Status = %s, 요청 처리 시간 = %sms]",
                MDC.get(REQUEST_ID.name),
                MDC.get(REQUEST_IP.name),
                MDC.get(REQUEST_METHOD.name),
                MDC.get(REQUEST_URI.name),
                httpResponse.status,
                stopWatch.totalTimeMillis
            )
        }
    }

    private fun readResponseData(response: HttpServletResponse): String {
        if (response is ContentCachingResponseWrapper) {
            val bodyContents: ByteArray = response.contentAsByteArray

            if (bodyContents.isEmpty()) {
                return EMPTY_RESULT
            }
            return createResponse(bodyContents)
        }
        return EMPTY_RESULT
    }

    private fun createResponse(bodyContents: ByteArray): String {
        val result = String(bodyContents, StandardCharsets.UTF_8)
        if (result.contains("</html>")) {
            return EMPTY_RESULT
        }
        return result
    }

    companion object {
        private const val EMPTY_RESULT = "{ Empty }"
    }
}
