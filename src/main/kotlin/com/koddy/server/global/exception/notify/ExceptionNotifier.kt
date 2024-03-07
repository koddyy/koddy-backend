package com.koddy.server.global.exception.notify

import jakarta.servlet.http.HttpServletRequest

fun interface ExceptionNotifier {
    fun send(
        request: HttpServletRequest,
        exception: Exception,
    )
}
