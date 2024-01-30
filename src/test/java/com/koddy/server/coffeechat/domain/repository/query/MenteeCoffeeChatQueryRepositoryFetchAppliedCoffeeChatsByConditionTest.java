package com.koddy.server.coffeechat.domain.repository.query;

import com.koddy.server.coffeechat.domain.model.CoffeeChat;
import com.koddy.server.coffeechat.domain.repository.CoffeeChatRepository;
import com.koddy.server.coffeechat.domain.repository.query.spec.MenteeCoffeeChatQueryCondition;
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
import static com.koddy.server.common.fixture.CoffeeChatFixture.금요일_2주차_20_00_시작;
import static com.koddy.server.common.fixture.CoffeeChatFixture.금요일_3주차_20_00_시작;
import static com.koddy.server.common.fixture.CoffeeChatFixture.금요일_4주차_20_00_시작;
import static com.koddy.server.common.fixture.CoffeeChatFixture.수요일_1주차_20_00_시작;
import static com.koddy.server.common.fixture.CoffeeChatFixture.수요일_2주차_20_00_시작;
import static com.koddy.server.common.fixture.CoffeeChatFixture.수요일_3주차_20_00_시작;
import static com.koddy.server.common.fixture.CoffeeChatFixture.수요일_4주차_20_00_시작;
import static com.koddy.server.common.fixture.CoffeeChatFixture.월요일_1주차_20_00_시작;
import static com.koddy.server.common.fixture.CoffeeChatFixture.월요일_2주차_20_00_시작;
import static com.koddy.server.common.fixture.CoffeeChatFixture.월요일_3주차_20_00_시작;
import static com.koddy.server.common.fixture.CoffeeChatFixture.월요일_4주차_20_00_시작;
import static com.koddy.server.common.fixture.CoffeeChatFixture.토요일_1주차_20_00_시작;
import static com.koddy.server.common.fixture.CoffeeChatFixture.토요일_2주차_20_00_시작;
import static com.koddy.server.common.fixture.CoffeeChatFixture.토요일_3주차_20_00_시작;
import static com.koddy.server.common.fixture.CoffeeChatFixture.토요일_4주차_20_00_시작;
import static com.koddy.server.common.fixture.CoffeeChatFixture.화요일_1주차_20_00_시작;
import static com.koddy.server.common.fixture.CoffeeChatFixture.화요일_2주차_20_00_시작;
import static com.koddy.server.common.fixture.CoffeeChatFixture.화요일_3주차_20_00_시작;
import static com.koddy.server.common.fixture.CoffeeChatFixture.화요일_4주차_20_00_시작;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Import(MenteeCoffeeChatQueryRepositoryImpl.class)
@DisplayName("Member -> MenteeCoffeeChatQueryRepository [fetchAppliedCoffeeChatsByCondition] 테스트")
class MenteeCoffeeChatQueryRepositoryFetchAppliedCoffeeChatsByConditionTest extends CoffeeChatQueryRepositorySupporter {
    @Autowired
    private MenteeCoffeeChatQueryRepositoryImpl sut;

    @Autowired
    private CoffeeChatRepository coffeeChatRepository;

    private CoffeeChat[] coffeeChats = new CoffeeChat[20];

    @BeforeEach
    void setUp() {
        initMembers();
        coffeeChats = coffeeChatRepository.saveAll(List.of(
                MenteeFlow.apply(월요일_1주차_20_00_시작, mentees[0], mentors[0]),
                MenteeFlow.applyAndApprove(화요일_1주차_20_00_시작, mentees[0], mentors[1]),
                MenteeFlow.apply(수요일_1주차_20_00_시작, mentees[0], mentors[2]),
                MenteeFlow.applyAndReject(토요일_1주차_20_00_시작, mentees[0], mentors[3]),
                MenteeFlow.applyAndCancel(금요일_1주차_20_00_시작, mentees[0], mentors[4]),
                MenteeFlow.applyAndApprove(월요일_2주차_20_00_시작, mentees[0], mentors[5]),
                MenteeFlow.apply(화요일_2주차_20_00_시작, mentees[0], mentors[6]),
                MenteeFlow.applyAndApprove(수요일_2주차_20_00_시작, mentees[0], mentors[7]),
                MenteeFlow.applyAndComplete(토요일_2주차_20_00_시작, mentees[0], mentors[8]),
                MenteeFlow.apply(금요일_2주차_20_00_시작, mentees[0], mentors[9]),
                MenteeFlow.applyAndReject(월요일_3주차_20_00_시작, mentees[0], mentors[10]),
                MenteeFlow.apply(화요일_3주차_20_00_시작, mentees[0], mentors[11]),
                MenteeFlow.applyAndCancel(수요일_3주차_20_00_시작, mentees[0], mentors[12]),
                MenteeFlow.applyAndComplete(토요일_3주차_20_00_시작, mentees[0], mentors[13]),
                MenteeFlow.applyAndApprove(금요일_3주차_20_00_시작, mentees[0], mentors[14]),
                MenteeFlow.apply(월요일_4주차_20_00_시작, mentees[0], mentors[15]),
                MenteeFlow.applyAndCancel(화요일_4주차_20_00_시작, mentees[0], mentors[16]),
                MenteeFlow.apply(수요일_4주차_20_00_시작, mentees[0], mentors[17]),
                MenteeFlow.apply(토요일_4주차_20_00_시작, mentees[0], mentors[18]),
                MenteeFlow.applyAndReject(금요일_4주차_20_00_시작, mentees[0], mentors[19])
        )).toArray(CoffeeChat[]::new);
    }

