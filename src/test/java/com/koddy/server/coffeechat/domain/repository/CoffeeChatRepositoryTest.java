package com.koddy.server.coffeechat.domain.repository;

import com.koddy.server.coffeechat.domain.model.CoffeeChat;
import com.koddy.server.coffeechat.domain.model.Reservation;
import com.koddy.server.coffeechat.exception.CoffeeChatException;
import com.koddy.server.common.RepositoryTest;
import com.koddy.server.common.fixture.MenteeFixture;
import com.koddy.server.common.fixture.MentorFixture;
import com.koddy.server.common.fixture.StrategyFixture;
import com.koddy.server.member.domain.model.mentee.Mentee;
import com.koddy.server.member.domain.model.mentor.Mentor;
import com.koddy.server.member.domain.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.APPLY;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.APPROVE;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.PENDING;
import static com.koddy.server.coffeechat.exception.CoffeeChatExceptionCode.APPLIED_OR_SUGGESTED_COFFEE_CHAT_NOT_FOUND;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_1;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_10;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_2;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_3;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_4;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_5;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_6;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_7;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_8;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_9;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("CoffeeChat -> CoffeeChatRepository 테스트")
class CoffeeChatRepositoryTest extends RepositoryTest {
    @Autowired
    private CoffeeChatRepository sut;

    @Autowired
    private MemberRepository memberRepository;

    private final String applyReason = "신청..";
    private final String question = "질문..";
    private final String rejectReason = "거절..";

    @Test
    @DisplayName("상태에 따른 CoffeeChat을 조회한다")
    void findByIdAndStatus() {
        // given
        final Mentor mentor = createMentor(MENTOR_1);
        final Mentee mentee = createMentee(MENTEE_1);
        final CoffeeChat coffeeChatA = apply(
                mentee,
                mentor,
                new Reservation(LocalDateTime.of(2024, 2, 1, 9, 0)),
                new Reservation(LocalDateTime.of(2024, 2, 1, 10, 0))
        );
        final CoffeeChat coffeeChatB = suggest(mentor, mentee);

        /* 1차 조회 */
        assertAll(
                () -> assertThat(sut.findByIdAndStatus(coffeeChatA.getId(), APPLY)).isPresent(),
                () -> assertThat(sut.findByIdAndStatus(coffeeChatB.getId(), APPLY)).isPresent()
        );

        /* coffeeChatA 수락 */
        coffeeChatA.approveFromMenteeApply(StrategyFixture.KAKAO_ID.toDomain());
        assertAll(
                () -> assertThat(sut.findByIdAndStatus(coffeeChatA.getId(), APPROVE)).isPresent(),
                () -> assertThat(sut.findByIdAndStatus(coffeeChatB.getId(), APPLY)).isPresent()
        );

        /* coffeeChatB 1차 수락 */
        coffeeChatB.pendingFromMentorSuggest(
                question,
                new Reservation(LocalDateTime.of(2024, 2, 1, 18, 0)),
                new Reservation(LocalDateTime.of(2024, 2, 1, 19, 0))
        );
        assertAll(
                () -> assertThat(sut.findByIdAndStatus(coffeeChatA.getId(), APPROVE)).isPresent(),
                () -> assertThat(sut.findByIdAndStatus(coffeeChatB.getId(), PENDING)).isPresent()
        );

        /* coffeeChatB 최종 수락 */
        coffeeChatB.approvePendingCoffeeChat(StrategyFixture.KAKAO_ID.toDomain());
        assertAll(
                () -> assertThat(sut.findByIdAndStatus(coffeeChatA.getId(), APPROVE)).isPresent(),
                () -> assertThat(sut.findByIdAndStatus(coffeeChatB.getId(), APPROVE)).isPresent()
        );
    }

