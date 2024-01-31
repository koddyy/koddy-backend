package com.koddy.server.coffeechat.domain.repository.query;

import com.koddy.server.coffeechat.domain.model.CoffeeChat;
import com.koddy.server.coffeechat.domain.repository.CoffeeChatRepository;
import com.koddy.server.coffeechat.domain.repository.query.response.MentorCoffeeChatScheduleData;
import com.koddy.server.coffeechat.domain.repository.query.spec.MentorCoffeeChatQueryCondition;
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
@DisplayName("CoffeeChat -> MentorCoffeeChatScheduleQueryRepository [fetchSuggestedCoffeeChatsByCondition] 테스트")
class MentorCoffeeChatQueryRepositoryFetchSuggestedCoffeeChatsByConditionTestSchedule extends CoffeeChatQueryRepositorySupporter {
    @Autowired
    private MentorCoffeeChatScheduleQueryRepositoryImpl sut;

    @Autowired
    private CoffeeChatRepository coffeeChatRepository;

    private CoffeeChat[] coffeeChats = new CoffeeChat[20];

    @BeforeEach
    void setUp() {
        initMembers();
        coffeeChats = coffeeChatRepository.saveAll(List.of(
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
    @DisplayName("1. 멘토가 제안한 커피챗에 대한 상태별 리스트에 포함된 멘티 정보를 조회한다 [전체 -> 상태 변경 최신순]")
    void recent() {
        // given
        final MentorCoffeeChatQueryCondition condition = new MentorCoffeeChatQueryCondition(mentors[0].getId(), null);

        /* 페이지 1 */
        final Slice<MentorCoffeeChatScheduleData> result1 = sut.fetchSuggestedCoffeeChatsByCondition(condition, pageable1);
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
        final Slice<MentorCoffeeChatScheduleData> result2 = sut.fetchSuggestedCoffeeChatsByCondition(condition, pageable2);
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
    @DisplayName("2. 멘토가 제안한 커피챗에 대한 상태별 리스트에 포함된 멘티 정보를 조회한다 [제안 -> APPLY]")
    void apply() {
        // given
        final MentorCoffeeChatQueryCondition condition = new MentorCoffeeChatQueryCondition(mentors[0].getId(), List.of(APPLY));

        /* 페이지 1 */
        final Slice<MentorCoffeeChatScheduleData> result1 = sut.fetchSuggestedCoffeeChatsByCondition(condition, pageable1);
        assertAll(
                () -> assertThat(result1.hasNext()).isFalse(),
                () -> assertThat(result1.getContent())
                        .map(MentorCoffeeChatScheduleData::id)
                        .containsExactly(
                                coffeeChats[19].getId(), coffeeChats[16].getId(), coffeeChats[13].getId(),
                                coffeeChats[11].getId(), coffeeChats[10].getId(), coffeeChats[8].getId(),
                                coffeeChats[4].getId(), coffeeChats[2].getId(), coffeeChats[0].getId()
                        ),
                () -> assertThat(result1.getContent())
                        .map(MentorCoffeeChatScheduleData::status)
                        .containsExactly(
                                coffeeChats[19].getStatus().getValue(), coffeeChats[16].getStatus().getValue(), coffeeChats[13].getStatus().getValue(),
                                coffeeChats[11].getStatus().getValue(), coffeeChats[10].getStatus().getValue(), coffeeChats[8].getStatus().getValue(),
                                coffeeChats[4].getStatus().getValue(), coffeeChats[2].getStatus().getValue(), coffeeChats[0].getStatus().getValue()
                        ),
                () -> assertThat(result1.getContent())
                        .map(MentorCoffeeChatScheduleData::menteeId)
                        .containsExactly(
                                mentees[19].getId(), mentees[16].getId(), mentees[13].getId(),
                                mentees[11].getId(), mentees[10].getId(), mentees[8].getId(),
                                mentees[4].getId(), mentees[2].getId(), mentees[0].getId()
                        )
        );

        /* 페이지 2 */
        final Slice<MentorCoffeeChatScheduleData> result2 = sut.fetchSuggestedCoffeeChatsByCondition(condition, pageable2);
        assertAll(
                () -> assertThat(result2.hasNext()).isFalse(),
                () -> assertThat(result2.getContent()).isEmpty()
        );
    }

    @Test
    @DisplayName("3. 멘토가 제안한 커피챗에 대한 상태별 리스트에 포함된 멘티 정보를 조회한다 [수락 -> PENDING]")
    void pending() {
        // given
        final MentorCoffeeChatQueryCondition condition = new MentorCoffeeChatQueryCondition(mentors[0].getId(), List.of(PENDING));

        /* 페이지 1 */
        final Slice<MentorCoffeeChatScheduleData> result1 = sut.fetchSuggestedCoffeeChatsByCondition(condition, pageable1);
        assertAll(
                () -> assertThat(result1.hasNext()).isFalse(),
                () -> assertThat(result1.getContent())
                        .map(MentorCoffeeChatScheduleData::id)
                        .containsExactly(
                                coffeeChats[18].getId(), coffeeChats[14].getId(), coffeeChats[3].getId()
                        ),
                () -> assertThat(result1.getContent())
                        .map(MentorCoffeeChatScheduleData::status)
                        .containsExactly(
                                coffeeChats[18].getStatus().getValue(),
                                coffeeChats[14].getStatus().getValue(),
                                coffeeChats[3].getStatus().getValue()
                        ),
                () -> assertThat(result1.getContent())
                        .map(MentorCoffeeChatScheduleData::menteeId)
                        .containsExactly(mentees[18].getId(), mentees[14].getId(), mentees[3].getId())
        );

        /* 페이지 2 */
        final Slice<MentorCoffeeChatScheduleData> result2 = sut.fetchSuggestedCoffeeChatsByCondition(condition, pageable2);
        assertAll(
                () -> assertThat(result2.hasNext()).isFalse(),
                () -> assertThat(result2.getContent()).isEmpty()
        );
    }

    @Test
    @DisplayName("4. 멘토가 제안한 커피챗에 대한 상태별 리스트에 포함된 멘티 정보를 조회한다 [예정 -> APPROVE]")
    void approve() {
        // given
        final MentorCoffeeChatQueryCondition condition = new MentorCoffeeChatQueryCondition(mentors[0].getId(), List.of(APPROVE));

        /* 페이지 1 */
        final Slice<MentorCoffeeChatScheduleData> result1 = sut.fetchSuggestedCoffeeChatsByCondition(condition, pageable1);
        assertAll(
                () -> assertThat(result1.hasNext()).isFalse(),
                () -> assertThat(result1.getContent())
                        .map(MentorCoffeeChatScheduleData::id)
                        .containsExactly(coffeeChats[12].getId(), coffeeChats[5].getId()),
                () -> assertThat(result1.getContent())
                        .map(MentorCoffeeChatScheduleData::status)
                        .containsExactly(coffeeChats[12].getStatus().getValue(), coffeeChats[5].getStatus().getValue()),
                () -> assertThat(result1.getContent())
                        .map(MentorCoffeeChatScheduleData::menteeId)
                        .containsExactly(mentees[12].getId(), mentees[5].getId())
        );

        /* 페이지 2 */
        final Slice<MentorCoffeeChatScheduleData> result2 = sut.fetchSuggestedCoffeeChatsByCondition(condition, pageable2);
        assertAll(
                () -> assertThat(result2.hasNext()).isFalse(),
                () -> assertThat(result2.getContent()).isEmpty()
        );
    }

    @Test
    @DisplayName("5. 멘토가 제안한 커피챗에 대한 상태별 리스트에 포함된 멘티 정보를 조회한다 [완료 -> COMPLETE]")
    void complete() {
        // given
        final MentorCoffeeChatQueryCondition condition = new MentorCoffeeChatQueryCondition(mentors[0].getId(), List.of(COMPLETE));

        /* 페이지 1 */
        final Slice<MentorCoffeeChatScheduleData> result1 = sut.fetchSuggestedCoffeeChatsByCondition(condition, pageable1);
        assertAll(
                () -> assertThat(result1.hasNext()).isFalse(),
                () -> assertThat(result1.getContent())
                        .map(MentorCoffeeChatScheduleData::id)
                        .containsExactly(coffeeChats[15].getId()),
                () -> assertThat(result1.getContent())
                        .map(MentorCoffeeChatScheduleData::status)
                        .containsExactly(coffeeChats[15].getStatus().getValue()),
                () -> assertThat(result1.getContent())
                        .map(MentorCoffeeChatScheduleData::menteeId)
                        .containsExactly(mentees[15].getId())
        );

        /* 페이지 2 */
        final Slice<MentorCoffeeChatScheduleData> result2 = sut.fetchSuggestedCoffeeChatsByCondition(condition, pageable2);
        assertAll(
                () -> assertThat(result2.hasNext()).isFalse(),
                () -> assertThat(result2.getContent()).isEmpty()
        );
    }

    @Test
    @DisplayName("6. 멘토가 제안한 커피챗에 대한 상태별 리스트에 포함된 멘티 정보를 조회한다 [취소 -> CANCEL or REJECT]")
    void cancelOrReject() {
        // given
        final MentorCoffeeChatQueryCondition condition = new MentorCoffeeChatQueryCondition(mentors[0].getId(), List.of(CANCEL, REJECT));

        /* 페이지 1 */
        final Slice<MentorCoffeeChatScheduleData> result1 = sut.fetchSuggestedCoffeeChatsByCondition(condition, pageable1);
        assertAll(
                () -> assertThat(result1.hasNext()).isFalse(),
                () -> assertThat(result1.getContent())
                        .map(MentorCoffeeChatScheduleData::id)
                        .containsExactly(
                                coffeeChats[17].getId(), coffeeChats[9].getId(), coffeeChats[7].getId(),
                                coffeeChats[6].getId(), coffeeChats[1].getId()
                        ),
                () -> assertThat(result1.getContent())
                        .map(MentorCoffeeChatScheduleData::status)
                        .containsExactly(
                                coffeeChats[17].getStatus().getValue(), coffeeChats[9].getStatus().getValue(), coffeeChats[7].getStatus().getValue(),
                                coffeeChats[6].getStatus().getValue(), coffeeChats[1].getStatus().getValue()
                        ),
                () -> assertThat(result1.getContent())
                        .map(MentorCoffeeChatScheduleData::menteeId)
                        .containsExactly(
                                mentees[17].getId(), mentees[9].getId(), mentees[7].getId(),
                                mentees[6].getId(), mentees[1].getId()
                        )
        );

        /* 페이지 2 */
        final Slice<MentorCoffeeChatScheduleData> result2 = sut.fetchSuggestedCoffeeChatsByCondition(condition, pageable2);
        assertAll(
                () -> assertThat(result2.hasNext()).isFalse(),
                () -> assertThat(result2.getContent()).isEmpty()
        );
    }
}
