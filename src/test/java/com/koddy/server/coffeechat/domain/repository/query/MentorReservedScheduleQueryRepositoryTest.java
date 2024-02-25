package com.koddy.server.coffeechat.domain.repository.query;

import com.koddy.server.coffeechat.domain.model.CoffeeChat;
import com.koddy.server.coffeechat.domain.repository.CoffeeChatRepository;
import com.koddy.server.common.RepositoryTest;
import com.koddy.server.common.fixture.CoffeeChatFixture.MenteeFlow;
import com.koddy.server.common.fixture.CoffeeChatFixture.MentorFlow;
import com.koddy.server.member.domain.model.mentee.Mentee;
import com.koddy.server.member.domain.model.mentor.Mentor;
import com.koddy.server.member.domain.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.List;

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
import static org.junit.jupiter.api.Assertions.assertAll;

@Import(MentorReservedScheduleQueryRepositoryImpl.class)
@DisplayName("CoffeeChat -> MentorReservedScheduleQueryRepository 테스트")
class MentorReservedScheduleQueryRepositoryTest extends RepositoryTest {
    @Autowired
    private MentorReservedScheduleQueryRepositoryImpl sut;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CoffeeChatRepository coffeeChatRepository;

    /**
     * Mentor <br>
     * -> mentees[0] 제안 + 1차 수락 (2024-02-05) = PENTING <br>
     * -> mentees[1] 제안 = SUGGEST <br>
     * -> mentees[2] 제안 + 거절 = REJECT <br>
     * -> mentees[3] 신청 (2024-02-19) = APPLY <br>
     * -> mentees[4] 신청 (2024-03-04) + 수락 = APPROVE <br>
     * -> mentees[5] 제안 + 1차 수락 (2024-03-15) + 최종 수락 = APPROVE <br>
     * -> mentees[6] 제안 + 1차 수락 (2024-04-01) + 거절 = REJECT <br>
     * -> mentees[7] 신청 (2024-04-05) = APPLY <br>
     * -> mentees[8] 신청 (2024-04-17) + 수락 = APPROVE <br>
     * -> mentees[9] 제안 + 1차 수락 (2024-04-10) = PENDING
     */
    @Test
    @DisplayName("특정 Year/Month에 예약된 커피챗을 조회한다 -> MenteeFlow=[MENTEE_APPLY, MENTOR_APPROVE] & MentorFlow=[MENTEE_PENDING, MENTOR_FINALLY_APPROVE]")
    void fetchReservedCoffeeChat() {
        // given
        final Mentor mentor = memberRepository.save(MENTOR_1.toDomain());
        final Mentee[] mentees = memberRepository.saveAll(List.of(
                MENTEE_1.toDomain(),
                MENTEE_2.toDomain(),
                MENTEE_3.toDomain(),
                MENTEE_4.toDomain(),
                MENTEE_5.toDomain(),
                MENTEE_6.toDomain(),
                MENTEE_7.toDomain(),
                MENTEE_8.toDomain(),
                MENTEE_9.toDomain(),
                MENTEE_10.toDomain()
        )).toArray(Mentee[]::new);

        final LocalDateTime coffeeChat0Start = LocalDateTime.of(2024, 2, 5, 18, 0);
        final LocalDateTime coffeeChat3Start = LocalDateTime.of(2024, 2, 19, 18, 0);
        final LocalDateTime coffeeChat4Start = LocalDateTime.of(2024, 3, 4, 18, 0);
        final LocalDateTime coffeeChat5Start = LocalDateTime.of(2024, 3, 15, 18, 0);
        final LocalDateTime coffeeChat6Start = LocalDateTime.of(2024, 4, 1, 18, 0);
        final LocalDateTime coffeeChat7Start = LocalDateTime.of(2024, 4, 5, 18, 0);
        final LocalDateTime coffeeChat8Start = LocalDateTime.of(2024, 4, 17, 18, 0);
        final LocalDateTime coffeeChat9Start = LocalDateTime.of(2024, 4, 10, 18, 0);

        final CoffeeChat[] coffeeChats = coffeeChatRepository.saveAll(List.of(
                MentorFlow.suggestAndPending(coffeeChat0Start, coffeeChat0Start.plusMinutes(30), mentor, mentees[0]),
                MentorFlow.suggest(mentor, mentees[1]),
                MentorFlow.suggestAndReject(mentor, mentees[2]),
                MenteeFlow.apply(coffeeChat3Start, coffeeChat3Start.plusMinutes(30), mentees[3], mentor),
                MenteeFlow.applyAndApprove(coffeeChat4Start, coffeeChat4Start.plusMinutes(30), mentees[4], mentor),
                MentorFlow.suggestAndPending(coffeeChat5Start, coffeeChat5Start.plusMinutes(30), mentor, mentees[5]),
                MentorFlow.suggestAndFinallyCancel(coffeeChat6Start, coffeeChat6Start.plusMinutes(30), mentor, mentees[6]),
                MenteeFlow.apply(coffeeChat7Start, coffeeChat7Start.plusMinutes(30), mentees[7], mentor),
                MenteeFlow.applyAndApprove(coffeeChat8Start, coffeeChat8Start.plusMinutes(30), mentees[8], mentor),
                MentorFlow.suggestAndPending(coffeeChat9Start, coffeeChat9Start.plusMinutes(30), mentor, mentees[9])
        )).toArray(CoffeeChat[]::new);

        // when
        final List<CoffeeChat> result1 = sut.fetchReservedCoffeeChat(mentor.getId(), 2024, 1);
        final List<CoffeeChat> result2 = sut.fetchReservedCoffeeChat(mentor.getId(), 2024, 2);
        final List<CoffeeChat> result3 = sut.fetchReservedCoffeeChat(mentor.getId(), 2024, 3);
        final List<CoffeeChat> result4 = sut.fetchReservedCoffeeChat(mentor.getId(), 2024, 4);
        final List<CoffeeChat> result5 = sut.fetchReservedCoffeeChat(mentor.getId(), 2024, 5);

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
