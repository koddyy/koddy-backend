package com.koddy.server.coffeechat.domain.model;

import com.koddy.server.coffeechat.exception.CoffeeChatException;
import com.koddy.server.common.UnitTest;
import com.koddy.server.common.fixture.CoffeeChatFixture.MenteeFlow;
import com.koddy.server.common.fixture.CoffeeChatFixture.MentorFlow;
import com.koddy.server.member.domain.model.mentee.Mentee;
import com.koddy.server.member.domain.model.mentor.Mentor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTEE_APPLY;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTEE_APPLY_COFFEE_CHAT_COMPLETE;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.CANCEL_FROM_MENTEE_FLOW;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTEE_PENDING;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTEE_REJECT;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_APPROVE;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.CANCEL_FROM_MENTOR_FLOW;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_FINALLY_APPROVE;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_FINALLY_CANCEL;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_REJECT;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_SUGGEST;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_SUGGEST_COFFEE_CHAT_COMPLETE;
import static com.koddy.server.coffeechat.exception.CoffeeChatExceptionCode.CANNOT_COMPLETE_STATUS;
import static com.koddy.server.common.fixture.CoffeeChatFixture.월요일_1주차_20_00_시작;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_1;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static com.koddy.server.common.fixture.StrategyFixture.KAKAO_ID;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("CoffeeChat -> 도메인 Aggregate [CoffeeChat] 테스트")
class CoffeeChatTest extends UnitTest {
    private final Mentee mentee = MENTEE_1.toDomain().apply(1L);
    private final Mentor mentor = MENTOR_1.toDomain().apply(2L);

    @Nested
    @DisplayName("CoffeeChat 초기 생성")
    class Construct {
        @Test
        @DisplayName("멘티 -> 멘토에게 커피챗을 신청한다")
        void applyCoffeeChat() {
            // when
            final CoffeeChat coffeeChat = MenteeFlow.apply(월요일_1주차_20_00_시작, mentee, mentor).apply(1L);

            // then
            assertAll(
                    () -> assertThat(coffeeChat.getMentorId()).isEqualTo(mentor.getId()),
                    () -> assertThat(coffeeChat.getMenteeId()).isEqualTo(mentee.getId()),
                    () -> assertThat(coffeeChat.getStatus()).isEqualTo(MENTEE_APPLY),
                    () -> assertThat(coffeeChat.getReason().getApplyReason()).isNotNull(),
                    () -> assertThat(coffeeChat.getReason().getSuggestReason()).isNull(),
                    () -> assertThat(coffeeChat.getReason().getCancelReason()).isNull(),
                    () -> assertThat(coffeeChat.getReason().getRejectReason()).isNull(),
                    () -> assertThat(coffeeChat.getQuestion()).isNull(),
                    () -> assertThat(coffeeChat.getReservation().getStart()).isEqualTo(월요일_1주차_20_00_시작.getStart()),
                    () -> assertThat(coffeeChat.getReservation().getEnd()).isEqualTo(월요일_1주차_20_00_시작.getEnd()),
                    () -> assertThat(coffeeChat.getStrategy()).isNull()
            );
        }

        @Test
        @DisplayName("멘토 -> 멘티에게 커피챗을 제안한다")
        void suggestCoffeeChat() {
            // when
            final CoffeeChat coffeeChat = MentorFlow.suggest(mentor, mentee).apply(1L);

            // then
            assertAll(
                    () -> assertThat(coffeeChat.getMentorId()).isEqualTo(mentor.getId()),
                    () -> assertThat(coffeeChat.getMenteeId()).isEqualTo(mentee.getId()),
                    () -> assertThat(coffeeChat.getStatus()).isEqualTo(MENTOR_SUGGEST),
                    () -> assertThat(coffeeChat.getReason().getApplyReason()).isNull(),
                    () -> assertThat(coffeeChat.getReason().getSuggestReason()).isNotNull(),
                    () -> assertThat(coffeeChat.getReason().getCancelReason()).isNull(),
                    () -> assertThat(coffeeChat.getReason().getRejectReason()).isNull(),
                    () -> assertThat(coffeeChat.getQuestion()).isNull(),
                    () -> assertThat(coffeeChat.getReservation()).isNull(),
                    () -> assertThat(coffeeChat.getStrategy()).isNull()
            );
        }
    }

