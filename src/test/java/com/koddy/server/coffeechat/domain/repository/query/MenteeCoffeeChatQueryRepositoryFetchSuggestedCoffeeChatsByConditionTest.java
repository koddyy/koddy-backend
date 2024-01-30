package com.koddy.server.coffeechat.domain.repository.query;

import com.koddy.server.coffeechat.domain.model.CoffeeChat;
import com.koddy.server.coffeechat.domain.repository.CoffeeChatRepository;
import com.koddy.server.coffeechat.domain.repository.query.spec.MenteeCoffeeChatQueryCondition;
import com.koddy.server.common.fixture.CoffeeChatFixture.MentorFlow;
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
import static com.koddy.server.common.fixture.CoffeeChatFixture.수요일_3주차_20_00_시작;
import static com.koddy.server.common.fixture.CoffeeChatFixture.월요일_2주차_20_00_시작;
import static com.koddy.server.common.fixture.CoffeeChatFixture.월요일_3주차_20_00_시작;
import static com.koddy.server.common.fixture.CoffeeChatFixture.토요일_2주차_20_00_시작;
import static com.koddy.server.common.fixture.CoffeeChatFixture.토요일_3주차_20_00_시작;
import static com.koddy.server.common.fixture.CoffeeChatFixture.토요일_4주차_20_00_시작;
import static com.koddy.server.common.fixture.CoffeeChatFixture.화요일_1주차_20_00_시작;
import static com.koddy.server.common.fixture.CoffeeChatFixture.화요일_2주차_20_00_시작;
import static com.koddy.server.common.fixture.CoffeeChatFixture.화요일_4주차_20_00_시작;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Import(MenteeCoffeeChatQueryRepositoryImpl.class)
@DisplayName("Member -> MenteeCoffeeChatQueryRepository [fetchSuggestedCoffeeChatsByCondition] 테스트")
class MenteeCoffeeChatQueryRepositoryFetchSuggestedCoffeeChatsByConditionTest extends CoffeeChatQueryRepositorySupporter {
    @Autowired
    private MenteeCoffeeChatQueryRepositoryImpl sut;

    @Autowired
    private CoffeeChatRepository coffeeChatRepository;

    private CoffeeChat[] coffeeChats = new CoffeeChat[20];

    @BeforeEach
    void setUp() {
        initMembers();
        coffeeChats = coffeeChatRepository.saveAll(List.of(
                MentorFlow.suggest(mentors[0], mentees[0]),
                MentorFlow.suggestAndFinallyApprove(화요일_1주차_20_00_시작, mentors[1], mentees[0]),
                MentorFlow.suggest(mentors[2], mentees[0]),
                MentorFlow.suggestAndCancel(mentors[3], mentees[0]),
                MentorFlow.suggest(mentors[4], mentees[0]),
                MentorFlow.suggestAndFinallyApprove(월요일_2주차_20_00_시작, mentors[5], mentees[0]),
                MentorFlow.suggestAndPending(화요일_2주차_20_00_시작, mentors[6], mentees[0]),
                MentorFlow.suggest(mentors[7], mentees[0]),
                MentorFlow.suggestAndFinallyReject(토요일_2주차_20_00_시작, mentors[8], mentees[0]),
                MentorFlow.suggest(mentors[9], mentees[0]),
                MentorFlow.suggestAndPending(월요일_3주차_20_00_시작, mentors[10], mentees[0]),
                MentorFlow.suggest(mentors[11], mentees[0]),
                MentorFlow.suggestAndComplete(수요일_3주차_20_00_시작, mentors[12], mentees[0]),
                MentorFlow.suggest(mentors[13], mentees[0]),
                MentorFlow.suggestAndPending(토요일_3주차_20_00_시작, mentors[14], mentees[0]),
                MentorFlow.suggest(mentors[15], mentees[0]),
                MentorFlow.suggestAndFinallyApprove(화요일_4주차_20_00_시작, mentors[16], mentees[0]),
                MentorFlow.suggestAndCancel(mentors[17], mentees[0]),
                MentorFlow.suggest(mentors[18], mentees[0]),
                MentorFlow.suggestAndFinallyReject(토요일_4주차_20_00_시작, mentors[19], mentees[0])
        )).toArray(CoffeeChat[]::new);
    }

