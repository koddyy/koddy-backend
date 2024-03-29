package com.koddy.server.coffeechat.application.usecase

import com.koddy.server.auth.domain.model.Authenticated
import com.koddy.server.coffeechat.application.usecase.query.GetCoffeeChatScheduleDetails
import com.koddy.server.coffeechat.application.usecase.query.response.MenteeCoffeeChatScheduleDetails
import com.koddy.server.coffeechat.application.usecase.query.response.MentorCoffeeChatScheduleDetails
import com.koddy.server.coffeechat.domain.model.CoffeeChat
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus
import com.koddy.server.coffeechat.domain.service.CoffeeChatReader
import com.koddy.server.common.UnitTestKt
import com.koddy.server.common.fixture.CoffeeChatFixture.월요일_1주차_20_00_시작
import com.koddy.server.common.fixture.MenteeFixtureStore.menteeFixture
import com.koddy.server.common.fixture.MenteeFlow
import com.koddy.server.common.fixture.MentorFixtureStore.mentorFixture
import com.koddy.server.common.fixture.MentorFlow
import com.koddy.server.common.mock.fake.FakeEncryptor
import com.koddy.server.member.domain.model.Language
import com.koddy.server.member.domain.model.mentee.Mentee
import com.koddy.server.member.domain.model.mentor.Mentor
import com.koddy.server.member.domain.service.MemberReader
import io.kotest.assertions.assertSoftly
import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldContainExactlyInAnyOrder
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify

