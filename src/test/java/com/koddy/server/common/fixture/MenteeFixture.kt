package com.koddy.server.common.fixture

import com.koddy.server.acceptance.member.MemberAcceptanceStep
import com.koddy.server.auth.domain.model.AuthMember
import com.koddy.server.auth.domain.model.AuthToken
import com.koddy.server.auth.domain.model.oauth.OAuthProvider
import com.koddy.server.auth.infrastructure.social.google.response.GoogleUserResponse
import com.koddy.server.auth.infrastructure.social.kakao.response.KakaoUserResponse
import com.koddy.server.auth.infrastructure.social.zoom.response.ZoomUserResponse
import com.koddy.server.common.utils.TokenUtils.ACCESS_TOKEN
import com.koddy.server.common.utils.TokenUtils.REFRESH_TOKEN
import com.koddy.server.member.domain.model.Email
import com.koddy.server.member.domain.model.Language
import com.koddy.server.member.domain.model.Nationality
import com.koddy.server.member.domain.model.SocialPlatform
import com.koddy.server.member.domain.model.mentee.Interest
import com.koddy.server.member.domain.model.mentee.Mentee
import java.time.LocalDateTime
import java.util.UUID

object MenteeFixtureStore {
    fun menteeFixture(sequence: Int): MenteeFixture {
        require(sequence > 0)
        return createMenteeFixture(sequence = sequence)
    }

    fun menteeFixture(id: Long): MenteeFixture {
        require(id > 0)
        return createMenteeFixture(id = id)
    }

    data class MenteeFixture(
        val id: Long = 0L,
        val platform: SocialPlatform,
        val name: String,
        val profileImageUrl: String,
        val nationality: Nationality,
        val introduction: String,
        val languages: List<Language>,
        val interest: Interest,
    ) {
        fun toDomain(): Mentee {
            return Mentee(
                id = this.id,
                platform = this.platform,
                name = this.name,
                nationality = this.nationality,
                languages = this.languages,
                interest = this.interest,
            ).also {
                it.completeProfile(
                    introduction = this.introduction,
                    profileImageUrl = this.profileImageUrl,
                )
            }
        }

        fun toDomainWithLanguages(languages: List<Language>): Mentee {
            return Mentee(
                id = this.id,
                platform = this.platform,
                name = this.name,
                nationality = this.nationality,
                languages = languages,
                interest = this.interest,
            ).also {
                it.completeProfile(
                    introduction = this.introduction,
                    profileImageUrl = this.profileImageUrl,
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
                AuthToken(ACCESS_TOKEN, REFRESH_TOKEN),
            )
        }

        fun 회원가입과_로그인을_진행한다(): AuthMember {
            val result = MemberAcceptanceStep.멘티_회원가입_후_로그인을_진행한다(this).extract()
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
            val result = MemberAcceptanceStep.멘티_회원가입_후_로그인을_진행한다(this).extract()
            val memberId = result.jsonPath().getLong("id")
            val accessToken = result.header(AuthToken.ACCESS_TOKEN_HEADER).split(" ".toRegex())[1]
            val refreshToken = result.cookie(AuthToken.REFRESH_TOKEN_HEADER)

            MemberAcceptanceStep.멘티_프로필을_완성시킨다(this, accessToken)
            return AuthMember(
                memberId,
                this.name,
                AuthToken(accessToken, refreshToken),
            )
        }
    }

    private fun createMenteeFixture(sequence: Int): MenteeFixture {
        require(sequence >= 0)
        return MenteeFixture(
            platform = SocialPlatform(
                provider = OAuthProvider.GOOGLE,
                socialId = "ID-MENTEE-${sequence}",
                email = Email("mentee${sequence}@gmail.com"),
            ),
            name = "멘티${sequence}",
            profileImageUrl = "s3/Mentee${sequence}.png",
            nationality = pickNationality(sequence.toLong()),
            introduction = "Hello World ~~ $sequence",
            languages = pickLanguages(sequence.toLong()),
            interest = pickInterest(sequence.toLong()),
        )
    }

    private fun createMenteeFixture(id: Long): MenteeFixture {
        require(id > 0)
        return MenteeFixture(
            id = id,
            platform = SocialPlatform(
                provider = OAuthProvider.GOOGLE,
                socialId = "ID-MENTEE-${id}",
                email = Email("mentee${id}@gmail.com"),
            ),
            name = "멘티${id}",
            profileImageUrl = "s3/Mentee${id}.png",
            nationality = pickNationality(id),
            introduction = "Hello World ~~ $id",
            languages = pickLanguages(id),
            interest = pickInterest(id),
        )
    }

    private fun pickNationality(id: Long): Nationality {
        return when (id.toInt() - 1) {
            0 -> Nationality.USA
            1 -> Nationality.JAPAN
            2 -> Nationality.CHINA
            3 -> Nationality.VIETNAM
            else -> Nationality.ETC
        }
    }

    private fun pickLanguages(id: Long): List<Language> {
        return when (id.toInt() - 1) {
            0 -> LanguageFixture.메인_한국어_서브_영어()
            else -> LanguageFixture.메인_한국어_서브_일본어_중국어()
        }
    }

    private fun pickInterest(id: Long): Interest {
        return when (id.toInt() - 1) {
            0 -> Interest(school = "경기대학교", major = "컴퓨터공학부")
            1 -> Interest(school = "서울대학교", major = "컴퓨터공학부")
            2 -> Interest(school = "연세대학교", major = "컴퓨터공학부")
            3 -> Interest(school = "고려대학교", major = "컴퓨터공학부")
            else -> Interest(school = "경기대학교", major = "컴퓨터공학부")
        }
    }
}
