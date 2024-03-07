package com.koddy.server.global.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.servlet.NoHandlerFoundException

@ControllerAdvice
class AnonymousRequestExceptionHandler {
    @ExceptionHandler(NoHandlerFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNoHandlerFoundException(): String = "anonymous"
}
