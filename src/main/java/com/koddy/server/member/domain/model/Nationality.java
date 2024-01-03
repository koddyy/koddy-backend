package com.koddy.server.member.domain.model;

import com.koddy.server.member.exception.MemberException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

import static com.koddy.server.member.exception.MemberExceptionCode.INVALID_NATIONALITY;

@Getter
@RequiredArgsConstructor
public enum Nationality {
    KOREA("한국", "Korea"),
    USA("미국", "USA"),
    JAPAN("일본", "Japen"),
    CHINA("중국", "China"),
    VIETNAM("베트남", "Vietnam"),
    OTHERS("Others", "Others"),
    ;

    private final String kor;
    private final String eng;

    public static Nationality from(final String kor) {
        return Arrays.stream(values())
                .filter(it -> it.kor.equals(kor))
                .findFirst()
                .orElseThrow(() -> new MemberException(INVALID_NATIONALITY));
    }
}