@UnitTestKt
@DisplayName("CoffeeChat -> GetCoffeeChatScheduleDetailsUseCase 테스트")
internal class GetCoffeeChatScheduleDetailsUseCaseTest : DescribeSpec({
    val coffeeChatReader = mockk<CoffeeChatReader>()
    val memberReader = mockk<MemberReader>()
    val encryptor = FakeEncryptor()
    val sut = GetCoffeeChatScheduleDetailsUseCase(
        coffeeChatReader,
        memberReader,
        encryptor,
    )

    val mentor: Mentor = mentorFixture(id = 1L).toDomain()
    val mentee: Mentee = menteeFixture(id = 2L).toDomain()

    describe("GetCoffeeChatScheduleDetailsUseCase's invoke (멘토 입장)") {
        val authenticated = Authenticated(mentor.id, mentor.authority)
        val coffeeChat: CoffeeChat = MentorFlow.suggestAndPending(id = 1L, fixture = 월요일_1주차_20_00_시작, mentor = mentor, mentee = mentee)
        val query = GetCoffeeChatScheduleDetails(authenticated, coffeeChat.id)
        every { coffeeChatReader.getById(query.coffeeChatId) } returns coffeeChat

        context("멘토가 내 일정에서 특정 커피챗에 대한 상세 조회를 진행하면") {
            every { memberReader.getMenteeWithNative(coffeeChat.menteeId) } returns mentee
            every { memberReader.getMemberAvailableLanguagesWithNative(mentee.id) } returns mentee.availableLanguages

            it("관련된 커피챗 정보 + 멘티 정보를 조회한다") {
                val result: MentorCoffeeChatScheduleDetails = sut.invoke(query) as MentorCoffeeChatScheduleDetails

                verify(exactly = 1) {
                    coffeeChatReader.getById(query.coffeeChatId)
                    memberReader.getMenteeWithNative(coffeeChat.menteeId)
                    memberReader.getMemberAvailableLanguagesWithNative(mentee.id)
                }
                verify(exactly = 0) { memberReader.getMentorWithNative(mentor.id) }
                assertSoftly {
                    result.mentee.id shouldBe mentee.id
                    result.mentee.name shouldBe mentee.name
                    result.mentee.profileImageUrl shouldBe mentee.profileImageUrl
                    result.mentee.nationality shouldBe mentee.nationality.code
                    result.mentee.introduction shouldBe mentee.introduction
                    result.mentee.languages.main shouldBe mentee.languages.filter { it.type == Language.Type.MAIN }.map { it.category.code }.first()
                    result.mentee.languages.sub shouldContainExactlyInAnyOrder mentee.languages.filter { it.type == Language.Type.SUB }.map { it.category.code }
                    result.mentee.interestSchool shouldBe mentee.interest.school
                    result.mentee.interestMajor shouldBe mentee.interest.major
                    result.mentee.status shouldBe mentee.status.name
                    result.coffeeChat.id shouldBe coffeeChat.id
                    result.coffeeChat.status shouldBe CoffeeChatStatus.MENTEE_PENDING.name
                    result.coffeeChat.applyReason shouldBe null
                    result.coffeeChat.suggestReason shouldNotBe null
                    result.coffeeChat.cancelReason shouldBe null
                    result.coffeeChat.rejectReason shouldBe null
                    result.coffeeChat.question shouldNotBe null
                    result.coffeeChat.start shouldBe 월요일_1주차_20_00_시작.start
                    result.coffeeChat.end shouldBe 월요일_1주차_20_00_시작.end
                    result.coffeeChat.chatType shouldBe null
                    result.coffeeChat.chatValue shouldBe null
                }
            }
        }
    }

    describe("GetCoffeeChatScheduleDetailsUseCase's invoke (멘티 입장)") {
        val authenticated = Authenticated(mentee.id, mentee.authority)
        val coffeeChat: CoffeeChat = MenteeFlow.applyAndApprove(id = 1L, fixture = 월요일_1주차_20_00_시작, mentee = mentee, mentor = mentor)
        val query = GetCoffeeChatScheduleDetails(authenticated, coffeeChat.id)
        every { coffeeChatReader.getById(query.coffeeChatId) } returns coffeeChat

        context("멘티가 내 일정에서 특정 커피챗에 대한 상세 조회를 진행하면") {
            every { memberReader.getMentorWithNative(coffeeChat.mentorId) } returns mentor
            every { memberReader.getMemberAvailableLanguagesWithNative(mentor.id) } returns mentor.availableLanguages

            it("관련된 커피챗 정보 + 멘토 정보를 조회한다") {
                val result: MenteeCoffeeChatScheduleDetails = sut.invoke(query) as MenteeCoffeeChatScheduleDetails

                verify(exactly = 1) {
                    coffeeChatReader.getById(query.coffeeChatId)
                    memberReader.getMentorWithNative(coffeeChat.mentorId)
                    memberReader.getMemberAvailableLanguagesWithNative(mentor.id)
                }
                verify(exactly = 0) { memberReader.getMenteeWithNative(mentee.id) }
                assertSoftly {
                    result.mentor.id shouldBe mentor.id
                    result.mentor.name shouldBe mentor.name
                    result.mentor.profileImageUrl shouldBe mentor.profileImageUrl
                    result.mentor.introduction shouldBe mentor.introduction
                    result.mentor.languages.main shouldBe mentor.languages.filter { it.type == Language.Type.MAIN }.map { it.category.code }.first()
                    result.mentor.languages.sub shouldContainExactlyInAnyOrder mentor.languages.filter { it.type == Language.Type.SUB }.map { it.category.code }
                    result.mentor.school shouldBe mentor.universityProfile.school
                    result.mentor.major shouldBe mentor.universityProfile.major
                    result.mentor.enteredIn shouldBe mentor.universityProfile.enteredIn
                    result.mentor.status shouldBe mentor.status.name
                    result.coffeeChat.id shouldBe coffeeChat.id
                    result.coffeeChat.status shouldBe CoffeeChatStatus.MENTOR_APPROVE.name
                    result.coffeeChat.applyReason shouldNotBe null
                    result.coffeeChat.suggestReason shouldBe null
                    result.coffeeChat.cancelReason shouldBe null
                    result.coffeeChat.rejectReason shouldBe null
                    result.coffeeChat.question shouldNotBe null
                    result.coffeeChat.start shouldBe 월요일_1주차_20_00_시작.start
                    result.coffeeChat.end shouldBe 월요일_1주차_20_00_시작.end
                    result.coffeeChat.chatType shouldBe 월요일_1주차_20_00_시작.strategy.type.value
                    result.coffeeChat.chatValue shouldNotBe 월요일_1주차_20_00_시작.strategy.value
                    result.coffeeChat.chatValue shouldBe encryptor.decrypt(월요일_1주차_20_00_시작.strategy.value)
                }
            }
        }
    }
})
