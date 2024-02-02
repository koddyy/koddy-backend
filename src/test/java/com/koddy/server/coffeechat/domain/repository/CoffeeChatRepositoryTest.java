package com.koddy.server.coffeechat.domain.repository;

import com.koddy.server.coffeechat.domain.model.CoffeeChat;
import com.koddy.server.coffeechat.exception.CoffeeChatException;
import com.koddy.server.common.RepositoryTest;
import com.koddy.server.common.fixture.CoffeeChatFixture.MenteeFlow;
import com.koddy.server.common.fixture.CoffeeChatFixture.MentorFlow;
import com.koddy.server.member.domain.model.mentee.Mentee;
import com.koddy.server.member.domain.model.mentor.Mentor;
import com.koddy.server.member.domain.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

import static com.koddy.server.coffeechat.exception.CoffeeChatExceptionCode.APPLIED_COFFEE_CHAT_NOT_FOUND;
import static com.koddy.server.coffeechat.exception.CoffeeChatExceptionCode.APPLIED_OR_SUGGESTED_COFFEE_CHAT_NOT_FOUND;
import static com.koddy.server.coffeechat.exception.CoffeeChatExceptionCode.PENDING_COFFEE_CHAT_NOT_FOUND;
import static com.koddy.server.coffeechat.exception.CoffeeChatExceptionCode.SUGGESTED_COFFEE_CHAT_NOT_FOUND;
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
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_2;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_20;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_3;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_11;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_2;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_3;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("CoffeeChat -> CoffeeChatRepository 테스트")
class CoffeeChatRepositoryTest extends RepositoryTest {
    @Autowired
    private CoffeeChatRepository sut;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("멘토 기준에서 멘티가 신청한 커피챗을 가져온다")
    void getMenteeAppliedCoffeeChat() {
        // given
        final Mentee mentee = memberRepository.save(MENTEE_1.toDomain());
        final Mentor[] mentors = memberRepository.saveAll(List.of(
                MENTOR_1.toDomain(),
                MENTOR_2.toDomain(),
                MENTOR_3.toDomain()
        )).toArray(Mentor[]::new);

        final CoffeeChat applyToMentor0 = sut.save(MenteeFlow.apply(월요일_1주차_20_00_시작, mentee, mentors[0]));
        final CoffeeChat applyToMentor1 = sut.save(MenteeFlow.apply(월요일_1주차_20_00_시작, mentee, mentors[1]));
        final CoffeeChat applyToMentor2 = sut.save(MenteeFlow.apply(월요일_1주차_20_00_시작, mentee, mentors[2]));

        // when - then
        assertAll(
                () -> {
                    assertThat(sut.getMenteeAppliedCoffeeChat(applyToMentor0.getId(), mentors[0].getId())).isEqualTo(applyToMentor0);
                    assertThatThrownBy(() -> sut.getMenteeAppliedCoffeeChat(applyToMentor0.getId(), mentors[1].getId()))
                            .isInstanceOf(CoffeeChatException.class)
                            .hasMessage(APPLIED_COFFEE_CHAT_NOT_FOUND.getMessage());
                    assertThatThrownBy(() -> sut.getMenteeAppliedCoffeeChat(applyToMentor0.getId(), mentors[2].getId()))
                            .isInstanceOf(CoffeeChatException.class)
                            .hasMessage(APPLIED_COFFEE_CHAT_NOT_FOUND.getMessage());
                },
                () -> {
                    assertThat(sut.getMenteeAppliedCoffeeChat(applyToMentor1.getId(), mentors[1].getId())).isEqualTo(applyToMentor1);
                    assertThatThrownBy(() -> sut.getMenteeAppliedCoffeeChat(applyToMentor1.getId(), mentors[0].getId()))
                            .isInstanceOf(CoffeeChatException.class)
                            .hasMessage(APPLIED_COFFEE_CHAT_NOT_FOUND.getMessage());
                    assertThatThrownBy(() -> sut.getMenteeAppliedCoffeeChat(applyToMentor1.getId(), mentors[2].getId()))
                            .isInstanceOf(CoffeeChatException.class)
                            .hasMessage(APPLIED_COFFEE_CHAT_NOT_FOUND.getMessage());
                },
                () -> {
                    assertThat(sut.getMenteeAppliedCoffeeChat(applyToMentor2.getId(), mentors[2].getId())).isEqualTo(applyToMentor2);
                    assertThatThrownBy(() -> sut.getMenteeAppliedCoffeeChat(applyToMentor2.getId(), mentors[0].getId()))
                            .isInstanceOf(CoffeeChatException.class)
                            .hasMessage(APPLIED_COFFEE_CHAT_NOT_FOUND.getMessage());
                    assertThatThrownBy(() -> sut.getMenteeAppliedCoffeeChat(applyToMentor2.getId(), mentors[1].getId()))
                            .isInstanceOf(CoffeeChatException.class)
                            .hasMessage(APPLIED_COFFEE_CHAT_NOT_FOUND.getMessage());
                }
        );
    }

