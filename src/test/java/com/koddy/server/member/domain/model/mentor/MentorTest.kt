package com.koddy.server.member.domain.model.mentor

import com.koddy.server.coffeechat.domain.model.Reservation
import com.koddy.server.common.UnitTestKt
import com.koddy.server.common.fixture.LanguageFixture
import com.koddy.server.common.fixture.MentorFixtureStore.mentorFixture
import com.koddy.server.common.toLocalDate
import com.koddy.server.common.toLocalDateTime
import com.koddy.server.common.toLocalTime
import com.koddy.server.member.domain.model.Member
import com.koddy.server.member.domain.model.Nationality
import com.koddy.server.member.domain.model.Role
import com.koddy.server.member.domain.model.mentor.UniversityAuthentication.AuthenticationStatus
import com.koddy.server.member.exception.MemberException
import com.koddy.server.member.exception.MemberExceptionCode.AVAILABLE_LANGUAGE_MUST_EXISTS
import com.koddy.server.member.exception.MemberExceptionCode.CANNOT_RESERVATION
import com.koddy.server.member.exception.MemberExceptionCode.MAIN_LANGUAGE_MUST_BE_ONLY_ONE
import com.koddy.server.member.exception.MemberExceptionCode.MENTOR_NOT_FILL_IN_SCHEDULE
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage
import java.time.LocalTime

