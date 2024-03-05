package com.koddy.server.common.fixture

import com.koddy.server.acceptance.member.MemberAcceptanceStep
import com.koddy.server.auth.domain.model.AuthMember
import com.koddy.server.auth.domain.model.AuthToken
import com.koddy.server.auth.domain.model.oauth.OAuthProvider
import com.koddy.server.auth.infrastructure.social.google.response.GoogleUserResponse
import com.koddy.server.auth.infrastructure.social.kakao.response.KakaoUserResponse
import com.koddy.server.auth.infrastructure.social.zoom.response.ZoomUserResponse
import com.koddy.server.common.utils.TokenUtils
import com.koddy.server.member.domain.model.Email
import com.koddy.server.member.domain.model.Language
import com.koddy.server.member.domain.model.SocialPlatform
import com.koddy.server.member.domain.model.mentor.Mentor
import com.koddy.server.member.domain.model.mentor.MentoringPeriod
import com.koddy.server.member.domain.model.mentor.Timeline
import com.koddy.server.member.domain.model.mentor.UniversityProfile
import com.koddy.server.member.presentation.request.MentorScheduleRequest
import com.koddy.server.member.presentation.request.model.MentoringPeriodRequestModel
import java.time.LocalDateTime
import java.util.UUID

object MentorFixtureStore {
    fun mentorFixture(sequence: Int): MentorFixture {
        require(sequence > 0)
        return createMentorFixture(sequence = sequence)
    }

    fun mentorFixture(id: Long): MentorFixture {
        require(id > 0)
        return createMentorFixture(id = id)
    }