    @Test
    @DisplayName("멘토 기준에서 자신이 제안하고 멘티가 1차 수락한 커피챗을 가져온다")
    void getMenteePendingCoffeeChat() {
        // given
        final Mentor[] mentors = memberRepository.saveAll(List.of(
                MENTOR_1.toDomain(),
                MENTOR_2.toDomain(),
                MENTOR_3.toDomain()
        )).toArray(Mentor[]::new);
        final Mentee mentee = memberRepository.save(MENTEE_1.toDomain());

        final CoffeeChat pendingToMentor0 = sut.save(MentorFlow.suggestAndPending(월요일_1주차_20_00_시작, mentors[0], mentee));
        final CoffeeChat pendingToMentor1 = sut.save(MentorFlow.suggestAndPending(월요일_1주차_20_00_시작, mentors[1], mentee));
        final CoffeeChat pendingToMentor2 = sut.save(MentorFlow.suggestAndPending(월요일_1주차_20_00_시작, mentors[2], mentee));

        // when - then
        assertAll(
                () -> {
                    assertThat(sut.getMenteePendingCoffeeChat(pendingToMentor0.getId(), mentors[0].getId())).isEqualTo(pendingToMentor0);
                    assertThatThrownBy(() -> sut.getMenteePendingCoffeeChat(pendingToMentor0.getId(), mentors[1].getId()))
                            .isInstanceOf(CoffeeChatException.class)
                            .hasMessage(PENDING_COFFEE_CHAT_NOT_FOUND.getMessage());
                    assertThatThrownBy(() -> sut.getMenteePendingCoffeeChat(pendingToMentor0.getId(), mentors[2].getId()))
                            .isInstanceOf(CoffeeChatException.class)
                            .hasMessage(PENDING_COFFEE_CHAT_NOT_FOUND.getMessage());
                },
                () -> {
                    assertThat(sut.getMenteePendingCoffeeChat(pendingToMentor1.getId(), mentors[1].getId())).isEqualTo(pendingToMentor1);
                    assertThatThrownBy(() -> sut.getMenteePendingCoffeeChat(pendingToMentor1.getId(), mentors[0].getId()))
                            .isInstanceOf(CoffeeChatException.class)
                            .hasMessage(PENDING_COFFEE_CHAT_NOT_FOUND.getMessage());
                    assertThatThrownBy(() -> sut.getMenteePendingCoffeeChat(pendingToMentor1.getId(), mentors[2].getId()))
                            .isInstanceOf(CoffeeChatException.class)
                            .hasMessage(PENDING_COFFEE_CHAT_NOT_FOUND.getMessage());
                },
                () -> {
                    assertThat(sut.getMenteePendingCoffeeChat(pendingToMentor2.getId(), mentors[2].getId())).isEqualTo(pendingToMentor2);
                    assertThatThrownBy(() -> sut.getMenteePendingCoffeeChat(pendingToMentor2.getId(), mentors[0].getId()))
                            .isInstanceOf(CoffeeChatException.class)
                            .hasMessage(PENDING_COFFEE_CHAT_NOT_FOUND.getMessage());
                    assertThatThrownBy(() -> sut.getMenteePendingCoffeeChat(pendingToMentor2.getId(), mentors[1].getId()))
                            .isInstanceOf(CoffeeChatException.class)
                            .hasMessage(PENDING_COFFEE_CHAT_NOT_FOUND.getMessage());
                }
        );
    }

