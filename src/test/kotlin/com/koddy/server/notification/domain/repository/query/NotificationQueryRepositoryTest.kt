package com.koddy.server.notification.domain.repository.query

import com.koddy.server.coffeechat.domain.model.CoffeeChat
import com.koddy.server.coffeechat.domain.repository.CoffeeChatRepository
import com.koddy.server.common.RepositoryTestKt
import com.koddy.server.common.fixture.CoffeeChatFixture.금요일_1주차_20_00_시작
import com.koddy.server.common.fixture.CoffeeChatFixture.금요일_2주차_20_00_시작
import com.koddy.server.common.fixture.CoffeeChatFixture.수요일_1주차_20_00_시작
import com.koddy.server.common.fixture.CoffeeChatFixture.수요일_2주차_20_00_시작
import com.koddy.server.common.fixture.CoffeeChatFixture.수요일_3주차_20_00_시작
import com.koddy.server.common.fixture.CoffeeChatFixture.수요일_4주차_20_00_시작
import com.koddy.server.common.fixture.CoffeeChatFixture.월요일_1주차_20_00_시작
import com.koddy.server.common.fixture.CoffeeChatFixture.월요일_2주차_20_00_시작
import com.koddy.server.common.fixture.CoffeeChatFixture.월요일_3주차_20_00_시작
import com.koddy.server.common.fixture.CoffeeChatFixture.월요일_4주차_20_00_시작
import com.koddy.server.common.fixture.MenteeFixtureStore.menteeFixture
import com.koddy.server.common.fixture.MenteeFlow
import com.koddy.server.common.fixture.MentorFixtureStore.mentorFixture
import com.koddy.server.common.fixture.MentorFlow
import com.koddy.server.common.fixture.NotificationFixture.멘토_수신_MENTEE_APPLY_FROM_MENTEE_FLOW
import com.koddy.server.common.fixture.NotificationFixture.멘토_수신_MENTEE_PENDING_FROM_MENTOR_FLOW
import com.koddy.server.common.fixture.NotificationFixture.멘토_수신_MENTOR_APPROVE_FROM_MENTEE_FLOW
import com.koddy.server.common.fixture.NotificationFixture.멘토_수신_MENTOR_FINALLY_APPROVE_FROM_MENTOR_FLOW
import com.koddy.server.common.fixture.NotificationFixture.멘티_수신_MENTOR_APPROVE_FROM_MENTEE_FLOW
import com.koddy.server.common.fixture.NotificationFixture.멘티_수신_MENTOR_FINALLY_APPROVE_FROM_MENTOR_FLOW
import com.koddy.server.common.fixture.NotificationFixture.멘티_수신_MENTOR_FINALLY_CANCEL_FROM_MENTOR_FLOW
import com.koddy.server.common.fixture.NotificationFixture.멘티_수신_MENTOR_REJECT_FROM_MENTEE_FLOW
import com.koddy.server.common.fixture.NotificationFixture.멘티_수신_MENTOR_SUGGEST_FROM_MENTOR_FLOW
import com.koddy.server.global.query.PageCreator
import com.koddy.server.member.domain.model.mentee.Mentee
import com.koddy.server.member.domain.model.mentor.Mentor
import com.koddy.server.member.domain.repository.MemberRepository
import com.koddy.server.notification.domain.model.Notification
import com.koddy.server.notification.domain.repository.NotificationRepository
import com.koddy.server.notification.domain.repository.query.response.NotificationDetails
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.context.annotation.Import
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice

