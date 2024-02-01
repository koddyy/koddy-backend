package com.koddy.server.member.domain.model;

import com.koddy.server.member.exception.MemberException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

import static com.koddy.server.member.exception.MemberExceptionCode.INVALID_NATIONALITY;

@Getter
@RequiredArgsConstructor
public enum Nationality {
    KOREA("KR", "한국"),
    USA("EN", "미국"),
    JAPAN("CN", "일본"),
    CHINA("JP", "중국"),
    VIETNAM("VN", "베트남"),
    ETC("ETC", "ETC"),
    ;

    private final String code;
    private final String value;

    public static Nationality from(final String code) {
        return Arrays.stream(values())
                .filter(it -> it.code.equalsIgnoreCase(code))
                .findFirst()
                .orElseThrow(() -> new MemberException(INVALID_NATIONALITY));
    }
}
