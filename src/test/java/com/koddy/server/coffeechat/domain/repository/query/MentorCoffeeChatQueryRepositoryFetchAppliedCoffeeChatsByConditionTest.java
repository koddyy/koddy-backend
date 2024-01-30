package com.koddy.server.coffeechat.domain.repository.query;

import com.koddy.server.coffeechat.domain.model.CoffeeChat;
import com.koddy.server.coffeechat.domain.repository.CoffeeChatRepository;
import com.koddy.server.coffeechat.domain.repository.query.spec.MentorCoffeeChatQueryCondition;
import com.koddy.server.common.fixture.CoffeeChatFixture.MenteeFlow;
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
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.REJECT;
import static com.koddy.server.common.fixture.CoffeeChatFixture.금요일_1주차_20_00_시작;
import static com.koddy.server.common.fixture.CoffeeChatFixture.금요일_1주차_21_00_시작;
import static com.koddy.server.common.fixture.CoffeeChatFixture.금요일_2주차_20_00_시작;
import static com.koddy.server.common.fixture.CoffeeChatFixture.금요일_2주차_21_00_시작;
import static com.koddy.server.common.fixture.CoffeeChatFixture.수요일_1주차_20_00_시작;
import static com.koddy.server.common.fixture.CoffeeChatFixture.수요일_1주차_21_00_시작;
import static com.koddy.server.common.fixture.CoffeeChatFixture.수요일_2주차_20_00_시작;
import static com.koddy.server.common.fixture.CoffeeChatFixture.수요일_2주차_21_00_시작;
import static com.koddy.server.common.fixture.CoffeeChatFixture.수요일_3주차_20_00_시작;
import static com.koddy.server.common.fixture.CoffeeChatFixture.수요일_3주차_21_00_시작;
import static com.koddy.server.common.fixture.CoffeeChatFixture.수요일_4주차_20_00_시작;
import static com.koddy.server.common.fixture.CoffeeChatFixture.수요일_4주차_21_00_시작;
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
@DisplayName("Member -> MentorCoffeeChatQueryRepository [fetchAppliedCoffeeChatsByCondition] 테스트")
class MentorCoffeeChatQueryRepositoryFetchAppliedCoffeeChatsByConditionTest extends CoffeeChatQueryRepositorySupporter {
    @Autowired
    private MentorCoffeeChatQueryRepositoryImpl sut;

    @Autowired
    private CoffeeChatRepository coffeeChatRepository;

    private CoffeeChat[] coffeeChats = new CoffeeChat[20];

    @BeforeEach
    void setUp() {
        initMembers();
        coffeeChats = coffeeChatRepository.saveAll(List.of(
                MenteeFlow.apply(월요일_1주차_20_00_시작, mentees[0], mentors[0]),
                MenteeFlow.applyAndReject(월요일_1주차_21_00_시작, mentees[1], mentors[0]),
                MenteeFlow.apply(월요일_2주차_20_00_시작, mentees[2], mentors[0]),
                MenteeFlow.applyAndApprove(월요일_2주차_21_00_시작, mentees[3], mentors[0]),
                MenteeFlow.apply(월요일_3주차_20_00_시작, mentees[4], mentors[0]),
                MenteeFlow.applyAndApprove(월요일_3주차_21_00_시작, mentees[5], mentors[0]),
                MenteeFlow.apply(월요일_4주차_20_00_시작, mentees[6], mentors[0]),
                MenteeFlow.apply(월요일_4주차_21_00_시작, mentees[7], mentors[0]),
                MenteeFlow.apply(수요일_1주차_20_00_시작, mentees[8], mentors[0]),
                MenteeFlow.applyAndCancel(수요일_1주차_21_00_시작, mentees[9], mentors[0]),
                MenteeFlow.apply(수요일_2주차_20_00_시작, mentees[10], mentors[0]),
                MenteeFlow.applyAndApprove(수요일_2주차_21_00_시작, mentees[11], mentors[0]),
                MenteeFlow.apply(수요일_3주차_20_00_시작, mentees[12], mentors[0]),
                MenteeFlow.applyAndReject(수요일_3주차_21_00_시작, mentees[13], mentors[0]),
                MenteeFlow.applyAndCancel(수요일_4주차_20_00_시작, mentees[14], mentors[0]),
                MenteeFlow.apply(수요일_4주차_21_00_시작, mentees[15], mentors[0]),
                MenteeFlow.applyAndApprove(금요일_1주차_20_00_시작, mentees[16], mentors[0]),
                MenteeFlow.apply(금요일_1주차_21_00_시작, mentees[17], mentors[0]),
                MenteeFlow.applyAndComplete(금요일_2주차_20_00_시작, mentees[18], mentors[0]),
                MenteeFlow.apply(금요일_2주차_21_00_시작, mentees[19], mentors[0])
        )).toArray(CoffeeChat[]::new);
    }

    @Test
    @DisplayName("멘토가 신청받은 커피챗 정보를 조회한다 [상태 변경 최신순]")
    void recent() {
        // given
        final MentorCoffeeChatQueryCondition condition = new MentorCoffeeChatQueryCondition(mentors[0].getId(), null);

        /* 페이지 1 */
        final Slice<CoffeeChat> result1 = sut.fetchAppliedCoffeeChatsByCondition(condition, pageable1);
        assertAll(
                () -> assertThat(result1.hasNext()).isTrue(),
                () -> assertThat(result1.getContent()).containsExactly(
                        coffeeChats[19], coffeeChats[18], coffeeChats[17], coffeeChats[16], coffeeChats[15],
                        coffeeChats[14], coffeeChats[13], coffeeChats[12], coffeeChats[11], coffeeChats[10]
                )
        );

        /* 페이지 2 */
        final Slice<CoffeeChat> result2 = sut.fetchAppliedCoffeeChatsByCondition(condition, pageable2);
        assertAll(
                () -> assertThat(result2.hasNext()).isFalse(),
                () -> assertThat(result2.getContent()).containsExactly(
                        coffeeChats[9], coffeeChats[8], coffeeChats[7], coffeeChats[6], coffeeChats[5],
                        coffeeChats[4], coffeeChats[3], coffeeChats[2], coffeeChats[1], coffeeChats[0]
                )
        );
    }

