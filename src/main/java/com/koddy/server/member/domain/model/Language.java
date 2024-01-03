package com.koddy.server.member.domain.model;

import com.koddy.server.member.exception.MemberException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;

import static com.koddy.server.member.exception.MemberExceptionCode.INVALID_LANGUAGE;

@Getter
@RequiredArgsConstructor
public enum Language {
    KR("KR", "한국어"),
    EN("EN", "영어"),
    CN("CN", "중국어"),
    JP("JP", "일본어"),
    VN("VN", "베트남어"),
    ;

    private final String code;
    private final String value;

    public static Language from(final String code) {
        return Arrays.stream(values())
                .filter(it -> it.code.equals(code))
                .findFirst()
                .orElseThrow(() -> new MemberException(INVALID_LANGUAGE));
    }

    public static List<Language> of(final List<String> codes) {
        return codes.stream()
                .map(Language::from)
                .toList();
    }
}
