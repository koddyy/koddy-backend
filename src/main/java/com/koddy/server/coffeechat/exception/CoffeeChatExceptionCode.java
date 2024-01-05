package com.koddy.server.coffeechat.exception;

import com.koddy.server.global.base.KoddyExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;

@Getter
@RequiredArgsConstructor
public enum CoffeeChatExceptionCode implements KoddyExceptionCode {
    INVALID_MEETING_LINK_PROVIDER(BAD_REQUEST, "COFFEE_CHAT_001", "제공하지 않는 Meeting Link Provider입니다."),
    ;

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
