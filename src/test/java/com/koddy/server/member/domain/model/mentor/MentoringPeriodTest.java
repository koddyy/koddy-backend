package com.koddy.server.member.domain.model.mentor;

import com.koddy.server.common.UnitTest;
import com.koddy.server.member.exception.MemberException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static com.koddy.server.member.domain.model.mentor.MentoringPeriod.TimeUnit.HALF_HOUR;
import static com.koddy.server.member.exception.MemberExceptionCode.SCHEDULE_PERIOD_TIME_MUST_ALIGN;
import static com.koddy.server.member.exception.MemberExceptionCode.SCHEDULE_PERIOD_TIME_MUST_EXISTS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Member/Mentor -> 도메인 [MentoringPeriod] 테스트")
class MentoringPeriodTest extends UnitTest {
    @Nested
    @DisplayName("MentoringPeriod 생성")
    class Construct {
        @Test
        @DisplayName("날짜가 입력되지 않으면 MentoringPeriod 생성에 실패한다")
        void throwExceptionByDateNotExists() {
            assertThatThrownBy(() -> MentoringPeriod.of(LocalDate.of(2024, 1, 1), null))
                    .isInstanceOf(MemberException.class)
                    .hasMessage(SCHEDULE_PERIOD_TIME_MUST_EXISTS.getMessage());
        }

        @Test
        @DisplayName("멘토링 종료날짜가 시작날짜 이전이면 MentoringPeriod 생성에 실패한다")
        void throwExceptionByStartEndNotAlign() {
            final LocalDate startDate = LocalDate.of(2024, 3, 1);
            final LocalDate endDate = LocalDate.of(2024, 2, 1);

            assertThatThrownBy(() -> MentoringPeriod.of(startDate, endDate))
                    .isInstanceOf(MemberException.class)
                    .hasMessage(SCHEDULE_PERIOD_TIME_MUST_ALIGN.getMessage());
        }

        @Test
        @DisplayName("MentoringPeriod을 생성한다")
        void construct() {
            // given
            final LocalDate startDate = LocalDate.of(2024, 2, 1);
            final LocalDate endDate = LocalDate.of(2024, 3, 1);

            // when
            final MentoringPeriod mentoringPeriod = MentoringPeriod.of(startDate, endDate);

            // then
            assertAll(
                    () -> assertThat(mentoringPeriod.getStartDate()).isEqualTo(startDate),
                    () -> assertThat(mentoringPeriod.getEndDate()).isEqualTo(endDate),
                    () -> assertThat(mentoringPeriod.getTimeUnit()).isEqualTo(HALF_HOUR)
            );
        }
    }

    @Test
    @DisplayName("주어진 날짜가 멘토링 진행 기간에 포함되는지 확인한다")
    void isDateIncluded() {
        // given
        final LocalDate startDate = LocalDate.of(2024, 2, 1);
        final LocalDate endDate = LocalDate.of(2024, 3, 1);
        final MentoringPeriod mentoringPeriod = MentoringPeriod.of(startDate, endDate);

        // when
        final boolean actual1 = mentoringPeriod.isDateIncluded(LocalDate.of(2024, 2, 1));
        final boolean actual2 = mentoringPeriod.isDateIncluded(LocalDate.of(2024, 2, 15));
        final boolean actual3 = mentoringPeriod.isDateIncluded(LocalDate.of(2024, 3, 1));
        final boolean actual4 = mentoringPeriod.isDateIncluded(LocalDate.of(2024, 3, 15));

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isTrue(),
                () -> assertThat(actual3).isTrue(),
                () -> assertThat(actual4).isFalse()
        );
    }

    @Test
    @DisplayName("멘토링 진행 시간이 멘토의 TimeUnit이랑 일치하는지 확인한다")
    void allowedTimeUnit() {
        // given
        final LocalDate startDate = LocalDate.of(2024, 2, 1);
        final LocalDate endDate = LocalDate.of(2024, 3, 1);
        final MentoringPeriod mentoringPeriod = MentoringPeriod.of(startDate, endDate); // TODO default = 30

        // when
        final LocalDateTime standard = LocalDateTime.of(2024, 2, 15, 18, 0);

        final boolean actual1 = mentoringPeriod.allowedTimeUnit(standard, standard.plusMinutes(30));
        final boolean actual2 = mentoringPeriod.allowedTimeUnit(standard, standard.plusMinutes(60));

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isFalse()
        );
    }
}
