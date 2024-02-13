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

import java.util.List;

import static com.koddy.server.common.fixture.CoffeeChatFixture.월요일_1주차_20_00_시작;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_1;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_2;
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