@UnitTestKt
@DisplayName("Member/Mentor -> 도메인 Aggregate [Mentor] 생성 & 정보 관리 테스트")
internal class MentorCreateTest : FeatureSpec({
    feature("Mentor 생성") {
        val fixture = mentorFixture(sequence = 1)

        scenario("메인 언어를 (0..N)개 선택했다면 Mentor를 생성할 수 없다") {
            shouldThrow<MemberException> {
                Mentor(
                    platform = fixture.platform,
                    name = fixture.name,
                    languages = listOf(),
                    universityProfile = fixture.universityProfile,
                )
            } shouldHaveMessage AVAILABLE_LANGUAGE_MUST_EXISTS.message

            shouldThrow<MemberException> {
                Mentor(
                    platform = fixture.platform,
                    name = fixture.name,
                    languages = listOf(
                        LanguageFixture.KR_MAIN.toDomain(),
                        LanguageFixture.EN_MAIN.toDomain(),
                        LanguageFixture.JP_SUB.toDomain(),
                    ),
                    universityProfile = fixture.universityProfile,
                )
            } shouldHaveMessage MAIN_LANGUAGE_MUST_BE_ONLY_ONE.message
        }

        scenario("메인 언어를 정확히 1개 선택했다면 Mentor를 생성할 수 있다") {
            listOf(
                listOf(LanguageFixture.KR_MAIN.toDomain()),
                listOf(
                    LanguageFixture.KR_MAIN.toDomain(),
                    LanguageFixture.EN_SUB.toDomain(),
                    LanguageFixture.JP_SUB.toDomain(),
                ),
            ).forEach { language ->
                val result = Mentor(
                    platform = fixture.platform,
                    name = fixture.name,
                    languages = language,
                    universityProfile = fixture.universityProfile,
                )

                assertSoftly {
                    // Common
                    result.platform.provider shouldBe fixture.platform.provider
                    result.platform.socialId shouldBe fixture.platform.socialId
                    result.platform.email!!.value shouldBe fixture.platform.email!!.value
                    result.name shouldBe fixture.name
                    result.nationality shouldBe Nationality.KOREA
                    result.introduction shouldBe null
                    result.profileImageUrl shouldBe null
                    result.isProfileComplete shouldBe false
                    result.status shouldBe Member.Status.ACTIVE
                    result.role shouldBe Role.MENTOR
                    result.languages.map { it.category } shouldContainExactly language.map { it.category }
                    result.languages.map { it.type } shouldContainExactly language.map { it.type }

                    // Mentor
                    result.universityProfile.school shouldBe fixture.universityProfile.school
                    result.universityProfile.major shouldBe fixture.universityProfile.major
                    result.universityProfile.enteredIn shouldBe fixture.universityProfile.enteredIn
                    result.universityAuthentication shouldBe null
                    result.mentoringPeriod shouldBe null
                    result.schedules shouldBe emptyList()
                }
            }
        }
    }

    feature("Mentor's completeProfile & profileComplete") {
        val fixtureA = mentorFixture(id = 1L)
        val fixtureB = mentorFixture(id = 2L)
        val fixtureC = mentorFixture(id = 3L)

        scenario("Mentor 프로필이 완성되었는지 확인한다 [자기소개 & 프로필 이미지]") {
            /**
             * MentorA
             */
            // 완성
            val mentorA: Mentor = fixtureA.toDomain()
            mentorA.isProfileComplete shouldBe true

            /**
             * MentorB
             */
            // 미완성
            val mentorB = Mentor(
                id = fixtureB.id,
                platform = fixtureB.platform,
                name = fixtureB.name,
                languages = fixtureB.languages,
                universityProfile = fixtureB.universityProfile,
            )
            mentorB.isProfileComplete shouldBe false

            // 완성
            mentorB.completeProfile(
                introduction = fixtureB.introduction,
                profileImageUrl = fixtureB.profileImageUrl,
                mentoringPeriod = fixtureB.mentoringPeriod,
                timelines = fixtureB.timelines,
            )
            mentorB.isProfileComplete shouldBe true

            /**
             * MentorC
             */
            // 미완성
            val mentorC = Mentor(
                id = fixtureC.id,
                platform = fixtureC.platform,
                name = fixtureC.name,
                languages = fixtureC.languages,
                universityProfile = fixtureC.universityProfile,
            )
            mentorC.isProfileComplete shouldBe false

            // 자기소개, 프로필 이미지 URL 입력
            mentorC.completeProfile(
                introduction = fixtureC.introduction,
                profileImageUrl = fixtureC.profileImageUrl,
                mentoringPeriod = null,
                timelines = emptyList(),
            )
            mentorC.isProfileComplete shouldBe false

            // 멘토링 관련 정보 입력
            mentorC.completeProfile(
                introduction = fixtureC.introduction,
                profileImageUrl = fixtureC.profileImageUrl,
                mentoringPeriod = fixtureC.mentoringPeriod,
                timelines = fixtureC.timelines,
            )
            mentorC.isProfileComplete shouldBe true
        }
    }

    feature("Mentor's updateBasicInfo") {
        val fixtureA = mentorFixture(id = 1L)
        val fixtureB = mentorFixture(id = 2L)

        scenario("Mentor 기본 정보를 수정한다") {
            val mentor: Mentor = fixtureA.toDomain()

            mentor.updateBasicInfo(
                name = fixtureB.name,
                profileImageUrl = fixtureB.profileImageUrl,
                introduction = fixtureB.introduction,
                languages = fixtureB.languages,
                school = fixtureB.universityProfile.school,
                major = fixtureB.universityProfile.major,
                enteredIn = fixtureB.universityProfile.enteredIn,
            )
            assertSoftly {
                // Effected
                mentor.name shouldBe fixtureB.name
                mentor.introduction shouldBe fixtureB.introduction
                mentor.profileImageUrl shouldBe fixtureB.profileImageUrl
                mentor.languages.map { it.category } shouldContainExactly fixtureB.languages.map { it.category }
                mentor.languages.map { it.type } shouldContainExactly fixtureB.languages.map { it.type }
                mentor.universityProfile.school shouldBe fixtureB.universityProfile.school
                mentor.universityProfile.major shouldBe fixtureB.universityProfile.major
                mentor.universityProfile.enteredIn shouldBe fixtureB.universityProfile.enteredIn

                // Not Effected
                mentor.platform.provider shouldBe fixtureA.platform.provider
                mentor.platform.socialId shouldBe fixtureA.platform.socialId
                mentor.platform.email!!.value shouldBe fixtureA.platform.email!!.value
                mentor.nationality shouldBe Nationality.KOREA
                mentor.isProfileComplete shouldBe true
                mentor.status shouldBe Member.Status.ACTIVE
                mentor.role shouldBe Role.MENTOR
            }
        }
    }

    feature("Mentor's updateSchedules") {
        val fixtureA = mentorFixture(id = 1L)
        val fixtureB = mentorFixture(id = 2L)

        scenario("Mentor 스케줄 정보를 수정한다") {
            val mentor: Mentor = fixtureA.toDomain()

            mentor.updateSchedules(
                mentoringPeriod = fixtureB.mentoringPeriod,
                timelines = fixtureB.timelines,
            )
            assertSoftly {
                mentor.mentoringPeriod!!.startDate shouldBe fixtureB.mentoringPeriod.startDate
                mentor.mentoringPeriod!!.endDate shouldBe fixtureB.mentoringPeriod.endDate
                mentor.mentoringPeriod!!.timeUnit shouldBe fixtureB.mentoringPeriod.timeUnit
                mentor.schedules.map { it.timeline.dayOfWeek } shouldContainExactly fixtureB.timelines.map { it.dayOfWeek }
                mentor.schedules.map { it.timeline.startTime } shouldContainExactly fixtureB.timelines.map { it.startTime }
                mentor.schedules.map { it.timeline.endTime } shouldContainExactly fixtureB.timelines.map { it.endTime }
            }
        }
    }
})

