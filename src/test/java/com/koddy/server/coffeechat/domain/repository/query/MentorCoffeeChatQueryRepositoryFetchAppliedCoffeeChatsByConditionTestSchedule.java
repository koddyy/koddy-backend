package com.koddy.server.coffeechat.domain.repository.query;

import com.koddy.server.coffeechat.domain.model.CoffeeChat;
import com.koddy.server.coffeechat.domain.repository.CoffeeChatRepository;
import com.koddy.server.coffeechat.domain.repository.query.response.MentorCoffeeChatScheduleData;
import com.koddy.server.coffeechat.domain.repository.query.spec.AppliedCoffeeChatQueryCondition;
import com.koddy.server.common.fixture.CoffeeChatFixture.MenteeFlow;
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
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_APPROVE;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_REJECT;
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

@Import(MentorCoffeeChatScheduleQueryRepositoryImpl.class)
@DisplayName("CoffeeChat -> MentorCoffeeChatScheduleQueryRepository [fetchAppliedCoffeeChatsByCondition] 테스트")
class MentorCoffeeChatQueryRepositoryFetchAppliedCoffeeChatsByConditionTestSchedule extends CoffeeChatQueryRepositorySupporter {
    @Autowired
    private MentorCoffeeChatScheduleQueryRepositoryImpl sut;

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
    @DisplayName("1. 멘토가 신청받은 커피챗에 대한 상태별 리스트에 포함된 멘티 정보를 조회한다 [전체 -> 상태 변경 최신순]")
    void recent() {
        // given
        final AppliedCoffeeChatQueryCondition condition = new AppliedCoffeeChatQueryCondition(mentors[0].getId(), List.of());

        /* 페이지 1 */
        final Slice<MentorCoffeeChatScheduleData> result1 = sut.fetchAppliedCoffeeChatsByCondition(condition, pageable1);
        assertAll(
                () -> assertThat(result1.hasNext()).isTrue(),
                () -> assertThat(result1.getContent())
                        .map(MentorCoffeeChatScheduleData::id)
                        .containsExactly(
                                coffeeChats[19].getId(), coffeeChats[18].getId(), coffeeChats[17].getId(),
                                coffeeChats[16].getId(), coffeeChats[15].getId(), coffeeChats[14].getId(),
                                coffeeChats[13].getId(), coffeeChats[12].getId(), coffeeChats[11].getId(),
                                coffeeChats[10].getId()
                        ),
                () -> assertThat(result1.getContent())
                        .map(MentorCoffeeChatScheduleData::status)
                        .containsExactly(
                                coffeeChats[19].getStatus().getValue(), coffeeChats[18].getStatus().getValue(), coffeeChats[17].getStatus().getValue(),
                                coffeeChats[16].getStatus().getValue(), coffeeChats[15].getStatus().getValue(), coffeeChats[14].getStatus().getValue(),
                                coffeeChats[13].getStatus().getValue(), coffeeChats[12].getStatus().getValue(), coffeeChats[11].getStatus().getValue(),
                                coffeeChats[10].getStatus().getValue()
                        ),
                () -> assertThat(result1.getContent())
                        .map(MentorCoffeeChatScheduleData::menteeId)
                        .containsExactly(
                                mentees[19].getId(), mentees[18].getId(), mentees[17].getId(),
                                mentees[16].getId(), mentees[15].getId(), mentees[14].getId(),
                                mentees[13].getId(), mentees[12].getId(), mentees[11].getId(),
                                mentees[10].getId()
                        )
        );

        /* 페이지 2 */
        final Slice<MentorCoffeeChatScheduleData> result2 = sut.fetchAppliedCoffeeChatsByCondition(condition, pageable2);
        assertAll(
                () -> assertThat(result2.hasNext()).isFalse(),
                () -> assertThat(result2.getContent())
                        .map(MentorCoffeeChatScheduleData::id)
                        .containsExactly(
                                coffeeChats[9].getId(), coffeeChats[8].getId(), coffeeChats[7].getId(),
                                coffeeChats[6].getId(), coffeeChats[5].getId(), coffeeChats[4].getId(),
                                coffeeChats[3].getId(), coffeeChats[2].getId(), coffeeChats[1].getId(),
                                coffeeChats[0].getId()
                        ),
                () -> assertThat(result2.getContent())
                        .map(MentorCoffeeChatScheduleData::status)
                        .containsExactly(
                                coffeeChats[9].getStatus().getValue(), coffeeChats[8].getStatus().getValue(), coffeeChats[7].getStatus().getValue(),
                                coffeeChats[6].getStatus().getValue(), coffeeChats[5].getStatus().getValue(), coffeeChats[4].getStatus().getValue(),
                                coffeeChats[3].getStatus().getValue(), coffeeChats[2].getStatus().getValue(), coffeeChats[1].getStatus().getValue(),
                                coffeeChats[0].getStatus().getValue()
                        ),
                () -> assertThat(result2.getContent())
                        .map(MentorCoffeeChatScheduleData::menteeId)
                        .containsExactly(
                                mentees[9].getId(), mentees[8].getId(), mentees[7].getId(),
                                mentees[6].getId(), mentees[5].getId(), mentees[4].getId(),
                                mentees[3].getId(), mentees[2].getId(), mentees[1].getId(),
                                mentees[0].getId()
                        )
        );
    }

