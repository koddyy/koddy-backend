package com.koddy.server.coffeechat.domain.service

import com.koddy.server.coffeechat.domain.model.Reservation
import com.koddy.server.coffeechat.domain.repository.query.MentorReservedScheduleQueryRepository
import com.koddy.server.common.UnitTestKt
import com.koddy.server.common.fixture.CoffeeChatFixture.월요일_1주차_20_00_시작
import com.koddy.server.common.fixture.MenteeFixture.MENTEE_1
import com.koddy.server.common.fixture.MenteeFlow
import com.koddy.server.common.fixture.MentorFixture.MENTOR_1
import com.koddy.server.member.domain.model.mentee.Mentee
import com.koddy.server.member.domain.model.mentor.DayOfWeek
import com.koddy.server.member.domain.model.mentor.Mentor
import com.koddy.server.member.domain.model.mentor.MentoringPeriod
import com.koddy.server.member.domain.model.mentor.Timeline
import com.koddy.server.member.exception.MemberException
import com.koddy.server.member.exception.MemberExceptionCode.CANNOT_RESERVATION
import com.koddy.server.member.exception.MemberExceptionCode.MENTOR_NOT_FILL_IN_SCHEDULE
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.throwable.shouldHaveMessage
import io.mockk.every
import io.mockk.mockk
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

