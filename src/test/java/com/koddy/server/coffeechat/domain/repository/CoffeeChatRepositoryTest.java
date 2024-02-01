package com.koddy.server.coffeechat.domain.repository;

import com.koddy.server.coffeechat.domain.model.CoffeeChat;
import com.koddy.server.coffeechat.domain.model.Reservation;
import com.koddy.server.coffeechat.exception.CoffeeChatException;
import com.koddy.server.common.RepositoryTest;
import com.koddy.server.common.fixture.CoffeeChatFixture.MenteeFlow;
import com.koddy.server.common.fixture.CoffeeChatFixture.MentorFlow;
import com.koddy.server.common.fixture.StrategyFixture;
import com.koddy.server.member.domain.model.mentee.Mentee;
import com.koddy.server.member.domain.model.mentor.Mentor;
import com.koddy.server.member.domain.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.APPLY;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.APPROVE;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.PENDING;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.SUGGEST;
import static com.koddy.server.coffeechat.exception.CoffeeChatExceptionCode.APPLIED_OR_SUGGESTED_COFFEE_CHAT_NOT_FOUND;
import static com.koddy.server.common.fixture.CoffeeChatFixture.월요일_1주차_20_00_시작;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_1;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_11;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_12;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_13;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_14;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_15;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_16;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_17;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_18;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_19;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_20;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_11;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("CoffeeChat -> CoffeeChatRepository 테스트")
class CoffeeChatRepositoryTest extends RepositoryTest {
    @Autowired
    private CoffeeChatRepository sut;

    @Autowired
    private MemberRepository memberRepository;

    private Mentor mentor;
    private Mentee mentee;

    @BeforeEach
    void setUp() {
        mentor = memberRepository.save(MENTOR_1.toDomain());
        mentee = memberRepository.save(MENTEE_1.toDomain());
    }

