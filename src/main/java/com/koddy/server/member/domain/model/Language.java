package com.koddy.server.member.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Language {
    KOREAN("한국어", "KR"),
    ENGLISH("영어", "EN"),
    CHINESE("중국어", "CN"),
    JAPANESE("일본어", "JP"),
    VIETNAMESE("베트남어", "VN"),
    ;

    private final String value;
    private final String code;
}
