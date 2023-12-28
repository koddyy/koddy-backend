package com.koddy.server.member.domain.model.mentor;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Day {
    MONDAY("월요일", "MON"),
    TUESDAY("화요일", "TUE"),
    WEDNESDAY("수요일", "WED"),
    THURSDAY("목요일", "THU"),
    FRIDAY("금요일", "FRI"),
    SATURDAY("토요일", "SAT"),
    SUNDAY("일요일", "SUN");

    private final String kor;
    private final String eng;
}
