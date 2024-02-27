package com.koddy.server.auth.infrastructure.social.kakao.response

import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.annotation.JsonNaming
import com.koddy.server.auth.domain.model.oauth.OAuthUserResponse
import java.time.LocalDateTime

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy::class)
data class KakaoUserResponse(
    val id: String,
    val connectedAt: LocalDateTime,
    val kakaoAccount: KakaoAccount?,
) : OAuthUserResponse {
    override fun id(): String = id

    override fun name(): String = kakaoAccount!!.profile.nickname

    override fun email(): String = kakaoAccount!!.email

    override fun profileImageUrl(): String = kakaoAccount!!.profile.profileImageUrl
}

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy::class)
data class KakaoAccount(
    val profile: Profile,
    val profileNicknameNeedsAgreement: Boolean?,
    val profileImageNeesAgreement: Boolean?,
    val name: String?,
    val nameNeedsAgreement: Boolean?,
    val email: String,
    val emailNeedsAgreement: Boolean,
    val isEmailValid: Boolean,
    val isEmailVerified: Boolean,
    val ageRange: String?,
    val ageRangeNeedsAgreement: Boolean?,
    val birthYear: String?,
    val birthYearNeedsAgreement: Boolean?,
    val birthDay: String?,
    val birthDayType: String?,
    val birthDayNeedsAgreement: Boolean?,
    val gender: String?,
    val genderNeedsAgreement: Boolean?,
    val phoneNumber: String?,
    val phoneNumberNeedsAgreement: Boolean?,
)

@JsonNaming(value = PropertyNamingStrategies.SnakeCaseStrategy::class)
data class Profile(
    val nickname: String,
    val thumbnailImageUrl: String,
    val profileImageUrl: String,
    val isDefaultImage: Boolean,
)
