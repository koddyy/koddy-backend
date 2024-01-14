package com.koddy.server.common.fixture;

import com.koddy.server.member.domain.model.mentor.DayOfWeek;
import com.koddy.server.member.domain.model.mentor.Timeline;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalTime;
import java.util.List;

import static com.koddy.server.member.domain.model.mentor.DayOfWeek.FRI;
import static com.koddy.server.member.domain.model.mentor.DayOfWeek.MON;
import static com.koddy.server.member.domain.model.mentor.DayOfWeek.SAT;
import static com.koddy.server.member.domain.model.mentor.DayOfWeek.SUN;
import static com.koddy.server.member.domain.model.mentor.DayOfWeek.THU;
import static com.koddy.server.member.domain.model.mentor.DayOfWeek.TUE;
import static com.koddy.server.member.domain.model.mentor.DayOfWeek.WED;

@Getter
@RequiredArgsConstructor
public enum TimelineFixture {
    MON_09_17(MON, LocalTime.of(9, 0), LocalTime.of(17, 0)),
    TUE_09_17(TUE, LocalTime.of(9, 0), LocalTime.of(17, 0)),
    WED_09_17(WED, LocalTime.of(9, 0), LocalTime.of(17, 0)),
    THU_09_17(THU, LocalTime.of(9, 0), LocalTime.of(17, 0)),
    FRI_09_17(FRI, LocalTime.of(9, 0), LocalTime.of(17, 0)),
    SAT_09_17(SAT, LocalTime.of(9, 0), LocalTime.of(17, 0)),
    SUN_09_17(SUN, LocalTime.of(9, 0), LocalTime.of(17, 0)),
    ;

    private final DayOfWeek dayOfWeek;
    private final LocalTime startTime;
    private final LocalTime endTime;

    public Timeline toDomain() {
        return Timeline.of(dayOfWeek, startTime, endTime);
    }

    public static List<Timeline> 월_수_금() {
        return List.of(
                MON_09_17.toDomain(),
                WED_09_17.toDomain(),
                FRI_09_17.toDomain()
        );
    }

    public static List<Timeline> 화_목_토() {
        return List.of(
                TUE_09_17.toDomain(),
                THU_09_17.toDomain(),
                SAT_09_17.toDomain()
        );
    }

    public static List<Timeline> 월_화_수_목_금() {
        return List.of(
                MON_09_17.toDomain(),
                TUE_09_17.toDomain(),
                WED_09_17.toDomain(),
                THU_09_17.toDomain(),
                FRI_09_17.toDomain()
        );
    }

    public static List<Timeline> 주말() {
        return List.of(
                SAT_09_17.toDomain(),
                SUN_09_17.toDomain()
        );
    }

    public static List<Timeline> allDays() {
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
