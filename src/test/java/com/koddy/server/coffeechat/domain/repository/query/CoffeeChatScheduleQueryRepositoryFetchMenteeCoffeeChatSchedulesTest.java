//package com.koddy.server.coffeechat.domain.repository.query;
//
//import com.koddy.server.coffeechat.domain.model.CoffeeChat;
//import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus;
//import com.koddy.server.coffeechat.domain.repository.CoffeeChatRepository;
//import com.koddy.server.coffeechat.domain.repository.query.response.CoffeeChatCountPerCategory;
//import com.koddy.server.coffeechat.domain.repository.query.response.MenteeCoffeeChatScheduleData;
//import com.koddy.server.coffeechat.domain.repository.query.spec.MenteeCoffeeChatQueryCondition;
//import com.koddy.server.common.fixture.MenteeFlow;
//import com.koddy.server.common.fixture.MentorFlow;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Import;
//import org.springframework.data.domain.Slice;
//
//import java.util.List;
//
//import static com.koddy.server.common.fixture.CoffeeChatFixture.금요일_1주차_20_00_시작;
//import static com.koddy.server.common.fixture.CoffeeChatFixture.금요일_2주차_20_00_시작;
//import static com.koddy.server.common.fixture.CoffeeChatFixture.금요일_3주차_20_00_시작;
//import static com.koddy.server.common.fixture.CoffeeChatFixture.수요일_1주차_20_00_시작;
//import static com.koddy.server.common.fixture.CoffeeChatFixture.수요일_2주차_20_00_시작;
//import static com.koddy.server.common.fixture.CoffeeChatFixture.수요일_3주차_20_00_시작;
//import static com.koddy.server.common.fixture.CoffeeChatFixture.월요일_1주차_20_00_시작;
//import static com.koddy.server.common.fixture.CoffeeChatFixture.월요일_2주차_20_00_시작;
//import static com.koddy.server.common.fixture.CoffeeChatFixture.월요일_3주차_20_00_시작;
//import static com.koddy.server.common.fixture.CoffeeChatFixture.토요일_1주차_20_00_시작;
//import static com.koddy.server.common.fixture.CoffeeChatFixture.토요일_2주차_20_00_시작;
//import static com.koddy.server.common.fixture.CoffeeChatFixture.토요일_3주차_20_00_시작;
//import static com.koddy.server.common.fixture.CoffeeChatFixture.화요일_1주차_20_00_시작;
//import static com.koddy.server.common.fixture.CoffeeChatFixture.화요일_2주차_20_00_시작;
//import static com.koddy.server.common.fixture.CoffeeChatFixture.화요일_3주차_20_00_시작;
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.assertAll;
//
//@Import(CoffeeChatScheduleQueryRepositoryImpl.class)
//@DisplayName("CoffeeChat -> CoffeeChatScheduleQueryRepository [fetchMenteeCoffeeChatSchedules] 테스트")
//public class CoffeeChatScheduleQueryRepositoryFetchMenteeCoffeeChatSchedulesTest extends CoffeeChatScheduleQueryRepositorySupporter {
//    @Autowired
//    private CoffeeChatScheduleQueryRepositoryImpl sut;
//
//    @Autowired
//    private CoffeeChatRepository coffeeChatRepository;
//
//    private CoffeeChat[] coffeeChats = new CoffeeChat[30];
//
//    @BeforeEach
//    void setUp() {
//        initMembers();
//        coffeeChats = coffeeChatRepository.saveAll(List.of(
//                MenteeFlow.apply(월요일_1주차_20_00_시작, mentees[0], mentors[0]), // 대기
//                MenteeFlow.applyAndApprove(화요일_1주차_20_00_시작, mentees[0], mentors[1]), // 예정
//                MenteeFlow.apply(수요일_1주차_20_00_시작, mentees[0], mentors[2]), // 대기
//                MenteeFlow.applyAndReject(토요일_1주차_20_00_시작, mentees[0], mentors[3]), // 지나간
//                MenteeFlow.applyAndCancel(금요일_1주차_20_00_시작, mentees[0], mentors[4]), // 지나간
//                MenteeFlow.apply(월요일_2주차_20_00_시작, mentees[0], mentors[5]), // 대기
//                MenteeFlow.apply(화요일_2주차_20_00_시작, mentees[0], mentors[6]), // 대기
//                MenteeFlow.applyAndApprove(수요일_2주차_20_00_시작, mentees[0], mentors[7]), // 예정
//                MenteeFlow.applyAndComplete(토요일_2주차_20_00_시작, mentees[0], mentors[8]), // 지나간
//                MenteeFlow.apply(금요일_2주차_20_00_시작, mentees[0], mentors[9]), // 대기
//                MenteeFlow.applyAndReject(월요일_3주차_20_00_시작, mentees[0], mentors[10]), // 지나간
//                MenteeFlow.apply(화요일_3주차_20_00_시작, mentees[0], mentors[11]), // 대기
//                MenteeFlow.applyAndCancel(수요일_3주차_20_00_시작, mentees[0], mentors[12]), // 지나간
//                MenteeFlow.applyAndComplete(토요일_3주차_20_00_시작, mentees[0], mentors[13]), // 지나간
//                MenteeFlow.apply(금요일_3주차_20_00_시작, mentees[0], mentors[14]), // 대기
//
//                MentorFlow.suggest(mentors[15], mentees[0]), // 제안
//                MentorFlow.suggestAndPending(화요일_1주차_20_00_시작, mentors[16], mentees[0]), // 대기
//                MentorFlow.suggest(mentors[17], mentees[0]), // 제안
//                MentorFlow.suggestAndCancel(mentors[18], mentees[0]), // 지나간
//                MentorFlow.suggest(mentors[19], mentees[0]), // 제안
//                MentorFlow.suggestAndFinallyApprove(월요일_2주차_20_00_시작, mentors[0], mentees[0]), // 예정
//                MentorFlow.suggestAndPending(화요일_2주차_20_00_시작, mentors[1], mentees[0]), // 대기
//                MentorFlow.suggest(mentors[2], mentees[0]), // 제안
//                MentorFlow.suggestAndFinallyCancel(토요일_2주차_20_00_시작, mentors[3], mentees[0]), // 지나간
//                MentorFlow.suggest(mentors[4], mentees[0]), // 제안
//                MentorFlow.suggestAndPending(월요일_3주차_20_00_시작, mentors[5], mentees[0]), // 대기
//                MentorFlow.suggest(mentors[6], mentees[0]), // 제안
//                MentorFlow.suggestAndComplete(수요일_3주차_20_00_시작, mentors[7], mentees[0]), // 지나간
//                MentorFlow.suggest(mentors[8], mentees[0]), // 제안
//                MentorFlow.suggestAndPending(토요일_3주차_20_00_시작, mentors[9], mentees[0]) // 대기
//        )).toArray(CoffeeChat[]::new);
//    }
//
//    @Test
//    @DisplayName("0. 멘티의 상태별 커피챗 개수를 조회한다 [대기, 제안, 예정, 지나간]")
//    void counts() {
//        final CoffeeChatCountPerCategory result = sut.fetchMenteeCoffeeChatCountPerCategory(mentees[0].getId());
//        assertAll(
//                () -> assertThat(result.waiting()).isEqualTo(11),
//                () -> assertThat(result.suggested()).isEqualTo(7),
//                () -> assertThat(result.scheduled()).isEqualTo(3),
//                () -> assertThat(result.passed()).isEqualTo(9),
//                () -> assertThat(coffeeChatRepository.countByMenteeIdAndStatusIn(mentees[0].getId(), CoffeeChatStatus.withWaitingCategory())).isEqualTo(11),
//                () -> assertThat(coffeeChatRepository.countByMenteeIdAndStatusIn(mentees[0].getId(), CoffeeChatStatus.withSuggstedCategory())).isEqualTo(7),
//                () -> assertThat(coffeeChatRepository.countByMenteeIdAndStatusIn(mentees[0].getId(), CoffeeChatStatus.withScheduledCategory())).isEqualTo(3),
//                () -> assertThat(coffeeChatRepository.countByMenteeIdAndStatusIn(mentees[0].getId(), CoffeeChatStatus.withPassedCategory())).isEqualTo(9)
//        );
//    }
//
//    @Test
//    @DisplayName("1. 멘티의 내 일정 `대기 상태` 커피챗 정보를 조회한다")
//    void waiting() {
//        // given
//        final MenteeCoffeeChatQueryCondition condition = new MenteeCoffeeChatQueryCondition(mentees[0].getId(), CoffeeChatStatus.withWaitingCategory());
//
//        /* 페이지 1 */
//        final Slice<MenteeCoffeeChatScheduleData> result1 = sut.fetchMenteeCoffeeChatSchedules(condition, pageable1);
//        assertAll(
//                () -> assertThat(result1.hasNext()).isTrue(),
//                () -> assertThat(result1.getContent())
//                        .map(MenteeCoffeeChatScheduleData::id)
//                        .containsExactly(
//                                coffeeChats[29].getId(), coffeeChats[25].getId(), coffeeChats[21].getId(),
//                                coffeeChats[16].getId(), coffeeChats[14].getId(), coffeeChats[11].getId(),
//                                coffeeChats[9].getId(), coffeeChats[6].getId(), coffeeChats[5].getId(),
//                                coffeeChats[2].getId()
//                        ),
//                () -> assertThat(result1.getContent())
//                        .map(MenteeCoffeeChatScheduleData::status)
//                        .containsExactly(
//                                coffeeChats[29].getStatus().name(), coffeeChats[25].getStatus().name(), coffeeChats[21].getStatus().name(),
//                                coffeeChats[16].getStatus().name(), coffeeChats[14].getStatus().name(), coffeeChats[11].getStatus().name(),
//                                coffeeChats[9].getStatus().name(), coffeeChats[6].getStatus().name(), coffeeChats[5].getStatus().name(),
//                                coffeeChats[2].getStatus().name()
//                        ),
//                () -> assertThat(result1.getContent())
//                        .map(MenteeCoffeeChatScheduleData::mentorId)
//                        .containsExactly(
//                                mentors[9].getId(), mentors[5].getId(), mentors[1].getId(),
//                                mentors[16].getId(), mentors[14].getId(), mentors[11].getId(),
//                                mentors[9].getId(), mentors[6].getId(), mentors[5].getId(),
//                                mentors[2].getId()
//                        )
//        );
//
//        /* 페이지 2 */
//        final Slice<MenteeCoffeeChatScheduleData> result2 = sut.fetchMenteeCoffeeChatSchedules(condition, pageable2);
//        assertAll(
//                () -> assertThat(result2.hasNext()).isFalse(),
//                () -> assertThat(result2.getContent())
//                        .map(MenteeCoffeeChatScheduleData::id)
//                        .containsExactly(coffeeChats[0].getId()),
//                () -> assertThat(result2.getContent())
//                        .map(MenteeCoffeeChatScheduleData::status)
//                        .containsExactly(coffeeChats[0].getStatus().name()),
//                () -> assertThat(result2.getContent())
//                        .map(MenteeCoffeeChatScheduleData::mentorId)
//                        .containsExactly(mentors[0].getId())
//        );
//    }
//
//    @Test
//    @DisplayName("2. 멘티의 내 일정 `제안 상태` 커피챗 정보를 조회한다")
//    void suggest() {
//        // given
//        final MenteeCoffeeChatQueryCondition condition = new MenteeCoffeeChatQueryCondition(mentees[0].getId(), CoffeeChatStatus.withSuggstedCategory());
//
//        /* 페이지 1 */
//        final Slice<MenteeCoffeeChatScheduleData> result1 = sut.fetchMenteeCoffeeChatSchedules(condition, pageable1);
//        assertAll(
//                () -> assertThat(result1.hasNext()).isFalse(),
//                () -> assertThat(result1.getContent())
//                        .map(MenteeCoffeeChatScheduleData::id)
//                        .containsExactly(
//                                coffeeChats[28].getId(), coffeeChats[26].getId(), coffeeChats[24].getId(),
//                                coffeeChats[22].getId(), coffeeChats[19].getId(), coffeeChats[17].getId(),
//                                coffeeChats[15].getId()
//                        ),
//                () -> assertThat(result1.getContent())
//                        .map(MenteeCoffeeChatScheduleData::status)
//                        .containsExactly(
//                                coffeeChats[28].getStatus().name(), coffeeChats[26].getStatus().name(), coffeeChats[24].getStatus().name(),
//                                coffeeChats[22].getStatus().name(), coffeeChats[19].getStatus().name(), coffeeChats[17].getStatus().name(),
//                                coffeeChats[15].getStatus().name()
//                        ),
//                () -> assertThat(result1.getContent())
//                        .map(MenteeCoffeeChatScheduleData::mentorId)
//                        .containsExactly(
//                                mentors[8].getId(), mentors[6].getId(), mentors[4].getId(),
//                                mentors[2].getId(), mentors[19].getId(), mentors[17].getId(),
//                                mentors[15].getId()
//                        )
//        );
//
//        /* 페이지 2 */
//        final Slice<MenteeCoffeeChatScheduleData> result2 = sut.fetchMenteeCoffeeChatSchedules(condition, pageable2);
//        assertAll(
//                () -> assertThat(result2.hasNext()).isFalse(),
//                () -> assertThat(result2.getContent()).isEmpty()
//        );
//    }
//
//    @Test
//    @DisplayName("3. 멘티의 내 일정 `예정 상태` 커피챗 정보를 조회한다")
//    void scheduled() {
//        // given
//        final MenteeCoffeeChatQueryCondition condition = new MenteeCoffeeChatQueryCondition(mentees[0].getId(), CoffeeChatStatus.withScheduledCategory());
//
//        /* 페이지 1 */
//        final Slice<MenteeCoffeeChatScheduleData> result1 = sut.fetchMenteeCoffeeChatSchedules(condition, pageable1);
//        assertAll(
//                () -> assertThat(result1.hasNext()).isFalse(),
//                () -> assertThat(result1.getContent())
//                        .map(MenteeCoffeeChatScheduleData::id)
//                        .containsExactly(coffeeChats[20].getId(), coffeeChats[7].getId(), coffeeChats[1].getId()),
//                () -> assertThat(result1.getContent())
//                        .map(MenteeCoffeeChatScheduleData::status)
//                        .containsExactly(coffeeChats[20].getStatus().name(), coffeeChats[7].getStatus().name(), coffeeChats[1].getStatus().name()),
//                () -> assertThat(result1.getContent())
//                        .map(MenteeCoffeeChatScheduleData::mentorId)
//                        .containsExactly(mentors[0].getId(), mentors[7].getId(), mentors[1].getId())
//        );
//
//        /* 페이지 2 */
//        final Slice<MenteeCoffeeChatScheduleData> result2 = sut.fetchMenteeCoffeeChatSchedules(condition, pageable2);
//        assertAll(
//                () -> assertThat(result2.hasNext()).isFalse(),
//                () -> assertThat(result2.getContent()).isEmpty()
//        );
//    }
//
//    @Test
//    @DisplayName("4. 멘티의 내 일정 `지나간 상태` 커피챗 정보를 조회한다")
//    void passed() {
//        // given
//        final MenteeCoffeeChatQueryCondition condition = new MenteeCoffeeChatQueryCondition(mentees[0].getId(), CoffeeChatStatus.withPassedCategory());
//
//        /* 페이지 1 */
//        final Slice<MenteeCoffeeChatScheduleData> result1 = sut.fetchMenteeCoffeeChatSchedules(condition, pageable1);
//        assertAll(
//                () -> assertThat(result1.hasNext()).isFalse(),
//                () -> assertThat(result1.getContent())
//                        .map(MenteeCoffeeChatScheduleData::id)
//                        .containsExactly(
//                                coffeeChats[27].getId(), coffeeChats[23].getId(), coffeeChats[18].getId(),
//                                coffeeChats[13].getId(), coffeeChats[12].getId(), coffeeChats[10].getId(),
//                                coffeeChats[8].getId(), coffeeChats[4].getId(), coffeeChats[3].getId()
//                        ),
//                () -> assertThat(result1.getContent())
//                        .map(MenteeCoffeeChatScheduleData::status)
//                        .containsExactly(
//                                coffeeChats[27].getStatus().name(), coffeeChats[23].getStatus().name(), coffeeChats[18].getStatus().name(),
//                                coffeeChats[13].getStatus().name(), coffeeChats[12].getStatus().name(), coffeeChats[10].getStatus().name(),
//                                coffeeChats[8].getStatus().name(), coffeeChats[4].getStatus().name(), coffeeChats[3].getStatus().name()
//                        ),
//                () -> assertThat(result1.getContent())
//                        .map(MenteeCoffeeChatScheduleData::mentorId)
//                        .containsExactly(
//                                mentors[7].getId(), mentors[3].getId(), mentors[18].getId(),
//                                mentors[13].getId(), mentors[12].getId(), mentors[10].getId(),
//                                mentors[8].getId(), mentors[4].getId(), mentors[3].getId()
//                        )
//        );
//
//        /* 페이지 2 */
//        final Slice<MenteeCoffeeChatScheduleData> result2 = sut.fetchMenteeCoffeeChatSchedules(condition, pageable2);
//        assertAll(
//                () -> assertThat(result2.hasNext()).isFalse(),
//                () -> assertThat(result2.getContent()).isEmpty()
//        );
//    }
//}
