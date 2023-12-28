package com.koddy.server.member.domain.model.mentor;

import com.koddy.server.member.exception.MemberException;
import com.koddy.server.member.exception.MemberExceptionCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DisplayName("Member/Mentor -> 도메인 [Period VO] 테스트")
class PeriodTest {
    @Nested
    @DisplayName("Period 생성")
    class Construct {
        @Test
        @DisplayName("시간이 입력되지 않으면 Period 생성에 실패한다")
        void throwExceptionByTimeNotExists() {
            assertThatThrownBy(() -> Period.of(LocalTime.of(9, 0), null))
                    .isInstanceOf(MemberException.class)
                    .hasMessage(MemberExceptionCode.PERIOD_MUST_EXISTS.getMessage());
        }

        @Test
        @DisplayName("멘토링 종료날짜가 시작날짜 이전이면 Period 생성에 실패한다")
        void throwExceptionByStartEndNotAlign() {
            final LocalTime startDate = LocalTime.of(10, 0);
            final LocalTime endDate = LocalTime.of(9, 0);

            assertThatThrownBy(() -> Period.of(startDate, endDate))
                    .isInstanceOf(MemberException.class)
                    .hasMessage(MemberExceptionCode.START_END_MUST_BE_ALIGN.getMessage());
        }

        @Test
        @DisplayName("Period을 생성한다")
        void construct() {
            final LocalTime startDate = LocalTime.of(9, 0);
            final LocalTime endDate = LocalTime.of(17, 0);

            assertDoesNotThrow(() -> Period.of(startDate, endDate));
        }
    }

    @Test
    @DisplayName("주어진 시간이 멘토링 가능 시간에 포함되는지 확인한다")
    void isTimeIncluded() {
        // given
        final LocalTime startDate = LocalTime.of(9, 0);
        final LocalTime endDate = LocalTime.of(17, 0);
        final Period period = Period.of(startDate, endDate);

        // when
        final boolean actual1 = period.isTimeIncluded(LocalTime.of(13, 0));
        final boolean actual2 = period.isTimeIncluded(LocalTime.of(20, 0));

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isFalse()
        );
    }
}
