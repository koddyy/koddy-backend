package com.koddy.server.coffeechat.domain.repository.query;

import com.koddy.server.coffeechat.domain.model.CoffeeChat;
import com.koddy.server.coffeechat.domain.repository.CoffeeChatRepository;
import com.koddy.server.coffeechat.domain.repository.query.spec.MenteeCoffeeChatQueryCondition;
import com.koddy.server.common.fixture.CoffeeChatFixture.MentorFlow;
import com.koddy.server.member.domain.model.mentor.Mentor;
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

    @BeforeEach
    void setUp() {
        initMembers();
        coffeeChatRepository.saveAll(List.of(
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
    @DisplayName("1. 멘티가 제안받은 커피챗에 대한 상태별 리스트에 포함된 멘토 정보를 조회한다 [전체 -> 상태 변경 최신순]")
    void recent() {
        // given
        final MenteeCoffeeChatQueryCondition condition = new MenteeCoffeeChatQueryCondition(mentees[0].getId(), List.of());

        /* 페이지 1 */
        final Slice<Mentor> result1 = sut.fetchSuggestedCoffeeChatsByCondition(condition, pageable1);
        assertAll(
                () -> assertThat(result1.hasNext()).isTrue(),
                () -> assertThat(result1.getContent()).containsExactly(
                        mentors[19], mentors[18], mentors[17], mentors[16], mentors[15],
                        mentors[14], mentors[13], mentors[12], mentors[11], mentors[10]
                )
        );

        /* 페이지 2 */
        final Slice<Mentor> result2 = sut.fetchSuggestedCoffeeChatsByCondition(condition, pageable2);
        assertAll(
                () -> assertThat(result2.hasNext()).isFalse(),
                () -> assertThat(result2.getContent()).containsExactly(
                        mentors[9], mentors[8], mentors[7], mentors[6], mentors[5],
                        mentors[4], mentors[3], mentors[2], mentors[1], mentors[0]
                )
        );
    }

    @Test
    @DisplayName("2. 멘티가 제안받은 커피챗에 대한 상태별 리스트에 포함된 멘토 정보를 조회한다 [제안 -> APPLY]")
    void apply() {
        // given
        final MenteeCoffeeChatQueryCondition condition = new MenteeCoffeeChatQueryCondition(mentees[0].getId(), List.of(APPLY));

        /* 페이지 1 */
        final Slice<Mentor> result1 = sut.fetchSuggestedCoffeeChatsByCondition(condition, pageable1);
        assertAll(
                () -> assertThat(result1.hasNext()).isFalse(),
                () -> assertThat(result1.getContent()).containsExactly(
                        mentors[18], mentors[15], mentors[13], mentors[11],
                        mentors[9], mentors[7], mentors[4], mentors[2],
                        mentors[0]
                )
        );

        /* 페이지 2 */
        final Slice<Mentor> result2 = sut.fetchSuggestedCoffeeChatsByCondition(condition, pageable2);
        assertAll(
                () -> assertThat(result2.hasNext()).isFalse(),
                () -> assertThat(result2.getContent()).isEmpty()
        );
    }

    @Test
    @DisplayName("3. 멘티가 제안받은 커피챗에 대한 상태별 리스트에 포함된 멘토 정보를 조회한다 [수락 -> PENDING]")
    void pending() {
        // given
        final MenteeCoffeeChatQueryCondition condition = new MenteeCoffeeChatQueryCondition(mentees[0].getId(), List.of(PENDING));

        /* 페이지 1 */
        final Slice<Mentor> result1 = sut.fetchSuggestedCoffeeChatsByCondition(condition, pageable1);
        assertAll(
                () -> assertThat(result1.hasNext()).isFalse(),
                () -> assertThat(result1.getContent()).containsExactly(mentors[14], mentors[10], mentors[6])
        );

        /* 페이지 2 */
        final Slice<Mentor> result2 = sut.fetchSuggestedCoffeeChatsByCondition(condition, pageable2);
        assertAll(
                () -> assertThat(result2.hasNext()).isFalse(),
                () -> assertThat(result2.getContent()).isEmpty()
        );
    }

    @Test
    @DisplayName("4. 멘티가 제안받은 커피챗에 대한 상태별 리스트에 포함된 멘토 정보를 조회한다 [예정 -> APPROVE]")
    void approve() {
        // given
        final MenteeCoffeeChatQueryCondition condition = new MenteeCoffeeChatQueryCondition(mentees[0].getId(), List.of(APPROVE));

        /* 페이지 1 */
        final Slice<Mentor> result1 = sut.fetchSuggestedCoffeeChatsByCondition(condition, pageable1);
        assertAll(
                () -> assertThat(result1.hasNext()).isFalse(),
                () -> assertThat(result1.getContent()).containsExactly(mentors[16], mentors[5], mentors[1])
        );

        /* 페이지 2 */
        final Slice<Mentor> result2 = sut.fetchSuggestedCoffeeChatsByCondition(condition, pageable2);
        assertAll(
                () -> assertThat(result2.hasNext()).isFalse(),
                () -> assertThat(result2.getContent()).isEmpty()
        );
    }

    @Test
    @DisplayName("5. 멘티가 제안받은 커피챗에 대한 상태별 리스트에 포함된 멘토 정보를 조회한다 [완료 -> COMPLETE]")
    void complete() {
        // given
        final MenteeCoffeeChatQueryCondition condition = new MenteeCoffeeChatQueryCondition(mentees[0].getId(), List.of(COMPLETE));

        /* 페이지 1 */
        final Slice<Mentor> result1 = sut.fetchSuggestedCoffeeChatsByCondition(condition, pageable1);
        assertAll(
                () -> assertThat(result1.hasNext()).isFalse(),
                () -> assertThat(result1.getContent()).containsExactly(mentors[12])
        );

        /* 페이지 2 */
        final Slice<Mentor> result2 = sut.fetchSuggestedCoffeeChatsByCondition(condition, pageable2);
        assertAll(
                () -> assertThat(result2.hasNext()).isFalse(),
                () -> assertThat(result2.getContent()).isEmpty()
        );
    }

    @Test
    @DisplayName("6. 멘티가 제안받은 커피챗에 대한 상태별 리스트에 포함된 멘토 정보를 조회한다 [취소 -> CANCEL or REJECT]")
    void cancelOrReject() {
        // given
        final MenteeCoffeeChatQueryCondition condition = new MenteeCoffeeChatQueryCondition(mentees[0].getId(), List.of(CANCEL, REJECT));

        /* 페이지 1 */
        final Slice<Mentor> result1 = sut.fetchSuggestedCoffeeChatsByCondition(condition, pageable1);
        assertAll(
                () -> assertThat(result1.hasNext()).isFalse(),
                () -> assertThat(result1.getContent()).containsExactly(mentors[19], mentors[17], mentors[8], mentors[3])
        );

        /* 페이지 2 */
        final Slice<Mentor> result2 = sut.fetchSuggestedCoffeeChatsByCondition(condition, pageable2);
        assertAll(
                () -> assertThat(result2.hasNext()).isFalse(),
                () -> assertThat(result2.getContent()).isEmpty()
        );
    }
}
