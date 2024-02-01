package com.koddy.server.member.domain.repository.query;

import com.koddy.server.coffeechat.domain.model.CoffeeChat;
import com.koddy.server.coffeechat.domain.repository.CoffeeChatRepository;
import com.koddy.server.common.RepositoryTest;
import com.koddy.server.common.fixture.CoffeeChatFixture.MenteeFlow;
import com.koddy.server.common.fixture.MenteeFixture;
import com.koddy.server.member.domain.model.mentee.Mentee;
import com.koddy.server.member.domain.model.mentor.Mentor;
import com.koddy.server.member.domain.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;

import java.util.Arrays;
import java.util.List;

import static com.koddy.server.common.fixture.CoffeeChatFixture.수요일_1주차_20_00_시작;
import static com.koddy.server.common.fixture.CoffeeChatFixture.수요일_1주차_21_00_시작;
import static com.koddy.server.common.fixture.CoffeeChatFixture.월요일_1주차_20_00_시작;
import static com.koddy.server.common.fixture.CoffeeChatFixture.월요일_1주차_21_00_시작;
import static com.koddy.server.common.fixture.CoffeeChatFixture.월요일_2주차_20_00_시작;
import static com.koddy.server.common.fixture.CoffeeChatFixture.월요일_2주차_21_00_시작;
import static com.koddy.server.common.fixture.CoffeeChatFixture.월요일_3주차_20_00_시작;
import static com.koddy.server.common.fixture.CoffeeChatFixture.월요일_3주차_21_00_시작;
import static com.koddy.server.common.fixture.CoffeeChatFixture.월요일_4주차_20_00_시작;
import static com.koddy.server.common.fixture.CoffeeChatFixture.월요일_4주차_21_00_시작;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Import(MentorMainSearchRepositoryImpl.class)
@DisplayName("Member -> MentorMainSearchRepository [fetchAppliedMentees] 테스트")
class MentorMainSearchRepositoryFetchAppliedMenteesTest extends RepositoryTest {
    @Autowired
    private MentorMainSearchRepositoryImpl sut;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CoffeeChatRepository coffeeChatRepository;

    private Mentor mentor;
    private final Mentee[] mentees = new Mentee[10];

    @BeforeEach
    void setUp() {
        mentor = memberRepository.save(MENTOR_1.toDomain());
        final List<MenteeFixture> fixtures = Arrays.stream(MenteeFixture.values())
                .limit(10)
                .toList();
        Arrays.setAll(mentees, it -> memberRepository.save(fixtures.get(it).toDomain()));
    }