    @Test
    @DisplayName("멘티가 제안받은 커피챗 정보를 조회한다 [상태 변경 최신순]")
    void recent() {
        // given
        final MenteeCoffeeChatQueryCondition condition = new MenteeCoffeeChatQueryCondition(mentees[0].getId(), null);

        /* 페이지 1 */
        final Slice<CoffeeChat> result1 = sut.fetchSuggestedCoffeeChatsByCondition(condition, pageable1);
        assertAll(
                () -> assertThat(result1.hasNext()).isTrue(),
                () -> assertThat(result1.getContent()).containsExactly(
                        coffeeChats[19], coffeeChats[18], coffeeChats[17], coffeeChats[16], coffeeChats[15],
                        coffeeChats[14], coffeeChats[13], coffeeChats[12], coffeeChats[11], coffeeChats[10]
                )
        );

        /* 페이지 2 */
        final Slice<CoffeeChat> result2 = sut.fetchSuggestedCoffeeChatsByCondition(condition, pageable2);
        assertAll(
                () -> assertThat(result2.hasNext()).isFalse(),
                () -> assertThat(result2.getContent()).containsExactly(
                        coffeeChats[9], coffeeChats[8], coffeeChats[7], coffeeChats[6], coffeeChats[5],
                        coffeeChats[4], coffeeChats[3], coffeeChats[2], coffeeChats[1], coffeeChats[0]
                )
        );
    }

    @Test
    @DisplayName("멘티가 제안받은 커피챗 정보를 조회한다 [상태 = APPLY]")
    void apply() {
        // given
        final MenteeCoffeeChatQueryCondition condition = new MenteeCoffeeChatQueryCondition(mentees[0].getId(), APPLY);

        /* 페이지 1 */
        final Slice<CoffeeChat> result1 = sut.fetchSuggestedCoffeeChatsByCondition(condition, pageable1);
        assertAll(
                () -> assertThat(result1.hasNext()).isFalse(),
                () -> assertThat(result1.getContent()).containsExactly(
                        coffeeChats[18], coffeeChats[15], coffeeChats[13], coffeeChats[11],
                        coffeeChats[9], coffeeChats[7], coffeeChats[4], coffeeChats[2],
                        coffeeChats[0]
                )
        );

        /* 페이지 2 */
        final Slice<CoffeeChat> result2 = sut.fetchSuggestedCoffeeChatsByCondition(condition, pageable2);
        assertAll(
                () -> assertThat(result2.hasNext()).isFalse(),
                () -> assertThat(result2.getContent()).isEmpty()
        );
    }

    @Test
    @DisplayName("멘티가 제안받은 커피챗 정보를 조회한다 [상태 = CANCEL]")
    void cancel() {
        // given
        final MenteeCoffeeChatQueryCondition condition = new MenteeCoffeeChatQueryCondition(mentees[0].getId(), CANCEL);

        /* 페이지 1 */
        final Slice<CoffeeChat> result1 = sut.fetchSuggestedCoffeeChatsByCondition(condition, pageable1);
        assertAll(
                () -> assertThat(result1.hasNext()).isFalse(),
                () -> assertThat(result1.getContent()).containsExactly(coffeeChats[17], coffeeChats[3])
        );

        /* 페이지 2 */
        final Slice<CoffeeChat> result2 = sut.fetchSuggestedCoffeeChatsByCondition(condition, pageable2);
        assertAll(
                () -> assertThat(result2.hasNext()).isFalse(),
                () -> assertThat(result2.getContent()).isEmpty()
        );
    }