    @Nested
    @DisplayName("멘티의 커피챗 신청에 대한 처리")
    class FromMenteeApply {
        @Test
        @DisplayName("거절한다")
        void reject() {
            // given
            final CoffeeChat coffeeChat = MenteeFlow.apply(월요일_1주차_20_00_시작, mentee, mentor).apply(1L);

            // when
            final String rejectReason = "거절..";
            coffeeChat.rejectFromMenteeApply(rejectReason);

            // then
            assertAll(
                    () -> assertThat(coffeeChat.getMentorId()).isEqualTo(mentor.getId()),
                    () -> assertThat(coffeeChat.getMenteeId()).isEqualTo(mentee.getId()),
                    () -> assertThat(coffeeChat.getStatus()).isEqualTo(MENTOR_REJECT),
                    () -> assertThat(coffeeChat.getReason().getApplyReason()).isNotNull(),
                    () -> assertThat(coffeeChat.getReason().getSuggestReason()).isNull(),
                    () -> assertThat(coffeeChat.getReason().getCancelReason()).isNull(),
                    () -> assertThat(coffeeChat.getReason().getRejectReason()).isNotNull(),
                    () -> assertThat(coffeeChat.getQuestion()).isNull(),
                    () -> assertThat(coffeeChat.getReservation().getStart()).isEqualTo(월요일_1주차_20_00_시작.getStart()),
                    () -> assertThat(coffeeChat.getReservation().getEnd()).isEqualTo(월요일_1주차_20_00_시작.getEnd()),
                    () -> assertThat(coffeeChat.getStrategy()).isNull()
            );
        }

        @Test
        @DisplayName("수락한다")
        void approve() {
            // given
            final CoffeeChat coffeeChat = MenteeFlow.apply(월요일_1주차_20_00_시작, mentee, mentor).apply(1L);

            // when
            final Strategy strategy = KAKAO_ID.toDomain();
            coffeeChat.approveFromMenteeApply("질문..", strategy);

            // then
            assertAll(
                    () -> assertThat(coffeeChat.getMentorId()).isEqualTo(mentor.getId()),
                    () -> assertThat(coffeeChat.getMenteeId()).isEqualTo(mentee.getId()),
                    () -> assertThat(coffeeChat.getStatus()).isEqualTo(MENTOR_APPROVE),
                    () -> assertThat(coffeeChat.getReason().getApplyReason()).isNotNull(),
                    () -> assertThat(coffeeChat.getReason().getSuggestReason()).isNull(),
                    () -> assertThat(coffeeChat.getReason().getCancelReason()).isNull(),
                    () -> assertThat(coffeeChat.getReason().getRejectReason()).isNull(),
                    () -> assertThat(coffeeChat.getQuestion()).isNotNull(),
                    () -> assertThat(coffeeChat.getReservation().getStart()).isEqualTo(월요일_1주차_20_00_시작.getStart()),
                    () -> assertThat(coffeeChat.getReservation().getEnd()).isEqualTo(월요일_1주차_20_00_시작.getEnd()),
                    () -> assertThat(coffeeChat.getStrategy()).isEqualTo(strategy)
            );
        }
    }

    @Nested
    @DisplayName("멘토의 커피챗 제안에 대한 처리")
    class FromMentorSuggest {
        @Test
        @DisplayName("거절한다")
        void reject() {
            // given
            final CoffeeChat coffeeChat = MentorFlow.suggest(mentor, mentee).apply(1L);

            // when
            final String rejectReason = "거절..";
            coffeeChat.rejectFromMentorSuggest(rejectReason);

            // then
            assertAll(
                    () -> assertThat(coffeeChat.getMentorId()).isEqualTo(mentor.getId()),
                    () -> assertThat(coffeeChat.getMenteeId()).isEqualTo(mentee.getId()),
                    () -> assertThat(coffeeChat.getStatus()).isEqualTo(MENTEE_REJECT),
                    () -> assertThat(coffeeChat.getReason().getApplyReason()).isNull(),
                    () -> assertThat(coffeeChat.getReason().getSuggestReason()).isNotNull(),
                    () -> assertThat(coffeeChat.getReason().getCancelReason()).isNull(),
                    () -> assertThat(coffeeChat.getReason().getRejectReason()).isNotNull(),
                    () -> assertThat(coffeeChat.getQuestion()).isNull(),
                    () -> assertThat(coffeeChat.getReservation()).isNull(),
                    () -> assertThat(coffeeChat.getStrategy()).isNull()
            );
        }

