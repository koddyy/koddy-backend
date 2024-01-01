package com.koddy.server.member.domain.model.mentor;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Day {
    MON("월요일", "MON"),
    TUE("화요일", "TUE"),
    WED("수요일", "WED"),
    THU("목요일", "THU"),
    FRI("금요일", "FRI"),
    SAT("토요일", "SAT"),
    SUN("일요일", "SUN");

    private final String kor;
    private final String eng;
}
