package com.koddy.server.member.domain.repository.query;

import com.koddy.server.coffeechat.domain.model.CoffeeChat;
import com.koddy.server.coffeechat.domain.repository.CoffeeChatRepository;
import com.koddy.server.common.RepositoryTest;
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

import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Import(MenteeMainSearchRepositoryImpl.class)
@DisplayName("Member -> MenteeMainSearchRepository [fetchSuggestedMentors] 테스트")
class MenteeMainSearchRepositoryFetchSuggestedMentorsTest extends RepositoryTest {
    @Autowired
    private MenteeMainSearchRepositoryImpl sut;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CoffeeChatRepository coffeeChatRepository;

    private Mentee mentee;
    private final Mentor[] mentors = new Mentor[10];

    @BeforeEach
    void setUp() {
        mentee = memberRepository.save(MENTEE_1.toDomain());
        final List<MentorFixture> fixtures = Arrays.stream(MentorFixture.values())
                .limit(10)
                .toList();
        Arrays.setAll(mentors, it -> memberRepository.save(fixtures.get(it).toDomain()));
    }

    @Test
    @DisplayName("멘티 자신에게 커피챗을 제안한 멘토를 limit 개수만큼 최근에 신청한 순서대로 조회한다")
    void findAppliedMentees() {
        // given
        final CoffeeChat coffeeChat0 = suggest(mentors[0], mentee);
        final CoffeeChat coffeeChat1 = suggest(mentors[1], mentee);
        final CoffeeChat coffeeChat2 = suggest(mentors[2], mentee);
        final CoffeeChat coffeeChat3 = suggest(mentors[3], mentee);
        final CoffeeChat coffeeChat4 = suggest(mentors[4], mentee);
        final CoffeeChat coffeeChat5 = suggest(mentors[5], mentee);
        final CoffeeChat coffeeChat6 = suggest(mentors[6], mentee);
        final CoffeeChat coffeeChat7 = suggest(mentors[7], mentee);
        final CoffeeChat coffeeChat8 = suggest(mentors[8], mentee);
        final CoffeeChat coffeeChat9 = suggest(mentors[9], mentee);

        /* limit별 조회 */
        final List<Mentor> result1 = sut.fetchSuggestedMentors(mentee.getId(), 3);
        final List<Mentor> result2 = sut.fetchSuggestedMentors(mentee.getId(), 5);
        final List<Mentor> result3 = sut.fetchSuggestedMentors(mentee.getId(), 7);
        final List<Mentor> result4 = sut.fetchSuggestedMentors(mentee.getId(), 10);

        assertAll(
                () -> assertThat(result1).containsExactly(mentors[9], mentors[8], mentors[7]),
                () -> assertThat(result2).containsExactly(mentors[9], mentors[8], mentors[7], mentors[6], mentors[5]),
                () -> assertThat(result3).containsExactly(mentors[9], mentors[8], mentors[7], mentors[6], mentors[5], mentors[4], mentors[3]),
                () -> assertThat(result4).containsExactly(mentors[9], mentors[8], mentors[7], mentors[6], mentors[5], mentors[4], mentors[3], mentors[2], mentors[1], mentors[0])
        );

        /* cancel 후 limit별 조회 */
        coffeeChat3.cancel();
        coffeeChat5.cancel();
        coffeeChat7.cancel();
        coffeeChat9.cancel();

        final List<Mentor> result5 = sut.fetchSuggestedMentors(mentee.getId(), 3);
        final List<Mentor> result6 = sut.fetchSuggestedMentors(mentee.getId(), 5);
        final List<Mentor> result7 = sut.fetchSuggestedMentors(mentee.getId(), 7);
        final List<Mentor> result8 = sut.fetchSuggestedMentors(mentee.getId(), 10);

        assertAll(
                () -> assertThat(result5).containsExactly(mentors[8], mentors[6], mentors[4]),
                () -> assertThat(result6).containsExactly(mentors[8], mentors[6], mentors[4], mentors[2], mentors[1]),
                () -> assertThat(result7).containsExactly(mentors[8], mentors[6], mentors[4], mentors[2], mentors[1], mentors[0]),
                () -> assertThat(result8).containsExactly(mentors[8], mentors[6], mentors[4], mentors[2], mentors[1], mentors[0])
        );
    }

    private CoffeeChat suggest(final Mentor mentor, final Mentee mentee) {
        return coffeeChatRepository.save(CoffeeChat.suggestCoffeeChat(mentor, mentee, "신청.."));
    }
}
