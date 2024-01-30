package com.koddy.server.coffeechat.domain.repository.query;

import com.koddy.server.coffeechat.domain.model.CoffeeChat;
import com.koddy.server.coffeechat.domain.repository.CoffeeChatRepository;
import com.koddy.server.coffeechat.domain.repository.query.spec.MentorCoffeeChatQueryCondition;
import com.koddy.server.common.fixture.CoffeeChatFixture.MentorFlow;
import com.koddy.server.member.domain.model.mentee.Mentee;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Slice;

import java.util.List;

import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.APPLY;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.APPROVE;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.CANCEL;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.COMPLETE;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.PENDING;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.REJECT;
import static com.koddy.server.common.fixture.CoffeeChatFixture.월요일_1주차_20_00_시작;
import static com.koddy.server.common.fixture.CoffeeChatFixture.월요일_1주차_21_00_시작;
import static com.koddy.server.common.fixture.CoffeeChatFixture.월요일_2주차_20_00_시작;
import static com.koddy.server.common.fixture.CoffeeChatFixture.월요일_2주차_21_00_시작;
import static com.koddy.server.common.fixture.CoffeeChatFixture.월요일_3주차_20_00_시작;
import static com.koddy.server.common.fixture.CoffeeChatFixture.월요일_3주차_21_00_시작;
import static com.koddy.server.common.fixture.CoffeeChatFixture.월요일_4주차_20_00_시작;
import static com.koddy.server.common.fixture.CoffeeChatFixture.월요일_4주차_21_00_시작;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Import(MentorCoffeeChatQueryRepositoryImpl.class)
@DisplayName("Member -> MentorCoffeeChatQueryRepository [fetchSuggestedCoffeeChatsByCondition] 테스트")
class MentorCoffeeChatQueryRepositoryFetchSuggestedCoffeeChatsByConditionTest extends CoffeeChatQueryRepositorySupporter {
    @Autowired
    private MentorCoffeeChatQueryRepositoryImpl sut;

    @Autowired
    private CoffeeChatRepository coffeeChatRepository;

    @BeforeEach
    void setUp() {
        initMembers();
        coffeeChatRepository.saveAll(List.of(
                MentorFlow.suggest(mentors[0], mentees[0]),
                MentorFlow.suggestAndFinallyReject(월요일_1주차_20_00_시작, mentors[0], mentees[1]),
                MentorFlow.suggest(mentors[0], mentees[2]),
                MentorFlow.suggestAndPending(월요일_1주차_21_00_시작, mentors[0], mentees[3]),
                MentorFlow.suggest(mentors[0], mentees[4]),
                MentorFlow.suggestAndFinallyApprove(월요일_2주차_20_00_시작, mentors[0], mentees[5]),
                MentorFlow.suggestAndCancel(mentors[0], mentees[6]),
                MentorFlow.suggestAndReject(mentors[0], mentees[7]),
                MentorFlow.suggest(mentors[0], mentees[8]),
                MentorFlow.suggestAndCancel(mentors[0], mentees[9]),
                MentorFlow.suggest(mentors[0], mentees[10]),
                MentorFlow.suggest(mentors[0], mentees[11]),
                MentorFlow.suggestAndFinallyApprove(월요일_2주차_21_00_시작, mentors[0], mentees[12]),
                MentorFlow.suggest(mentors[0], mentees[13]),
                MentorFlow.suggestAndPending(월요일_3주차_20_00_시작, mentors[0], mentees[14]),
                MentorFlow.suggestAndComplete(월요일_3주차_21_00_시작, mentors[0], mentees[15]),
                MentorFlow.suggest(mentors[0], mentees[16]),
                MentorFlow.suggestAndFinallyReject(월요일_4주차_20_00_시작, mentors[0], mentees[17]),
                MentorFlow.suggestAndPending(월요일_4주차_21_00_시작, mentors[0], mentees[18]),
                MentorFlow.suggest(mentors[0], mentees[19])
        )).toArray(CoffeeChat[]::new);
    }

    @Test
    @DisplayName("멘토가 제안한 커피챗 정보를 조회한다 [상태 변경 최신순]")
    void recent() {
        // given
        final MentorCoffeeChatQueryCondition condition = new MentorCoffeeChatQueryCondition(mentors[0].getId(), null);

        /* 페이지 1 */
        final Slice<Mentee> result1 = sut.fetchSuggestedCoffeeChatsByCondition(condition, pageable1);
        assertAll(
                () -> assertThat(result1.hasNext()).isTrue(),
                () -> assertThat(result1.getContent()).containsExactly(
                        mentees[19], mentees[18], mentees[17], mentees[16], mentees[15],
                        mentees[14], mentees[13], mentees[12], mentees[11], mentees[10]
                )
        );

        /* 페이지 2 */
        final Slice<Mentee> result2 = sut.fetchSuggestedCoffeeChatsByCondition(condition, pageable2);
        assertAll(
                () -> assertThat(result2.hasNext()).isFalse(),
                () -> assertThat(result2.getContent()).containsExactly(
                        mentees[9], mentees[8], mentees[7], mentees[6], mentees[5],
                        mentees[4], mentees[3], mentees[2], mentees[1], mentees[0]
                )
        );
    }

