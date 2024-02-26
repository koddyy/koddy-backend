package com.koddy.server.coffeechat.domain.repository.query;

import com.koddy.server.coffeechat.domain.repository.CoffeeChatRepository;
import com.koddy.server.coffeechat.domain.repository.query.response.CoffeeChatCountPerCategory;
import com.koddy.server.common.RepositoryTest;
import com.koddy.server.common.fixture.CoffeeChatFixture.MenteeFlow;
import com.koddy.server.common.fixture.CoffeeChatFixture.MentorFlow;
import com.koddy.server.common.fixture.MenteeFixture;
import com.koddy.server.common.fixture.MentorFixture;
import com.koddy.server.member.domain.model.mentee.Mentee;
import com.koddy.server.member.domain.model.mentor.Mentor;
import com.koddy.server.member.domain.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.util.Arrays;
import java.util.List;

import static com.koddy.server.common.fixture.CoffeeChatFixture.수요일_1주차_20_00_시작;
import static com.koddy.server.common.fixture.CoffeeChatFixture.월요일_1주차_20_00_시작;
import static com.koddy.server.common.fixture.CoffeeChatFixture.월요일_2주차_20_00_시작;
import static com.koddy.server.common.fixture.CoffeeChatFixture.월요일_3주차_20_00_시작;
import static com.koddy.server.common.fixture.CoffeeChatFixture.월요일_4주차_20_00_시작;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Import(CoffeeChatScheduleQueryRepositoryImpl.class)
@DisplayName("CoffeeChat -> CoffeeChatScheduleQueryRepository 테스트")
class CoffeeChatScheduleQueryRepositoryTest extends RepositoryTest {
    @Autowired
    private CoffeeChatScheduleQueryRepositoryImpl sut;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CoffeeChatRepository coffeeChatRepository;

    private final Mentee[] mentees = new Mentee[7];
    private final Mentor[] mentors = new Mentor[7];

    @BeforeEach
    void setUp() {
        final List<MenteeFixture> menteeFixtures = Arrays.stream(MenteeFixture.values())
                .limit(7)
                .toList();
        Arrays.setAll(mentees, it -> memberRepository.save(menteeFixtures.get(it).toDomain()));

        final List<MentorFixture> mentorFixtures = Arrays.stream(MentorFixture.values())
                .limit(7)
                .toList();
        Arrays.setAll(mentors, it -> memberRepository.save(mentorFixtures.get(it).toDomain()));
    }

    @Test
    @DisplayName("멘토의 카테고리별 커피챗 개수를 조회한다")
    void fetchMentorCoffeeChatCountPerCategory() {
        // 대기 = 0, 제안 = 0, 예정 = 0, 지나간 = 0
        final CoffeeChatCountPerCategory result1 = sut.fetchMentorCoffeeChatCountPerCategory(mentors[0].getId());
        assertCoffeeChatCountMatch(result1, List.of(0, 0, 0, 0));

        // 대기 = 0, 제안 = 1, 예정 = 0, 지나간 = 0
        coffeeChatRepository.save(MentorFlow.suggest(mentors[0], mentees[0]));
        final CoffeeChatCountPerCategory result2 = sut.fetchMentorCoffeeChatCountPerCategory(mentors[0].getId());
        assertCoffeeChatCountMatch(result2, List.of(0, 1, 0, 0));

        // 대기 = 0, 제안 = 2, 예정 = 0, 지나간 = 0
        coffeeChatRepository.save(MentorFlow.suggest(mentors[0], mentees[1]));
        final CoffeeChatCountPerCategory result3 = sut.fetchMentorCoffeeChatCountPerCategory(mentors[0].getId());
        assertCoffeeChatCountMatch(result3, List.of(0, 2, 0, 0));

        // 대기 = 1, 제안 = 2, 예정 = 0, 지나간 = 0
        coffeeChatRepository.save(MentorFlow.suggestAndPending(월요일_1주차_20_00_시작, mentors[0], mentees[2]));
        final CoffeeChatCountPerCategory result4 = sut.fetchMentorCoffeeChatCountPerCategory(mentors[0].getId());
        assertCoffeeChatCountMatch(result4, List.of(1, 2, 0, 0));

        // 대기 = 2, 제안 = 2, 예정 = 0, 지나간 = 0
        coffeeChatRepository.save(MenteeFlow.apply(월요일_2주차_20_00_시작, mentees[3], mentors[0]));
        final CoffeeChatCountPerCategory result5 = sut.fetchMentorCoffeeChatCountPerCategory(mentors[0].getId());
        assertCoffeeChatCountMatch(result5, List.of(2, 2, 0, 0));

        // 대기 = 2, 제안 = 2, 예정 = 1, 지나간 = 0
        coffeeChatRepository.save(MenteeFlow.applyAndApprove(월요일_3주차_20_00_시작, mentees[4], mentors[0]));
        final CoffeeChatCountPerCategory result6 = sut.fetchMentorCoffeeChatCountPerCategory(mentors[0].getId());
        assertCoffeeChatCountMatch(result6, List.of(2, 2, 1, 0));

        // 대기 = 2, 제안 = 2, 예정 = 1, 지나간 = 1
        coffeeChatRepository.save(MenteeFlow.applyAndComplete(월요일_4주차_20_00_시작, mentees[5], mentors[0]));
        final CoffeeChatCountPerCategory result7 = sut.fetchMentorCoffeeChatCountPerCategory(mentors[0].getId());
        assertCoffeeChatCountMatch(result7, List.of(2, 2, 1, 1));

        // 대기 = 2, 제안 = 2, 예정 = 2, 지나간 = 1
        coffeeChatRepository.save(MentorFlow.suggestAndFinallyApprove(수요일_1주차_20_00_시작, mentors[0], mentees[6]));
        final CoffeeChatCountPerCategory result8 = sut.fetchMentorCoffeeChatCountPerCategory(mentors[0].getId());
        assertCoffeeChatCountMatch(result8, List.of(2, 2, 2, 1));
    }

