package com.koddy.server.member.exception;

import com.koddy.server.global.base.KoddyExceptionCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.CONFLICT;
import static org.springframework.http.HttpStatus.NOT_FOUND;

@Getter
@RequiredArgsConstructor
public enum MemberExceptionCode implements KoddyExceptionCode {
    INVALID_EMAIL_PATTERN(BAD_REQUEST, "MEMBER_001", "이메일 형식에 맞지 않습니다."),
    AVAILABLE_LANGUAGE_MUST_EXISTS(BAD_REQUEST, "MEMBER_002", "사용 가능한 언어는 하나 이상 존재해야 합니다."),
    MAIN_LANGUAGE_MUST_BE_ONLY_ONE(BAD_REQUEST, "MEMBER_003", "사용 가능한 메인 언어는 하나만 선택해주세요."),
    SCHEDULE_PERIOD_TIME_MUST_EXISTS(BAD_REQUEST, "MEMBER_004", "스케줄 정보를 빠짐없이 선택해주세요."),
    SCHEDULE_PERIOD_TIME_MUST_ALIGN(BAD_REQUEST, "MEMBER_005", "시작이 종료 이후가 될 수 없습니다."),
    MEMBER_NOT_FOUND(NOT_FOUND, "MEMBER_006", "사용자 정보가 존재하지 않습니다."),
    MENTOR_NOT_FOUND(NOT_FOUND, "MEMBER_007", "멘토 정보가 존재하지 않습니다."),
    MENTEE_NOT_FOUND(NOT_FOUND, "MEMBER_008", "멘티 정보가 존재하지 않습니다."),
    INVALID_NATIONALITY(BAD_REQUEST, "MEMBER_009", "유효하지 않은 국적입니다."),
    INVALID_LANGUAGE_CATEGORY(BAD_REQUEST, "MEMBER_010", "유효하지 않은 언어 종류입니다."),
    INVALID_LANGUAGE_TYPE(BAD_REQUEST, "MEMBER_011", "유효하지 않은 언어 레벨입니다."),
    INVALID_DAY(BAD_REQUEST, "MEMBER_012", "유효하지 않은 날짜입니다."),
    INVALID_TIME_UNIT(BAD_REQUEST, "MEMBER_013", "설정할 수 없는 멘토링 시간 단위입니다."),
    CANNOT_RESERVATION(CONFLICT, "MEMBER_014", "이미 예약되었거나 멘토링이 가능하지 않은 날짜입니다."),
    ;

    private final HttpStatus status;
    private final String errorCode;
    private final String message;
}
