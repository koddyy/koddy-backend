package com.koddy.server.global.exception.notify

import jakarta.servlet.http.HttpServletRequest

@FunctionalInterface
fun interface ErrorNotifier {
    fun send(
        request: HttpServletRequest,
        exception: Exception,
    )
}
