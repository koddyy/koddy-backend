package com.koddy.server.coffeechat.domain.repository.query

import com.koddy.server.coffeechat.domain.model.CoffeeChat
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus
import com.koddy.server.coffeechat.domain.repository.CoffeeChatRepository
import com.koddy.server.coffeechat.domain.repository.query.response.CoffeeChatCountPerCategory
import com.koddy.server.coffeechat.domain.repository.query.response.MentorCoffeeChatScheduleData
import com.koddy.server.coffeechat.domain.repository.query.spec.MentorCoffeeChatQueryCondition
import com.koddy.server.common.RepositoryTestKt
import com.koddy.server.common.fixture.CoffeeChatFixture.금요일_1주차_20_00_시작
import com.koddy.server.common.fixture.CoffeeChatFixture.금요일_2주차_20_00_시작
import com.koddy.server.common.fixture.CoffeeChatFixture.금요일_3주차_20_00_시작
import com.koddy.server.common.fixture.CoffeeChatFixture.수요일_1주차_20_00_시작
import com.koddy.server.common.fixture.CoffeeChatFixture.수요일_2주차_20_00_시작
import com.koddy.server.common.fixture.CoffeeChatFixture.수요일_3주차_20_00_시작
import com.koddy.server.common.fixture.CoffeeChatFixture.월요일_1주차_20_00_시작
import com.koddy.server.common.fixture.CoffeeChatFixture.월요일_2주차_20_00_시작
import com.koddy.server.common.fixture.CoffeeChatFixture.월요일_3주차_20_00_시작
import com.koddy.server.common.fixture.CoffeeChatFixture.토요일_1주차_20_00_시작
import com.koddy.server.common.fixture.CoffeeChatFixture.토요일_2주차_20_00_시작
import com.koddy.server.common.fixture.CoffeeChatFixture.토요일_3주차_20_00_시작
import com.koddy.server.common.fixture.CoffeeChatFixture.화요일_1주차_20_00_시작
import com.koddy.server.common.fixture.CoffeeChatFixture.화요일_2주차_20_00_시작
import com.koddy.server.common.fixture.CoffeeChatFixture.화요일_3주차_20_00_시작
import com.koddy.server.common.fixture.MenteeFlow
import com.koddy.server.common.fixture.MentorFlow
import com.koddy.server.member.domain.repository.MemberRepository
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.context.annotation.Import
import org.springframework.data.domain.Slice

