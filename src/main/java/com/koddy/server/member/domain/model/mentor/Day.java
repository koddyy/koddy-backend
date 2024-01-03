package com.koddy.server.member.domain.model.mentor;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;

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

    public static Day of(final int year, final int month, final int day) {
        return switch (LocalDate.of(year, month, day).getDayOfWeek()) {
            case MONDAY -> MON;
            case TUESDAY -> TUE;
            case WEDNESDAY -> WED;
            case THURSDAY -> THU;
            case FRIDAY -> FRI;
            case SATURDAY -> SAT;
            case SUNDAY -> SUN;
        };
    }
}
