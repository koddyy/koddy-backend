package com.koddy.server.coffeechat.domain.repository;

import com.koddy.server.coffeechat.domain.model.CoffeeChat;
import com.koddy.server.common.RepositoryTest;
import com.koddy.server.common.fixture.CoffeeChatFixture.MenteeFlow;
import com.koddy.server.member.domain.model.mentee.Mentee;
import com.koddy.server.member.domain.model.mentor.Mentor;
import com.koddy.server.member.domain.repository.MemberRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;

import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.AUTO_CANCEL_FROM_MENTEE_FLOW;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.AUTO_CANCEL_FROM_MENTOR_FLOW;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.CANCEL_FROM_MENTEE_FLOW;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.CANCEL_FROM_MENTOR_FLOW;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTEE_APPLY;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTEE_APPLY_COFFEE_CHAT_COMPLETE;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTEE_PENDING;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTEE_REJECT;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_APPROVE;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_FINALLY_APPROVE;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_FINALLY_CANCEL;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_REJECT;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_SUGGEST;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_SUGGEST_COFFEE_CHAT_COMPLETE;
import static com.koddy.server.common.fixture.CoffeeChatFixture.MentorFlow;
import static com.koddy.server.common.fixture.CoffeeChatFixture.금요일_1주차_20_00_시작;
import static com.koddy.server.common.fixture.CoffeeChatFixture.금요일_2주차_20_00_시작;
import static com.koddy.server.common.fixture.CoffeeChatFixture.수요일_1주차_20_00_시작;
import static com.koddy.server.common.fixture.CoffeeChatFixture.수요일_2주차_20_00_시작;
import static com.koddy.server.common.fixture.CoffeeChatFixture.수요일_3주차_20_00_시작;
import static com.koddy.server.common.fixture.CoffeeChatFixture.수요일_4주차_20_00_시작;
import static com.koddy.server.common.fixture.CoffeeChatFixture.월요일_1주차_20_00_시작;
import static com.koddy.server.common.fixture.CoffeeChatFixture.월요일_2주차_20_00_시작;
import static com.koddy.server.common.fixture.CoffeeChatFixture.월요일_3주차_20_00_시작;
import static com.koddy.server.common.fixture.CoffeeChatFixture.월요일_4주차_20_00_시작;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_1;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_2;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_3;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_4;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_5;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_2;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("CoffeeChat -> CoffeeChatRepository 테스트")
class CoffeeChatRepositoryTest extends RepositoryTest {
    @Autowired
    private CoffeeChatRepository sut;

    @Autowired
    private MemberRepository memberRepository;