@UnitTestKt
@DisplayName("Member/Mentor -> 도메인 Aggregate [Mentor] 학교 인증 테스트")
internal class MentorAuthenticateUnivTest : FeatureSpec({
    val fixture = mentorFixture(id = 1L)

    feature("Mentor's authWithMail & authComplete") {
        scenario("학교 메일로 인증을 진행한다") {
            val mentor: Mentor = fixture.toDomain()
            val schoolMail = "sjiwon@kyonggi.ac.kr"

            // 인증 시도
            mentor.authWithMail(schoolMail)
            assertSoftly {
                mentor.universityAuthentication!!.schoolMail shouldBe schoolMail
                mentor.universityAuthentication!!.proofDataUploadUrl shouldBe null
                mentor.universityAuthentication!!.status shouldBe AuthenticationStatus.ATTEMPT
                mentor.isAuthenticated shouldBe false
            }

            // 인증 완료
            mentor.authComplete()
            assertSoftly {
                mentor.universityAuthentication!!.schoolMail shouldBe schoolMail
                mentor.universityAuthentication!!.proofDataUploadUrl shouldBe null
                mentor.universityAuthentication!!.status shouldBe AuthenticationStatus.SUCCESS
                mentor.isAuthenticated shouldBe true
            }
        }
    }

    feature("Mentor's authWithProofData & authComplete") {
        scenario("학교 메일로 인증을 진행한다") {
            val mentor: Mentor = fixture.toDomain()
            val proofDataUploadUrl = "https://google.com"

            // 인증 시도
            mentor.authWithProofData(proofDataUploadUrl)
            assertSoftly {
                mentor.universityAuthentication!!.schoolMail shouldBe null
                mentor.universityAuthentication!!.proofDataUploadUrl shouldBe proofDataUploadUrl
                mentor.universityAuthentication!!.status shouldBe AuthenticationStatus.ATTEMPT
                mentor.isAuthenticated shouldBe false
            }

            // 인증 완료
            mentor.authComplete()
            assertSoftly {
                mentor.universityAuthentication!!.schoolMail shouldBe null
                mentor.universityAuthentication!!.proofDataUploadUrl shouldBe proofDataUploadUrl
                mentor.universityAuthentication!!.status shouldBe AuthenticationStatus.SUCCESS
                mentor.isAuthenticated shouldBe true
            }
        }
    }
})

