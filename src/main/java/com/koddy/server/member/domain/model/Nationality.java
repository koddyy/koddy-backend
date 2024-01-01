package com.koddy.server.member.domain.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

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
}