    @Test
    @DisplayName("상태에 따른 CoffeeChat을 조회한다")
    void findByIdAndStatus() {
        // given
        final CoffeeChat coffeeChatA = sut.save(MenteeFlow.apply(월요일_1주차_20_00_시작, mentee, mentor));
        final CoffeeChat coffeeChatB = sut.save(MentorFlow.suggest(mentor, mentee));

        /* 1차 조회 */
        assertAll(
                () -> assertThat(sut.findByIdAndStatus(coffeeChatA.getId(), APPLY)).isPresent(),
                () -> assertThat(sut.findByIdAndStatus(coffeeChatB.getId(), SUGGEST)).isPresent()
        );

        /* coffeeChatA 수락 */
        coffeeChatA.approveFromMenteeApply(StrategyFixture.KAKAO_ID.toDomain());
        assertAll(
                () -> assertThat(sut.findByIdAndStatus(coffeeChatA.getId(), APPROVE)).isPresent(),
                () -> assertThat(sut.findByIdAndStatus(coffeeChatB.getId(), SUGGEST)).isPresent()
        );

        /* coffeeChatB 1차 수락 */
        coffeeChatB.pendingFromMentorSuggest(
                "질문..",
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
    @DisplayName("멘티가 신청 or 멘토가 제안한 커피챗을 조회한다")
    void getAppliedOrSuggestedCoffeeChat() {
        // given
        final CoffeeChat appliedCoffeeChat = sut.save(MenteeFlow.apply(월요일_1주차_20_00_시작, mentee, mentor));
        final CoffeeChat suggestedCoffeeChat = sut.save(MentorFlow.suggest(mentor, mentee));

        // when - then
        assertAll(
                // 멘티가 신청한 커피챗
                () -> assertThat(sut.getAppliedOrSuggestedCoffeeChat(appliedCoffeeChat.getId(), mentee.getId())).isEqualTo(appliedCoffeeChat),
                () -> assertThatThrownBy(() -> sut.getAppliedOrSuggestedCoffeeChat(appliedCoffeeChat.getId(), mentor.getId()))
                        .isInstanceOf(CoffeeChatException.class)
                        .hasMessage(APPLIED_OR_SUGGESTED_COFFEE_CHAT_NOT_FOUND.getMessage()),

                // 멘토가 제안한 커피챗
                () -> assertThat(sut.getAppliedOrSuggestedCoffeeChat(suggestedCoffeeChat.getId(), mentor.getId())).isEqualTo(suggestedCoffeeChat),
                () -> assertThatThrownBy(() -> sut.getAppliedOrSuggestedCoffeeChat(suggestedCoffeeChat.getId(), mentee.getId()))
                        .isInstanceOf(CoffeeChatException.class)
                        .hasMessage(APPLIED_OR_SUGGESTED_COFFEE_CHAT_NOT_FOUND.getMessage())
        );
    }

    @Test
    @DisplayName("특정 Year-Month에 예약된 커피챗을 조회한다 -> 멘티 신청=[APPLY, APPROVE] & 멘토 제안=[PENDING, APPROVE]")
    void getReservedCoffeeChat() {
        // given
        final Mentee[] mentees = memberRepository.saveAll(List.of(
                MENTEE_11.toDomain(),
                MENTEE_12.toDomain(),
                MENTEE_13.toDomain(),
                MENTEE_14.toDomain(),
                MENTEE_15.toDomain(),
                MENTEE_16.toDomain(),
                MENTEE_17.toDomain(),
                MENTEE_18.toDomain(),
                MENTEE_19.toDomain(),
                MENTEE_20.toDomain()
        )).toArray(Mentee[]::new);

        /**
         * Mentor
         * -> mentees[0] 제안 + 1차 수락 (2024-02-05) = PENTING
         * -> mentees[1] 제안 = SUGGEST
         * -> mentees[2] 제안 + 거절 = REJECT
         * -> mentees[3] 신청 (2024-02-19) = APPLY
         * -> mentees[4] 신청 (2024-03-04) + 수락 = APPROVE
         * -> mentees[5] 제안 + 1차 수락 (2024-03-15) + 최종 수락 = APPROVE
         * -> mentees[6] 제안 + 1차 수락 (2024-04-01) + 거절 = REJECT
         * -> mentees[7] 신청 (2024-04-05) = APPLY
         * -> mentees[8] 신청 (2024-04-17) + 수락 = APPROVE
         * -> mentees[9] 제안 + 1차 수락 (2024-04-10) = PENDING
         */
        final Mentor mentor = memberRepository.save(MENTOR_11.toDomain());

        final LocalDateTime coffeeChat0Start = LocalDateTime.of(2024, 2, 5, 18, 0);
        final LocalDateTime coffeeChat3Start = LocalDateTime.of(2024, 2, 19, 18, 0);
        final LocalDateTime coffeeChat4Start = LocalDateTime.of(2024, 3, 4, 18, 0);
        final LocalDateTime coffeeChat5Start = LocalDateTime.of(2024, 3, 15, 18, 0);
        final LocalDateTime coffeeChat6Start = LocalDateTime.of(2024, 4, 1, 18, 0);
        final LocalDateTime coffeeChat7Start = LocalDateTime.of(2024, 4, 5, 18, 0);
        final LocalDateTime coffeeChat8Start = LocalDateTime.of(2024, 4, 17, 18, 0);
        final LocalDateTime coffeeChat9Start = LocalDateTime.of(2024, 4, 10, 18, 0);

        final CoffeeChat[] coffeeChats = sut.saveAll(List.of(
                MentorFlow.suggestAndPending(coffeeChat0Start, coffeeChat0Start.plusMinutes(30), mentor, mentees[0]),
                MentorFlow.suggest(mentor, mentees[1]),
                MentorFlow.suggestAndReject(mentor, mentees[2]),
                MenteeFlow.apply(coffeeChat3Start, coffeeChat3Start.plusMinutes(30), mentees[3], mentor),
                MenteeFlow.applyAndApprove(coffeeChat4Start, coffeeChat4Start.plusMinutes(30), mentees[4], mentor),
                MentorFlow.suggestAndPending(coffeeChat5Start, coffeeChat5Start.plusMinutes(30), mentor, mentees[5]),
                MentorFlow.suggestAndFinallyReject(coffeeChat6Start, coffeeChat6Start.plusMinutes(30), mentor, mentees[6]),
                MenteeFlow.apply(coffeeChat7Start, coffeeChat7Start.plusMinutes(30), mentees[7], mentor),
                MenteeFlow.applyAndApprove(coffeeChat8Start, coffeeChat8Start.plusMinutes(30), mentees[8], mentor),
                MentorFlow.suggestAndPending(coffeeChat9Start, coffeeChat9Start.plusMinutes(30), mentor, mentees[9])
        )).toArray(CoffeeChat[]::new);

        // when
        final List<CoffeeChat> result1 = sut.getReservedCoffeeChat(mentor.getId(), 2024, 1);
        final List<CoffeeChat> result2 = sut.getReservedCoffeeChat(mentor.getId(), 2024, 2);
        final List<CoffeeChat> result3 = sut.getReservedCoffeeChat(mentor.getId(), 2024, 3);
        final List<CoffeeChat> result4 = sut.getReservedCoffeeChat(mentor.getId(), 2024, 4);
        final List<CoffeeChat> result5 = sut.getReservedCoffeeChat(mentor.getId(), 2024, 5);

        // then
        assertAll(
                () -> assertThat(result1).isEmpty(),
                () -> assertThat(result2).containsExactly(coffeeChats[0], coffeeChats[3]),
                () -> assertThat(result3).containsExactly(coffeeChats[4], coffeeChats[5]),
                () -> assertThat(result4).containsExactly(coffeeChats[7], coffeeChats[9], coffeeChats[8]),
                () -> assertThat(result5).isEmpty()
        );
    }
}
