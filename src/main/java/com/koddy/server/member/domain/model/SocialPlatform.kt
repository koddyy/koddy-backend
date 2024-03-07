package com.koddy.server.member.domain.model

import com.koddy.server.auth.domain.model.oauth.OAuthProvider
import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.Embedded
import jakarta.persistence.EnumType.STRING
import jakarta.persistence.Enumerated

@Embeddable
data class SocialPlatform(
    @Enumerated(STRING)
    @Column(name = "social_provider", columnDefinition = "VARCHAR(30)")
    val provider: OAuthProvider,

    @Column(name = "social_id", unique = true)
    val socialId: String?,

    @Embedded
    val email: Email?,
) {
    fun syncEmail(email: Email): SocialPlatform {
        return SocialPlatform(provider, socialId, email)
    }
}