@RepositoryTestKt
@Import(NotificationQueryRepositoryImpl::class)
@DisplayName("Notification -> NotificationQueryRepository 테스트")
internal class NotificationQueryRepositoryTest(
    private val sut: NotificationQueryRepositoryImpl,
    private val memberRepository: MemberRepository,
    private val coffeeChatRepository: CoffeeChatRepository,
    private val notificationRepository: NotificationRepository,
) {
    companion object {
        private val pageable1: Pageable = PageCreator.create(1)
        private val pageable2: Pageable = PageCreator.create(2)
    }

    private lateinit var mentor: Mentor
    private lateinit var mentee: Mentee
    private lateinit var coffeeChats: List<CoffeeChat>
    private lateinit var notifications: List<Notification>

    @BeforeEach
    fun setUp() {
        mentor = memberRepository.save(mentorFixture(sequence = 1).toDomain())
        mentee = memberRepository.save(menteeFixture(sequence = 1).toDomain())
        coffeeChats = coffeeChatRepository.saveAll(
            listOf(
                MentorFlow.suggestAndFinallyCancel(fixture = 월요일_1주차_20_00_시작, mentor = mentor, mentee = mentee),
                MenteeFlow.applyAndApprove(fixture = 월요일_2주차_20_00_시작, mentee = mentee, mentor = mentor),
                MentorFlow.suggestAndFinallyApprove(fixture = 월요일_3주차_20_00_시작, mentor = mentor, mentee = mentee),
                MenteeFlow.applyAndReject(fixture = 월요일_4주차_20_00_시작, mentee = mentee, mentor = mentor),
                MentorFlow.suggestAndPending(fixture = 수요일_1주차_20_00_시작, mentor = mentor, mentee = mentee),
                MentorFlow.suggestAndFinallyCancel(fixture = 수요일_2주차_20_00_시작, mentor = mentor, mentee = mentee),
                MenteeFlow.applyAndApprove(fixture = 수요일_3주차_20_00_시작, mentee = mentee, mentor = mentor),
                MentorFlow.suggestAndFinallyApprove(fixture = 수요일_4주차_20_00_시작, mentor = mentor, mentee = mentee),
                MenteeFlow.applyAndReject(fixture = 금요일_1주차_20_00_시작, mentee = mentee, mentor = mentor),
                MentorFlow.suggestAndPending(fixture = 금요일_2주차_20_00_시작, mentor = mentor, mentee = mentee),
            ),
        )
        notifications = notificationRepository.saveAll(
            listOf(
                멘티_수신_MENTOR_SUGGEST_FROM_MENTOR_FLOW.toDomain(target = mentee, coffeeChat = coffeeChats[0]),
                멘토_수신_MENTEE_PENDING_FROM_MENTOR_FLOW.toDomain(target = mentor, coffeeChat = coffeeChats[0]),
                멘티_수신_MENTOR_FINALLY_CANCEL_FROM_MENTOR_FLOW.toDomain(target = mentee, coffeeChat = coffeeChats[0]),
                멘토_수신_MENTEE_APPLY_FROM_MENTEE_FLOW.toDomain(target = mentor, coffeeChat = coffeeChats[1]),
                멘티_수신_MENTOR_APPROVE_FROM_MENTEE_FLOW.toDomain(target = mentee, coffeeChat = coffeeChats[1]),
                멘토_수신_MENTOR_APPROVE_FROM_MENTEE_FLOW.toDomain(target = mentor, coffeeChat = coffeeChats[1]),
                멘티_수신_MENTOR_SUGGEST_FROM_MENTOR_FLOW.toDomain(target = mentee, coffeeChat = coffeeChats[2]),
                멘토_수신_MENTEE_PENDING_FROM_MENTOR_FLOW.toDomain(target = mentor, coffeeChat = coffeeChats[2]),
                멘티_수신_MENTOR_FINALLY_APPROVE_FROM_MENTOR_FLOW.toDomain(target = mentee, coffeeChat = coffeeChats[2]),
                멘토_수신_MENTOR_FINALLY_APPROVE_FROM_MENTOR_FLOW.toDomain(target = mentor, coffeeChat = coffeeChats[2]),
                멘토_수신_MENTEE_APPLY_FROM_MENTEE_FLOW.toDomain(target = mentor, coffeeChat = coffeeChats[3]),
                멘티_수신_MENTOR_REJECT_FROM_MENTEE_FLOW.toDomain(target = mentee, coffeeChat = coffeeChats[3]),
                멘티_수신_MENTOR_SUGGEST_FROM_MENTOR_FLOW.toDomain(target = mentee, coffeeChat = coffeeChats[4]),
                멘토_수신_MENTEE_PENDING_FROM_MENTOR_FLOW.toDomain(target = mentor, coffeeChat = coffeeChats[4]),
                멘티_수신_MENTOR_SUGGEST_FROM_MENTOR_FLOW.toDomain(target = mentee, coffeeChat = coffeeChats[5]),
                멘토_수신_MENTEE_PENDING_FROM_MENTOR_FLOW.toDomain(target = mentor, coffeeChat = coffeeChats[5]),
                멘티_수신_MENTOR_FINALLY_CANCEL_FROM_MENTOR_FLOW.toDomain(target = mentee, coffeeChat = coffeeChats[5]),
                멘토_수신_MENTEE_APPLY_FROM_MENTEE_FLOW.toDomain(target = mentor, coffeeChat = coffeeChats[6]),
                멘티_수신_MENTOR_APPROVE_FROM_MENTEE_FLOW.toDomain(target = mentee, coffeeChat = coffeeChats[6]),
                멘토_수신_MENTOR_APPROVE_FROM_MENTEE_FLOW.toDomain(target = mentor, coffeeChat = coffeeChats[6]),
                멘티_수신_MENTOR_SUGGEST_FROM_MENTOR_FLOW.toDomain(target = mentee, coffeeChat = coffeeChats[7]),
                멘토_수신_MENTEE_PENDING_FROM_MENTOR_FLOW.toDomain(target = mentor, coffeeChat = coffeeChats[7]),
                멘티_수신_MENTOR_FINALLY_APPROVE_FROM_MENTOR_FLOW.toDomain(target = mentee, coffeeChat = coffeeChats[7]),
                멘토_수신_MENTOR_FINALLY_APPROVE_FROM_MENTOR_FLOW.toDomain(target = mentor, coffeeChat = coffeeChats[7]),
                멘토_수신_MENTEE_APPLY_FROM_MENTEE_FLOW.toDomain(target = mentor, coffeeChat = coffeeChats[8]),
                멘티_수신_MENTOR_REJECT_FROM_MENTEE_FLOW.toDomain(target = mentee, coffeeChat = coffeeChats[8]),
                멘티_수신_MENTOR_SUGGEST_FROM_MENTOR_FLOW.toDomain(target = mentee, coffeeChat = coffeeChats[9]),
                멘토_수신_MENTEE_PENDING_FROM_MENTOR_FLOW.toDomain(target = mentor, coffeeChat = coffeeChats[9]),
            ),
        )
    }

    @Test
    @DisplayName("멘토의 알림 내역을 조회한다")
    fun fetchMentorNotifications() {
        /* 페이지 1 */
        val result1: Slice<NotificationDetails> = sut.fetchMentorNotifications(mentor.id, pageable1)
        assertSoftly(result1) {
            hasNext() shouldBe true
            content.map { it.id } shouldContainExactly listOf(
                notifications[27].id, notifications[24].id, notifications[23].id,
                notifications[21].id, notifications[19].id, notifications[17].id,
                notifications[15].id, notifications[13].id, notifications[10].id,
                notifications[9].id,
            )
            content.map { it.coffeeChatId } shouldContainExactly listOf(
                coffeeChats[9].id, coffeeChats[8].id, coffeeChats[7].id,
                coffeeChats[7].id, coffeeChats[6].id, coffeeChats[6].id,
                coffeeChats[5].id, coffeeChats[4].id, coffeeChats[3].id,
                coffeeChats[2].id,
            )
        }

        /* 페이지 2 */
        val result2: Slice<NotificationDetails> = sut.fetchMentorNotifications(mentor.id, pageable2)
        assertSoftly(result2) {
            hasNext() shouldBe false
            content.map { it.id } shouldContainExactly listOf(
                notifications[7].id, notifications[5].id, notifications[3].id, notifications[1].id,
            )
            content.map { it.coffeeChatId } shouldContainExactly listOf(
                coffeeChats[2].id, coffeeChats[1].id, coffeeChats[1].id, coffeeChats[0].id,
            )
        }
    }

    @Test
    @DisplayName("멘티의 알림 내역을 조회한다")
    fun fetchMenteeNotifications() {
        /* 페이지 1 */
        val result1: Slice<NotificationDetails> = sut.fetchMenteeNotifications(mentee.id, pageable1)
        assertSoftly(result1) {
            hasNext() shouldBe true
            content.map { it.id } shouldContainExactly listOf(
                notifications[26].id, notifications[25].id, notifications[22].id,
                notifications[20].id, notifications[18].id, notifications[16].id,
                notifications[14].id, notifications[12].id, notifications[11].id,
                notifications[8].id,
            )
            content.map { it.coffeeChatId } shouldContainExactly listOf(
                coffeeChats[9].id, coffeeChats[8].id, coffeeChats[7].id,
                coffeeChats[7].id, coffeeChats[6].id, coffeeChats[5].id,
                coffeeChats[5].id, coffeeChats[4].id, coffeeChats[3].id,
                coffeeChats[2].id,
            )
        }

        /* 페이지 2 */
        val result2: Slice<NotificationDetails> = sut.fetchMenteeNotifications(mentee.id, pageable2)
        assertSoftly(result2) {
            hasNext() shouldBe false
            content.map { it.id } shouldContainExactly listOf(
                notifications[6].id, notifications[4].id, notifications[2].id, notifications[0].id,
            )
            content.map { it.coffeeChatId } shouldContainExactly listOf(
                coffeeChats[2].id, coffeeChats[1].id, coffeeChats[0].id, coffeeChats[0].id,
            )
        }
    }
}
