package com.koddy.server.member.domain.model.mentor;

import com.koddy.server.common.UnitTest;
import com.koddy.server.member.exception.MemberException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalTime;

import static com.koddy.server.member.domain.model.mentor.DayOfWeek.MON;
import static com.koddy.server.member.exception.MemberExceptionCode.SCHEDULE_PERIOD_TIME_MUST_ALIGN;
import static com.koddy.server.member.exception.MemberExceptionCode.SCHEDULE_PERIOD_TIME_MUST_EXISTS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DisplayName("Member/Mentor -> 도메인 [Timeline] 테스트")
class TimelineTest extends UnitTest {
    @Nested
    @DisplayName("Timeline 생성")
    class Construct {
        @Test
        @DisplayName("시간이 입력되지 않으면 Timeline 생성에 실패한다")
        void throwExceptionByTimeNotExists() {
            assertThatThrownBy(() -> Timeline.of(MON, LocalTime.of(9, 0), null))
                    .isInstanceOf(MemberException.class)
                    .hasMessage(SCHEDULE_PERIOD_TIME_MUST_EXISTS.getMessage());
        }

        @Test
        @DisplayName("멘토링 종료시간가 시작시간 이전이면 Timeline 생성에 실패한다")
        void throwExceptionByStartEndNotAlign() {
            final LocalTime startTime = LocalTime.of(10, 0);
            final LocalTime endTime = LocalTime.of(9, 0);

            assertThatThrownBy(() -> Timeline.of(MON, startTime, endTime))
                    .isInstanceOf(MemberException.class)
                    .hasMessage(SCHEDULE_PERIOD_TIME_MUST_ALIGN.getMessage());
        }

        @Test
        @DisplayName("Timeline을 생성한다")
        void construct() {
            final LocalTime startTime = LocalTime.of(9, 0);
            final LocalTime endTime = LocalTime.of(10, 0);

            assertDoesNotThrow(() -> Timeline.of(MON, startTime, endTime));
        }
    }

    @Test
    @DisplayName("주어진 시간이 멘토링 가능 시간에 포함되는지 확인한다")
    void isTimeIncluded() {
        // given
        final LocalTime startTime = LocalTime.of(9, 0);
        final LocalTime endTime = LocalTime.of(10, 0);
        final Timeline timeline = Timeline.of(MON, startTime, endTime);

        // when
        final boolean actual1 = timeline.isTimeIncluded(LocalTime.of(9, 0));
        final boolean actual2 = timeline.isTimeIncluded(LocalTime.of(9, 30));
        final boolean actual3 = timeline.isTimeIncluded(LocalTime.of(10, 0));
        final boolean actual4 = timeline.isTimeIncluded(LocalTime.of(10, 10));

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isTrue(),
                () -> assertThat(actual3).isTrue(),
                () -> assertThat(actual4).isFalse()
        );
    }
}