        @Test
        @DisplayName("1차 수락한다 (멘토 최종 수락 대기)")
        void pending() {
            // given
            final CoffeeChat coffeeChat = MentorFlow.suggest(mentor, mentee).apply(1L);

            // when
            final String question = "질문..";
            final LocalDateTime start = 월요일_1주차_20_00_시작.getStart();
            final LocalDateTime end = 월요일_1주차_20_00_시작.getEnd();
            coffeeChat.pendingFromMentorSuggest(question, Reservation.of(start, end));

            // then
            assertAll(
                    () -> assertThat(coffeeChat.getMentorId()).isEqualTo(mentor.getId()),
                    () -> assertThat(coffeeChat.getMenteeId()).isEqualTo(mentee.getId()),
                    () -> assertThat(coffeeChat.getStatus()).isEqualTo(MENTEE_PENDING),
                    () -> assertThat(coffeeChat.getReason().getApplyReason()).isNull(),
                    () -> assertThat(coffeeChat.getReason().getSuggestReason()).isNotNull(),
                    () -> assertThat(coffeeChat.getReason().getCancelReason()).isNull(),
                    () -> assertThat(coffeeChat.getReason().getRejectReason()).isNull(),
                    () -> assertThat(coffeeChat.getQuestion()).isEqualTo(question),
                    () -> assertThat(coffeeChat.getReservation().getStart()).isEqualTo(start),
                    () -> assertThat(coffeeChat.getReservation().getEnd()).isEqualTo(end),
                    () -> assertThat(coffeeChat.getStrategy()).isNull()
            );
        }
    }

    @Nested
    @DisplayName("최종 결정 대기 상태인 CoffeeChat에 대한 멘토의 결정")
    class PendingCoffeeChat {
        @Test
        @DisplayName("최종 취소한다")
        void finallyCancel() {
            // given
            final CoffeeChat coffeeChat = MentorFlow.suggestAndPending(월요일_1주차_20_00_시작, mentor, mentee).apply(1L);

            // when
            final String rejectReason = "최종 취소..";
            coffeeChat.finallyCancelPendingCoffeeChat(rejectReason);

            // then
            assertAll(
                    () -> assertThat(coffeeChat.getMentorId()).isEqualTo(mentor.getId()),
                    () -> assertThat(coffeeChat.getMenteeId()).isEqualTo(mentee.getId()),
                    () -> assertThat(coffeeChat.getStatus()).isEqualTo(MENTOR_FINALLY_CANCEL),
                    () -> assertThat(coffeeChat.getReason().getApplyReason()).isNull(),
                    () -> assertThat(coffeeChat.getReason().getSuggestReason()).isNotNull(),
                    () -> assertThat(coffeeChat.getReason().getCancelReason()).isNotNull(),
                    () -> assertThat(coffeeChat.getReason().getRejectReason()).isNull(),
                    () -> assertThat(coffeeChat.getQuestion()).isNotNull(),
                    () -> assertThat(coffeeChat.getReservation().getStart()).isEqualTo(월요일_1주차_20_00_시작.getStart()),
                    () -> assertThat(coffeeChat.getReservation().getEnd()).isEqualTo(월요일_1주차_20_00_시작.getEnd()),
                    () -> assertThat(coffeeChat.getStrategy()).isNull()
            );
        }

        @Test
        @DisplayName("최종 수락한다")
        void finallyApprove() {
            // given
            final CoffeeChat coffeeChat = MentorFlow.suggestAndPending(월요일_1주차_20_00_시작, mentor, mentee).apply(1L);

            // when
            final Strategy strategy = KAKAO_ID.toDomain();
            coffeeChat.finallyApprovePendingCoffeeChat(strategy);

            // then
            assertAll(
                    () -> assertThat(coffeeChat.getMentorId()).isEqualTo(mentor.getId()),
                    () -> assertThat(coffeeChat.getMenteeId()).isEqualTo(mentee.getId()),
                    () -> assertThat(coffeeChat.getStatus()).isEqualTo(MENTOR_FINALLY_APPROVE),
                    () -> assertThat(coffeeChat.getReason().getApplyReason()).isNull(),
                    () -> assertThat(coffeeChat.getReason().getSuggestReason()).isNotNull(),
                    () -> assertThat(coffeeChat.getReason().getCancelReason()).isNull(),
                    () -> assertThat(coffeeChat.getReason().getRejectReason()).isNull(),
                    () -> assertThat(coffeeChat.getQuestion()).isNotNull(),
                    () -> assertThat(coffeeChat.getReservation().getStart()).isEqualTo(월요일_1주차_20_00_시작.getStart()),
                    () -> assertThat(coffeeChat.getReservation().getEnd()).isEqualTo(월요일_1주차_20_00_시작.getEnd()),
                    () -> assertThat(coffeeChat.getStrategy()).isEqualTo(strategy)
            );
        }
    }

