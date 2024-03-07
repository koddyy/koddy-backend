package com.koddy.server.member.application.usecase

import com.koddy.server.coffeechat.domain.model.CoffeeChat
import com.koddy.server.coffeechat.domain.repository.query.MentorReservedScheduleQueryRepository
import com.koddy.server.common.UnitTestKt
import com.koddy.server.common.fixture.MenteeFixtureStore.menteeFixture
import com.koddy.server.common.fixture.MentorFixtureStore.mentorFixture
import com.koddy.server.common.fixture.MentorFlow
import com.koddy.server.member.application.usecase.query.GetMentorReservedSchedule
import com.koddy.server.member.application.usecase.query.response.MentorReservedSchedule
import com.koddy.server.member.domain.model.mentee.Mentee
import com.koddy.server.member.domain.model.mentor.Mentor
import com.koddy.server.member.domain.service.MemberReader
import io.kotest.assertions.assertSoftly
import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import java.time.LocalDateTime

@UnitTestKt
@DisplayName("Member -> GetMentorReservedScheduleUseCase 테스트")
internal class GetMentorReservedScheduleUseCaseTest : FeatureSpec({
    val memberReader = mockk<MemberReader>()
    val mentorReservedScheduleQueryRepository = mockk<MentorReservedScheduleQueryRepository>()
    val sut = GetMentorReservedScheduleUseCase(
        memberReader,
        mentorReservedScheduleQueryRepository,
    )

    val mentor: Mentor = mentorFixture(id = 1L).toDomain()
    val menteeA: Mentee = menteeFixture(id = 2L).toDomain()
    val menteeB: Mentee = menteeFixture(id = 3L).toDomain()
    val menteeC: Mentee = menteeFixture(id = 4L).toDomain()

    feature("GetMentorReservedScheduleUseCase's invoke") {
        scenario("특정 Year/Month에 대해서 멘토의 예약된 스케줄 정보를 조회한다") {
            val query = GetMentorReservedSchedule(mentor.id, 2024, 2)
            every { memberReader.getMentorWithSchedules(query.mentorId) } returns mentor

            val start1 = LocalDateTime.of(2024, 2, 18, 18, 0)
            val start2 = LocalDateTime.of(2024, 2, 15, 18, 0)
            val coffeeChats: List<CoffeeChat> = listOf(
                MentorFlow.suggestAndReject(id = 1L, mentor = mentor, mentee = menteeA),
                MentorFlow.suggestAndPending(id = 2L, start = start1, end = start1.plusMinutes(30), mentor = mentor, mentee = menteeB),
                MentorFlow.suggestAndPending(id = 3L, start = start2, end = start2.plusMinutes(30), mentor = mentor, mentee = menteeC),
            )
            every {
                mentorReservedScheduleQueryRepository.fetchReservedCoffeeChat(
                    query.mentorId,
                    query.year,
                    query.month,
                )
            } returns listOf(
                coffeeChats[2],
                coffeeChats[1],
            )

            val result: MentorReservedSchedule = sut.invoke(query)
            assertSoftly(result) {
                period!!.startDate shouldBe mentor.mentoringPeriod!!.startDate
                period!!.endDate shouldBe mentor.mentoringPeriod!!.endDate
                schedules.map { it.dayOfWeek } shouldContainExactly mentor.schedules.map { it.timeline.dayOfWeek.kor }
                schedules.map { it.start.hour } shouldContainExactly mentor.schedules.map { it.timeline.startTime.hour }
                schedules.map { it.start.minute } shouldContainExactly mentor.schedules.map { it.timeline.startTime.minute }
                schedules.map { it.end.hour } shouldContainExactly mentor.schedules.map { it.timeline.endTime.hour }
                schedules.map { it.end.minute } shouldContainExactly mentor.schedules.map { it.timeline.endTime.minute }
                timeUnit shouldBe mentor.mentoringTimeUnit
                reserved.map { it.start } shouldContainExactly listOf(
                    coffeeChats[2].reservation!!.start,
                    coffeeChats[1].reservation!!.start,
                )
                reserved.map { it.end } shouldContainExactly listOf(
                    coffeeChats[2].reservation!!.end,
                    coffeeChats[1].reservation!!.end,
                )
            }
        }
    }
})
