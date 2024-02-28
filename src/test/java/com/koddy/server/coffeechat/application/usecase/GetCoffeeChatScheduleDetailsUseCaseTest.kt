package com.koddy.server.coffeechat.application.usecase

import com.koddy.server.auth.domain.model.Authenticated
import com.koddy.server.coffeechat.application.usecase.query.GetCoffeeChatScheduleDetails
import com.koddy.server.coffeechat.application.usecase.query.response.MenteeCoffeeChatScheduleDetails
import com.koddy.server.coffeechat.application.usecase.query.response.MentorCoffeeChatScheduleDetails
import com.koddy.server.coffeechat.domain.model.CoffeeChat
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus
import com.koddy.server.coffeechat.domain.repository.CoffeeChatRepository
import com.koddy.server.common.UnitTestKt
import com.koddy.server.common.fixture.CoffeeChatFixture.MenteeFlow
import com.koddy.server.common.fixture.CoffeeChatFixture.MentorFlow
import com.koddy.server.common.fixture.CoffeeChatFixture.월요일_1주차_20_00_시작
import com.koddy.server.common.fixture.MenteeFixture.MENTEE_1
import com.koddy.server.common.fixture.MentorFixture.MENTOR_1
import com.koddy.server.common.mock.fake.FakeEncryptor
import com.koddy.server.member.domain.model.Language
import com.koddy.server.member.domain.model.mentee.Mentee
import com.koddy.server.member.domain.model.mentor.Mentor
import com.koddy.server.member.domain.repository.AvailableLanguageRepository
import com.koddy.server.member.domain.repository.MenteeRepository
import com.koddy.server.member.domain.repository.MentorRepository
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
    val coffeeChatRepository = mockk<CoffeeChatRepository>()
    val mentorRepository = mockk<MentorRepository>()
    val menteeRepository = mockk<MenteeRepository>()
    val availableLanguageRepository = mockk<AvailableLanguageRepository>()
    val encryptor = FakeEncryptor()
    val sut = GetCoffeeChatScheduleDetailsUseCase(
        coffeeChatRepository,
        mentorRepository,
        menteeRepository,
        availableLanguageRepository,
        encryptor,
    )

    val mentor: Mentor = MENTOR_1.toDomain().apply(1L)
    val mentee: Mentee = MENTEE_1.toDomain().apply(2L)

    describe("GetCoffeeChatScheduleDetailsUseCase's invoke (멘토 입장)") {
        val authenticated = Authenticated(mentor.id, mentor.authority)
        val coffeeChat: CoffeeChat = MentorFlow.suggestAndPending(월요일_1주차_20_00_시작, mentor, mentee).apply(1L)
        val query = GetCoffeeChatScheduleDetails(authenticated, coffeeChat.id)
        every { coffeeChatRepository.getById(query.coffeeChatId) } returns coffeeChat

        context("멘토가 내 일정에서 특정 커피챗에 대한 상세 조회를 진행하면") {
            every { menteeRepository.getByIdWithNative(coffeeChat.menteeId) } returns mentee
            every { availableLanguageRepository.findByMemberIdWithNative(mentee.id) } returns mentee.availableLanguages

            it("관련된 커피챗 정보 + 멘티 정보를 조회한다") {
                val result: MentorCoffeeChatScheduleDetails = sut.invoke(query) as MentorCoffeeChatScheduleDetails

                verify(exactly = 1) {
                    coffeeChatRepository.getById(query.coffeeChatId)
                    menteeRepository.getByIdWithNative(coffeeChat.menteeId)
                    availableLanguageRepository.findByMemberIdWithNative(mentee.id)
                }
                verify(exactly = 0) { mentorRepository.getByIdWithNative(coffeeChat.mentorId) }
                assertSoftly(result) {
                    mentee().id shouldBe mentee.id
                    mentee().name shouldBe mentee.name
                    mentee().profileImageUrl shouldBe mentee.profileImageUrl
                    mentee().nationality shouldBe mentee.nationality.code
                    mentee().introduction shouldBe mentee.introduction
                    mentee().languages.main shouldBe Language.Category.EN.code
                    mentee().languages.sub shouldContainExactlyInAnyOrder listOf(Language.Category.KR.code)
                    mentee().interestSchool shouldBe mentee.interest.school
                    mentee().interestMajor shouldBe mentee.interest.major
                    mentee().status shouldBe mentee.status.name
                    coffeeChat().id shouldBe coffeeChat.id
                    coffeeChat().status shouldBe CoffeeChatStatus.MENTEE_PENDING.name
                    coffeeChat().applyReason shouldBe null
                    coffeeChat().suggestReason shouldNotBe null
                    coffeeChat().cancelReason shouldBe null
                    coffeeChat().rejectReason shouldBe null
                    coffeeChat().question shouldNotBe null
                    coffeeChat().start shouldBe 월요일_1주차_20_00_시작.start
                    coffeeChat().end shouldBe 월요일_1주차_20_00_시작.end
                    coffeeChat().chatType shouldBe null
                    coffeeChat().chatValue shouldBe null
                }
            }
        }
    }

    describe("GetCoffeeChatScheduleDetailsUseCase's invoke (멘티 입장)") {
        val authenticated = Authenticated(mentee.id, mentee.authority)
        val coffeeChat: CoffeeChat = MenteeFlow.applyAndApprove(월요일_1주차_20_00_시작, mentee, mentor).apply(1L)
        val query = GetCoffeeChatScheduleDetails(authenticated, coffeeChat.id)
        every { coffeeChatRepository.getById(query.coffeeChatId) } returns coffeeChat

        context("멘티가 내 일정에서 특정 커피챗에 대한 상세 조회를 진행하면") {
            every { mentorRepository.getByIdWithNative(coffeeChat.mentorId) } returns mentor
            every { availableLanguageRepository.findByMemberIdWithNative(mentor.id) } returns mentor.availableLanguages

            it("관련된 커피챗 정보 + 멘토 정보를 조회한다") {
                val result: MenteeCoffeeChatScheduleDetails = sut.invoke(query) as MenteeCoffeeChatScheduleDetails

                verify(exactly = 1) {
                    coffeeChatRepository.getById(query.coffeeChatId)
                    mentorRepository.getByIdWithNative(coffeeChat.mentorId)
                    availableLanguageRepository.findByMemberIdWithNative(mentor.id)
                }
                verify(exactly = 0) { menteeRepository.getByIdWithNative(coffeeChat.menteeId) }
                assertSoftly(result) {
                    mentor().id shouldBe mentor.id
                    mentor().name shouldBe mentor.name
                    mentor().profileImageUrl shouldBe mentor.profileImageUrl
                    mentor().introduction shouldBe mentor.introduction
                    mentor().languages.main shouldBe Language.Category.KR.code
                    mentor().languages.sub shouldContainExactlyInAnyOrder listOf(Language.Category.EN.code)
                    mentor().school shouldBe mentor.universityProfile.school
                    mentor().major shouldBe mentor.universityProfile.major
                    mentor().enteredIn shouldBe mentor.universityProfile.enteredIn
                    mentor().status shouldBe mentor.status.name
                    coffeeChat().id shouldBe coffeeChat.id
                    coffeeChat().status shouldBe CoffeeChatStatus.MENTOR_APPROVE.name
                    coffeeChat().applyReason shouldNotBe null
                    coffeeChat().suggestReason shouldBe null
                    coffeeChat().cancelReason shouldBe null
                    coffeeChat().rejectReason shouldBe null
                    coffeeChat().question shouldNotBe null
                    coffeeChat().start shouldBe 월요일_1주차_20_00_시작.start
                    coffeeChat().end shouldBe 월요일_1주차_20_00_시작.end
                    coffeeChat().chatType shouldBe 월요일_1주차_20_00_시작.strategy.type.eng
                    coffeeChat().chatValue shouldNotBe 월요일_1주차_20_00_시작.strategy.value
                    coffeeChat().chatValue shouldBe encryptor.decrypt(월요일_1주차_20_00_시작.strategy.value)
                }
            }
        }
    }
})