    @Test
    @DisplayName("멘토가 제안 or 멘티가 신청하고 APPLY 상태인 커피챗을 조회한다")
    void getAppliedOrSuggestedCoffeeChat() {
        // given
        final Mentor mentor = createMentor(MENTOR_1);
        final Mentee mentee = createMentee(MENTEE_1);
        final CoffeeChat mentorSuggestedCoffeeChat = suggest(mentor, mentee);
        final CoffeeChat menteeAppliedCoffeeChat = apply(
                mentee,
                mentor,
                new Reservation(LocalDateTime.of(2024, 2, 1, 9, 0)),
                new Reservation(LocalDateTime.of(2024, 2, 1, 10, 0))
        );

        // when - then
        assertAll(
                // 멘토가 제안한 커피챗
                () -> assertThat(sut.getAppliedOrSuggestedCoffeeChat(mentorSuggestedCoffeeChat.getId(), mentor.getId())).isEqualTo(mentorSuggestedCoffeeChat),
                () -> assertThatThrownBy(() -> sut.getAppliedOrSuggestedCoffeeChat(mentorSuggestedCoffeeChat.getId(), mentee.getId()))
                        .isInstanceOf(CoffeeChatException.class)
                        .hasMessage(APPLIED_OR_SUGGESTED_COFFEE_CHAT_NOT_FOUND.getMessage()),

                // 멘티가 신청한 커피챗
                () -> assertThat(sut.getAppliedOrSuggestedCoffeeChat(menteeAppliedCoffeeChat.getId(), mentee.getId())).isEqualTo(menteeAppliedCoffeeChat),
                () -> assertThatThrownBy(() -> sut.getAppliedOrSuggestedCoffeeChat(menteeAppliedCoffeeChat.getId(), mentor.getId()))
                        .isInstanceOf(CoffeeChatException.class)
                        .hasMessage(APPLIED_OR_SUGGESTED_COFFEE_CHAT_NOT_FOUND.getMessage())
        );
    }

    @Test
    @DisplayName("특정 Year-Month에 예약된(APPLY, PENDING, APPROVE) 커피챗을 조회한다")
    void getReservedCoffeeChat() {
        // given
        final Mentee mentee1 = memberRepository.save(MENTEE_1.toDomain());
        final Mentee mentee2 = memberRepository.save(MENTEE_2.toDomain());
        final Mentee mentee3 = memberRepository.save(MENTEE_3.toDomain());
        final Mentee mentee4 = memberRepository.save(MENTEE_4.toDomain());
        final Mentee mentee5 = memberRepository.save(MENTEE_5.toDomain());
        final Mentee mentee6 = memberRepository.save(MENTEE_6.toDomain());
        final Mentee mentee7 = memberRepository.save(MENTEE_7.toDomain());
        final Mentee mentee8 = memberRepository.save(MENTEE_8.toDomain());
        final Mentee mentee9 = memberRepository.save(MENTEE_9.toDomain());
        final Mentee mentee10 = memberRepository.save(MENTEE_10.toDomain());

        /**
         * Mentor
         * -> mentee1 제안 + 1차 수락 (2024-02-05) = PENTING
         * -> mentee2 제안 = APPLY
         * -> mentee3 제안 + 거절 = REJECT
         * -> mentee4 신청 (2024-02-19) = APPLY
         * -> mentee5 신청 (2024-03-04) + 수락 = APPROVE
         * -> mentee6 제안 + 1차 수락 (2024-03-15) + 최종 수락 = APPROVE
         * -> mentee7 제안 + 1차 수락 (2024-04-01) + 거절 = REJECT
         * -> mentee8 신청 (2024-04-05) = APPLY
         * -> mentee9 신청 (2024-04-17) + 수락 = APPROVE
         * -> mentee10 제안 + 1차 수락 (2024-04-10) = PENDING
         */
        final Mentor mentor = memberRepository.save(MENTOR_1.toDomain());

        final CoffeeChat coffeeChat1 = suggestAndPending(
                mentor,
                mentee1,
                new Reservation(LocalDateTime.of(2024, 2, 5, 18, 0)),
                new Reservation(LocalDateTime.of(2024, 2, 5, 18, 30))
        );
        final CoffeeChat coffeeChat2 = suggest(mentor, mentee2);
        final CoffeeChat coffeeChat3 = suggestAndReject(mentor, mentee3);
        final CoffeeChat coffeeChat4 = apply(
                mentee4,
                mentor,
                new Reservation(LocalDateTime.of(2024, 2, 19, 18, 0)),
                new Reservation(LocalDateTime.of(2024, 2, 19, 18, 30))
        );
        final CoffeeChat coffeeChat5 = applyAndApprove(
                mentee5,
                mentor,
                new Reservation(LocalDateTime.of(2024, 3, 4, 18, 0)),
                new Reservation(LocalDateTime.of(2024, 3, 4, 18, 30))
        );
        final CoffeeChat coffeeChat6 = suggestAndPendingAndApprove(
                mentor,
                mentee6,
                new Reservation(LocalDateTime.of(2024, 3, 15, 18, 0)),
                new Reservation(LocalDateTime.of(2024, 3, 15, 18, 30))
        );
        final CoffeeChat coffeeChat7 = suggestAndPendingAndReject(
                mentor,
                mentee7,
                new Reservation(LocalDateTime.of(2024, 4, 1, 18, 0)),
                new Reservation(LocalDateTime.of(2024, 4, 1, 18, 30))
        );
        final CoffeeChat coffeeChat8 = apply(
                mentee8,
                mentor,
                new Reservation(LocalDateTime.of(2024, 4, 5, 19, 0)),
                new Reservation(LocalDateTime.of(2024, 4, 5, 19, 30))
        );
        final CoffeeChat coffeeChat9 = applyAndApprove(
                mentee9,
                mentor,
                new Reservation(LocalDateTime.of(2024, 4, 17, 20, 0)),
                new Reservation(LocalDateTime.of(2024, 4, 17, 20, 30))
        );
        final CoffeeChat coffeeChat10 = suggestAndPending(
                mentor,
                mentee10,
                new Reservation(LocalDateTime.of(2024, 4, 10, 21, 0)),
                new Reservation(LocalDateTime.of(2024, 4, 10, 21, 30))
        );

        // when
        final List<CoffeeChat> result1 = sut.getReservedCoffeeChat(mentor.getId(), 2024, 1);
        final List<CoffeeChat> result2 = sut.getReservedCoffeeChat(mentor.getId(), 2024, 2);
        final List<CoffeeChat> result3 = sut.getReservedCoffeeChat(mentor.getId(), 2024, 3);
        final List<CoffeeChat> result4 = sut.getReservedCoffeeChat(mentor.getId(), 2024, 4);
        final List<CoffeeChat> result5 = sut.getReservedCoffeeChat(mentor.getId(), 2024, 5);

        // then
        assertAll(
                () -> assertThat(result1).isEmpty(),
                () -> assertThat(result2).containsExactly(coffeeChat1, coffeeChat4),
                () -> assertThat(result3).containsExactly(coffeeChat5, coffeeChat6),
                () -> assertThat(result4).containsExactly(coffeeChat8, coffeeChat10, coffeeChat9),
                () -> assertThat(result5).isEmpty()
        );
    }