    @Test
    @DisplayName("멘토가 신청받은 커피챗 정보를 조회한다 [상태 = APPLY]")
    void apply() {
        // given
        final MentorCoffeeChatQueryCondition condition = new MentorCoffeeChatQueryCondition(mentors[0].getId(), APPLY);

        /* 페이지 1 */
        final Slice<CoffeeChat> result1 = sut.fetchAppliedCoffeeChatsByCondition(condition, pageable1);
        assertAll(
                () -> assertThat(result1.hasNext()).isTrue(),
                () -> assertThat(result1.getContent()).containsExactly(
                        coffeeChats[19], coffeeChats[17], coffeeChats[15], coffeeChats[12], coffeeChats[10],
                        coffeeChats[8], coffeeChats[7], coffeeChats[6], coffeeChats[4], coffeeChats[2]
                )
        );

        /* 페이지 2 */
        final Slice<CoffeeChat> result2 = sut.fetchAppliedCoffeeChatsByCondition(condition, pageable2);
        assertAll(
                () -> assertThat(result2.hasNext()).isFalse(),
                () -> assertThat(result2.getContent()).containsExactly(coffeeChats[0])
        );
    }

    @Test
    @DisplayName("멘토가 신청받은 커피챗 정보를 조회한다 [상태 = CANCEL]")
    void cancel() {
        // given
        final MentorCoffeeChatQueryCondition condition = new MentorCoffeeChatQueryCondition(mentors[0].getId(), CANCEL);

        /* 페이지 1 */
        final Slice<CoffeeChat> result1 = sut.fetchAppliedCoffeeChatsByCondition(condition, pageable1);
        assertAll(
                () -> assertThat(result1.hasNext()).isFalse(),
                () -> assertThat(result1.getContent()).containsExactly(coffeeChats[14], coffeeChats[9])
        );

        /* 페이지 2 */
        final Slice<CoffeeChat> result2 = sut.fetchAppliedCoffeeChatsByCondition(condition, pageable2);
        assertAll(
                () -> assertThat(result2.hasNext()).isFalse(),
                () -> assertThat(result2.getContent()).isEmpty()
        );
    }

    @Test
    @DisplayName("멘토가 신청받은 커피챗 정보를 조회한다 [상태 = APPROVE]")
    void approve() {
        // given
        final MentorCoffeeChatQueryCondition condition = new MentorCoffeeChatQueryCondition(mentors[0].getId(), APPROVE);

        /* 페이지 1 */
        final Slice<CoffeeChat> result1 = sut.fetchAppliedCoffeeChatsByCondition(condition, pageable1);
        assertAll(
                () -> assertThat(result1.hasNext()).isFalse(),
                () -> assertThat(result1.getContent()).containsExactly(coffeeChats[16], coffeeChats[11], coffeeChats[5], coffeeChats[3])
        );

        /* 페이지 2 */
        final Slice<CoffeeChat> result2 = sut.fetchAppliedCoffeeChatsByCondition(condition, pageable2);
        assertAll(
                () -> assertThat(result2.hasNext()).isFalse(),
                () -> assertThat(result2.getContent()).isEmpty()
        );
    }

    @Test
    @DisplayName("멘토가 신청받은 커피챗 정보를 조회한다 [상태 = REJECT]")
    void reject() {
        // given
        final MentorCoffeeChatQueryCondition condition = new MentorCoffeeChatQueryCondition(mentors[0].getId(), REJECT);

        /* 페이지 1 */
        final Slice<CoffeeChat> result1 = sut.fetchAppliedCoffeeChatsByCondition(condition, pageable1);
        assertAll(
                () -> assertThat(result1.hasNext()).isFalse(),
                () -> assertThat(result1.getContent()).containsExactly(coffeeChats[13], coffeeChats[1])
        );

        /* 페이지 2 */
        final Slice<CoffeeChat> result2 = sut.fetchAppliedCoffeeChatsByCondition(condition, pageable2);
        assertAll(
                () -> assertThat(result2.hasNext()).isFalse(),
                () -> assertThat(result2.getContent()).isEmpty()
        );
    }

    @Test
    @DisplayName("멘토가 신청받은 커피챗 정보를 조회한다 [상태 = COMPLETE]")
    void complete() {
        // given
        final MentorCoffeeChatQueryCondition condition = new MentorCoffeeChatQueryCondition(mentors[0].getId(), COMPLETE);

        /* 페이지 1 */
        final Slice<CoffeeChat> result1 = sut.fetchAppliedCoffeeChatsByCondition(condition, pageable1);
        assertAll(
                () -> assertThat(result1.hasNext()).isFalse(),
                () -> assertThat(result1.getContent()).containsExactly(coffeeChats[18])
        );

        /* 페이지 2 */
        final Slice<CoffeeChat> result2 = sut.fetchAppliedCoffeeChatsByCondition(condition, pageable2);
        assertAll(
                () -> assertThat(result2.hasNext()).isFalse(),
                () -> assertThat(result2.getContent()).isEmpty()
        );
    }
}