    @Test
    @DisplayName("멘티가 신청한 커피챗 정보를 조회한다 [상태 변경 최신순]")
    void recent() {
        // given
        final MenteeCoffeeChatQueryCondition condition = new MenteeCoffeeChatQueryCondition(mentees[0].getId(), null);

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
    @DisplayName("멘티가 신청한 커피챗 정보를 조회한다 [상태 = APPLY]")
    void apply() {
        // given
        final MenteeCoffeeChatQueryCondition condition = new MenteeCoffeeChatQueryCondition(mentees[0].getId(), APPLY);

        /* 페이지 1 */
        final Slice<CoffeeChat> result1 = sut.fetchAppliedCoffeeChatsByCondition(condition, pageable1);
        assertAll(
                () -> assertThat(result1.hasNext()).isFalse(),
                () -> assertThat(result1.getContent()).containsExactly(
                        coffeeChats[18], coffeeChats[17], coffeeChats[15], coffeeChats[11],
                        coffeeChats[9], coffeeChats[6], coffeeChats[2], coffeeChats[0]
                )
        );

        /* 페이지 2 */
        final Slice<CoffeeChat> result2 = sut.fetchAppliedCoffeeChatsByCondition(condition, pageable2);
        assertAll(
                () -> assertThat(result2.hasNext()).isFalse(),
                () -> assertThat(result2.getContent()).isEmpty()
        );
    }

    @Test
    @DisplayName("멘티가 신청한 커피챗 정보를 조회한다 [상태 = CANCEL]")
    void cancel() {
        // given
        final MenteeCoffeeChatQueryCondition condition = new MenteeCoffeeChatQueryCondition(mentees[0].getId(), CANCEL);

        /* 페이지 1 */
        final Slice<CoffeeChat> result1 = sut.fetchAppliedCoffeeChatsByCondition(condition, pageable1);
        assertAll(
                () -> assertThat(result1.hasNext()).isFalse(),
                () -> assertThat(result1.getContent()).containsExactly(coffeeChats[16], coffeeChats[12], coffeeChats[4])
        );

        /* 페이지 2 */
        final Slice<CoffeeChat> result2 = sut.fetchAppliedCoffeeChatsByCondition(condition, pageable2);
        assertAll(
                () -> assertThat(result2.hasNext()).isFalse(),
                () -> assertThat(result2.getContent()).isEmpty()
        );
    }

    @Test
    @DisplayName("멘티가 신청한 커피챗 정보를 조회한다 [상태 = APPROVE]")
    void approve() {
        // given
        final MenteeCoffeeChatQueryCondition condition = new MenteeCoffeeChatQueryCondition(mentees[0].getId(), APPROVE);

        /* 페이지 1 */
        final Slice<CoffeeChat> result1 = sut.fetchAppliedCoffeeChatsByCondition(condition, pageable1);
        assertAll(
                () -> assertThat(result1.hasNext()).isFalse(),
                () -> assertThat(result1.getContent()).containsExactly(coffeeChats[14], coffeeChats[7], coffeeChats[5], coffeeChats[1])
        );

        /* 페이지 2 */
        final Slice<CoffeeChat> result2 = sut.fetchAppliedCoffeeChatsByCondition(condition, pageable2);
        assertAll(
                () -> assertThat(result2.hasNext()).isFalse(),
                () -> assertThat(result2.getContent()).isEmpty()
        );
    }

    @Test
    @DisplayName("멘티가 신청한 커피챗 정보를 조회한다 [상태 = REJECT]")
    void reject() {
        // given
        final MenteeCoffeeChatQueryCondition condition = new MenteeCoffeeChatQueryCondition(mentees[0].getId(), REJECT);

        /* 페이지 1 */
        final Slice<CoffeeChat> result1 = sut.fetchAppliedCoffeeChatsByCondition(condition, pageable1);
        assertAll(
                () -> assertThat(result1.hasNext()).isFalse(),
                () -> assertThat(result1.getContent()).containsExactly(coffeeChats[19], coffeeChats[10], coffeeChats[3])
        );

        /* 페이지 2 */
        final Slice<CoffeeChat> result2 = sut.fetchAppliedCoffeeChatsByCondition(condition, pageable2);
        assertAll(
                () -> assertThat(result2.hasNext()).isFalse(),
                () -> assertThat(result2.getContent()).isEmpty()
        );
    }

    @Test
    @DisplayName("멘티가 신청한 커피챗 정보를 조회한다 [상태 = COMPLETE]")
    void complete() {
        // given
        final MenteeCoffeeChatQueryCondition condition = new MenteeCoffeeChatQueryCondition(mentees[0].getId(), COMPLETE);

        /* 페이지 1 */
        final Slice<CoffeeChat> result1 = sut.fetchAppliedCoffeeChatsByCondition(condition, pageable1);
        assertAll(
                () -> assertThat(result1.hasNext()).isFalse(),
                () -> assertThat(result1.getContent()).containsExactly(coffeeChats[13], coffeeChats[8])
        );

        /* 페이지 2 */
        final Slice<CoffeeChat> result2 = sut.fetchAppliedCoffeeChatsByCondition(condition, pageable2);
        assertAll(
                () -> assertThat(result2.hasNext()).isFalse(),
                () -> assertThat(result2.getContent()).isEmpty()
        );
    }
}