    @Test
    @DisplayName("멘토 자신에게 커피챗을 신청한 멘티를 limit 개수만큼 최근에 신청한 순서대로 조회한다")
    void findAppliedMentees() {
        // given
        final CoffeeChat coffeeChat0 = coffeeChatRepository.save(MenteeFlow.apply(월요일_1주차_20_00_시작, mentees[0], mentor));
        final CoffeeChat coffeeChat1 = coffeeChatRepository.save(MenteeFlow.apply(월요일_1주차_21_00_시작, mentees[1], mentor));
        final CoffeeChat coffeeChat2 = coffeeChatRepository.save(MenteeFlow.apply(월요일_2주차_20_00_시작, mentees[2], mentor));
        final CoffeeChat coffeeChat3 = coffeeChatRepository.save(MenteeFlow.apply(월요일_2주차_21_00_시작, mentees[3], mentor));
        final CoffeeChat coffeeChat4 = coffeeChatRepository.save(MenteeFlow.apply(월요일_3주차_20_00_시작, mentees[4], mentor));
        final CoffeeChat coffeeChat5 = coffeeChatRepository.save(MenteeFlow.apply(월요일_3주차_21_00_시작, mentees[5], mentor));
        final CoffeeChat coffeeChat6 = coffeeChatRepository.save(MenteeFlow.apply(월요일_4주차_20_00_시작, mentees[6], mentor));
        final CoffeeChat coffeeChat7 = coffeeChatRepository.save(MenteeFlow.apply(월요일_4주차_21_00_시작, mentees[7], mentor));
        final CoffeeChat coffeeChat8 = coffeeChatRepository.save(MenteeFlow.apply(수요일_1주차_20_00_시작, mentees[8], mentor));
        final CoffeeChat coffeeChat9 = coffeeChatRepository.save(MenteeFlow.apply(수요일_1주차_21_00_시작, mentees[9], mentor));

        /* limit별 조회 */
        final Page<Mentee> result1 = sut.fetchAppliedMentees(mentor.getId(), 3);
        final Page<Mentee> result2 = sut.fetchAppliedMentees(mentor.getId(), 5);
        final Page<Mentee> result3 = sut.fetchAppliedMentees(mentor.getId(), 7);
        final Page<Mentee> result4 = sut.fetchAppliedMentees(mentor.getId(), 10);

        assertAll(
                () -> assertThat(result1.hasNext()).isTrue(),
                () -> assertThat(result1.getTotalElements()).isEqualTo(10),
                () -> assertThat(result1.getContent()).containsExactly(mentees[9], mentees[8], mentees[7]),
                () -> assertThat(result2.hasNext()).isTrue(),
                () -> assertThat(result2.getTotalElements()).isEqualTo(10),
                () -> assertThat(result2.getContent()).containsExactly(mentees[9], mentees[8], mentees[7], mentees[6], mentees[5]),
                () -> assertThat(result3.hasNext()).isTrue(),
                () -> assertThat(result3.getTotalElements()).isEqualTo(10),
                () -> assertThat(result3.getContent()).containsExactly(
                        mentees[9], mentees[8], mentees[7], mentees[6],
                        mentees[5], mentees[4], mentees[3]
                ),
                () -> assertThat(result4.hasNext()).isFalse(),
                () -> assertThat(result4.getTotalElements()).isEqualTo(10),
                () -> assertThat(result4.getContent()).containsExactly(
                        mentees[9], mentees[8], mentees[7], mentees[6], mentees[5],
                        mentees[4], mentees[3], mentees[2], mentees[1], mentees[0]
                )
        );

        /* cancel 후 limit별 조회 */
        coffeeChat3.cancel();
        coffeeChat5.cancel();
        coffeeChat7.cancel();
        coffeeChat9.cancel();

        final Page<Mentee> result5 = sut.fetchAppliedMentees(mentor.getId(), 3);
        final Page<Mentee> result6 = sut.fetchAppliedMentees(mentor.getId(), 5);
        final Page<Mentee> result7 = sut.fetchAppliedMentees(mentor.getId(), 7);
        final Page<Mentee> result8 = sut.fetchAppliedMentees(mentor.getId(), 10);

        assertAll(
                () -> assertThat(result5.hasNext()).isTrue(),
                () -> assertThat(result5.getTotalElements()).isEqualTo(6),
                () -> assertThat(result5.getContent()).containsExactly(mentees[8], mentees[6], mentees[4]),
                () -> assertThat(result6.hasNext()).isTrue(),
                () -> assertThat(result6.getTotalElements()).isEqualTo(6),
                () -> assertThat(result6.getContent()).containsExactly(mentees[8], mentees[6], mentees[4], mentees[2], mentees[1]),
                () -> assertThat(result7.hasNext()).isFalse(),
                () -> assertThat(result7.getTotalElements()).isEqualTo(6),
                () -> assertThat(result7.getContent()).containsExactly(mentees[8], mentees[6], mentees[4], mentees[2], mentees[1], mentees[0]),
                () -> assertThat(result8.hasNext()).isFalse(),
                () -> assertThat(result8.getTotalElements()).isEqualTo(6),
                () -> assertThat(result8.getContent()).containsExactly(mentees[8], mentees[6], mentees[4], mentees[2], mentees[1], mentees[0])
        );
    }
}