    data class MentorFixture(
        val id: Long = 0L,
        val platform: SocialPlatform,
        val name: String,
        val profileImageUrl: String,
        val introduction: String,
        val languages: List<Language>,
        val universityProfile: UniversityProfile,
        val mentoringPeriod: MentoringPeriod,
        val timelines: List<Timeline>,
    ) {
        fun toDomain(): Mentor {
            return Mentor(
                id = this.id,
                platform = this.platform,
                name = this.name,
                languages = this.languages,
                universityProfile = this.universityProfile,
            ).also {
                it.completeProfile(
                    introduction = this.introduction,
                    profileImageUrl = this.profileImageUrl,
                    mentoringPeriod = this.mentoringPeriod,
                    timelines = this.timelines,
                )
            }
        }

        fun toDomainWithLanguages(languages: List<Language>): Mentor {
            return Mentor(
                id = this.id,
                platform = this.platform,
                name = this.name,
                languages = languages,
                universityProfile = this.universityProfile,
            ).also {
                it.completeProfile(
                    introduction = this.introduction,
                    profileImageUrl = this.profileImageUrl,
                    mentoringPeriod = this.mentoringPeriod,
                    timelines = this.timelines,
                )
            }
        }

        fun toDomainWithMentoringInfo(
            mentoringPeriod: MentoringPeriod,
            timelines: List<Timeline>,
        ): Mentor {
            return Mentor(
                id = this.id,
                platform = this.platform,
                name = this.name,
                languages = this.languages,
                universityProfile = this.universityProfile,
            ).also {
                it.completeProfile(
                    introduction = this.introduction,
                    profileImageUrl = this.profileImageUrl,
                    mentoringPeriod = mentoringPeriod,
                    timelines = timelines,
                )
            }
        }

        fun toDomainWithLanguagesAndMentoringInfo(
            languages: List<Language>,
            mentoringPeriod: MentoringPeriod,
            timelines: List<Timeline>,
        ): Mentor {
            return Mentor(
                id = this.id,
                platform = this.platform,
                name = this.name,
                languages = languages,
                universityProfile = this.universityProfile,
            ).also {
                it.completeProfile(
                    introduction = this.introduction,
                    profileImageUrl = this.profileImageUrl,
                    mentoringPeriod = mentoringPeriod,
                    timelines = timelines,
                )
            }
        }

        fun toGoogleUserResponse(): GoogleUserResponse {
            return GoogleUserResponse(
                sub = this.platform.socialId!!,
                name = this.name,
                givenName = this.name,
                familyName = this.name,
                picture = this.profileImageUrl,
                email = this.platform.email!!.value,
                emailVerified = true,
                locale = "kr",
            )
        }

        fun toKakaoUserResponse(): KakaoUserResponse {
            return KakaoUserResponse(
                id = platform.socialId!!,
                connectedAt = LocalDateTime.now(),
                kakaoAccount = null,
            )
        }

        fun toZoomUserResponse(): ZoomUserResponse {
            return ZoomUserResponse(
                id = platform.socialId!!,
                accountId = platform.socialId,
                accountNumber = UUID.randomUUID().toString(),
                firstName = this.name,
                lastName = this.name,
                displayName = this.name,
                email = platform.email!!.value,
                roleName = UUID.randomUUID().toString(),
                pmi = UUID.randomUUID().toString(),
                personalMeetingUrl = UUID.randomUUID().toString(),
                timezone = "Asia/Seoul",
                picUrl = UUID.randomUUID().toString(),
            )
        }

        fun toAuthMember(): AuthMember {
            return AuthMember(
                toDomain(),
                AuthToken(TokenUtils.ACCESS_TOKEN, TokenUtils.REFRESH_TOKEN),
            )
        }

        fun 회원가입과_로그인을_진행한다(): AuthMember {
            val result = MemberAcceptanceStep.멘토_회원가입_후_로그인을_진행한다(this).extract()
            val memberId = result.jsonPath().getLong("id")
            val accessToken = result.header(AuthToken.ACCESS_TOKEN_HEADER).split(" ".toRegex())[1]
            val refreshToken = result.cookie(AuthToken.REFRESH_TOKEN_HEADER)

            return AuthMember(
                memberId,
                this.name,
                AuthToken(accessToken, refreshToken),
            )
        }

        fun 회원가입과_로그인을_하고_프로필을_완성시킨다(): AuthMember {
            val result = MemberAcceptanceStep.멘토_회원가입_후_로그인을_진행한다(this).extract()
            val memberId = result.jsonPath().getLong("id")
            val accessToken = result.header(AuthToken.ACCESS_TOKEN_HEADER).split(" ".toRegex())[1]
            val refreshToken = result.cookie(AuthToken.REFRESH_TOKEN_HEADER)

            MemberAcceptanceStep.멘토_프로필을_완성시킨다(this, accessToken)
            return AuthMember(
                memberId,
                this.name,
                AuthToken(accessToken, refreshToken),
            )
        }

        fun 회원가입과_로그인을_하고_프로필을_완성시킨다(period: MentoringPeriodRequestModel): AuthMember {
            val result = MemberAcceptanceStep.멘토_회원가입_후_로그인을_진행한다(this).extract()
            val memberId = result.jsonPath().getLong("id")
            val accessToken = result.header(AuthToken.ACCESS_TOKEN_HEADER).split(" ".toRegex())[1]
            val refreshToken = result.cookie(AuthToken.REFRESH_TOKEN_HEADER)

            MemberAcceptanceStep.멘토_프로필을_완성시킨다(this, period, accessToken)
            return AuthMember(
                memberId,
                this.name,
                AuthToken(accessToken, refreshToken),
            )
        }

        fun 회원가입과_로그인을_하고_프로필을_완성시킨다(schedules: List<MentorScheduleRequest>): AuthMember {
            val result = MemberAcceptanceStep.멘토_회원가입_후_로그인을_진행한다(this).extract()
            val memberId = result.jsonPath().getLong("id")
            val accessToken = result.header(AuthToken.ACCESS_TOKEN_HEADER).split(" ".toRegex())[1]
            val refreshToken = result.cookie(AuthToken.REFRESH_TOKEN_HEADER)

            MemberAcceptanceStep.멘토_프로필을_완성시킨다(this, schedules, accessToken)
            return AuthMember(
                memberId,
                this.name,
                AuthToken(accessToken, refreshToken),
            )
        }

        fun 회원가입과_로그인을_하고_프로필을_완성시킨다(
            period: MentoringPeriodRequestModel,
            schedules: List<MentorScheduleRequest>,
        ): AuthMember {
            val result = MemberAcceptanceStep.멘토_회원가입_후_로그인을_진행한다(this).extract()
            val memberId = result.jsonPath().getLong("id")
            val accessToken = result.header(AuthToken.ACCESS_TOKEN_HEADER).split(" ".toRegex())[1]
            val refreshToken = result.cookie(AuthToken.REFRESH_TOKEN_HEADER)

            MemberAcceptanceStep.멘토_프로필을_완성시킨다(this, period, schedules, accessToken)
            return AuthMember(
                memberId,
                this.name,
                AuthToken(accessToken, refreshToken),
            )
        }
    }