    @Test
    @DisplayName("멘티 기준에서 멘토가 제안한 커피챗을 가져온다")
    void getMentorSuggestedCoffeeChat() {
        // given
        final Mentor mentor = memberRepository.save(MENTOR_1.toDomain());
        final Mentee[] mentees = memberRepository.saveAll(List.of(
                MENTEE_1.toDomain(),
                MENTEE_2.toDomain(),
                MENTEE_3.toDomain()
        )).toArray(Mentee[]::new);

        final CoffeeChat suggestToMentee0 = sut.save(MentorFlow.suggest(mentor, mentees[0]));
        final CoffeeChat suggestToMentee1 = sut.save(MentorFlow.suggest(mentor, mentees[1]));
        final CoffeeChat suggestToMentee2 = sut.save(MentorFlow.suggest(mentor, mentees[2]));

        // when - then
        assertAll(
                () -> {
                    assertThat(sut.getMentorSuggestedCoffeeChat(suggestToMentee0.getId(), mentees[0].getId())).isEqualTo(suggestToMentee0);
                    assertThatThrownBy(() -> sut.getMentorSuggestedCoffeeChat(suggestToMentee0.getId(), mentees[1].getId()))
                            .isInstanceOf(CoffeeChatException.class)
                            .hasMessage(SUGGESTED_COFFEE_CHAT_NOT_FOUND.getMessage());
                    assertThatThrownBy(() -> sut.getMentorSuggestedCoffeeChat(suggestToMentee0.getId(), mentees[2].getId()))
                            .isInstanceOf(CoffeeChatException.class)
                            .hasMessage(SUGGESTED_COFFEE_CHAT_NOT_FOUND.getMessage());
                },
                () -> {
                    assertThat(sut.getMentorSuggestedCoffeeChat(suggestToMentee1.getId(), mentees[1].getId())).isEqualTo(suggestToMentee1);
                    assertThatThrownBy(() -> sut.getMentorSuggestedCoffeeChat(suggestToMentee1.getId(), mentees[0].getId()))
                            .isInstanceOf(CoffeeChatException.class)
                            .hasMessage(SUGGESTED_COFFEE_CHAT_NOT_FOUND.getMessage());
                    assertThatThrownBy(() -> sut.getMentorSuggestedCoffeeChat(suggestToMentee1.getId(), mentees[2].getId()))
                            .isInstanceOf(CoffeeChatException.class)
                            .hasMessage(SUGGESTED_COFFEE_CHAT_NOT_FOUND.getMessage());
                },
                () -> {
                    assertThat(sut.getMentorSuggestedCoffeeChat(suggestToMentee2.getId(), mentees[2].getId())).isEqualTo(suggestToMentee2);
                    assertThatThrownBy(() -> sut.getMentorSuggestedCoffeeChat(suggestToMentee2.getId(), mentees[0].getId()))
                            .isInstanceOf(CoffeeChatException.class)
                            .hasMessage(SUGGESTED_COFFEE_CHAT_NOT_FOUND.getMessage());
                    assertThatThrownBy(() -> sut.getMentorSuggestedCoffeeChat(suggestToMentee2.getId(), mentees[1].getId()))
                            .isInstanceOf(CoffeeChatException.class)
                            .hasMessage(SUGGESTED_COFFEE_CHAT_NOT_FOUND.getMessage());
                }
        );
    }

    @Test
    @DisplayName("멘티가 신청 or 멘토가 제안한 커피챗을 조회한다")
    void getAppliedOrSuggestedCoffeeChat() {
        // given
        final Mentor mentor = memberRepository.save(MENTOR_1.toDomain());
        final Mentee mentee = memberRepository.save(MENTEE_1.toDomain());

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
