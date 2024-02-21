package com.koddy.server.member.domain.model.mentor;

import com.koddy.server.global.utils.TimeUtils;
import com.koddy.server.member.exception.MemberException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;

import static com.koddy.server.member.domain.model.mentor.MentoringPeriod.TimeUnit.HALF_HOUR;
import static com.koddy.server.member.exception.MemberExceptionCode.INVALID_TIME_UNIT;
import static com.koddy.server.member.exception.MemberExceptionCode.SCHEDULE_PERIOD_TIME_MUST_ALIGN;
import static com.koddy.server.member.exception.MemberExceptionCode.SCHEDULE_PERIOD_TIME_MUST_EXISTS;
import static jakarta.persistence.EnumType.STRING;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Embeddable
public class MentoringPeriod {
    @Column(name = "mentoring_start_date")
    private LocalDate startDate;

    @Column(name = "mentoring_end_date")
    private LocalDate endDate;

    @Enumerated(STRING)
    @Column(name = "mentoring_time_unit", nullable = false, columnDefinition = "VARCHAR(20)")
    private TimeUnit timeUnit;

    private MentoringPeriod(final LocalDate startDate, final LocalDate endDate, final TimeUnit timeUnit) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.timeUnit = timeUnit;
    }

    // TODO 회의하고 UI 픽스 후 TimeUnit 선택할 수 있도록 Request에 적용
    public static MentoringPeriod of(final LocalDate startDate, final LocalDate endDate) {
        validateDateExists(startDate, endDate);
        validateStartIsBeforeEnd(startDate, endDate);
        return new MentoringPeriod(startDate, endDate, HALF_HOUR);
    }

    private static void validateDateExists(final LocalDate startDate, final LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new MemberException(SCHEDULE_PERIOD_TIME_MUST_EXISTS);
        }
    }

    private static void validateStartIsBeforeEnd(final LocalDate startDate, final LocalDate endDate) {
        if (TimeUtils.isGreator(startDate, endDate)) {
            throw new MemberException(SCHEDULE_PERIOD_TIME_MUST_ALIGN);
        }
    }

    /**
     * start <= ... <= end
     */
    public boolean isDateIncluded(final LocalDate target) {
        return TimeUtils.isLowerOrEqual(startDate, target) && TimeUtils.isLowerOrEqual(target, endDate);
    }

    public boolean allowedTimeUnit(final LocalDateTime start, final LocalDateTime end) {
        final long requestDuration = ChronoUnit.MINUTES.between(start, end);
        return timeUnit.getValue() == requestDuration;
    }

    @Getter
    @RequiredArgsConstructor
    public enum TimeUnit {
        HALF_HOUR(30),
        ONE_HOUR(60),
        ;

        private final int value;

        public static TimeUnit from(final int value) {
            return Arrays.stream(values())
                    .filter(it -> it.value == value)
                    .findFirst()
                    .orElseThrow(() -> new MemberException(INVALID_TIME_UNIT));
        }
    }
}
