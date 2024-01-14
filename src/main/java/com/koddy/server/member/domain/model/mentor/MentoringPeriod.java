package com.koddy.server.member.domain.model.mentor;

import com.koddy.server.member.exception.MemberException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

import static com.koddy.server.member.exception.MemberExceptionCode.SCHEDULE_PERIOD_TIME_MUST_ALIGN;
import static com.koddy.server.member.exception.MemberExceptionCode.SCHEDULE_PERIOD_TIME_MUST_EXISTS;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Embeddable
public class MentoringPeriod {
    @Column(name = "mentoring_start_date")
    private LocalDate startDate;

    @Column(name = "mentoring_end_date")
    private LocalDate endDate;

    private MentoringPeriod(final LocalDate startDate, final LocalDate endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public static MentoringPeriod of(final LocalDate startDate, final LocalDate endDate) {
        validateDateExists(startDate, endDate);
        validateStartIsBeforeEnd(startDate, endDate);
        return new MentoringPeriod(startDate, endDate);
    }

    private static void validateDateExists(final LocalDate startDate, final LocalDate endDate) {
        if (startDate == null || endDate == null) {
            throw new MemberException(SCHEDULE_PERIOD_TIME_MUST_EXISTS);
        }
    }

    private static void validateStartIsBeforeEnd(final LocalDate startDate, final LocalDate endDate) {
        if (startDate.isAfter(endDate)) {
            throw new MemberException(SCHEDULE_PERIOD_TIME_MUST_ALIGN);
        }
    }
}
