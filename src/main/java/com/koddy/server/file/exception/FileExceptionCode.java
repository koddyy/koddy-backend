package com.koddy.server.file.exception;

import com.koddy.server.global.base.KoddyExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.INTERNAL_SERVER_ERROR;

@Getter
@RequiredArgsConstructor
public enum FileExceptionCode implements KoddyExceptionCode {
    INVALID_FILE_EXTENSION(BAD_REQUEST, "UPLOAD_001", "파일 확장자는 [JPG, JPEG, PNG]만 가능합니다"),
    UPLOAD_FAILURE(INTERNAL_SERVER_ERROR, "UPLOAD_002", "서버 내부 오류로 인해 파일 업로드에 실패했습니다."),
    ;

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
