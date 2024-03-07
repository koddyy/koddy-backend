package com.koddy.server.global.base

import org.springframework.http.HttpStatus

interface BusinessExceptionCode {
    val status: HttpStatus
    val errorCode: String
    val message: String
}
