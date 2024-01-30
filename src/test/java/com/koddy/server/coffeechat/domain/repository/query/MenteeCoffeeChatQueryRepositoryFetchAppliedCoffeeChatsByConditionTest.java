package com.koddy.server.coffeechat.domain.repository.query;

import com.koddy.server.coffeechat.domain.model.CoffeeChat;
import com.koddy.server.coffeechat.domain.repository.CoffeeChatRepository;
import com.koddy.server.coffeechat.domain.repository.query.spec.MenteeCoffeeChatQueryCondition;
import com.koddy.server.common.fixture.CoffeeChatFixture.MenteeFlow;
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

    @BeforeEach
    void setUp() {
        initMembers();
        coffeeChatRepository.saveAll(List.of(
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
    @DisplayName("1. 멘티가 신청한 커피챗에 대한 상태별 리스트에 포함된 멘토 정보를 조회한다 [전체 -> 상태 변경 최신순]")
    void recent() {
        // given
        final MenteeCoffeeChatQueryCondition condition = new MenteeCoffeeChatQueryCondition(mentees[0].getId(), List.of());

        /* 페이지 1 */
        final Slice<Mentor> result1 = sut.fetchAppliedCoffeeChatsByCondition(condition, pageable1);
        assertAll(
                () -> assertThat(result1.hasNext()).isTrue(),
                () -> assertThat(result1.getContent()).containsExactly(
                        mentors[19], mentors[18], mentors[17], mentors[16], mentors[15],
                        mentors[14], mentors[13], mentors[12], mentors[11], mentors[10]
                )
        );

        /* 페이지 2 */
        final Slice<Mentor> result2 = sut.fetchAppliedCoffeeChatsByCondition(condition, pageable2);
        assertAll(
                () -> assertThat(result2.hasNext()).isFalse(),
                () -> assertThat(result2.getContent()).containsExactly(
                        mentors[9], mentors[8], mentors[7], mentors[6], mentors[5],
                        mentors[4], mentors[3], mentors[2], mentors[1], mentors[0]
                )
        );
    }

    @Test
    @DisplayName("2. 멘티가 신청한 커피챗에 대한 상태별 리스트에 포함된 멘토 정보를 조회한다 [신청 -> APPLY]")
    void apply() {
        // given
        final MenteeCoffeeChatQueryCondition condition = new MenteeCoffeeChatQueryCondition(mentees[0].getId(), List.of(APPLY));

        /* 페이지 1 */
        final Slice<Mentor> result1 = sut.fetchAppliedCoffeeChatsByCondition(condition, pageable1);
        assertAll(
                () -> assertThat(result1.hasNext()).isFalse(),
                () -> assertThat(result1.getContent()).containsExactly(
                        mentors[18], mentors[17], mentors[15], mentors[11],
                        mentors[9], mentors[6], mentors[2], mentors[0]
                )
        );

        /* 페이지 2 */
        final Slice<Mentor> result2 = sut.fetchAppliedCoffeeChatsByCondition(condition, pageable2);
        assertAll(
                () -> assertThat(result2.hasNext()).isFalse(),
                () -> assertThat(result2.getContent()).isEmpty()
        );
    }

    @Test
    @DisplayName("3. 멘티가 신청한 커피챗에 대한 상태별 리스트에 포함된 멘토 정보를 조회한다 [예정 -> APPROVE]")
    void approve() {
        // given
        final MenteeCoffeeChatQueryCondition condition = new MenteeCoffeeChatQueryCondition(mentees[0].getId(), List.of(APPROVE));

        /* 페이지 1 */
        final Slice<Mentor> result1 = sut.fetchAppliedCoffeeChatsByCondition(condition, pageable1);
        assertAll(
                () -> assertThat(result1.hasNext()).isFalse(),
                () -> assertThat(result1.getContent()).containsExactly(mentors[14], mentors[7], mentors[5], mentors[1])
        );

        /* 페이지 2 */
        final Slice<Mentor> result2 = sut.fetchAppliedCoffeeChatsByCondition(condition, pageable2);
        assertAll(
                () -> assertThat(result2.hasNext()).isFalse(),
                () -> assertThat(result2.getContent()).isEmpty()
        );
    }

    @Test
    @DisplayName("4. 멘티가 신청한 커피챗에 대한 상태별 리스트에 포함된 멘토 정보를 조회한다 [완료 -> COMPLETE]")
    void complete() {
        // given
        final MenteeCoffeeChatQueryCondition condition = new MenteeCoffeeChatQueryCondition(mentees[0].getId(), List.of(COMPLETE));

        /* 페이지 1 */
        final Slice<Mentor> result1 = sut.fetchAppliedCoffeeChatsByCondition(condition, pageable1);
        assertAll(
                () -> assertThat(result1.hasNext()).isFalse(),
                () -> assertThat(result1.getContent()).containsExactly(mentors[13], mentors[8])
        );

        /* 페이지 2 */
        final Slice<Mentor> result2 = sut.fetchAppliedCoffeeChatsByCondition(condition, pageable2);
        assertAll(
                () -> assertThat(result2.hasNext()).isFalse(),
                () -> assertThat(result2.getContent()).isEmpty()
        );
    }

    @Test
    @DisplayName("5. 멘티가 신청한 커피챗에 대한 상태별 리스트에 포함된 멘토 정보를 조회한다 [취소 -> CANCEL or REJECT]")
    void cancelOrReject() {
        // given
        final MenteeCoffeeChatQueryCondition condition = new MenteeCoffeeChatQueryCondition(mentees[0].getId(), List.of(CANCEL, REJECT));

        /* 페이지 1 */
        final Slice<Mentor> result1 = sut.fetchAppliedCoffeeChatsByCondition(condition, pageable1);
        assertAll(
                () -> assertThat(result1.hasNext()).isFalse(),
                () -> assertThat(result1.getContent()).containsExactly(
                        mentors[19], mentors[16], mentors[12],
                        mentors[10], mentors[4], mentors[3]
                )
        );

        /* 페이지 2 */
        final Slice<Mentor> result2 = sut.fetchAppliedCoffeeChatsByCondition(condition, pageable2);
        assertAll(
                () -> assertThat(result2.hasNext()).isFalse(),
                () -> assertThat(result2.getContent()).isEmpty()
        );
    }
}
