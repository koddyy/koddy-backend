package com.koddy.server.coffeechat.domain.repository.query;

import com.koddy.server.coffeechat.domain.model.CoffeeChat;
import com.koddy.server.coffeechat.domain.repository.CoffeeChatRepository;
import com.koddy.server.coffeechat.domain.repository.query.response.MentorCoffeeChatScheduleData;
import com.koddy.server.coffeechat.domain.repository.query.spec.MentorCoffeeChatQueryCondition;
import com.koddy.server.common.fixture.CoffeeChatFixture.MenteeFlow;
import com.koddy.server.common.fixture.CoffeeChatFixture.MentorFlow;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Slice;

import java.util.List;

import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTEE_APPLY;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTEE_APPLY_COFFEE_CHAT_COMPLETE;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTEE_CANCEL;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTEE_PENDING;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTEE_REJECT;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_APPROVE;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_CANCEL;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_FINALLY_APPROVE;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_FINALLY_REJECT;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_REJECT;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_SUGGEST;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_SUGGEST_COFFEE_CHAT_COMPLETE;
import static com.koddy.server.common.fixture.CoffeeChatFixture.금요일_1주차_20_00_시작;
import static com.koddy.server.common.fixture.CoffeeChatFixture.금요일_2주차_20_00_시작;
import static com.koddy.server.common.fixture.CoffeeChatFixture.금요일_3주차_20_00_시작;
import static com.koddy.server.common.fixture.CoffeeChatFixture.수요일_1주차_20_00_시작;
import static com.koddy.server.common.fixture.CoffeeChatFixture.수요일_2주차_20_00_시작;
import static com.koddy.server.common.fixture.CoffeeChatFixture.수요일_3주차_20_00_시작;
import static com.koddy.server.common.fixture.CoffeeChatFixture.월요일_1주차_20_00_시작;
import static com.koddy.server.common.fixture.CoffeeChatFixture.월요일_2주차_20_00_시작;
import static com.koddy.server.common.fixture.CoffeeChatFixture.월요일_3주차_20_00_시작;
import static com.koddy.server.common.fixture.CoffeeChatFixture.토요일_1주차_20_00_시작;
import static com.koddy.server.common.fixture.CoffeeChatFixture.토요일_2주차_20_00_시작;
import static com.koddy.server.common.fixture.CoffeeChatFixture.토요일_3주차_20_00_시작;
import static com.koddy.server.common.fixture.CoffeeChatFixture.화요일_1주차_20_00_시작;
import static com.koddy.server.common.fixture.CoffeeChatFixture.화요일_2주차_20_00_시작;
import static com.koddy.server.common.fixture.CoffeeChatFixture.화요일_3주차_20_00_시작;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Import(CoffeeChatScheduleQueryRepositoryImpl.class)
@DisplayName("CoffeeChat -> CoffeeChatScheduleQueryRepository [fetchMentorCoffeeChatSchedules] 테스트")
public class CoffeeChatScheduleQueryRepositoryFetchMentorCoffeeChatSchedulesTest extends CoffeeChatScheduleQueryRepositorySupporter {
    @Autowired
    private CoffeeChatScheduleQueryRepositoryImpl sut;

    @Autowired
    private CoffeeChatRepository coffeeChatRepository;

    private CoffeeChat[] coffeeChats = new CoffeeChat[30];

    @BeforeEach
    void setUp() {
        initMembers();
        coffeeChats = coffeeChatRepository.saveAll(List.of(
                MenteeFlow.apply(월요일_1주차_20_00_시작, mentees[0], mentors[0]), // 대기
                MenteeFlow.applyAndApprove(화요일_1주차_20_00_시작, mentees[1], mentors[0]), // 예정
                MenteeFlow.apply(수요일_1주차_20_00_시작, mentees[2], mentors[0]), // 대기
                MenteeFlow.applyAndReject(토요일_1주차_20_00_시작, mentees[3], mentors[0]), // 지나간
                MenteeFlow.applyAndCancel(금요일_1주차_20_00_시작, mentees[4], mentors[0]), // 지나간
                MenteeFlow.applyAndApprove(월요일_2주차_20_00_시작, mentees[5], mentors[0]), // 예정
                MenteeFlow.apply(화요일_2주차_20_00_시작, mentees[6], mentors[0]), // 대기
                MenteeFlow.applyAndApprove(수요일_2주차_20_00_시작, mentees[7], mentors[0]), // 예정
                MenteeFlow.applyAndComplete(토요일_2주차_20_00_시작, mentees[8], mentors[0]), // 지나간
                MenteeFlow.apply(금요일_2주차_20_00_시작, mentees[9], mentors[0]), // 대기
                MenteeFlow.applyAndReject(월요일_3주차_20_00_시작, mentees[10], mentors[0]), // 지나간
                MenteeFlow.apply(화요일_3주차_20_00_시작, mentees[11], mentors[0]), // 대기
                MenteeFlow.applyAndCancel(수요일_3주차_20_00_시작, mentees[12], mentors[0]), // 지나간
                MenteeFlow.applyAndComplete(토요일_3주차_20_00_시작, mentees[13], mentors[0]), // 지나간
                MenteeFlow.applyAndApprove(금요일_3주차_20_00_시작, mentees[14], mentors[0]), // 예정

                MentorFlow.suggest(mentors[0], mentees[15]), // 대기
                MentorFlow.suggestAndFinallyApprove(화요일_1주차_20_00_시작, mentors[0], mentees[16]), // 예정
                MentorFlow.suggest(mentors[0], mentees[17]), // 대기
                MentorFlow.suggestAndCancel(mentors[0], mentees[18]), // 지나간
                MentorFlow.suggest(mentors[0], mentees[19]), // 대기
                MentorFlow.suggestAndFinallyApprove(월요일_2주차_20_00_시작, mentors[0], mentees[0]), // 예정
                MentorFlow.suggestAndPending(화요일_2주차_20_00_시작, mentors[0], mentees[1]), // 대기
                MentorFlow.suggest(mentors[0], mentees[2]), // 대기
                MentorFlow.suggestAndFinallyReject(토요일_2주차_20_00_시작, mentors[0], mentees[3]), // 지나간
                MentorFlow.suggest(mentors[0], mentees[4]), // 대기
                MentorFlow.suggestAndPending(월요일_3주차_20_00_시작, mentors[0], mentees[5]), // 대기
                MentorFlow.suggest(mentors[0], mentees[6]), // 대기
                MentorFlow.suggestAndComplete(수요일_3주차_20_00_시작, mentors[0], mentees[7]), // 지나간
                MentorFlow.suggest(mentors[0], mentees[8]), // 대기
                MentorFlow.suggestAndPending(토요일_3주차_20_00_시작, mentors[0], mentees[9]) // 대기
        )).toArray(CoffeeChat[]::new);
    }

