package com.koddy.server.global.log

import jakarta.servlet.http.HttpServletRequest

object RequestMetadataExtractor {
    fun getClientIP(request: HttpServletRequest): String {
        var ip: String? = request.getHeader("X-Forwarded-For")

        if (ip.isNullOrEmpty() || "unknown".equals(ip, ignoreCase = true)) {
            ip = request.getHeader("Proxy-Client-IP")
        }
        if (ip.isNullOrEmpty() || "unknown".equals(ip, ignoreCase = true)) {
            ip = request.getHeader("WL-Proxy-Client-IP")
        }
        if (ip.isNullOrEmpty() || "unknown".equals(ip, ignoreCase = true)) {
            ip = request.getHeader("HTTP_CLIENT_IP")
        }
        if (ip.isNullOrEmpty() || "unknown".equals(ip, ignoreCase = true)) {
            ip = request.getHeader("HTTP_X_FORWARDED_FOR")
        }
        if (ip.isNullOrEmpty() || "unknown".equals(ip, ignoreCase = true)) {
            ip = request.remoteAddr
        }

        return ip ?: "..."
    }

    fun getHttpMethod(request: HttpServletRequest): String = request.method

    fun getRequestUriWithQueryString(request: HttpServletRequest): String {
        val requestURI: String = request.requestURI
        val queryString: String? = request.queryString

        if (queryString.isNullOrEmpty()) {
            return requestURI
        }
        return "$requestURI?$queryString"
    }

    fun getSeveralParamsViaParsing(request: HttpServletRequest): String {
        return request.parameterNames
            .toList()
            .joinToString(
                separator = ", ",
                prefix = "[",
                postfix = "]",
            ) { "$it = ${request.getParameter(it)}" }
    }
}