    @Nested
    @DisplayName("신청/제안한 커피챗 취소")
    class Cancel {
        @Test
        @DisplayName("멘티는 자신이 신청한 커피챗을 취소한다")
        void cancelAppliedCoffeeChat() {
            // given
            final CoffeeChat coffeeChat = MenteeFlow.apply(월요일_1주차_20_00_시작, mentee, mentor).apply(1L);

            // when
            coffeeChat.cancel(CANCEL_FROM_MENTEE_FLOW, mentee.getId(), "취소..");

            // then
            assertAll(
                    () -> assertThat(coffeeChat.getMentorId()).isEqualTo(mentor.getId()),
                    () -> assertThat(coffeeChat.getMenteeId()).isEqualTo(mentee.getId()),
                    () -> assertThat(coffeeChat.getStatus()).isEqualTo(CANCEL_FROM_MENTEE_FLOW),
                    () -> assertThat(coffeeChat.getReason().getApplyReason()).isNotNull(),
                    () -> assertThat(coffeeChat.getReason().getSuggestReason()).isNull(),
                    () -> assertThat(coffeeChat.getReason().getCancelReason()).isNotNull(),
                    () -> assertThat(coffeeChat.getReason().getRejectReason()).isNull(),
                    () -> assertThat(coffeeChat.getCancelBy()).isEqualTo(mentee.getId()),
                    () -> assertThat(coffeeChat.getQuestion()).isNull(),
                    () -> assertThat(coffeeChat.getReservation().getStart()).isEqualTo(월요일_1주차_20_00_시작.getStart()),
                    () -> assertThat(coffeeChat.getReservation().getEnd()).isEqualTo(월요일_1주차_20_00_시작.getEnd()),
                    () -> assertThat(coffeeChat.getStrategy()).isNull()
            );
        }

        @Test
        @DisplayName("멘토는 자신이 제안한 커피챗을 취소한다")
        void cancelSuggestedCoffeeChat() {
            // given
            final CoffeeChat coffeeChat = MentorFlow.suggest(mentor, mentee).apply(1L);

            // when
            coffeeChat.cancel(CANCEL_FROM_MENTOR_FLOW, mentor.getId(), "취소..");

            // then
            assertAll(
                    () -> assertThat(coffeeChat.getMentorId()).isEqualTo(mentor.getId()),
                    () -> assertThat(coffeeChat.getMenteeId()).isEqualTo(mentee.getId()),
                    () -> assertThat(coffeeChat.getStatus()).isEqualTo(CANCEL_FROM_MENTOR_FLOW),
                    () -> assertThat(coffeeChat.getReason().getApplyReason()).isNull(),
                    () -> assertThat(coffeeChat.getReason().getSuggestReason()).isNotNull(),
                    () -> assertThat(coffeeChat.getReason().getCancelReason()).isNotNull(),
                    () -> assertThat(coffeeChat.getReason().getRejectReason()).isNull(),
                    () -> assertThat(coffeeChat.getCancelBy()).isEqualTo(mentor.getId()),
                    () -> assertThat(coffeeChat.getQuestion()).isNull(),
                    () -> assertThat(coffeeChat.getReservation()).isNull(),
                    () -> assertThat(coffeeChat.getStrategy()).isNull()
            );
        }
    }

    @Nested
    @DisplayName("신청/제안한 커피챗 진행 완료")
    class Complete {
        @Test
        @DisplayName("APPROVE 상태가 아니면 완료 상태로 갱신이 불가능하다")
        void throwExceptionByCannotCompleteStatus() {
            // given
            final CoffeeChat coffeeChatA = MenteeFlow.applyAndReject(월요일_1주차_20_00_시작, mentee, mentor).apply(1L);
            final CoffeeChat coffeeChatB = MentorFlow.suggestAndReject(mentor, mentee).apply(2L);

            // when - then
            assertAll(
                    () -> assertThatThrownBy(() -> coffeeChatA.complete(MENTEE_APPLY_COFFEE_CHAT_COMPLETE))
                            .isInstanceOf(CoffeeChatException.class)
                            .hasMessage(CANNOT_COMPLETE_STATUS.getMessage()),
                    () -> assertThatThrownBy(() -> coffeeChatB.complete(MENTOR_SUGGEST_COFFEE_CHAT_COMPLETE))
                            .isInstanceOf(CoffeeChatException.class)
                            .hasMessage(CANNOT_COMPLETE_STATUS.getMessage())
            );
        }