    @Test
    @DisplayName("1. 멘토의 내 일정 `대기 상태` 커피챗 정보를 조회한다 [MENTEE_APPLY / MENTOR_SUGGEST / MENTEE_PENDING]")
    void waiting() {
        // given
        final MentorCoffeeChatQueryCondition condition = new MentorCoffeeChatQueryCondition(
                mentors[0].getId(),
                List.of(MENTEE_APPLY, MENTOR_SUGGEST, MENTEE_PENDING)
        );

        /* 페이지 1 */
        final Slice<MentorCoffeeChatScheduleData> result1 = sut.fetchMentorCoffeeChatSchedules(condition, pageable1);
        assertAll(
                () -> assertThat(result1.hasNext()).isTrue(),
                () -> assertThat(result1.getContent())
                        .map(MentorCoffeeChatScheduleData::id)
                        .containsExactly(
                                coffeeChats[29].getId(), coffeeChats[28].getId(), coffeeChats[26].getId(),
                                coffeeChats[25].getId(), coffeeChats[24].getId(), coffeeChats[22].getId(),
                                coffeeChats[21].getId(), coffeeChats[19].getId(), coffeeChats[17].getId(),
                                coffeeChats[15].getId()
                        ),
                () -> assertThat(result1.getContent())
                        .map(MentorCoffeeChatScheduleData::status)
                        .containsExactly(
                                coffeeChats[29].getStatus().name(), coffeeChats[28].getStatus().name(), coffeeChats[26].getStatus().name(),
                                coffeeChats[25].getStatus().name(), coffeeChats[24].getStatus().name(), coffeeChats[22].getStatus().name(),
                                coffeeChats[21].getStatus().name(), coffeeChats[19].getStatus().name(), coffeeChats[17].getStatus().name(),
                                coffeeChats[15].getStatus().name()
                        ),
                () -> assertThat(result1.getContent())
                        .map(MentorCoffeeChatScheduleData::menteeId)
                        .containsExactly(
                                mentees[9].getId(), mentees[8].getId(), mentees[6].getId(),
                                mentees[5].getId(), mentees[4].getId(), mentees[2].getId(),
                                mentees[1].getId(), mentees[19].getId(), mentees[17].getId(),
                                mentees[15].getId()
                        )
        );

        /* 페이지 2 */
        final Slice<MentorCoffeeChatScheduleData> result2 = sut.fetchMentorCoffeeChatSchedules(condition, pageable2);
        assertAll(
                () -> assertThat(result2.hasNext()).isFalse(),
                () -> assertThat(result2.getContent())
                        .map(MentorCoffeeChatScheduleData::id)
                        .containsExactly(
                                coffeeChats[11].getId(), coffeeChats[9].getId(), coffeeChats[6].getId(),
                                coffeeChats[2].getId(), coffeeChats[0].getId()
                        ),
                () -> assertThat(result2.getContent())
                        .map(MentorCoffeeChatScheduleData::status)
                        .containsExactly(
                                coffeeChats[11].getStatus().name(), coffeeChats[9].getStatus().name(), coffeeChats[6].getStatus().name(),
                                coffeeChats[2].getStatus().name(), coffeeChats[0].getStatus().name()
                        ),
                () -> assertThat(result2.getContent())
                        .map(MentorCoffeeChatScheduleData::menteeId)
                        .containsExactly(
                                mentees[11].getId(), mentees[9].getId(), mentees[6].getId(),
                                mentees[2].getId(), mentees[0].getId()
                        )
        );
    }