@RepositoryTestKt
@Import(CoffeeChatScheduleQueryRepositoryImpl::class)
@DisplayName("CoffeeChat -> CoffeeChatScheduleQueryRepository [fetchMentorCoffeeChatSchedules] 테스트")
class CoffeeChatScheduleQueryRepositoryFetchMentorCoffeeChatSchedulesTest(
    private val sut: CoffeeChatScheduleQueryRepositoryImpl,
    private val coffeeChatRepository: CoffeeChatRepository,
    memberRepository: MemberRepository,
) : CoffeeChatScheduleQueryRepositorySupporter(memberRepository) {
    private lateinit var coffeeChats: List<CoffeeChat>

    @BeforeEach
    fun setUp() {
        initMembers()
        coffeeChats = coffeeChatRepository.saveAll(
            listOf(
                MenteeFlow.apply(fixture = 월요일_1주차_20_00_시작, mentee = mentees[0], mentor = mentors[0]),  // 대기
                MenteeFlow.applyAndApprove(fixture = 화요일_1주차_20_00_시작, mentee = mentees[1], mentor = mentors[0]),  // 예정
                MenteeFlow.apply(fixture = 수요일_1주차_20_00_시작, mentee = mentees[2], mentor = mentors[0]),  // 대기
                MenteeFlow.applyAndReject(fixture = 토요일_1주차_20_00_시작, mentee = mentees[3], mentor = mentors[0]),  // 지나간
                MenteeFlow.applyAndCancel(fixture = 금요일_1주차_20_00_시작, mentee = mentees[4], mentor = mentors[0]),  // 지나간
                MenteeFlow.apply(fixture = 월요일_2주차_20_00_시작, mentee = mentees[5], mentor = mentors[0]),  // 대기
                MenteeFlow.apply(fixture = 화요일_2주차_20_00_시작, mentee = mentees[6], mentor = mentors[0]),  // 대기
                MenteeFlow.applyAndApprove(fixture = 수요일_2주차_20_00_시작, mentee = mentees[7], mentor = mentors[0]),  // 예정
                MenteeFlow.applyAndComplete(fixture = 토요일_2주차_20_00_시작, mentee = mentees[8], mentor = mentors[0]),  // 지나간
                MenteeFlow.apply(fixture = 금요일_2주차_20_00_시작, mentee = mentees[9], mentor = mentors[0]),  // 대기
                MenteeFlow.applyAndReject(fixture = 월요일_3주차_20_00_시작, mentee = mentees[10], mentor = mentors[0]),  // 지나간
                MenteeFlow.apply(fixture = 화요일_3주차_20_00_시작, mentee = mentees[11], mentor = mentors[0]),  // 대기
                MenteeFlow.applyAndCancel(fixture = 수요일_3주차_20_00_시작, mentee = mentees[12], mentor = mentors[0]),  // 지나간
                MenteeFlow.applyAndComplete(fixture = 토요일_3주차_20_00_시작, mentee = mentees[13], mentor = mentors[0]),  // 지나간
                MenteeFlow.apply(fixture = 금요일_3주차_20_00_시작, mentee = mentees[14], mentor = mentors[0]),  // 대기

                MentorFlow.suggest(mentor = mentors[0], mentee = mentees[15]),  // 제안
                MentorFlow.suggestAndPending(fixture = 화요일_1주차_20_00_시작, mentor = mentors[0], mentee = mentees[16]),  // 대기
                MentorFlow.suggest(mentor = mentors[0], mentee = mentees[17]),  // 제안
                MentorFlow.suggestAndCancel(mentor = mentors[0], mentee = mentees[18]),  // 지나간
                MentorFlow.suggest(mentor = mentors[0], mentee = mentees[19]),  // 제안
                MentorFlow.suggestAndFinallyApprove(fixture = 월요일_2주차_20_00_시작, mentor = mentors[0], mentee = mentees[0]),  // 예정
                MentorFlow.suggestAndPending(fixture = 화요일_2주차_20_00_시작, mentor = mentors[0], mentee = mentees[1]),  // 대기
                MentorFlow.suggest(mentor = mentors[0], mentee = mentees[2]),  // 제안
                MentorFlow.suggestAndFinallyCancel(fixture = 토요일_2주차_20_00_시작, mentor = mentors[0], mentee = mentees[3]),  // 지나간
                MentorFlow.suggest(mentor = mentors[0], mentee = mentees[4]),  // 제안
                MentorFlow.suggestAndPending(fixture = 월요일_3주차_20_00_시작, mentor = mentors[0], mentee = mentees[5]),  // 대기
                MentorFlow.suggest(mentor = mentors[0], mentee = mentees[6]),  // 제안
                MentorFlow.suggestAndComplete(fixture = 수요일_3주차_20_00_시작, mentor = mentors[0], mentee = mentees[7]),  // 지나간
                MentorFlow.suggest(mentor = mentors[0], mentee = mentees[8]),  // 제안
                MentorFlow.suggestAndPending(fixture = 토요일_3주차_20_00_시작, mentor = mentors[0], mentee = mentees[9]), // 대기
            ),
        )
    }

    @Test
    @DisplayName("0. 멘토의 상태별 커피챗 개수를 조회한다 [대기, 제안, 예정, 지나간]")
    fun counts() {
        val result: CoffeeChatCountPerCategory = sut.fetchMentorCoffeeChatCountPerCategory(mentors[0].id)
        assertSoftly {
            result.waiting shouldBe 11
            coffeeChatRepository.countByMentorIdAndStatusIn(mentors[0].id, CoffeeChatStatus.withWaitingCategory()) shouldBe 11
            result.suggested shouldBe 7
            coffeeChatRepository.countByMentorIdAndStatusIn(mentors[0].id, CoffeeChatStatus.withSuggstedCategory()) shouldBe 7
            result.scheduled shouldBe 3
            coffeeChatRepository.countByMentorIdAndStatusIn(mentors[0].id, CoffeeChatStatus.withScheduledCategory()) shouldBe 3
            result.passed shouldBe 9
            coffeeChatRepository.countByMentorIdAndStatusIn(mentors[0].id, CoffeeChatStatus.withPassedCategory()) shouldBe 9
        }
    }

    @Test
    @DisplayName("1. 멘토의 내 일정 `대기 상태` 커피챗 정보를 조회한다")
    fun waiting() {
        // given
        val condition = MentorCoffeeChatQueryCondition(mentors[0].id, CoffeeChatStatus.withWaitingCategory())

        /* 페이지 1 */
        val result1: Slice<MentorCoffeeChatScheduleData> = sut.fetchMentorCoffeeChatSchedules(condition, pageable1)
        assertSoftly(result1) {
            hasNext() shouldBe true
            content.map { it.id } shouldContainExactly listOf(
                coffeeChats[29].id, coffeeChats[25].id, coffeeChats[21].id,
                coffeeChats[16].id, coffeeChats[14].id, coffeeChats[11].id,
                coffeeChats[9].id, coffeeChats[6].id, coffeeChats[5].id,
                coffeeChats[2].id,
            )
            content.map { it.status } shouldContainExactly listOf(
                coffeeChats[29].status, coffeeChats[25].status, coffeeChats[21].status,
                coffeeChats[16].status, coffeeChats[14].status, coffeeChats[11].status,
                coffeeChats[9].status, coffeeChats[6].status, coffeeChats[5].status,
                coffeeChats[2].status,
            )
            content.map { it.menteeId } shouldContainExactly listOf(
                mentees[9].id, mentees[5].id, mentees[1].id,
                mentees[16].id, mentees[14].id, mentees[11].id,
                mentees[9].id, mentees[6].id, mentees[5].id,
                mentees[2].id,
            )
        }

        /* 페이지 2 */
        val result2: Slice<MentorCoffeeChatScheduleData> = sut.fetchMentorCoffeeChatSchedules(condition, pageable2)
        assertSoftly(result2) {
            hasNext() shouldBe false
            content.map { it.id } shouldContainExactly listOf(coffeeChats[0].id)
            content.map { it.status } shouldContainExactly listOf(coffeeChats[0].status)
            content.map { it.menteeId } shouldContainExactly listOf(mentees[0].id)
        }
    }

    @Test
    @DisplayName("2. 멘토의 내 일정 `제안 상태` 커피챗 정보를 조회한다")
    fun suggest() {
        // given
        val condition = MentorCoffeeChatQueryCondition(mentors[0].id, CoffeeChatStatus.withSuggstedCategory())

        /* 페이지 1 */
        val result1: Slice<MentorCoffeeChatScheduleData> = sut.fetchMentorCoffeeChatSchedules(condition, pageable1)
        assertSoftly(result1) {
            hasNext() shouldBe false
            content.map { it.id } shouldContainExactly listOf(
                coffeeChats[28].id, coffeeChats[26].id, coffeeChats[24].id,
                coffeeChats[22].id, coffeeChats[19].id, coffeeChats[17].id,
                coffeeChats[15].id,
            )
            content.map { it.status } shouldContainExactly listOf(
                coffeeChats[28].status, coffeeChats[26].status, coffeeChats[24].status,
                coffeeChats[22].status, coffeeChats[19].status, coffeeChats[17].status,
                coffeeChats[15].status,
            )
            content.map { it.menteeId } shouldContainExactly listOf(
                mentees[8].id, mentees[6].id, mentees[4].id,
                mentees[2].id, mentees[19].id, mentees[17].id,
                mentees[15].id,
            )
        }

        /* 페이지 2 */
        val result2: Slice<MentorCoffeeChatScheduleData> = sut.fetchMentorCoffeeChatSchedules(condition, pageable2)
        assertSoftly(result2) {
            hasNext() shouldBe false
            content shouldBe emptyList()
        }
    }

    @Test
    @DisplayName("3. 멘토의 내 일정 `예정 상태` 커피챗 정보를 조회한다")
    fun scheduled() {
        // given
        val condition = MentorCoffeeChatQueryCondition(mentors[0].id, CoffeeChatStatus.withScheduledCategory())

        /* 페이지 1 */
        val result1: Slice<MentorCoffeeChatScheduleData> = sut.fetchMentorCoffeeChatSchedules(condition, pageable1)
        assertSoftly(result1) {
            hasNext() shouldBe false
            content.map { it.id } shouldContainExactly listOf(coffeeChats[20].id, coffeeChats[7].id, coffeeChats[1].id)
            content.map { it.status } shouldContainExactly listOf(coffeeChats[20].status, coffeeChats[7].status, coffeeChats[1].status)
            content.map { it.menteeId } shouldContainExactly listOf(mentees[0].id, mentees[7].id, mentees[1].id)
        }

        /* 페이지 2 */
        val result2: Slice<MentorCoffeeChatScheduleData> = sut.fetchMentorCoffeeChatSchedules(condition, pageable2)
        assertSoftly(result2) {
            hasNext() shouldBe false
            content shouldBe emptyList()
        }
    }

    @Test
    @DisplayName("4. 멘토의 내 일정 `지나간 상태` 커피챗 정보를 조회한다")
    fun passed() {
        // given
        val condition = MentorCoffeeChatQueryCondition(mentors[0].id, CoffeeChatStatus.withPassedCategory())

        /* 페이지 1 */
        val result1: Slice<MentorCoffeeChatScheduleData> = sut.fetchMentorCoffeeChatSchedules(condition, pageable1)
        assertSoftly(result1) {
            hasNext() shouldBe false
            content.map { it.id } shouldContainExactly listOf(
                coffeeChats[27].id, coffeeChats[23].id, coffeeChats[18].id,
                coffeeChats[13].id, coffeeChats[12].id, coffeeChats[10].id,
                coffeeChats[8].id, coffeeChats[4].id, coffeeChats[3].id,
            )
            content.map { it.status } shouldContainExactly listOf(
                coffeeChats[27].status, coffeeChats[23].status, coffeeChats[18].status,
                coffeeChats[13].status, coffeeChats[12].status, coffeeChats[10].status,
                coffeeChats[8].status, coffeeChats[4].status, coffeeChats[3].status,
            )
            content.map { it.menteeId } shouldContainExactly listOf(
                mentees[7].id, mentees[3].id, mentees[18].id,
                mentees[13].id, mentees[12].id, mentees[10].id,
                mentees[8].id, mentees[4].id, mentees[3].id,
            )
        }

        /* 페이지 2 */
        val result2: Slice<MentorCoffeeChatScheduleData> = sut.fetchMentorCoffeeChatSchedules(condition, pageable2)
        assertSoftly(result2) {
            hasNext() shouldBe false
            content shouldBe emptyList()
        }
    }
}
