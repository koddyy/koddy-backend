package com.koddy.server.member.domain.model.mentor;

import com.koddy.server.member.exception.MemberException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

import static com.koddy.server.member.exception.MemberExceptionCode.SCHEDULE_PERIOD_TIME_MUST_ALIGN;
import static com.koddy.server.member.exception.MemberExceptionCode.SCHEDULE_PERIOD_TIME_MUST_EXISTS;
import static jakarta.persistence.EnumType.STRING;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Embeddable
public class Timeline {
    @Enumerated(STRING)
    @Column(name = "day_of_week", nullable = false, columnDefinition = "VARCHAR(20)")
    private DayOfWeek dayOfWeek;

    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    private Timeline(final DayOfWeek dayOfWeek, final LocalTime startTime, final LocalTime endTime) {
        this.dayOfWeek = dayOfWeek;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public static Timeline of(final DayOfWeek dayOfWeek, final LocalTime startTime, final LocalTime endTime) {
        validateTimeExists(startTime, endTime);
        validateStartIsBeforeEnd(startTime, endTime);
        return new Timeline(dayOfWeek, startTime, endTime);
    }

    private static void validateTimeExists(final LocalTime startTime, final LocalTime endTime) {
        if (startTime == null || endTime == null) {
            throw new MemberException(SCHEDULE_PERIOD_TIME_MUST_EXISTS);
        }
    }

    private static void validateStartIsBeforeEnd(final LocalTime startTime, final LocalTime endTime) {
        if (startTime.isAfter(endTime)) {
            throw new MemberException(SCHEDULE_PERIOD_TIME_MUST_ALIGN);
        }
    }

    public boolean isTimeIncluded(final LocalTime time) {
        return time.isAfter(startTime) && time.isBefore(endTime);
    }
}