    @Test
    @DisplayName("2. 멘토의 내 일정 `예정 상태` 커피챗 정보를 조회한다 [MENTOR_APPROVE & MENTOR_FINALLY_APPROVE]")
    void scheduled() {
        // given
        final MentorCoffeeChatQueryCondition condition = new MentorCoffeeChatQueryCondition(
                mentors[0].getId(),
                List.of(MENTOR_APPROVE, MENTOR_FINALLY_APPROVE)
        );

        /* 페이지 1 */
        final Slice<MentorCoffeeChatScheduleData> result1 = sut.fetchMentorCoffeeChatSchedules(condition, pageable1);
        assertAll(
                () -> assertThat(result1.hasNext()).isFalse(),
                () -> assertThat(result1.getContent())
                        .map(MentorCoffeeChatScheduleData::id)
                        .containsExactly(
                                coffeeChats[20].getId(), coffeeChats[16].getId(), coffeeChats[14].getId(),
                                coffeeChats[7].getId(), coffeeChats[5].getId(), coffeeChats[1].getId()
                        ),
                () -> assertThat(result1.getContent())
                        .map(MentorCoffeeChatScheduleData::status)
                        .containsExactly(
                                coffeeChats[20].getStatus().name(), coffeeChats[16].getStatus().name(), coffeeChats[14].getStatus().name(),
                                coffeeChats[7].getStatus().name(), coffeeChats[5].getStatus().name(), coffeeChats[1].getStatus().name()
                        ),
                () -> assertThat(result1.getContent())
                        .map(MentorCoffeeChatScheduleData::menteeId)
                        .containsExactly(
                                mentees[0].getId(), mentees[16].getId(), mentees[14].getId(),
                                mentees[7].getId(), mentees[5].getId(), mentees[1].getId()
                        )
        );

        /* 페이지 2 */
        final Slice<MentorCoffeeChatScheduleData> result2 = sut.fetchMentorCoffeeChatSchedules(condition, pageable2);
        assertAll(
                () -> assertThat(result2.hasNext()).isFalse(),
                () -> assertThat(result2.getContent()).isEmpty()
        );
    }

    @Test
    @DisplayName("3. 멘토의 내 일정 `지나간 상태` 커피챗 정보를 조회한다 [MENTEE_CANCEL / MENTOR_REJECT / MENTEE_APPLY_COFFEE_CHAT_COMPLETE / MENTOR_CANCEL / MENTEE_REJECT / MENTOR_FINALLY_REJECT / MENTOR_SUGGEST_COFFEE_CHAT_COMPLETE]")
    void passed() {
        // given
        final MentorCoffeeChatQueryCondition condition = new MentorCoffeeChatQueryCondition(
                mentors[0].getId(),
                List.of(
                        MENTEE_CANCEL, MENTOR_REJECT, MENTEE_APPLY_COFFEE_CHAT_COMPLETE,
                        MENTOR_CANCEL, MENTEE_REJECT, MENTOR_FINALLY_REJECT, MENTOR_SUGGEST_COFFEE_CHAT_COMPLETE
                )
        );

        /* 페이지 1 */
        final Slice<MentorCoffeeChatScheduleData> result1 = sut.fetchMentorCoffeeChatSchedules(condition, pageable1);
        assertAll(
                () -> assertThat(result1.hasNext()).isFalse(),
                () -> assertThat(result1.getContent())
                        .map(MentorCoffeeChatScheduleData::id)
                        .containsExactly(
                                coffeeChats[27].getId(), coffeeChats[23].getId(), coffeeChats[18].getId(),
                                coffeeChats[13].getId(), coffeeChats[12].getId(), coffeeChats[10].getId(),
                                coffeeChats[8].getId(), coffeeChats[4].getId(), coffeeChats[3].getId()
                        ),
                () -> assertThat(result1.getContent())
                        .map(MentorCoffeeChatScheduleData::status)
                        .containsExactly(
                                coffeeChats[27].getStatus().name(), coffeeChats[23].getStatus().name(), coffeeChats[18].getStatus().name(),
                                coffeeChats[13].getStatus().name(), coffeeChats[12].getStatus().name(), coffeeChats[10].getStatus().name(),
                                coffeeChats[8].getStatus().name(), coffeeChats[4].getStatus().name(), coffeeChats[3].getStatus().name()
                        ),
                () -> assertThat(result1.getContent())
                        .map(MentorCoffeeChatScheduleData::menteeId)
                        .containsExactly(
                                mentees[7].getId(), mentees[3].getId(), mentees[18].getId(),
                                mentees[13].getId(), mentees[12].getId(), mentees[10].getId(),
                                mentees[8].getId(), mentees[4].getId(), mentees[3].getId()
                        )
        );

        /* 페이지 2 */
        final Slice<MentorCoffeeChatScheduleData> result2 = sut.fetchMentorCoffeeChatSchedules(condition, pageable2);
        assertAll(
                () -> assertThat(result2.hasNext()).isFalse(),
                () -> assertThat(result2.getContent()).isEmpty()
        );
    }
}
