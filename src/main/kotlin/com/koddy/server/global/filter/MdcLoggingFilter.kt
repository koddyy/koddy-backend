package com.koddy.server.global.filter

import com.koddy.server.global.filter.MdcKey.REQUEST_ID
import com.koddy.server.global.filter.MdcKey.REQUEST_IP
import com.koddy.server.global.filter.MdcKey.REQUEST_METHOD
import com.koddy.server.global.filter.MdcKey.REQUEST_PARAMS
import com.koddy.server.global.filter.MdcKey.REQUEST_TIME
import com.koddy.server.global.filter.MdcKey.REQUEST_URI
import com.koddy.server.global.filter.MdcKey.START_TIME_MILLIS
import com.koddy.server.global.log.RequestMetadataExtractor.getClientIP
import com.koddy.server.global.log.RequestMetadataExtractor.getHttpMethod
import com.koddy.server.global.log.RequestMetadataExtractor.getRequestUriWithQueryString
import com.koddy.server.global.log.RequestMetadataExtractor.getSeveralParamsViaParsing
import jakarta.servlet.Filter
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.MDC
import java.time.LocalDateTime
import java.util.UUID

class MdcLoggingFilter : Filter {
    override fun doFilter(
        request: ServletRequest,
        response: ServletResponse,
        chain: FilterChain,
    ) {
        setMdc(request as HttpServletRequest)
        chain.doFilter(request, response)
        MDC.clear()
    }

    private fun setMdc(request: HttpServletRequest) {
        MDC.put(REQUEST_ID.name, UUID.randomUUID().toString())
        MDC.put(REQUEST_IP.name, getClientIP(request))
        MDC.put(REQUEST_METHOD.name, getHttpMethod(request))
        MDC.put(REQUEST_URI.name, getRequestUriWithQueryString(request))
        MDC.put(REQUEST_PARAMS.name, getSeveralParamsViaParsing(request))
        MDC.put(REQUEST_TIME.name, LocalDateTime.now().toString())
        MDC.put(START_TIME_MILLIS.name, System.currentTimeMillis().toString())
    }
}