    @Test
    @DisplayName("멘티의 카테고리별 커피챗 개수를 조회한다")
    void fetchMenteeCoffeeChatCountPerCategory() {
        // 대기 = 0, 제안 = 0, 예정 = 0, 지나간 = 0
        final CoffeeChatCountPerCategory result1 = sut.fetchMenteeCoffeeChatCountPerCategory(mentees[0].getId());
        assertCoffeeChatCountMatch(result1, List.of(0, 0, 0, 0));

        // 대기 = 1, 제안 = 0, 예정 = 0, 지나간 = 0
        coffeeChatRepository.save(MentorFlow.suggestAndPending(월요일_1주차_20_00_시작, mentors[0], mentees[0]));
        final CoffeeChatCountPerCategory result2 = sut.fetchMenteeCoffeeChatCountPerCategory(mentees[0].getId());
        assertCoffeeChatCountMatch(result2, List.of(1, 0, 0, 0));

        // 대기 = 1, 제안 = 0, 예정 = 1, 지나간 = 0
        coffeeChatRepository.save(MentorFlow.suggestAndFinallyApprove(월요일_2주차_20_00_시작, mentors[1], mentees[0]));
        final CoffeeChatCountPerCategory result3 = sut.fetchMenteeCoffeeChatCountPerCategory(mentees[0].getId());
        assertCoffeeChatCountMatch(result3, List.of(1, 0, 1, 0));

        // 대기 = 2, 제안 = 0, 예정 = 1, 지나간 = 0
        coffeeChatRepository.save(MenteeFlow.apply(월요일_3주차_20_00_시작, mentees[0], mentors[2]));
        final CoffeeChatCountPerCategory result4 = sut.fetchMenteeCoffeeChatCountPerCategory(mentees[0].getId());
        assertCoffeeChatCountMatch(result4, List.of(2, 0, 1, 0));

        // 대기 = 2, 제안 = 0, 예정 = 1, 지나간 = 1
        coffeeChatRepository.save(MenteeFlow.applyAndComplete(월요일_4주차_20_00_시작, mentees[0], mentors[3]));
        final CoffeeChatCountPerCategory result5 = sut.fetchMenteeCoffeeChatCountPerCategory(mentees[0].getId());
        assertCoffeeChatCountMatch(result5, List.of(2, 0, 1, 1));

        // 대기 = 3, 제안 = 0, 예정 = 1, 지나간 = 1
        coffeeChatRepository.save(MenteeFlow.apply(수요일_1주차_20_00_시작, mentees[0], mentors[4]));
        final CoffeeChatCountPerCategory result6 = sut.fetchMenteeCoffeeChatCountPerCategory(mentees[0].getId());
        assertCoffeeChatCountMatch(result6, List.of(3, 0, 1, 1));

        // 대기 = 4, 제안 = 0, 예정 = 1, 지나간 = 1
        coffeeChatRepository.save(MentorFlow.suggestAndPending(수요일_1주차_20_00_시작, mentors[5], mentees[0]));
        final CoffeeChatCountPerCategory result7 = sut.fetchMenteeCoffeeChatCountPerCategory(mentees[0].getId());
        assertCoffeeChatCountMatch(result7, List.of(4, 0, 1, 1));

        // 대기 = 4, 제안 = 0, 예정 = 2, 지나간 = 1
        coffeeChatRepository.save(MenteeFlow.applyAndApprove(월요일_4주차_20_00_시작, mentees[0], mentors[6]));
        final CoffeeChatCountPerCategory result8 = sut.fetchMenteeCoffeeChatCountPerCategory(mentees[0].getId());
        assertCoffeeChatCountMatch(result8, List.of(4, 0, 2, 1));
    }

    private void assertCoffeeChatCountMatch(
            final CoffeeChatCountPerCategory result,
            final List<Integer> counts
    ) {
        assertAll(
                () -> assertThat(result.waiting()).isEqualTo((long) counts.get(0)),
                () -> assertThat(result.suggested()).isEqualTo((long) counts.get(1)),
                () -> assertThat(result.scheduled()).isEqualTo((long) counts.get(2)),
                () -> assertThat(result.passed()).isEqualTo((long) counts.get(3))
        );
    }
}