    @Test
    @DisplayName("2. 멘토가 신청받은 커피챗에 대한 상태별 리스트에 포함된 멘티 정보를 조회한다 [신청 -> MENTEE_APPLY]")
    void apply() {
        // given
        final AppliedCoffeeChatQueryCondition condition = new AppliedCoffeeChatQueryCondition(mentors[0].getId(), List.of(MENTEE_APPLY));

        /* 페이지 1 */
        final Slice<MentorCoffeeChatScheduleData> result1 = sut.fetchAppliedCoffeeChatsByCondition(condition, pageable1);
        assertAll(
                () -> assertThat(result1.hasNext()).isTrue(),
                () -> assertThat(result1.getContent())
                        .map(MentorCoffeeChatScheduleData::id)
                        .containsExactly(
                                coffeeChats[19].getId(), coffeeChats[17].getId(), coffeeChats[15].getId(),
                                coffeeChats[12].getId(), coffeeChats[10].getId(), coffeeChats[8].getId(),
                                coffeeChats[7].getId(), coffeeChats[6].getId(), coffeeChats[4].getId(),
                                coffeeChats[2].getId()
                        ),
                () -> assertThat(result1.getContent())
                        .map(MentorCoffeeChatScheduleData::status)
                        .containsExactly(
                                coffeeChats[19].getStatus().getValue(), coffeeChats[17].getStatus().getValue(), coffeeChats[15].getStatus().getValue(),
                                coffeeChats[12].getStatus().getValue(), coffeeChats[10].getStatus().getValue(), coffeeChats[8].getStatus().getValue(),
                                coffeeChats[7].getStatus().getValue(), coffeeChats[6].getStatus().getValue(), coffeeChats[4].getStatus().getValue(),
                                coffeeChats[2].getStatus().getValue()
                        ),
                () -> assertThat(result1.getContent())
                        .map(MentorCoffeeChatScheduleData::menteeId)
                        .containsExactly(
                                mentees[19].getId(), mentees[17].getId(), mentees[15].getId(),
                                mentees[12].getId(), mentees[10].getId(), mentees[8].getId(),
                                mentees[7].getId(), mentees[6].getId(), mentees[4].getId(),
                                mentees[2].getId()
                        )
        );

        /* 페이지 2 */
        final Slice<MentorCoffeeChatScheduleData> result2 = sut.fetchAppliedCoffeeChatsByCondition(condition, pageable2);
        assertAll(
                () -> assertThat(result2.hasNext()).isFalse(),
                () -> assertThat(result2.getContent())
                        .map(MentorCoffeeChatScheduleData::id)
                        .containsExactly(coffeeChats[0].getId()),
                () -> assertThat(result2.getContent())
                        .map(MentorCoffeeChatScheduleData::status)
                        .containsExactly(coffeeChats[0].getStatus().getValue()),
                () -> assertThat(result2.getContent())
                        .map(MentorCoffeeChatScheduleData::menteeId)
                        .containsExactly(mentees[0].getId())
        );
    }

