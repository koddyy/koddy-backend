package com.koddy.server.global.base;

import org.springframework.http.HttpStatus;

public interface KoddyExceptionCode {
    HttpStatus getStatus();

    String getErrorCode();

    String getMessage();
}
