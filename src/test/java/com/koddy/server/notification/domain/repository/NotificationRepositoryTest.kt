package com.koddy.server.notification.domain.repository

import com.koddy.server.coffeechat.domain.model.CoffeeChat
import com.koddy.server.coffeechat.domain.repository.CoffeeChatRepository
import com.koddy.server.common.RepositoryTestKt
import com.koddy.server.common.fixture.CoffeeChatFixture.MenteeFlow
import com.koddy.server.common.fixture.CoffeeChatFixture.MentorFlow
import com.koddy.server.common.fixture.CoffeeChatFixture.월요일_1주차_20_00_시작
import com.koddy.server.common.fixture.CoffeeChatFixture.월요일_2주차_20_00_시작
import com.koddy.server.common.fixture.CoffeeChatFixture.월요일_3주차_20_00_시작
import com.koddy.server.common.fixture.MenteeFixture.MENTEE_1
import com.koddy.server.common.fixture.MentorFixture.MENTOR_1
import com.koddy.server.common.fixture.NotificationFixture.멘토_수신_MENTEE_APPLY_FROM_MENTEE_FLOW
import com.koddy.server.common.fixture.NotificationFixture.멘토_수신_MENTOR_FINALLY_APPROVE_FROM_MENTOR_FLOW
import com.koddy.server.common.fixture.NotificationFixture.멘티_수신_MENTOR_FINALLY_APPROVE_FROM_MENTOR_FLOW
import com.koddy.server.common.fixture.NotificationFixture.멘티_수신_MENTOR_REJECT_FROM_MENTEE_FLOW
import com.koddy.server.common.fixture.NotificationFixture.멘티_수신_MENTOR_SUGGEST_FROM_MENTOR_FLOW
import com.koddy.server.member.domain.model.mentee.Mentee
import com.koddy.server.member.domain.model.mentor.Mentor
import com.koddy.server.member.domain.repository.MemberRepository
import com.koddy.server.notification.domain.model.Notification
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.collections.shouldContainAnyOf
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