    @Test
    @DisplayName("Status에 해당되는 커피챗 ID를 조회한다")
    void findIdsByStatusAndReservationStandard() {
        // given
        final Mentee[] mentees = memberRepository.saveAll(List.of(
                MENTEE_1.toDomain(),
                MENTEE_2.toDomain(),
                MENTEE_3.toDomain(),
                MENTEE_4.toDomain(),
                MENTEE_5.toDomain()
        )).toArray(Mentee[]::new);
        final Mentor[] mentors = memberRepository.saveAll(List.of(
                MENTOR_1.toDomain(),
                MENTOR_2.toDomain()
        )).toArray(Mentor[]::new);
        final CoffeeChat[] coffeeChats = sut.saveAll(List.of(
                MenteeFlow.apply(월요일_1주차_20_00_시작, mentees[0], mentors[0]),
                MenteeFlow.apply(월요일_2주차_20_00_시작, mentees[1], mentors[0]),
                MenteeFlow.applyAndReject(월요일_3주차_20_00_시작, mentees[2], mentors[0]),
                MenteeFlow.applyAndCancel(월요일_4주차_20_00_시작, mentees[3], mentors[0]),
                MenteeFlow.applyAndApprove(수요일_1주차_20_00_시작, mentees[4], mentors[0]),
                MentorFlow.suggest(mentors[1], mentees[0]),
                MentorFlow.suggest(mentors[1], mentees[1]),
                MentorFlow.suggestAndPending(수요일_2주차_20_00_시작, mentors[1], mentees[2]),
                MentorFlow.suggestAndPending(수요일_3주차_20_00_시작, mentors[1], mentees[3]),
                MentorFlow.suggestAndFinallyApprove(수요일_4주차_20_00_시작, mentors[1], mentees[4])
        )).toArray(CoffeeChat[]::new);
        final LocalDateTime now = LocalDateTime.now();

        // when - then
        assertAll(
                () -> assertThat(sut.findIdsByStatusAndReservationStandard(MENTEE_APPLY, 월요일_1주차_20_00_시작.getStart()))
                        .containsExactlyInAnyOrder(coffeeChats[0].getId()),
                () -> assertThat(sut.findIdsByStatusAndReservationStandard(MENTEE_APPLY, 월요일_2주차_20_00_시작.getStart()))
                        .containsExactlyInAnyOrder(coffeeChats[0].getId(), coffeeChats[1].getId()),
                () -> assertThat(sut.findIdsByStatusAndReservationStandard(MENTOR_REJECT, 월요일_3주차_20_00_시작.getStart()))
                        .containsExactlyInAnyOrder(coffeeChats[2].getId()),
                () -> assertThat(sut.findIdsByStatusAndReservationStandard(MENTOR_APPROVE, 수요일_1주차_20_00_시작.getStart()))
                        .containsExactlyInAnyOrder(coffeeChats[4].getId()),
                () -> assertThat(sut.findIdsByStatusAndReservationStandard(MENTEE_APPLY_COFFEE_CHAT_COMPLETE, now)).isEmpty(),
                () -> assertThat(sut.findIdsByStatusAndReservationStandard(CANCEL_FROM_MENTEE_FLOW, 월요일_4주차_20_00_시작.getStart()))
                        .containsExactlyInAnyOrder(coffeeChats[3].getId()),
                () -> assertThat(sut.findIdsByStatusAndReservationStandard(AUTO_CANCEL_FROM_MENTEE_FLOW, now)).isEmpty(),

                () -> assertThat(sut.findIdsByStatusAndReservationStandard(MENTOR_SUGGEST, now)).isEmpty(),
                () -> assertThat(sut.findIdsByStatusAndReservationStandard(MENTEE_REJECT, now)).isEmpty(),
                () -> assertThat(sut.findIdsByStatusAndReservationStandard(MENTEE_PENDING, 수요일_2주차_20_00_시작.getStart()))
                        .containsExactlyInAnyOrder(coffeeChats[7].getId()),
                () -> assertThat(sut.findIdsByStatusAndReservationStandard(MENTEE_PENDING, 수요일_3주차_20_00_시작.getStart()))
                        .containsExactlyInAnyOrder(coffeeChats[7].getId(), coffeeChats[8].getId()),
                () -> assertThat(sut.findIdsByStatusAndReservationStandard(MENTOR_FINALLY_CANCEL, now)).isEmpty(),
                () -> assertThat(sut.findIdsByStatusAndReservationStandard(MENTOR_FINALLY_APPROVE, 수요일_4주차_20_00_시작.getStart()))
                        .containsExactlyInAnyOrder(coffeeChats[9].getId()),
                () -> assertThat(sut.findIdsByStatusAndReservationStandard(MENTOR_SUGGEST_COFFEE_CHAT_COMPLETE, now)).isEmpty(),
                () -> assertThat(sut.findIdsByStatusAndReservationStandard(CANCEL_FROM_MENTOR_FLOW, now)).isEmpty(),
                () -> assertThat(sut.findIdsByStatusAndReservationStandard(AUTO_CANCEL_FROM_MENTOR_FLOW, now)).isEmpty()
        );
    }