@UnitTestKt
@DisplayName("CoffeeChat -> ReservationAvailabilityChecker 테스트")
internal class ReservationAvailabilityCheckerTest : FeatureSpec({
    val mentorReservedScheduleQueryRepository = mockk<MentorReservedScheduleQueryRepository>()
    val sut = ReservationAvailabilityChecker(mentorReservedScheduleQueryRepository)

    feature("ReservationAvailabilityChecker's check") {
        scenario("1. 멘토가 멘토링 관련 정보를 기입하지 않으면 예약할 수 없다") {
            val mentor: Mentor = Mentor(
                MENTOR_1.platform,
                MENTOR_1.name,
                MENTOR_1.languages,
                MENTOR_1.universityProfile,
            ).apply(1L)

            shouldThrow<MemberException> {
                sut.check(mentor, Reservation(start = 월요일_1주차_20_00_시작.start, end = 월요일_1주차_20_00_시작.end))
            } shouldHaveMessage MENTOR_NOT_FILL_IN_SCHEDULE.message
        }

        scenario("2. 멘토링 진행 기간에 포함되지 않으면 예약할 수 없다") {
            val period = MentoringPeriod(
                startDate = LocalDate.of(2024, 2, 6),
                endDate = LocalDate.of(2024, 3, 1),
            )
            val mentor: Mentor = MENTOR_1.toDomainWithMentoringInfo(period, MENTOR_1.timelines).apply(1L)

            val targets: List<LocalDateTime> = listOf(
                LocalDateTime.of(2024, 2, 5, 18, 0),
                LocalDateTime.of(2024, 3, 2, 18, 0),
            )
            targets.forEach {
                shouldThrow<MemberException> {
                    sut.check(mentor, Reservation(start = it, end = it.plusMinutes(30)))
                } shouldHaveMessage CANNOT_RESERVATION.message
            }
        }

        scenario("3. 멘토링 진행 시간이 멘토가 정한 TimeUnit과 일치하지 않으면 예약할 수 없다 [default = 30분]") {
            val period = MentoringPeriod(
                startDate = LocalDate.of(2024, 2, 1),
                endDate = LocalDate.of(2024, 3, 1),
            )
            val time: LocalTime = LocalTime.of(19, 0)
            val timelines: List<Timeline> = listOf(
                Timeline(dayOfWeek = DayOfWeek.TUE, startTime = time, endTime = time.plusHours(3)),
                Timeline(dayOfWeek = DayOfWeek.WED, startTime = time, endTime = time.plusHours(3)),
                Timeline(dayOfWeek = DayOfWeek.THU, startTime = time, endTime = time.plusHours(3)),
                Timeline(dayOfWeek = DayOfWeek.FRI, startTime = time, endTime = time.plusHours(3)),
            )
            val mentor: Mentor = MENTOR_1.toDomainWithMentoringInfo(period, timelines).apply(1L)

            val start: LocalDateTime = LocalDateTime.of(2024, 2, 5, 18, 0)
            listOf(10, 20, 29, 31, 40).forEach {
                shouldThrow<MemberException> {
                    sut.check(mentor, Reservation(start = start, end = start.plusMinutes(it.toLong())))
                } shouldHaveMessage CANNOT_RESERVATION.message
            }
        }

        scenario("4. 요일별 스케줄 시간대에 포함되지 않으면 예약할 수 없다") {
            val period = MentoringPeriod(
                startDate = LocalDate.of(2024, 2, 1),
                endDate = LocalDate.of(2024, 3, 1),
            )
            val time: LocalTime = LocalTime.of(19, 0)
            val timelines: List<Timeline> = listOf(
                Timeline(dayOfWeek = DayOfWeek.TUE, startTime = time, endTime = time.plusHours(3)),
                Timeline(dayOfWeek = DayOfWeek.WED, startTime = time, endTime = time.plusHours(3)),
                Timeline(dayOfWeek = DayOfWeek.THU, startTime = time, endTime = time.plusHours(3)),
                Timeline(dayOfWeek = DayOfWeek.FRI, startTime = time, endTime = time.plusHours(3)),
            )
            val mentor: Mentor = MENTOR_1.toDomainWithMentoringInfo(period, timelines).apply(1L)

            listOf(
                LocalDateTime.of(2024, 2, 5, 18, 0),
                LocalDateTime.of(2024, 2, 5, 18, 30),
                LocalDateTime.of(2024, 2, 5, 18, 50),
                LocalDateTime.of(2024, 2, 5, 21, 50),
                LocalDateTime.of(2024, 2, 5, 22, 0),
                LocalDateTime.of(2024, 2, 5, 22, 30),
            ).forEach {
                shouldThrow<MemberException> {
                    sut.check(mentor, Reservation(start = it, end = it.plusMinutes(30)))
                } shouldHaveMessage CANNOT_RESERVATION.message
            }
        }

        scenario("5. 이미 예약된 커피챗 시간대와 겹치면 예약할 수 없다") {
            val period = MentoringPeriod(
                startDate = LocalDate.of(2024, 2, 1),
                endDate = LocalDate.of(2024, 3, 1),
            )
            val time: LocalTime = LocalTime.of(13, 0)
            val timelines: List<Timeline> = listOf(
                Timeline(dayOfWeek = DayOfWeek.TUE, startTime = time, endTime = time.plusHours(9)),
                Timeline(dayOfWeek = DayOfWeek.WED, startTime = time, endTime = time.plusHours(9)),
                Timeline(dayOfWeek = DayOfWeek.THU, startTime = time, endTime = time.plusHours(9)),
                Timeline(dayOfWeek = DayOfWeek.FRI, startTime = time, endTime = time.plusHours(9)),
            )
            val mentor: Mentor = MENTOR_1.toDomainWithMentoringInfo(period, timelines).apply(1L)
            val mentee: Mentee = MENTEE_1.toDomain().apply(2L)

            val starts = listOf(
                LocalDateTime.of(2024, 2, 6, 18, 0),
                LocalDateTime.of(2024, 2, 7, 18, 0),
                LocalDateTime.of(2024, 2, 8, 18, 0),
            )
            every { mentorReservedScheduleQueryRepository.fetchReservedCoffeeChat(mentor.id, 2024, 2) } returns listOf(
                MenteeFlow.apply(id = 1L, start = starts[0], end = starts[0].plusHours(2), mentee = mentee, mentor = mentor),
                MenteeFlow.apply(id = 2L, start = starts[1], end = starts[1].plusHours(2), mentee = mentee, mentor = mentor),
                MenteeFlow.apply(id = 3L, start = starts[2], end = starts[2].plusHours(2), mentee = mentee, mentor = mentor),
            )

            listOf(
                LocalDateTime.of(2024, 2, 6, 17, 50),
                LocalDateTime.of(2024, 2, 6, 18, 0),
                LocalDateTime.of(2024, 2, 6, 19, 30),
                LocalDateTime.of(2024, 2, 6, 19, 50),
                LocalDateTime.of(2024, 2, 7, 17, 50),
                LocalDateTime.of(2024, 2, 7, 18, 0),
                LocalDateTime.of(2024, 2, 7, 19, 30),
                LocalDateTime.of(2024, 2, 7, 19, 50),
                LocalDateTime.of(2024, 2, 8, 17, 50),
                LocalDateTime.of(2024, 2, 8, 18, 0),
                LocalDateTime.of(2024, 2, 8, 19, 30),
                LocalDateTime.of(2024, 2, 8, 19, 50),
            ).forEach {
                shouldThrow<MemberException> {
                    sut.check(mentor, Reservation(start = it, end = it.plusMinutes(30)))
                } shouldHaveMessage CANNOT_RESERVATION.message
            }
        }

        scenario("6. 모든 검증이 통과되면 예약할 수 있다") {
            val period = MentoringPeriod(
                startDate = LocalDate.of(2024, 2, 1),
                endDate = LocalDate.of(2024, 3, 1),
            )
            val time: LocalTime = LocalTime.of(13, 0)
            val timelines: List<Timeline> = listOf(
                Timeline(dayOfWeek = DayOfWeek.TUE, startTime = time, endTime = time.plusHours(9)),
                Timeline(dayOfWeek = DayOfWeek.WED, startTime = time, endTime = time.plusHours(9)),
                Timeline(dayOfWeek = DayOfWeek.THU, startTime = time, endTime = time.plusHours(9)),
                Timeline(dayOfWeek = DayOfWeek.FRI, startTime = time, endTime = time.plusHours(9)),
            )
            val mentor: Mentor = MENTOR_1.toDomainWithMentoringInfo(period, timelines).apply(1L)
            val mentee: Mentee = MENTEE_1.toDomain().apply(2L)

            val startLines = listOf(
                LocalDateTime.of(2024, 2, 6, 18, 0),
                LocalDateTime.of(2024, 2, 7, 18, 0),
                LocalDateTime.of(2024, 2, 8, 18, 0),
                LocalDateTime.of(2024, 3, 1, 15, 0),
            )
            every { mentorReservedScheduleQueryRepository.fetchReservedCoffeeChat(mentor.id, 2024, 2) } returns listOf(
                MenteeFlow.apply(id = 1L, start = startLines[0], end = startLines[0].plusHours(2), mentee = mentee, mentor = mentor),
                MenteeFlow.apply(id = 2L, start = startLines[1], end = startLines[1].plusHours(2), mentee = mentee, mentor = mentor),
                MenteeFlow.apply(id = 3L, start = startLines[2], end = startLines[2].plusHours(2), mentee = mentee, mentor = mentor),
            )
            every { mentorReservedScheduleQueryRepository.fetchReservedCoffeeChat(mentor.id, 2024, 3) } returns listOf(
                MenteeFlow.apply(id = 4L, start = startLines[3], end = startLines[3].plusHours(2), mentee = mentee, mentor = mentor),
            )

            listOf(
                LocalDateTime.of(2024, 2, 1, 13, 0),
                LocalDateTime.of(2024, 2, 1, 16, 0),
                LocalDateTime.of(2024, 2, 1, 21, 30),
                LocalDateTime.of(2024, 2, 6, 13, 0),
                LocalDateTime.of(2024, 2, 6, 17, 30),
                LocalDateTime.of(2024, 2, 6, 20, 0),
                LocalDateTime.of(2024, 2, 6, 21, 30),
                LocalDateTime.of(2024, 2, 7, 13, 0),
                LocalDateTime.of(2024, 2, 7, 17, 30),
                LocalDateTime.of(2024, 2, 7, 20, 0),
                LocalDateTime.of(2024, 2, 7, 21, 30),
                LocalDateTime.of(2024, 2, 8, 13, 0),
                LocalDateTime.of(2024, 2, 8, 17, 30),
                LocalDateTime.of(2024, 2, 8, 20, 0),
                LocalDateTime.of(2024, 2, 8, 21, 30),
                LocalDateTime.of(2024, 3, 1, 13, 0),
                LocalDateTime.of(2024, 3, 1, 14, 30),
                LocalDateTime.of(2024, 3, 1, 17, 0),
                LocalDateTime.of(2024, 3, 1, 21, 30),
            ).forEach {
                shouldNotThrowAny { sut.check(mentor, Reservation(start = it, end = it.plusMinutes(30))) }
            }
        }
    }
})