@RepositoryTestKt
@DisplayName("Notification -> NotificationRepository 테스트")
internal class NotificationRepositoryTest(
    private val sut: NotificationRepository,
    private val memberRepository: MemberRepository,
    private val coffeeChatRepository: CoffeeChatRepository,
) {
    private lateinit var mentor: Mentor
    private lateinit var mentee: Mentee

    @BeforeEach
    fun setUp() {
        mentor = memberRepository.save(MENTOR_1.toDomain())
        mentee = memberRepository.save(MENTEE_1.toDomain())
    }

    @Test
    fun `자신에게 전달될 알림을 조회한다`() {
        // given
        val coffeeChats: List<CoffeeChat> = coffeeChatRepository.saveAll(
            listOf(
                MenteeFlow.apply(월요일_1주차_20_00_시작, mentee, mentor), // to mentor
                MenteeFlow.applyAndReject(월요일_2주차_20_00_시작, mentee, mentor), // to mentee
                MentorFlow.suggest(mentor, mentee), // to mentee
                MentorFlow.suggestAndFinallyApprove(월요일_3주차_20_00_시작, mentor, mentee), // to mentor & mentee
            ),
        )
        val notifications: List<Notification> = sut.saveAll(
            listOf(
                멘토_수신_MENTEE_APPLY_FROM_MENTEE_FLOW.toDomain(target = mentor, coffeeChat = coffeeChats[0]),
                멘티_수신_MENTOR_REJECT_FROM_MENTEE_FLOW.toDomain(target = mentee, coffeeChat = coffeeChats[1]),
                멘티_수신_MENTOR_SUGGEST_FROM_MENTOR_FLOW.toDomain(target = mentee, coffeeChat = coffeeChats[2]),
                멘토_수신_MENTOR_FINALLY_APPROVE_FROM_MENTOR_FLOW.toDomain(target = mentor, coffeeChat = coffeeChats[3]),
                멘티_수신_MENTOR_FINALLY_APPROVE_FROM_MENTOR_FLOW.toDomain(target = mentee, coffeeChat = coffeeChats[3]),
            ),
        )

        // when - then
        assertSoftly {
            sut.findByIdAndTargetId(notifications[0].id, mentor.id) shouldBe notifications[0]
            sut.findByIdAndTargetId(notifications[0].id, mentee.id) shouldBe null

            sut.findByIdAndTargetId(notifications[1].id, mentor.id) shouldBe null
            sut.findByIdAndTargetId(notifications[1].id, mentee.id) shouldBe notifications[1]

            sut.findByIdAndTargetId(notifications[2].id, mentor.id) shouldBe null
            sut.findByIdAndTargetId(notifications[2].id, mentee.id) shouldBe notifications[2]

            sut.findByIdAndTargetId(notifications[3].id, mentor.id) shouldBe notifications[3]
            sut.findByIdAndTargetId(notifications[3].id, mentee.id) shouldBe null

            sut.findByIdAndTargetId(notifications[4].id, mentor.id) shouldBe null
            sut.findByIdAndTargetId(notifications[4].id, mentee.id) shouldBe notifications[4]
        }
    }

    @Test
    fun `자신의 읽지 않은 알림을 모두 읽음 처리한다`() {
        // given
        val coffeeChats: List<CoffeeChat> = coffeeChatRepository.saveAll(
            listOf(
                MenteeFlow.apply(월요일_1주차_20_00_시작, mentee, mentor), // to mentor
                MenteeFlow.applyAndReject(월요일_2주차_20_00_시작, mentee, mentor), // to mentee
                MentorFlow.suggest(mentor, mentee), // to mentee
                MentorFlow.suggestAndFinallyApprove(월요일_3주차_20_00_시작, mentor, mentee), // to mentor & mentee
            ),
        )
        val notifications: List<Notification> = sut.saveAll(
            listOf(
                멘토_수신_MENTEE_APPLY_FROM_MENTEE_FLOW.toDomain(target = mentor, coffeeChat = coffeeChats[0]),
                멘티_수신_MENTOR_REJECT_FROM_MENTEE_FLOW.toDomain(target = mentee, coffeeChat = coffeeChats[1]),
                멘티_수신_MENTOR_SUGGEST_FROM_MENTOR_FLOW.toDomain(target = mentee, coffeeChat = coffeeChats[2]),
                멘토_수신_MENTOR_FINALLY_APPROVE_FROM_MENTOR_FLOW.toDomain(target = mentor, coffeeChat = coffeeChats[3]),
                멘티_수신_MENTOR_FINALLY_APPROVE_FROM_MENTOR_FLOW.toDomain(target = mentee, coffeeChat = coffeeChats[3]),
            ),
        )

        /* mentor read all -> notifications[0], notifications[3] */
        val mentorNotifications1: List<Notification> = sut.findByTargetId(mentor.id)
        assertSoftly {
            mentorNotifications1 shouldContainExactlyInAnyOrder listOf(notifications[0], notifications[3])
            mentorNotifications1.map { it.read } shouldContainAnyOf listOf(false, false)
        }

        sut.readAll(mentor.id)
        val mentorNotifications2: List<Notification> = sut.findByTargetId(mentor.id)
        assertSoftly {
            mentorNotifications2 shouldContainExactlyInAnyOrder listOf(notifications[0], notifications[3])
            mentorNotifications2.map { it.read } shouldContainAnyOf listOf(true, true)
        }

        /* mentee read all -> notifications[1], notifications[2], notifications[4] */
        val menteeNotifications1: List<Notification> = sut.findByTargetId(mentee.id)
        assertSoftly {
            menteeNotifications1 shouldContainExactlyInAnyOrder listOf(notifications[1], notifications[2], notifications[4])
            menteeNotifications1.map { it.read } shouldContainAnyOf listOf(false, false, false)
        }

        sut.readAll(mentee.id)
        val menteeNotifications2: List<Notification> = sut.findByTargetId(mentee.id)
        assertSoftly {
            menteeNotifications2 shouldContainExactlyInAnyOrder listOf(notifications[1], notifications[2], notifications[4])
            menteeNotifications2.map { it.read } shouldContainAnyOf listOf(true, true, true)
        }
    }
}