@UnitTestKt
@DisplayName("Member/Mentor -> 도메인 Aggregate [Mentor] 멘토링 시간 검증 테스트")
internal class MentorMentoringPeriodValidityTest : DescribeSpec({
    val fixture = mentorFixture(id = 1L)

    describe("Mentor's validateReservationData") {
        context("1. 멘토링 관련 정보(기간, 스케줄)를 기입하지 않았으면") {
            val mentor = Mentor(
                platform = fixture.platform,
                name = fixture.name,
                languages = fixture.languages,
                universityProfile = fixture.universityProfile,
            )

            it("MENTOR_NOT_FILL_IN_SCHEDULE 예외가 발생한다") {
                shouldThrow<MemberException> {
                    mentor.validateReservationData(
                        Reservation(
                            start = "2024/3/1-18:00".toLocalDateTime(),
                            end = "2024/3/1-18:30".toLocalDateTime(),
                        ),
                    )
                } shouldHaveMessage MENTOR_NOT_FILL_IN_SCHEDULE.message
            }
        }

        context("2. 예약 날짜가 멘토링 진행 기간에 포함되지 않으면") {
            val mentor: Mentor = fixture.toDomainWithMentoringInfo(
                mentoringPeriod = MentoringPeriod(
                    startDate = "2024/3/1".toLocalDate(),
                    endDate = "2024/3/10".toLocalDate(),
                    timeUnit = MentoringPeriod.TimeUnit.HALF_HOUR,
                ),
                timelines = fixture.timelines,
            )

            it("CANNOT_RESERVATION 예외가 발생한다") {
                listOf(
                    "2024/2/26-18:00".toLocalDateTime(),
                    "2024/3/11-18:00".toLocalDateTime(),
                ).forEach {
                    shouldThrow<MemberException> {
                        mentor.validateReservationData(
                            Reservation(start = it, end = it.plusMinutes(30)),
                        )
                    } shouldHaveMessage CANNOT_RESERVATION.message
                }
            }
        }

        context("3. 예약할 멘토링의 진행 시간이 멘토가 정한 TimeUnit이랑 일치하지 않으면") {
            val time: LocalTime = "09:00".toLocalTime()
            val mentor: Mentor = fixture.toDomainWithMentoringInfo(
                mentoringPeriod = MentoringPeriod(
                    startDate = "2024/3/1".toLocalDate(),
                    endDate = "2024/3/11".toLocalDate(),
                    timeUnit = MentoringPeriod.TimeUnit.HALF_HOUR,
                ),
                timelines = listOf(
                    Timeline(DayOfWeek.TUE, time, time.plusHours(12)),
                    Timeline(DayOfWeek.WED, time, time.plusHours(12)),
                    Timeline(DayOfWeek.THU, time, time.plusHours(12)),
                    Timeline(DayOfWeek.FRI, time, time.plusHours(12)),
                ),
            )

            it("CANNOT_RESERVATION 예외가 발생한다") {
                mapOf(
                    "2024/3/1-18:00".toLocalDateTime() to 10,
                    "2024/3/6-18:00".toLocalDateTime() to 29,
                    "2024/3/8-18:00".toLocalDateTime() to 31,
                    "2024/3/11-18:00".toLocalDateTime() to 60,
                ).forEach { (start, timeUnit) ->
                    shouldThrow<MemberException> {
                        mentor.validateReservationData(
                            Reservation(start = start, end = start.plusMinutes(timeUnit.toLong())),
                        )
                    } shouldHaveMessage CANNOT_RESERVATION.message
                }
            }
        }

        context("4. 예약할 멘토링 시각 정보가 멘토의 스케줄 시간대에 포함되지 않으면") {
            val time: LocalTime = "18:00".toLocalTime()
            val mentor: Mentor = fixture.toDomainWithMentoringInfo(
                mentoringPeriod = MentoringPeriod(
                    startDate = "2024/3/1".toLocalDate(),
                    endDate = "2024/3/11".toLocalDate(),
                    timeUnit = MentoringPeriod.TimeUnit.HALF_HOUR,
                ),
                timelines = listOf(
                    Timeline(DayOfWeek.WED, time, time.plusHours(3)),
                ),
            )

            it("CANNOT_RESERVATION 예외가 발생한다") {
                listOf(
                    "2024/3/5-18:00".toLocalDateTime(),
                    "2024/3/6-17:30".toLocalDateTime(),
                    "2024/3/6-17:50".toLocalDateTime(),
                    "2024/3/6-20:50".toLocalDateTime(),
                    "2024/3/6-21:00".toLocalDateTime(),
                    "2024/3/6-21:10".toLocalDateTime(),
                    "2024/3/7-18:00".toLocalDateTime(),
                ).forEach {
                    shouldThrow<MemberException> {
                        mentor.validateReservationData(
                            Reservation(start = it, end = it.plusMinutes(30)),
                        )
                    } shouldHaveMessage CANNOT_RESERVATION.message
                }
            }
        }

        context("5. 모든 검증에 통과하면") {
            val time: LocalTime = "18:00".toLocalTime()
            val mentor: Mentor = fixture.toDomainWithMentoringInfo(
                mentoringPeriod = MentoringPeriod(
                    startDate = "2024/3/1".toLocalDate(),
                    endDate = "2024/3/11".toLocalDate(),
                    timeUnit = MentoringPeriod.TimeUnit.HALF_HOUR,
                ),
                timelines = listOf(
                    Timeline(DayOfWeek.MON, time, time.plusHours(3)),
                    Timeline(DayOfWeek.WED, time, time.plusHours(3)),
                    Timeline(DayOfWeek.FRI, time, time.plusHours(3)),
                ),
            )

            it("1차 예약 시간 검증에 성공한다 -> 이후 멘토의 예약된 스케줄과 중복되는지 검증") {
                listOf(
                    "2024/3/1-18:00".toLocalDateTime(),
                    "2024/3/1-18:30".toLocalDateTime(),
                    "2024/3/1-20:30".toLocalDateTime(),
                    "2024/3/6-18:00".toLocalDateTime(),
                    "2024/3/6-18:30".toLocalDateTime(),
                    "2024/3/6-20:30".toLocalDateTime(),
                    "2024/3/11-18:00".toLocalDateTime(),
                    "2024/3/11-18:30".toLocalDateTime(),
                    "2024/3/11-20:30".toLocalDateTime(),
                ).forEach {
                    shouldNotThrowAny {
                        mentor.validateReservationData(
                            Reservation(start = it, end = it.plusMinutes(30)),
                        )
                    }
                }
            }
        }
    }
})
