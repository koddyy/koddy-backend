package com.koddy.server.coffeechat.domain.model;

import com.koddy.server.common.ParallelTest;
import com.koddy.server.member.domain.model.mentee.Mentee;
import com.koddy.server.member.domain.model.mentor.Mentor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.APPLY;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_1;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static com.koddy.server.common.fixture.ReservationFixture.TARGET_2024_01_01;
import static com.koddy.server.common.fixture.ReservationFixture.TARGET_2024_01_15;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("CoffeeChat -> 도메인 [CoffeeChat] 테스트")
class CoffeeChatTest extends ParallelTest {
    @Nested
    @DisplayName("CoffeeChat 초기 생성")
    class Construct {
        private final Mentee mentee = MENTEE_1.toDomain().apply(1L);
        private final Mentor mentor = MENTOR_1.toDomain().apply(2L);
        private final String applyReason = "신청 이유...";

        @Test
        @DisplayName("멘티 -> 멘토에게 커피챗을 신청한다")
        void menteeToMentor() {
            // when
            final Reservation start = TARGET_2024_01_01.toDomain();
            final Reservation end = TARGET_2024_01_15.toDomain();
            final CoffeeChat coffeeChat = CoffeeChat.applyMenteeToMentor(mentee, mentor, applyReason, start, end);

            // then
            assertAll(
                    () -> assertThat(coffeeChat.getApplier()).isEqualTo(mentee),
                    () -> assertThat(coffeeChat.getTarget()).isEqualTo(mentor),
                    () -> assertThat(coffeeChat.getApplyReason()).isEqualTo(applyReason),
                    () -> assertThat(coffeeChat.getStatus()).isEqualTo(APPLY),
                    () -> assertThat(coffeeChat.getStart()).isEqualTo(start),
                    () -> assertThat(coffeeChat.getEnd()).isEqualTo(end),
                    () -> assertThat(coffeeChat.getStrategy()).isNull()
            );
        }

        @Test
        @DisplayName("멘토 -> 멘티에게 커피챗을 제안한다")
        void mentorToMentee() {
            // when
            final CoffeeChat coffeeChat = CoffeeChat.applyMentorToMentee(mentor, mentee, applyReason);

            // then
            assertAll(
                    () -> assertThat(coffeeChat.getApplier()).isEqualTo(mentor),
                    () -> assertThat(coffeeChat.getTarget()).isEqualTo(mentee),
                    () -> assertThat(coffeeChat.getApplyReason()).isEqualTo(applyReason),
                    () -> assertThat(coffeeChat.getStatus()).isEqualTo(APPLY),
                    () -> assertThat(coffeeChat.getStart()).isNull(),
                    () -> assertThat(coffeeChat.getEnd()).isNull(),
                    () -> assertThat(coffeeChat.getStrategy()).isNull()
            );
        }
    }
}
