package com.koddy.server.member.domain.model.mentor;

import com.koddy.server.member.exception.MemberException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

import static com.koddy.server.member.exception.MemberExceptionCode.PERIOD_MUST_EXISTS;
import static com.koddy.server.member.exception.MemberExceptionCode.START_END_MUST_BE_ALIGN;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Embeddable
public class Period {
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    private Period(final LocalTime startTime, final LocalTime endTime) {
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public static Period of(final LocalTime startTime, final LocalTime endTime) {
        validateTimeExists(startTime, endTime);
        validateStartIsBeforeEnd(startTime, endTime);
        return new Period(startTime, endTime);
    }

    private static void validateTimeExists(final LocalTime startTime, final LocalTime endTime) {
        if (startTime == null || endTime == null) {
            throw new MemberException(PERIOD_MUST_EXISTS);
        }
    }

    private static void validateStartIsBeforeEnd(final LocalTime startTime, final LocalTime endTime) {
        if (startTime.isAfter(endTime)) {
            throw new MemberException(START_END_MUST_BE_ALIGN);
        }
    }

    public boolean isTimeIncluded(final LocalTime time) {
        return time.isAfter(startTime) && time.isBefore(endTime);
    }
}
