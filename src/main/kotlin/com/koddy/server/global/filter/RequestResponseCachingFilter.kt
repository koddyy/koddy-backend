package com.koddy.server.global.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.filter.OncePerRequestFilter
import org.springframework.web.util.ContentCachingResponseWrapper

class RequestResponseCachingFilter : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain,
    ) {
        val requestWrapper = ReadableRequestWrapper(request)
        val responseWrapper = ContentCachingResponseWrapper(response)
        filterChain.doFilter(requestWrapper, responseWrapper)
        responseWrapper.copyBodyToResponse()
    }
}
