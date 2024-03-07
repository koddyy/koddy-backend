package com.koddy.server.coffeechat.exception

import com.koddy.server.global.base.BusinessExceptionCode
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatus.BAD_REQUEST
import org.springframework.http.HttpStatus.CONFLICT
import org.springframework.http.HttpStatus.NOT_FOUND

enum class CoffeeChatExceptionCode(
    override val status: HttpStatus,
    override val errorCode: String,
    override val message: String,
) : BusinessExceptionCode {
    COFFEE_CHAT_NOT_FOUND(NOT_FOUND, "COFFEE_CHAT_001", "커피챗 정보가 존재하지 않습니다."),
    RESERVATION_MUST_ALIGN(BAD_REQUEST, "COFFEE_CHAT_002", "시작 시간은 종료 시간보다 느릴 수 없습니다."),
    INVALID_MEETING_LINK_PROVIDER(BAD_REQUEST, "COFFEE_CHAT_003", "제공하지 않는 Meeting Link Provider입니다."),
    ANONYMOUS_MEETING_LINK(CONFLICT, "COFFEE_CHAT_004", "존재하지 않거나 자동 삭제가 불가능한 미팅입니다."),
    INVALID_MEETING_STRATEGY(BAD_REQUEST, "COFFEE_CHAT_005", "제공하지 않는 미팅 방식입니다."),
    INVALID_COFFEECHAT_STATUS(BAD_REQUEST, "COFFEE_CHAT_006", "유효하지 않은 커피챗 상태입니다."),
    CANNOT_CANCEL_STATUS(CONFLICT, "COFFEE_CHAT_007", "취소할 수 없는 상태입니다."),
    CANNOT_REJECT_STATUS(CONFLICT, "COFFEE_CHAT_008", "거절할 수 없는 상태입니다."),
    CANNOT_APPROVE_STATUS(CONFLICT, "COFFEE_CHAT_009", "수락할 수 없는 상태입니다."),
    CANNOT_FINALLY_DECIDE_STATUS(CONFLICT, "COFFEE_CHAT_010", "최종 결정을 할 수 없는 상태입니다."),
    CANNOT_COMPLETE_STATUS(CONFLICT, "COFFEE_CHAT_011", "완료할 수 없는 상태입니다."),
}
