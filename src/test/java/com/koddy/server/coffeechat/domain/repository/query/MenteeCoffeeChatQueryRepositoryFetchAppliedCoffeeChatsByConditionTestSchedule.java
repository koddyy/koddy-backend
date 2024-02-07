package com.koddy.server.coffeechat.domain.repository.query;

import com.koddy.server.coffeechat.domain.model.CoffeeChat;
import com.koddy.server.coffeechat.domain.repository.CoffeeChatRepository;
import com.koddy.server.coffeechat.domain.repository.query.response.MenteeCoffeeChatScheduleData;
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

@Import(MenteeCoffeeChatScheduleQueryRepositoryImpl.class)
@DisplayName("CoffeeChat -> MenteeCoffeeChatScheduleQueryRepository [fetchAppliedCoffeeChatsByCondition] 테스트")
class MenteeCoffeeChatQueryRepositoryFetchAppliedCoffeeChatsByConditionTestSchedule extends CoffeeChatQueryRepositorySupporter {
    @Autowired
    private MenteeCoffeeChatScheduleQueryRepositoryImpl sut;

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
    @DisplayName("1. 멘티가 신청한 커피챗에 대한 상태별 리스트에 포함된 멘토 정보를 조회한다 [전체 -> 상태 변경 최신순]")
    void recent() {
        // given
        final AppliedCoffeeChatQueryCondition condition = new AppliedCoffeeChatQueryCondition(mentees[0].getId(), List.of());

        /* 페이지 1 */
        final Slice<MenteeCoffeeChatScheduleData> result1 = sut.fetchAppliedCoffeeChatsByCondition(condition, pageable1);
        assertAll(
                () -> assertThat(result1.hasNext()).isTrue(),
                () -> assertThat(result1.getContent())
                        .map(MenteeCoffeeChatScheduleData::id)
                        .containsExactly(
                                coffeeChats[19].getId(), coffeeChats[18].getId(), coffeeChats[17].getId(),
                                coffeeChats[16].getId(), coffeeChats[15].getId(), coffeeChats[14].getId(),
                                coffeeChats[13].getId(), coffeeChats[12].getId(), coffeeChats[11].getId(),
                                coffeeChats[10].getId()
                        ),
                () -> assertThat(result1.getContent())
                        .map(MenteeCoffeeChatScheduleData::status)
                        .containsExactly(
                                coffeeChats[19].getStatus().getValue(), coffeeChats[18].getStatus().getValue(), coffeeChats[17].getStatus().getValue(),
                                coffeeChats[16].getStatus().getValue(), coffeeChats[15].getStatus().getValue(), coffeeChats[14].getStatus().getValue(),
                                coffeeChats[13].getStatus().getValue(), coffeeChats[12].getStatus().getValue(), coffeeChats[11].getStatus().getValue(),
                                coffeeChats[10].getStatus().getValue()
                        ),
                () -> assertThat(result1.getContent())
                        .map(MenteeCoffeeChatScheduleData::mentorId)
                        .containsExactly(
                                mentors[19].getId(), mentors[18].getId(), mentors[17].getId(),
                                mentors[16].getId(), mentors[15].getId(), mentors[14].getId(),
                                mentors[13].getId(), mentors[12].getId(), mentors[11].getId(),
                                mentors[10].getId()
                        )
        );

        /* 페이지 2 */
        final Slice<MenteeCoffeeChatScheduleData> result2 = sut.fetchAppliedCoffeeChatsByCondition(condition, pageable2);
        assertAll(
                () -> assertThat(result2.hasNext()).isFalse(),
                () -> assertThat(result2.getContent())
                        .map(MenteeCoffeeChatScheduleData::id)
                        .containsExactly(
                                coffeeChats[9].getId(), coffeeChats[8].getId(), coffeeChats[7].getId(),
                                coffeeChats[6].getId(), coffeeChats[5].getId(), coffeeChats[4].getId(),
                                coffeeChats[3].getId(), coffeeChats[2].getId(), coffeeChats[1].getId(),
                                coffeeChats[0].getId()
                        ),
                () -> assertThat(result2.getContent())
                        .map(MenteeCoffeeChatScheduleData::status)
                        .containsExactly(
                                coffeeChats[9].getStatus().getValue(), coffeeChats[8].getStatus().getValue(), coffeeChats[7].getStatus().getValue(),
                                coffeeChats[6].getStatus().getValue(), coffeeChats[5].getStatus().getValue(), coffeeChats[4].getStatus().getValue(),
                                coffeeChats[3].getStatus().getValue(), coffeeChats[2].getStatus().getValue(), coffeeChats[1].getStatus().getValue(),
                                coffeeChats[0].getStatus().getValue()
                        ),
                () -> assertThat(result2.getContent())
                        .map(MenteeCoffeeChatScheduleData::mentorId)
                        .containsExactly(
                                mentors[9].getId(), mentors[8].getId(), mentors[7].getId(),
                                mentors[6].getId(), mentors[5].getId(), mentors[4].getId(),
                                mentors[3].getId(), mentors[2].getId(), mentors[1].getId(),
                                mentors[0].getId()
                        )
        );
    }

