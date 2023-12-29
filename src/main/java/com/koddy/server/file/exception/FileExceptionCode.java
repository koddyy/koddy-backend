package com.koddy.server.file.exception;

import com.koddy.server.global.base.KoddyExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Getter
@RequiredArgsConstructor
public enum FileExceptionCode implements KoddyExceptionCode {
    INVALID_FILE_EXTENSION(BAD_REQUEST, "UPLOAD_001", "파일 확장자는 [JPG, JPEG, PNG]만 가능합니다"),
    ;

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