    private fun createMentorFixture(sequence: Int): MentorFixture {
        require(sequence >= 0)
        return MentorFixture(
            platform = SocialPlatform(
                provider = OAuthProvider.GOOGLE,
                socialId = "ID-MENTOR-${sequence}",
                email = Email("mentor${sequence}@gmail.com"),
            ),
            name = "멘토${sequence}",
            profileImageUrl = "s3/Mentor${sequence}.png",
            introduction = "Hello World ~~ $sequence",
            languages = pickLanguages(sequence.toLong()),
            universityProfile = pickUniversityProfile(sequence.toLong()),
            mentoringPeriod = pickMentoringPeriod(sequence.toLong()),
            timelines = pickTimelines(sequence.toLong()),
        )
    }

    private fun createMentorFixture(id: Long): MentorFixture {
        require(id > 0)
        return MentorFixture(
            id = id,
            platform = SocialPlatform(
                provider = OAuthProvider.GOOGLE,
                socialId = "ID-MENTOR-${id}",
                email = Email("mentor${id}@gmail.com"),
            ),
            name = "멘토${id}",
            profileImageUrl = "s3/Mentor${id}.png",
            introduction = "Hello World ~~ $id",
            languages = pickLanguages(id),
            universityProfile = pickUniversityProfile(id),
            mentoringPeriod = pickMentoringPeriod(id),
            timelines = pickTimelines(id),
        )
    }

    private fun pickLanguages(id: Long): List<Language> {
        return when ((id.toInt() - 1) % 2) {
            0 -> LanguageFixture.메인_한국어_서브_영어()
            else -> LanguageFixture.메인_한국어_서브_일본어_중국어()
        }
    }

    private fun pickUniversityProfile(id: Long): UniversityProfile {
        return when ((id.toInt() - 1) % 5) {
            0 -> UniversityProfile(school = "경기대학교", major = "컴퓨터공학부", enteredIn = 18)
            1 -> UniversityProfile(school = "서울대학교", major = "컴퓨터공학부", enteredIn = 19)
            2 -> UniversityProfile(school = "연세대학교", major = "컴퓨터공학부", enteredIn = 20)
            3 -> UniversityProfile(school = "고려대학교", major = "컴퓨터공학부", enteredIn = 21)
            else -> UniversityProfile(school = "한양대학교", major = "컴퓨터공학부", enteredIn = 22)
        }
    }

    // TODO need Dynamic Selection
    private fun pickMentoringPeriod(id: Long): MentoringPeriod = MentoringPeriodFixture.FROM_01_01_TO_12_31.toDomain()

    private fun pickTimelines(id: Long): List<Timeline> {
        return when ((id.toInt() - 1) % 5) {
            0 -> TimelineFixture.월_수_금()
            1 -> TimelineFixture.화_목_토()
            2 -> TimelineFixture.월_화_수_목_금()
            3 -> TimelineFixture.주말()
            else -> TimelineFixture.allDays()
        }
    }
}