    @Test
    @DisplayName("3. 멘토가 신청받은 커피챗에 대한 상태별 리스트에 포함된 멘티 정보를 조회한다 [취소 & 거절 -> MENTEE_CANCEL, MENTOR_REJECT]")
    void cancelOrReject() {
        // given
        final AppliedCoffeeChatQueryCondition condition = new AppliedCoffeeChatQueryCondition(mentors[0].getId(), List.of(MENTEE_CANCEL, MENTOR_REJECT));

        /* 페이지 1 */
        final Slice<MentorCoffeeChatScheduleData> result1 = sut.fetchAppliedCoffeeChatsByCondition(condition, pageable1);
        assertAll(
                () -> assertThat(result1.hasNext()).isFalse(),
                () -> assertThat(result1.getContent())
                        .map(MentorCoffeeChatScheduleData::id)
                        .containsExactly(coffeeChats[14].getId(), coffeeChats[13].getId(), coffeeChats[9].getId(), coffeeChats[1].getId()),
                () -> assertThat(result1.getContent())
                        .map(MentorCoffeeChatScheduleData::status)
                        .containsExactly(
                                coffeeChats[14].getStatus().getValue(),
                                coffeeChats[13].getStatus().getValue(),
                                coffeeChats[9].getStatus().getValue(),
                                coffeeChats[1].getStatus().getValue()
                        ),
                () -> assertThat(result1.getContent())
                        .map(MentorCoffeeChatScheduleData::menteeId)
                        .containsExactly(mentees[14].getId(), mentees[13].getId(), mentees[9].getId(), mentees[1].getId())
        );

        /* 페이지 2 */
        final Slice<MentorCoffeeChatScheduleData> result2 = sut.fetchAppliedCoffeeChatsByCondition(condition, pageable2);
        assertAll(
                () -> assertThat(result2.hasNext()).isFalse(),
                () -> assertThat(result2.getContent()).isEmpty()
        );
    }

    @Test
    @DisplayName("4. 멘토가 신청받은 커피챗에 대한 상태별 리스트에 포함된 멘티 정보를 조회한다 [예정 -> MENTOR_APPROVE]")
    void approve() {
        // given
        final AppliedCoffeeChatQueryCondition condition = new AppliedCoffeeChatQueryCondition(mentors[0].getId(), List.of(MENTOR_APPROVE));

        /* 페이지 1 */
        final Slice<MentorCoffeeChatScheduleData> result1 = sut.fetchAppliedCoffeeChatsByCondition(condition, pageable1);
        assertAll(
                () -> assertThat(result1.hasNext()).isFalse(),
                () -> assertThat(result1.getContent())
                        .map(MentorCoffeeChatScheduleData::id)
                        .containsExactly(coffeeChats[16].getId(), coffeeChats[11].getId(), coffeeChats[5].getId(), coffeeChats[3].getId()),
                () -> assertThat(result1.getContent())
                        .map(MentorCoffeeChatScheduleData::status)
                        .containsExactly(
                                coffeeChats[16].getStatus().getValue(),
                                coffeeChats[11].getStatus().getValue(),
                                coffeeChats[5].getStatus().getValue(),
                                coffeeChats[3].getStatus().getValue()
                        ),
                () -> assertThat(result1.getContent())
                        .map(MentorCoffeeChatScheduleData::menteeId)
                        .containsExactly(mentees[16].getId(), mentees[11].getId(), mentees[5].getId(), mentees[3].getId())
        );

        /* 페이지 2 */
        final Slice<MentorCoffeeChatScheduleData> result2 = sut.fetchAppliedCoffeeChatsByCondition(condition, pageable2);
        assertAll(
                () -> assertThat(result2.hasNext()).isFalse(),
                () -> assertThat(result2.getContent()).isEmpty()
        );
    }

    @Test
    @DisplayName("5. 멘토가 신청받은 커피챗에 대한 상태별 리스트에 포함된 멘티 정보를 조회한다 [완료 -> MENTEE_APPLY_COFFEE_CHAT_COMPLETE]")
    void complete() {
        // given
        final AppliedCoffeeChatQueryCondition condition = new AppliedCoffeeChatQueryCondition(mentors[0].getId(), List.of(MENTEE_APPLY_COFFEE_CHAT_COMPLETE));

        /* 페이지 1 */
        final Slice<MentorCoffeeChatScheduleData> result1 = sut.fetchAppliedCoffeeChatsByCondition(condition, pageable1);
        assertAll(
                () -> assertThat(result1.hasNext()).isFalse(),
                () -> assertThat(result1.getContent())
                        .map(MentorCoffeeChatScheduleData::id)
                        .containsExactly(coffeeChats[18].getId()),
                () -> assertThat(result1.getContent())
                        .map(MentorCoffeeChatScheduleData::status)
                        .containsExactly(coffeeChats[18].getStatus().getValue()),
                () -> assertThat(result1.getContent())
                        .map(MentorCoffeeChatScheduleData::menteeId)
                        .containsExactly(mentees[18].getId())
        );

        /* 페이지 2 */
        final Slice<MentorCoffeeChatScheduleData> result2 = sut.fetchAppliedCoffeeChatsByCondition(condition, pageable2);
        assertAll(
                () -> assertThat(result2.hasNext()).isFalse(),
                () -> assertThat(result2.getContent()).isEmpty()
        );
    }
}
