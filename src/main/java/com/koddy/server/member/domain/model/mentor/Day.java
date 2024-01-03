package com.koddy.server.member.domain.model.mentor;

import com.koddy.server.member.exception.MemberException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import static com.koddy.server.member.exception.MemberExceptionCode.INVALID_DAY;

@Getter
@RequiredArgsConstructor
public enum Day {
    MON("월", "MON"),
    TUE("화", "TUE"),
    WED("수", "WED"),
    THU("목", "THU"),
    FRI("금", "FRI"),
    SAT("토", "SAT"),
    SUN("일", "SUN");

    private final String kor;
    private final String eng;

    public static Day from(final String kor) {
        return Arrays.stream(values())
                .filter(it -> it.kor.equals(kor))
                .findFirst()
                .orElseThrow(() -> new MemberException(INVALID_DAY));
    }

    public static List<Day> of(final List<String> kors) {
        return kors.stream()
                .map(Day::from)
                .toList();
    }

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