    @Test
    @DisplayName("멘토가 제안한 커피챗 정보를 조회한다 [상태 = APPLY]")
    void apply() {
        // given
        final MentorCoffeeChatQueryCondition condition = new MentorCoffeeChatQueryCondition(mentors[0].getId(), APPLY);

        /* 페이지 1 */
        final Slice<Mentee> result1 = sut.fetchSuggestedCoffeeChatsByCondition(condition, pageable1);
        assertAll(
                () -> assertThat(result1.hasNext()).isFalse(),
                () -> assertThat(result1.getContent()).containsExactly(
                        mentees[19], mentees[16], mentees[13], mentees[11], mentees[10],
                        mentees[8], mentees[4], mentees[2], mentees[0]
                )
        );

        /* 페이지 2 */
        final Slice<Mentee> result2 = sut.fetchSuggestedCoffeeChatsByCondition(condition, pageable2);
        assertAll(
                () -> assertThat(result2.hasNext()).isFalse(),
                () -> assertThat(result2.getContent()).isEmpty()
        );
    }

    @Test
    @DisplayName("멘토가 제안한 커피챗 정보를 조회한다 [상태 = CANCEL]")
    void cancel() {
        // given
        final MentorCoffeeChatQueryCondition condition = new MentorCoffeeChatQueryCondition(mentors[0].getId(), CANCEL);

        /* 페이지 1 */
        final Slice<Mentee> result1 = sut.fetchSuggestedCoffeeChatsByCondition(condition, pageable1);
        assertAll(
                () -> assertThat(result1.hasNext()).isFalse(),
                () -> assertThat(result1.getContent()).containsExactly(mentees[9], mentees[6])
        );

        /* 페이지 2 */
        final Slice<Mentee> result2 = sut.fetchSuggestedCoffeeChatsByCondition(condition, pageable2);
        assertAll(
                () -> assertThat(result2.hasNext()).isFalse(),
                () -> assertThat(result2.getContent()).isEmpty()
        );
    }

    @Test
    @DisplayName("멘토가 제안한 커피챗 정보를 조회한다 [상태 = PENDING]")
    void pending() {
        // given
        final MentorCoffeeChatQueryCondition condition = new MentorCoffeeChatQueryCondition(mentors[0].getId(), PENDING);

        /* 페이지 1 */
        final Slice<Mentee> result1 = sut.fetchSuggestedCoffeeChatsByCondition(condition, pageable1);
        assertAll(
                () -> assertThat(result1.hasNext()).isFalse(),
                () -> assertThat(result1.getContent()).containsExactly(mentees[18], mentees[14], mentees[3])
        );

        /* 페이지 2 */
        final Slice<Mentee> result2 = sut.fetchSuggestedCoffeeChatsByCondition(condition, pageable2);
        assertAll(
                () -> assertThat(result2.hasNext()).isFalse(),
                () -> assertThat(result2.getContent()).isEmpty()
        );
    }

    @Test
    @DisplayName("멘토가 제안한 커피챗 정보를 조회한다 [상태 = APPROVE]")
    void approve() {
        // given
        final MentorCoffeeChatQueryCondition condition = new MentorCoffeeChatQueryCondition(mentors[0].getId(), APPROVE);

        /* 페이지 1 */
        final Slice<Mentee> result1 = sut.fetchSuggestedCoffeeChatsByCondition(condition, pageable1);
        assertAll(
                () -> assertThat(result1.hasNext()).isFalse(),
                () -> assertThat(result1.getContent()).containsExactly(mentees[12], mentees[5])
        );

        /* 페이지 2 */
        final Slice<Mentee> result2 = sut.fetchSuggestedCoffeeChatsByCondition(condition, pageable2);
        assertAll(
                () -> assertThat(result2.hasNext()).isFalse(),
                () -> assertThat(result2.getContent()).isEmpty()
        );
    }

    @Test
    @DisplayName("멘토가 제안한 커피챗 정보를 조회한다 [상태 = REJECT]")
    void reject() {
        // given
        final MentorCoffeeChatQueryCondition condition = new MentorCoffeeChatQueryCondition(mentors[0].getId(), REJECT);

        /* 페이지 1 */
        final Slice<Mentee> result1 = sut.fetchSuggestedCoffeeChatsByCondition(condition, pageable1);
        assertAll(
                () -> assertThat(result1.hasNext()).isFalse(),
                () -> assertThat(result1.getContent()).containsExactly(mentees[17], mentees[7], mentees[1])
        );

        /* 페이지 2 */
        final Slice<Mentee> result2 = sut.fetchSuggestedCoffeeChatsByCondition(condition, pageable2);
        assertAll(
                () -> assertThat(result2.hasNext()).isFalse(),
                () -> assertThat(result2.getContent()).isEmpty()
        );
    }

    @Test
    @DisplayName("멘토가 제안한 커피챗 정보를 조회한다 [상태 = COMPLETE]")
    void complete() {
        // given
        final MentorCoffeeChatQueryCondition condition = new MentorCoffeeChatQueryCondition(mentors[0].getId(), COMPLETE);

        /* 페이지 1 */
        final Slice<Mentee> result1 = sut.fetchSuggestedCoffeeChatsByCondition(condition, pageable1);
        assertAll(
                () -> assertThat(result1.hasNext()).isFalse(),
                () -> assertThat(result1.getContent()).containsExactly(mentees[15])
        );

        /* 페이지 2 */
        final Slice<Mentee> result2 = sut.fetchSuggestedCoffeeChatsByCondition(condition, pageable2);
        assertAll(
                () -> assertThat(result2.hasNext()).isFalse(),
                () -> assertThat(result2.getContent()).isEmpty()
        );
    }
}
