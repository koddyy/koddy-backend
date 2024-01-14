package com.koddy.server.member.domain.model.mentor;

import com.koddy.server.common.ParallelTest;
import com.koddy.server.member.exception.MemberException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static com.koddy.server.member.domain.model.mentor.MentoringSetting.TimeUnit.HALF_HOUR;
import static com.koddy.server.member.exception.MemberExceptionCode.SCHEDULE_PERIOD_TIME_MUST_ALIGN;
import static com.koddy.server.member.exception.MemberExceptionCode.SCHEDULE_PERIOD_TIME_MUST_EXISTS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Member/Mentor -> 도메인 [MentoringSetting] 테스트")
class MentoringSettingTest extends ParallelTest {
    @Nested
    @DisplayName("MentoringSetting 생성")
    class Construct {
        @Test
        @DisplayName("날짜가 입력되지 않으면 MentoringSetting 생성에 실패한다")
        void throwExceptionByDateNotExists() {
            assertThatThrownBy(() -> MentoringSetting.of(LocalDate.of(2024, 1, 1), null))
                    .isInstanceOf(MemberException.class)
                    .hasMessage(SCHEDULE_PERIOD_TIME_MUST_EXISTS.getMessage());
        }

        @Test
        @DisplayName("멘토링 종료날짜가 시작날짜 이전이면 MentoringSetting 생성에 실패한다")
        void throwExceptionByStartEndNotAlign() {
            final LocalDate startDate = LocalDate.of(2024, 3, 1);
            final LocalDate endDate = LocalDate.of(2024, 2, 1);

            assertThatThrownBy(() -> MentoringSetting.of(startDate, endDate))
                    .isInstanceOf(MemberException.class)
                    .hasMessage(SCHEDULE_PERIOD_TIME_MUST_ALIGN.getMessage());
        }

        @Test
        @DisplayName("MentoringSetting을 생성한다")
        void construct() {
            // given
            final LocalDate startDate = LocalDate.of(2024, 2, 1);
            final LocalDate endDate = LocalDate.of(2024, 3, 1);

            // when
            final MentoringSetting mentoringSetting = MentoringSetting.of(startDate, endDate);

            // then
            assertAll(
                    () -> assertThat(mentoringSetting.getStartDate()).isEqualTo(startDate),
                    () -> assertThat(mentoringSetting.getEndDate()).isEqualTo(endDate),
                    () -> assertThat(mentoringSetting.getTimeUnit()).isEqualTo(HALF_HOUR)
            );
        }
    }
}
