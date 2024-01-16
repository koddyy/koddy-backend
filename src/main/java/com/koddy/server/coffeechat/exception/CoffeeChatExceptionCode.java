package com.koddy.server.coffeechat.exception;

import com.koddy.server.global.base.KoddyExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Getter
@RequiredArgsConstructor
public enum CoffeeChatExceptionCode implements KoddyExceptionCode {
    INVALID_MEETING_LINK_PROVIDER(BAD_REQUEST, "COFFEE_CHAT_001", "제공하지 않는 Meeting Link Provider입니다."),
    ANONYMOUS_MEETING_LINK(CONFLICT, "COFFEE_CHAT_002", "존재하지 않거나 자동 삭제가 불가능한 미팅입니다."),
    INVALID_MEETING_STRATEGY(BAD_REQUEST, "COFFEE_CHAT_003", "제공하지 않는 미팅 방식입니다."),
    APPLIED_COFFEE_CHAT_NOT_FOUND(NOT_FOUND, "COFFEE_CHAT_004", "신청 목록에 존재하지 않는 커피챗입니다."),
    PENDING_COFFEE_CHAT_NOT_FOUND(NOT_FOUND, "COFFEE_CHAT_005", "최종 결정 대기 목록에 존재하지 않는 커피챗입니다."),
    CANNOT_APPROVE_STATUS(CONFLICT, "COFFEE_CHAT_005", "수락할 수 없는 상태입니다."),
    CANNOT_REJECT_STATUS(CONFLICT, "COFFEE_CHAT_006", "거절할 수 없는 상태입니다."),
    CANNOT_FINALLY_DECIDE_STATUS(CONFLICT, "COFFEE_CHAT_007", "최종 결정을 할 수 없는 상태입니다."),
    CANNOT_CANCEL_STATUS(CONFLICT, "COFFEE_CHAT_008", "취소할 수 없는 상태입니다."),
    APPLIED_OR_SUGGESTED_COFFEE_CHAT_NOT_FOUND(NOT_FOUND, "COFFEE_CHAT_009", "신청/제안 상태인 커피챗이 존재하지 않습니다."),
    ;

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