    @Test
    @DisplayName("멘티가 제안받은 커피챗 정보를 조회한다 [상태 = PENDING]")
    void pending() {
        // given
        final MenteeCoffeeChatQueryCondition condition = new MenteeCoffeeChatQueryCondition(mentees[0].getId(), PENDING);

        /* 페이지 1 */
        final Slice<CoffeeChat> result1 = sut.fetchSuggestedCoffeeChatsByCondition(condition, pageable1);
        assertAll(
                () -> assertThat(result1.hasNext()).isFalse(),
                () -> assertThat(result1.getContent()).containsExactly(coffeeChats[14], coffeeChats[10], coffeeChats[6])
        );

        /* 페이지 2 */
        final Slice<CoffeeChat> result2 = sut.fetchSuggestedCoffeeChatsByCondition(condition, pageable2);
        assertAll(
                () -> assertThat(result2.hasNext()).isFalse(),
                () -> assertThat(result2.getContent()).isEmpty()
        );
    }

    @Test
    @DisplayName("멘티가 제안받은 커피챗 정보를 조회한다 [상태 = APPROVE]")
    void approve() {
        // given
        final MenteeCoffeeChatQueryCondition condition = new MenteeCoffeeChatQueryCondition(mentees[0].getId(), APPROVE);

        /* 페이지 1 */
        final Slice<CoffeeChat> result1 = sut.fetchSuggestedCoffeeChatsByCondition(condition, pageable1);
        assertAll(
                () -> assertThat(result1.hasNext()).isFalse(),
                () -> assertThat(result1.getContent()).containsExactly(coffeeChats[16], coffeeChats[5], coffeeChats[1])
        );

        /* 페이지 2 */
        final Slice<CoffeeChat> result2 = sut.fetchSuggestedCoffeeChatsByCondition(condition, pageable2);
        assertAll(
                () -> assertThat(result2.hasNext()).isFalse(),
                () -> assertThat(result2.getContent()).isEmpty()
        );
    }

    @Test
    @DisplayName("멘티가 제안받은 커피챗 정보를 조회한다 [상태 = REJECT]")
    void reject() {
        // given
        final MenteeCoffeeChatQueryCondition condition = new MenteeCoffeeChatQueryCondition(mentees[0].getId(), REJECT);

        /* 페이지 1 */
        final Slice<CoffeeChat> result1 = sut.fetchSuggestedCoffeeChatsByCondition(condition, pageable1);
        assertAll(
                () -> assertThat(result1.hasNext()).isFalse(),
                () -> assertThat(result1.getContent()).containsExactly(coffeeChats[19], coffeeChats[8])
        );

        /* 페이지 2 */
        final Slice<CoffeeChat> result2 = sut.fetchSuggestedCoffeeChatsByCondition(condition, pageable2);
        assertAll(
                () -> assertThat(result2.hasNext()).isFalse(),
                () -> assertThat(result2.getContent()).isEmpty()
        );
    }

    @Test
    @DisplayName("멘티가 제안받은 커피챗 정보를 조회한다 [상태 = COMPLETE]")
    void complete() {
        // given
        final MenteeCoffeeChatQueryCondition condition = new MenteeCoffeeChatQueryCondition(mentees[0].getId(), COMPLETE);

        /* 페이지 1 */
        final Slice<CoffeeChat> result1 = sut.fetchSuggestedCoffeeChatsByCondition(condition, pageable1);
        assertAll(
                () -> assertThat(result1.hasNext()).isFalse(),
                () -> assertThat(result1.getContent()).containsExactly(coffeeChats[12])
        );

        /* 페이지 2 */
        final Slice<CoffeeChat> result2 = sut.fetchSuggestedCoffeeChatsByCondition(condition, pageable2);
        assertAll(
                () -> assertThat(result2.hasNext()).isFalse(),
                () -> assertThat(result2.getContent()).isEmpty()
        );
    }
}
