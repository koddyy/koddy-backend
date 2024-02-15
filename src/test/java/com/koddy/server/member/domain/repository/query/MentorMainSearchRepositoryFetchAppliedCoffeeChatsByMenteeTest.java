package com.koddy.server.member.domain.repository.query;

import com.koddy.server.coffeechat.domain.model.CoffeeChat;
import com.koddy.server.coffeechat.domain.repository.CoffeeChatRepository;
import com.koddy.server.common.RepositoryTest;
import com.koddy.server.common.fixture.CoffeeChatFixture.MenteeFlow;
import com.koddy.server.common.fixture.MenteeFixture;
import com.koddy.server.member.domain.model.mentee.Mentee;
import com.koddy.server.member.domain.model.mentor.Mentor;
import com.koddy.server.member.domain.repository.MemberRepository;
import com.koddy.server.member.domain.repository.query.response.AppliedCoffeeChatsByMentee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;

import java.util.Arrays;
import java.util.List;

import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTEE_CANCEL;
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
@DisplayName("Member -> MentorMainSearchRepository [fetchAppliedCoffeeChatsByMentee] 테스트")
class MentorMainSearchRepositoryFetchAppliedCoffeeChatsByMenteeTest extends RepositoryTest {
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
    void fetchAppliedCoffeeChatsByMentee() {
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
        final Page<AppliedCoffeeChatsByMentee> result1 = sut.fetchAppliedMentees(mentor.getId(), 3);
        final Page<AppliedCoffeeChatsByMentee> result2 = sut.fetchAppliedMentees(mentor.getId(), 5);
        final Page<AppliedCoffeeChatsByMentee> result3 = sut.fetchAppliedMentees(mentor.getId(), 7);
        final Page<AppliedCoffeeChatsByMentee> result4 = sut.fetchAppliedMentees(mentor.getId(), 10);

        assertAll(
                () -> assertThat(result1.hasNext()).isTrue(),
                () -> assertThat(result1.getTotalElements()).isEqualTo(10),
                () -> assertThat(result1.getContent())
                        .map(AppliedCoffeeChatsByMentee::coffeeChatId)
                        .containsExactly(coffeeChat9.getId(), coffeeChat8.getId(), coffeeChat7.getId()),
                () -> assertThat(result1.getContent())
                        .map(AppliedCoffeeChatsByMentee::menteeId)
                        .containsExactly(mentees[9].getId(), mentees[8].getId(), mentees[7].getId()),
                () -> assertThat(result2.hasNext()).isTrue(),
                () -> assertThat(result2.getTotalElements()).isEqualTo(10),
                () -> assertThat(result2.getContent())
                        .map(AppliedCoffeeChatsByMentee::coffeeChatId)
                        .containsExactly(
                                coffeeChat9.getId(), coffeeChat8.getId(), coffeeChat7.getId(),
                                coffeeChat6.getId(), coffeeChat5.getId()
                        ),
                () -> assertThat(result2.getContent())
                        .map(AppliedCoffeeChatsByMentee::menteeId)
                        .containsExactly(
                                mentees[9].getId(), mentees[8].getId(), mentees[7].getId(),
                                mentees[6].getId(), mentees[5].getId()
                        ),
                () -> assertThat(result3.hasNext()).isTrue(),
                () -> assertThat(result3.getTotalElements()).isEqualTo(10),
                () -> assertThat(result3.getContent())
                        .map(AppliedCoffeeChatsByMentee::coffeeChatId)
                        .containsExactly(
                                coffeeChat9.getId(), coffeeChat8.getId(), coffeeChat7.getId(), coffeeChat6.getId(),
                                coffeeChat5.getId(), coffeeChat4.getId(), coffeeChat3.getId()
                        ),
                () -> assertThat(result3.getContent())
                        .map(AppliedCoffeeChatsByMentee::menteeId)
                        .containsExactly(
                                mentees[9].getId(), mentees[8].getId(), mentees[7].getId(), mentees[6].getId(),
                                mentees[5].getId(), mentees[4].getId(), mentees[3].getId()
                        ),
                () -> assertThat(result4.hasNext()).isFalse(),
                () -> assertThat(result4.getTotalElements()).isEqualTo(10),
                () -> assertThat(result4.getContent())
                        .map(AppliedCoffeeChatsByMentee::coffeeChatId)
                        .containsExactly(
                                coffeeChat9.getId(), coffeeChat8.getId(), coffeeChat7.getId(), coffeeChat6.getId(), coffeeChat5.getId(),
                                coffeeChat4.getId(), coffeeChat3.getId(), coffeeChat2.getId(), coffeeChat1.getId(), coffeeChat0.getId()
                        ),
                () -> assertThat(result4.getContent())
                        .map(AppliedCoffeeChatsByMentee::menteeId)
                        .containsExactly(
                                mentees[9].getId(), mentees[8].getId(), mentees[7].getId(), mentees[6].getId(), mentees[5].getId(),
                                mentees[4].getId(), mentees[3].getId(), mentees[2].getId(), mentees[1].getId(), mentees[0].getId()
                        )
        );

        /* cancel 후 limit별 조회 */
        coffeeChat3.cancel(MENTEE_CANCEL, "취소..");
        coffeeChat5.cancel(MENTEE_CANCEL, "취소..");
        coffeeChat7.cancel(MENTEE_CANCEL, "취소..");
        coffeeChat9.cancel(MENTEE_CANCEL, "취소..");

        final Page<AppliedCoffeeChatsByMentee> result5 = sut.fetchAppliedMentees(mentor.getId(), 3);
        final Page<AppliedCoffeeChatsByMentee> result6 = sut.fetchAppliedMentees(mentor.getId(), 5);
        final Page<AppliedCoffeeChatsByMentee> result7 = sut.fetchAppliedMentees(mentor.getId(), 7);
        final Page<AppliedCoffeeChatsByMentee> result8 = sut.fetchAppliedMentees(mentor.getId(), 10);

        assertAll(
                () -> assertThat(result5.hasNext()).isTrue(),
                () -> assertThat(result5.getTotalElements()).isEqualTo(6),
                () -> assertThat(result5.getContent())
                        .map(AppliedCoffeeChatsByMentee::coffeeChatId)
                        .containsExactly(coffeeChat8.getId(), coffeeChat6.getId(), coffeeChat4.getId()),
                () -> assertThat(result5.getContent())
                        .map(AppliedCoffeeChatsByMentee::menteeId)
                        .containsExactly(mentees[8].getId(), mentees[6].getId(), mentees[4].getId()),
                () -> assertThat(result6.hasNext()).isTrue(),
                () -> assertThat(result6.getTotalElements()).isEqualTo(6),
                () -> assertThat(result6.getContent())
                        .map(AppliedCoffeeChatsByMentee::coffeeChatId)
                        .containsExactly(
                                coffeeChat8.getId(), coffeeChat6.getId(), coffeeChat4.getId(),
                                coffeeChat2.getId(), coffeeChat1.getId()
                        ),
                () -> assertThat(result6.getContent())
                        .map(AppliedCoffeeChatsByMentee::menteeId)
                        .containsExactly(
                                mentees[8].getId(), mentees[6].getId(), mentees[4].getId(),
                                mentees[2].getId(), mentees[1].getId()
                        ),
                () -> assertThat(result7.hasNext()).isFalse(),
                () -> assertThat(result7.getTotalElements()).isEqualTo(6),
                () -> assertThat(result7.getContent())
                        .map(AppliedCoffeeChatsByMentee::coffeeChatId)
                        .containsExactly(
                                coffeeChat8.getId(), coffeeChat6.getId(), coffeeChat4.getId(),
                                coffeeChat2.getId(), coffeeChat1.getId(), coffeeChat0.getId()
                        ),
                () -> assertThat(result7.getContent())
                        .map(AppliedCoffeeChatsByMentee::menteeId)
                        .containsExactly(
                                mentees[8].getId(), mentees[6].getId(), mentees[4].getId(),
                                mentees[2].getId(), mentees[1].getId(), mentees[0].getId()
                        ),
                () -> assertThat(result8.hasNext()).isFalse(),
                () -> assertThat(result8.getTotalElements()).isEqualTo(6),
                () -> assertThat(result8.getContent())
                        .map(AppliedCoffeeChatsByMentee::coffeeChatId)
                        .containsExactly(
                                coffeeChat8.getId(), coffeeChat6.getId(), coffeeChat4.getId(),
                                coffeeChat2.getId(), coffeeChat1.getId(), coffeeChat0.getId()
                        ),
                () -> assertThat(result8.getContent())
                        .map(AppliedCoffeeChatsByMentee::menteeId)
                        .containsExactly(
                                mentees[8].getId(), mentees[6].getId(), mentees[4].getId(),
                                mentees[2].getId(), mentees[1].getId(), mentees[0].getId()
                        )
        );
    }
}
