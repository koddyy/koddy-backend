package com.koddy.server.common.fixture;

import com.koddy.server.member.domain.model.mentor.Day;
import com.koddy.server.member.domain.model.mentor.Period;
import com.koddy.server.member.domain.model.mentor.Schedule;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalTime;
import java.util.List;

import static com.koddy.server.member.domain.model.mentor.Day.FRIDAY;
import static com.koddy.server.member.domain.model.mentor.Day.MONDAY;
import static com.koddy.server.member.domain.model.mentor.Day.SATURDAY;
import static com.koddy.server.member.domain.model.mentor.Day.SUNDAY;
import static com.koddy.server.member.domain.model.mentor.Day.THURSDAY;
import static com.koddy.server.member.domain.model.mentor.Day.TUESDAY;
import static com.koddy.server.member.domain.model.mentor.Day.WEDNESDAY;

@Getter
@RequiredArgsConstructor
public enum ScheduleFixture {
    MON_09_17(MONDAY, Period.of(LocalTime.of(9, 0), LocalTime.of(17, 0))),
    TUE_09_17(TUESDAY, Period.of(LocalTime.of(9, 0), LocalTime.of(17, 0))),
    WED_09_17(WEDNESDAY, Period.of(LocalTime.of(9, 0), LocalTime.of(17, 0))),
    THU_09_17(THURSDAY, Period.of(LocalTime.of(9, 0), LocalTime.of(17, 0))),
    FRI_09_17(FRIDAY, Period.of(LocalTime.of(9, 0), LocalTime.of(17, 0))),
    SAT_09_17(SATURDAY, Period.of(LocalTime.of(9, 0), LocalTime.of(17, 0))),
    SUN_09_17(SUNDAY, Period.of(LocalTime.of(9, 0), LocalTime.of(17, 0))),
    ;

    private final Day day;
    private final Period period;

    public Schedule toDomain() {
        return new Schedule(day, period);
    }

    public static List<Schedule> 월_수_금() {
        return List.of(
                MON_09_17.toDomain(),
                WED_09_17.toDomain(),
                FRI_09_17.toDomain()
        );
    }

    public static List<Schedule> 화_목_토() {
        return List.of(
                TUE_09_17.toDomain(),
                THU_09_17.toDomain(),
                SAT_09_17.toDomain()
        );
    }

    public static List<Schedule> 월_화_수_목_금() {
        return List.of(
                MON_09_17.toDomain(),
                TUE_09_17.toDomain(),
                WED_09_17.toDomain(),
                THU_09_17.toDomain(),
                FRI_09_17.toDomain()
        );
    }

    public static List<Schedule> 주말() {
        return List.of(
                SAT_09_17.toDomain(),
                SUN_09_17.toDomain()
        );
    }

    public static List<Schedule> allDays() {
        return List.of(
                MON_09_17.toDomain(),
                TUE_09_17.toDomain(),
                WED_09_17.toDomain(),
                THU_09_17.toDomain(),
                FRI_09_17.toDomain(),
                SAT_09_17.toDomain(),
                SUN_09_17.toDomain()
        );
    }
}
