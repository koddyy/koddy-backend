package com.koddy.server.member.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Language {
    KR("한국어", "KR"),
    EN("영어", "EN"),
    CN("중국어", "CN"),
    JP("일본어", "JP"),
    VN("베트남어", "VN"),
    ;

    private final String value;
    private final String code;
}