        @Test
        @DisplayName("멘티가 신청한 APPROVE 상태의 커피챗을 진행한 후 완료 상태로 갱신한다")
        void successMenteeApply() {
            // given
            final CoffeeChat coffeeChat = MenteeFlow.applyAndApprove(월요일_1주차_20_00_시작, mentee, mentor).apply(1L);

            // when
            coffeeChat.complete(MENTEE_APPLY_COFFEE_CHAT_COMPLETE);

            // then
            assertAll(
                    () -> assertThat(coffeeChat.getMentorId()).isEqualTo(mentor.getId()),
                    () -> assertThat(coffeeChat.getMenteeId()).isEqualTo(mentee.getId()),
                    () -> assertThat(coffeeChat.getStatus()).isEqualTo(MENTEE_APPLY_COFFEE_CHAT_COMPLETE),
                    () -> assertThat(coffeeChat.getReason().getApplyReason()).isNotNull(),
                    () -> assertThat(coffeeChat.getReason().getSuggestReason()).isNull(),
                    () -> assertThat(coffeeChat.getReason().getCancelReason()).isNull(),
                    () -> assertThat(coffeeChat.getReason().getRejectReason()).isNull(),
                    () -> assertThat(coffeeChat.getQuestion()).isNotNull(),
                    () -> assertThat(coffeeChat.getReservation().getStart()).isEqualTo(월요일_1주차_20_00_시작.getStart()),
                    () -> assertThat(coffeeChat.getReservation().getEnd()).isEqualTo(월요일_1주차_20_00_시작.getEnd()),
                    () -> assertThat(coffeeChat.getStrategy()).isEqualTo(월요일_1주차_20_00_시작.getStrategy())
            );
        }

        @Test
        @DisplayName("멘토가 제안한 APPROVE 상태의 커피챗을 진행한 후 완료 상태로 갱신한다")
        void successMentorSuggest() {
            // given
            final CoffeeChat coffeeChat = MentorFlow.suggestAndFinallyApprove(월요일_1주차_20_00_시작, mentor, mentee).apply(2L);

            // when
            coffeeChat.complete(MENTOR_SUGGEST_COFFEE_CHAT_COMPLETE);

            // then
            assertAll(
                    () -> assertThat(coffeeChat.getMentorId()).isEqualTo(mentor.getId()),
                    () -> assertThat(coffeeChat.getMenteeId()).isEqualTo(mentee.getId()),
                    () -> assertThat(coffeeChat.getStatus()).isEqualTo(MENTOR_SUGGEST_COFFEE_CHAT_COMPLETE),
                    () -> assertThat(coffeeChat.getReason().getApplyReason()).isNull(),
                    () -> assertThat(coffeeChat.getReason().getSuggestReason()).isNotNull(),
                    () -> assertThat(coffeeChat.getReason().getCancelReason()).isNull(),
                    () -> assertThat(coffeeChat.getReason().getRejectReason()).isNull(),
                    () -> assertThat(coffeeChat.getQuestion()).isNotNull(),
                    () -> assertThat(coffeeChat.getReservation().getStart()).isEqualTo(월요일_1주차_20_00_시작.getStart()),
                    () -> assertThat(coffeeChat.getReservation().getEnd()).isEqualTo(월요일_1주차_20_00_시작.getEnd()),
                    () -> assertThat(coffeeChat.getStrategy()).isEqualTo(월요일_1주차_20_00_시작.getStrategy())
            );
        }
    }

    @Test
    @DisplayName("CoffeeChat의 Reservation[start, end]와 비교 대상 Reservation의 시간대가 겹치는지 확인한다")
    void isRequestReservationIncludedSchedules() {
        // given
        final LocalDateTime start = LocalDateTime.of(2024, 2, 2, 18, 0);
        final LocalDateTime end = start.plusMinutes(30);
        final CoffeeChat coffeeChat = MenteeFlow.apply(start, end, mentee, mentor).apply(1L);

        // when
        final boolean actual1 = coffeeChat.isRequestReservationIncludedSchedules(Reservation.of(start.minusMinutes(10), start.plusMinutes(10)));
        final boolean actual2 = coffeeChat.isRequestReservationIncludedSchedules(Reservation.of(start, start.plusMinutes(10)));
        final boolean actual3 = coffeeChat.isRequestReservationIncludedSchedules(Reservation.of(end.minusMinutes(10), end));
        final boolean actual4 = coffeeChat.isRequestReservationIncludedSchedules(Reservation.of(end, end.plusMinutes(10)));

        // then
        assertAll(
                () -> assertThat(actual1).isTrue(),
                () -> assertThat(actual2).isTrue(),
                () -> assertThat(actual3).isTrue(),
                () -> assertThat(actual4).isFalse()
        );
    }
}