    @Test
    @DisplayName("2. 멘티가 신청한 커피챗에 대한 상태별 리스트에 포함된 멘토 정보를 조회한다 [신청 -> APPLY]")
    void apply() {
        // given
        final AppliedCoffeeChatQueryCondition condition = new AppliedCoffeeChatQueryCondition(mentees[0].getId(), List.of(MENTEE_APPLY));

        /* 페이지 1 */
        final Slice<MenteeCoffeeChatScheduleData> result1 = sut.fetchAppliedCoffeeChatsByCondition(condition, pageable1);
        assertAll(
                () -> assertThat(result1.hasNext()).isFalse(),
                () -> assertThat(result1.getContent())
                        .map(MenteeCoffeeChatScheduleData::id)
                        .containsExactly(
                                coffeeChats[18].getId(), coffeeChats[17].getId(), coffeeChats[15].getId(),
                                coffeeChats[11].getId(), coffeeChats[9].getId(), coffeeChats[6].getId(),
                                coffeeChats[2].getId(), coffeeChats[0].getId()
                        ),
                () -> assertThat(result1.getContent())
                        .map(MenteeCoffeeChatScheduleData::status)
                        .containsExactly(
                                coffeeChats[18].getStatus().getValue(), coffeeChats[17].getStatus().getValue(), coffeeChats[15].getStatus().getValue(),
                                coffeeChats[11].getStatus().getValue(), coffeeChats[9].getStatus().getValue(), coffeeChats[6].getStatus().getValue(),
                                coffeeChats[2].getStatus().getValue(), coffeeChats[0].getStatus().getValue()
                        ),
                () -> assertThat(result1.getContent())
                        .map(MenteeCoffeeChatScheduleData::mentorId)
                        .containsExactly(
                                mentors[18].getId(), mentors[17].getId(), mentors[15].getId(),
                                mentors[11].getId(), mentors[9].getId(), mentors[6].getId(),
                                mentors[2].getId(), mentors[0].getId()
                        )
        );

        /* 페이지 2 */
        final Slice<MenteeCoffeeChatScheduleData> result2 = sut.fetchAppliedCoffeeChatsByCondition(condition, pageable2);
        assertAll(
                () -> assertThat(result2.hasNext()).isFalse(),
                () -> assertThat(result2.getContent()).isEmpty()
        );
    }

    @Test
    @DisplayName("3. 멘티가 신청한 커피챗에 대한 상태별 리스트에 포함된 멘토 정보를 조회한다 [예정 -> APPROVE]")
    void approve() {
        // given
        final AppliedCoffeeChatQueryCondition condition = new AppliedCoffeeChatQueryCondition(mentees[0].getId(), List.of(MENTOR_APPROVE));

        /* 페이지 1 */
        final Slice<MenteeCoffeeChatScheduleData> result1 = sut.fetchAppliedCoffeeChatsByCondition(condition, pageable1);
        assertAll(
                () -> assertThat(result1.hasNext()).isFalse(),
                () -> assertThat(result1.getContent())
                        .map(MenteeCoffeeChatScheduleData::id)
                        .containsExactly(coffeeChats[14].getId(), coffeeChats[7].getId(), coffeeChats[5].getId(), coffeeChats[1].getId()),
                () -> assertThat(result1.getContent())
                        .map(MenteeCoffeeChatScheduleData::status)
                        .containsExactly(
                                coffeeChats[14].getStatus().getValue(),
                                coffeeChats[7].getStatus().getValue(),
                                coffeeChats[5].getStatus().getValue(),
                                coffeeChats[1].getStatus().getValue()
                        ),
                () -> assertThat(result1.getContent())
                        .map(MenteeCoffeeChatScheduleData::mentorId)
                        .containsExactly(mentors[14].getId(), mentors[7].getId(), mentors[5].getId(), mentors[1].getId())
        );

        /* 페이지 2 */
        final Slice<MenteeCoffeeChatScheduleData> result2 = sut.fetchAppliedCoffeeChatsByCondition(condition, pageable2);
        assertAll(
                () -> assertThat(result2.hasNext()).isFalse(),
                () -> assertThat(result2.getContent()).isEmpty()
        );
    }

