package com.koddy.server.common.fixture;

import com.koddy.server.member.domain.model.mentor.DayOfWeek;
import com.koddy.server.member.domain.model.mentor.Timeline;

import java.time.LocalTime;
import java.util.List;

import static com.koddy.server.member.domain.model.mentor.DayOfWeek.FRI;
import static com.koddy.server.member.domain.model.mentor.DayOfWeek.MON;
import static com.koddy.server.member.domain.model.mentor.DayOfWeek.SAT;
import static com.koddy.server.member.domain.model.mentor.DayOfWeek.SUN;
import static com.koddy.server.member.domain.model.mentor.DayOfWeek.THU;
import static com.koddy.server.member.domain.model.mentor.DayOfWeek.TUE;
import static com.koddy.server.member.domain.model.mentor.DayOfWeek.WED;

public enum TimelineFixture {
    MON_09_22(MON, LocalTime.of(9, 0), LocalTime.of(22, 0)),
    TUE_09_22(TUE, LocalTime.of(9, 0), LocalTime.of(22, 0)),
    WED_09_22(WED, LocalTime.of(9, 0), LocalTime.of(22, 0)),
    THU_09_22(THU, LocalTime.of(9, 0), LocalTime.of(22, 0)),
    FRI_09_22(FRI, LocalTime.of(9, 0), LocalTime.of(22, 0)),
    SAT_09_22(SAT, LocalTime.of(9, 0), LocalTime.of(22, 0)),
    SUN_09_22(SUN, LocalTime.of(9, 0), LocalTime.of(22, 0)),
    ;

    private final DayOfWeek dayOfWeek;
    private final LocalTime startTime;
    private final LocalTime endTime;

    TimelineFixture(
            final DayOfWeek dayOfWeek,
            final LocalTime startTime,
            final LocalTime endTime
    ) {
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public Timeline toDomain() {
        return Timeline.of(dayOfWeek, startTime, endTime);
    }

    public static List<Timeline> 월_수_금() {
        return List.of(
                MON_09_22.toDomain(),
                WED_09_22.toDomain(),
                FRI_09_22.toDomain()
        );
    }

    public static List<Timeline> 화_목_토() {
        return List.of(
                TUE_09_22.toDomain(),
                THU_09_22.toDomain(),
                SAT_09_22.toDomain()
        );
    }

    public static List<Timeline> 월_화_수_목_금() {
        return List.of(
                MON_09_22.toDomain(),
                TUE_09_22.toDomain(),
                WED_09_22.toDomain(),
                THU_09_22.toDomain(),
                FRI_09_22.toDomain()
        );
    }

    public static List<Timeline> 주말() {
        return List.of(
                SAT_09_22.toDomain(),
                SUN_09_22.toDomain()
        );
    }

    public static List<Timeline> allDays() {
        return List.of(
                MON_09_22.toDomain(),
                TUE_09_22.toDomain(),
                WED_09_22.toDomain(),
                THU_09_22.toDomain(),
                FRI_09_22.toDomain(),
                SAT_09_22.toDomain(),
                SUN_09_22.toDomain()
        );
    }

    public DayOfWeek getDayOfWeek() {
        return dayOfWeek;
    }

    public LocalTime getStartTime() {
        return startTime;
    }

    public LocalTime getEndTime() {
        return endTime;
    }
}