    @Test
    @DisplayName("특정 커피챗들에 대해서 Status를 일괄 업데이트한다")
    void updateStatusInBatch() {
        // given
        final Mentee[] mentees = memberRepository.saveAll(List.of(
                MENTEE_1.toDomain(),
                MENTEE_2.toDomain(),
                MENTEE_3.toDomain(),
                MENTEE_4.toDomain(),
                MENTEE_5.toDomain()
        )).toArray(Mentee[]::new);
        final Mentor[] mentors = memberRepository.saveAll(List.of(
                MENTOR_1.toDomain(),
                MENTOR_2.toDomain()
        )).toArray(Mentor[]::new);
        final CoffeeChat[] coffeeChats = sut.saveAll(List.of(
                MenteeFlow.apply(월요일_1주차_20_00_시작, mentees[0], mentors[0]),
                MenteeFlow.apply(월요일_2주차_20_00_시작, mentees[1], mentors[0]),
                MenteeFlow.apply(월요일_3주차_20_00_시작, mentees[2], mentors[0]),
                MenteeFlow.applyAndApprove(월요일_4주차_20_00_시작, mentees[3], mentors[0]),
                MenteeFlow.applyAndApprove(수요일_1주차_20_00_시작, mentees[4], mentors[0]),
                MentorFlow.suggestAndPending(수요일_2주차_20_00_시작, mentors[1], mentees[0]),
                MentorFlow.suggestAndPending(수요일_3주차_20_00_시작, mentors[1], mentees[1]),
                MentorFlow.suggestAndPending(수요일_4주차_20_00_시작, mentors[1], mentees[2]),
                MentorFlow.suggestAndFinallyApprove(금요일_1주차_20_00_시작, mentors[1], mentees[3]),
                MentorFlow.suggestAndFinallyApprove(금요일_2주차_20_00_시작, mentors[1], mentees[4])
        )).toArray(CoffeeChat[]::new);

        // when
        sut.updateStatusInBatch(
                List.of(coffeeChats[0].getId(), coffeeChats[1].getId(), coffeeChats[2].getId()),
                AUTO_CANCEL_FROM_MENTEE_FLOW
        );
        sut.updateStatusInBatch(
                List.of(coffeeChats[3].getId(), coffeeChats[4].getId()),
                MENTEE_APPLY_COFFEE_CHAT_COMPLETE
        );
        sut.updateStatusInBatch(
                List.of(coffeeChats[5].getId(), coffeeChats[6].getId(), coffeeChats[7].getId()),
                AUTO_CANCEL_FROM_MENTOR_FLOW
        );
        sut.updateStatusInBatch(
                List.of(coffeeChats[8].getId(), coffeeChats[9].getId()),
                MENTOR_SUGGEST_COFFEE_CHAT_COMPLETE
        );

        // then
        assertAll(
                () -> assertThat(sut.getById(coffeeChats[0].getId()).getStatus()).isEqualTo(AUTO_CANCEL_FROM_MENTEE_FLOW),
                () -> assertThat(sut.getById(coffeeChats[1].getId()).getStatus()).isEqualTo(AUTO_CANCEL_FROM_MENTEE_FLOW),
                () -> assertThat(sut.getById(coffeeChats[2].getId()).getStatus()).isEqualTo(AUTO_CANCEL_FROM_MENTEE_FLOW),
                () -> assertThat(sut.getById(coffeeChats[3].getId()).getStatus()).isEqualTo(MENTEE_APPLY_COFFEE_CHAT_COMPLETE),
                () -> assertThat(sut.getById(coffeeChats[4].getId()).getStatus()).isEqualTo(MENTEE_APPLY_COFFEE_CHAT_COMPLETE),
                () -> assertThat(sut.getById(coffeeChats[5].getId()).getStatus()).isEqualTo(AUTO_CANCEL_FROM_MENTOR_FLOW),
                () -> assertThat(sut.getById(coffeeChats[6].getId()).getStatus()).isEqualTo(AUTO_CANCEL_FROM_MENTOR_FLOW),
                () -> assertThat(sut.getById(coffeeChats[7].getId()).getStatus()).isEqualTo(AUTO_CANCEL_FROM_MENTOR_FLOW),
                () -> assertThat(sut.getById(coffeeChats[8].getId()).getStatus()).isEqualTo(MENTOR_SUGGEST_COFFEE_CHAT_COMPLETE),
                () -> assertThat(sut.getById(coffeeChats[9].getId()).getStatus()).isEqualTo(MENTOR_SUGGEST_COFFEE_CHAT_COMPLETE)
        );
    }

    @Test
    @DisplayName("멘토/멘티 자신들과 연관된 커피챗을 가져온다")
    void findByIdAnd() {
        // given
        final Mentee[] mentees = memberRepository.saveAll(List.of(
                MENTEE_1.toDomain(),
                MENTEE_2.toDomain()
        )).toArray(Mentee[]::new);
        final Mentor[] mentors = memberRepository.saveAll(List.of(
                MENTOR_1.toDomain(),
                MENTOR_2.toDomain()
        )).toArray(Mentor[]::new);

        final CoffeeChat applyToMentor0 = sut.save(MenteeFlow.apply(월요일_1주차_20_00_시작, mentees[0], mentors[0]));
        final CoffeeChat applyToMentor1 = sut.save(MenteeFlow.apply(월요일_1주차_20_00_시작, mentees[1], mentors[1]));

        // when - then
        assertAll(
                () -> assertThat(sut.findByIdAndMentorId(applyToMentor0.getId(), mentors[0].getId())).isPresent(),
                () -> assertThat(sut.findByIdAndMentorId(applyToMentor0.getId(), mentors[1].getId())).isEmpty(),
                () -> assertThat(sut.findByIdAndMentorId(applyToMentor1.getId(), mentors[0].getId())).isEmpty(),
                () -> assertThat(sut.findByIdAndMentorId(applyToMentor1.getId(), mentors[1].getId())).isPresent(),

                () -> assertThat(sut.findByIdAndMenteeId(applyToMentor0.getId(), mentees[0].getId())).isPresent(),
                () -> assertThat(sut.findByIdAndMenteeId(applyToMentor0.getId(), mentees[1].getId())).isEmpty(),
                () -> assertThat(sut.findByIdAndMenteeId(applyToMentor1.getId(), mentees[0].getId())).isEmpty(),
                () -> assertThat(sut.findByIdAndMenteeId(applyToMentor1.getId(), mentees[1].getId())).isPresent()
        );
    }
}