    @Test
    @DisplayName("4. 멘티가 신청한 커피챗에 대한 상태별 리스트에 포함된 멘토 정보를 조회한다 [완료 -> COMPLETE]")
    void complete() {
        // given
        final AppliedCoffeeChatQueryCondition condition = new AppliedCoffeeChatQueryCondition(mentees[0].getId(), List.of(MENTEE_APPLY_COFFEE_CHAT_COMPLETE));

        /* 페이지 1 */
        final Slice<MenteeCoffeeChatScheduleData> result1 = sut.fetchAppliedCoffeeChatsByCondition(condition, pageable1);
        assertAll(
                () -> assertThat(result1.hasNext()).isFalse(),
                () -> assertThat(result1.getContent())
                        .map(MenteeCoffeeChatScheduleData::id)
                        .containsExactly(coffeeChats[13].getId(), coffeeChats[8].getId()),
                () -> assertThat(result1.getContent())
                        .map(MenteeCoffeeChatScheduleData::status)
                        .containsExactly(coffeeChats[13].getStatus().getValue(), coffeeChats[8].getStatus().getValue()),
                () -> assertThat(result1.getContent())
                        .map(MenteeCoffeeChatScheduleData::mentorId)
                        .containsExactly(mentors[13].getId(), mentors[8].getId())
        );

        /* 페이지 2 */
        final Slice<MenteeCoffeeChatScheduleData> result2 = sut.fetchAppliedCoffeeChatsByCondition(condition, pageable2);
        assertAll(
                () -> assertThat(result2.hasNext()).isFalse(),
                () -> assertThat(result2.getContent()).isEmpty()
        );
    }

    @Test
    @DisplayName("5. 멘티가 신청한 커피챗에 대한 상태별 리스트에 포함된 멘토 정보를 조회한다 [취소 -> CANCEL or REJECT]")
    void cancelOrReject() {
        // given
        final AppliedCoffeeChatQueryCondition condition = new AppliedCoffeeChatQueryCondition(mentees[0].getId(), List.of(MENTEE_CANCEL, MENTOR_REJECT));

        /* 페이지 1 */
        final Slice<MenteeCoffeeChatScheduleData> result1 = sut.fetchAppliedCoffeeChatsByCondition(condition, pageable1);
        assertAll(
                () -> assertThat(result1.hasNext()).isFalse(),
                () -> assertThat(result1.getContent())
                        .map(MenteeCoffeeChatScheduleData::id)
                        .containsExactly(
                                coffeeChats[19].getId(), coffeeChats[16].getId(), coffeeChats[12].getId(),
                                coffeeChats[10].getId(), coffeeChats[4].getId(), coffeeChats[3].getId()
                        ),
                () -> assertThat(result1.getContent())
                        .map(MenteeCoffeeChatScheduleData::status)
                        .containsExactly(
                                coffeeChats[19].getStatus().getValue(), coffeeChats[16].getStatus().getValue(), coffeeChats[12].getStatus().getValue(),
                                coffeeChats[10].getStatus().getValue(), coffeeChats[4].getStatus().getValue(), coffeeChats[3].getStatus().getValue()
                        ),
                () -> assertThat(result1.getContent())
                        .map(MenteeCoffeeChatScheduleData::mentorId)
                        .containsExactly(
                                mentors[19].getId(), mentors[16].getId(), mentors[12].getId(),
                                mentors[10].getId(), mentors[4].getId(), mentors[3].getId()
                        )
        );

        /* 페이지 2 */
        final Slice<MenteeCoffeeChatScheduleData> result2 = sut.fetchAppliedCoffeeChatsByCondition(condition, pageable2);
        assertAll(
                () -> assertThat(result2.hasNext()).isFalse(),
                () -> assertThat(result2.getContent()).isEmpty()
        );
    }
}