    private Mentee createMentee(final MenteeFixture fixture) {
        return memberRepository.save(fixture.toDomain());
    }

    private Mentor createMentor(final MentorFixture fixture) {
        return memberRepository.save(fixture.toDomain());
    }

    private CoffeeChat suggest(final Mentor mentor, final Mentee mentee) {
        return sut.save(CoffeeChat.suggestCoffeeChat(mentor, mentee, applyReason));
    }

    private CoffeeChat suggestAndPending(final Mentor mentor, final Mentee mentee, final Reservation start, final Reservation end) {
        final CoffeeChat coffeeChat = sut.save(CoffeeChat.suggestCoffeeChat(mentor, mentee, applyReason));
        coffeeChat.pendingFromMentorSuggest(question, start, end);
        return coffeeChat;
    }

    private CoffeeChat suggestAndPendingAndApprove(final Mentor mentor, final Mentee mentee, final Reservation start, final Reservation end) {
        final CoffeeChat coffeeChat = sut.save(CoffeeChat.suggestCoffeeChat(mentor, mentee, applyReason));
        coffeeChat.pendingFromMentorSuggest(question, start, end);
        coffeeChat.approvePendingCoffeeChat(StrategyFixture.ZOOM_LINK.toDomain());
        return coffeeChat;
    }

    private CoffeeChat suggestAndPendingAndReject(final Mentor mentor, final Mentee mentee, final Reservation start, final Reservation end) {
        final CoffeeChat coffeeChat = sut.save(CoffeeChat.suggestCoffeeChat(mentor, mentee, applyReason));
        coffeeChat.pendingFromMentorSuggest(question, start, end);
        coffeeChat.rejectPendingCoffeeChat(rejectReason);
        return coffeeChat;
    }

    private CoffeeChat suggestAndReject(final Mentor mentor, final Mentee mentee) {
        final CoffeeChat coffeeChat = sut.save(CoffeeChat.suggestCoffeeChat(mentor, mentee, applyReason));
        coffeeChat.rejectFromMentorSuggest(rejectReason);
        return coffeeChat;
    }

    private CoffeeChat apply(final Mentee mentee, final Mentor mentor, final Reservation start, final Reservation end) {
        return sut.save(CoffeeChat.applyCoffeeChat(mentee, mentor, applyReason, start, end));
    }

    private CoffeeChat applyAndApprove(final Mentee mentee, final Mentor mentor, final Reservation start, final Reservation end) {
        final CoffeeChat coffeeChat = sut.save(CoffeeChat.applyCoffeeChat(mentee, mentor, applyReason, start, end));
        coffeeChat.approveFromMenteeApply(StrategyFixture.ZOOM_LINK.toDomain());
        return coffeeChat;
    }
}
