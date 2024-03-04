package com.koddy.server.member.domain.model.mentee

import com.koddy.server.member.domain.model.Language
import com.koddy.server.member.domain.model.Member
import com.koddy.server.member.domain.model.Nationality
import com.koddy.server.member.domain.model.Role
import com.koddy.server.member.domain.model.SocialPlatform
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "mentee")
@DiscriminatorValue(value = Role.MENTEE_VALUE)
class Mentee(
    id: Long = 0L,
    platform: SocialPlatform,
    name: String,
    nationality: Nationality,
    languages: List<Language>,
    interest: Interest,
) : Member<Mentee>(
    id = id,
    platform = platform,
    name = name,
    nationality = nationality,
    role = Role.MENTEE,
    languages = languages,
) {
    @Embedded
    var interest: Interest = interest
        protected set

    override fun checkProfileCompleted() {
        super.profileComplete = isCompleted
    }

    private val isCompleted: Boolean
        get() = !introduction.isNullOrBlank() && !profileImageUrl.isNullOrBlank()

    public override fun completeProfile(
        introduction: String?,
        profileImageUrl: String?,
    ) {
        super.completeProfile(
            introduction = introduction,
            profileImageUrl = profileImageUrl,
        )
        checkProfileCompleted()
    }

    fun updateBasicInfo(
        name: String,
        nationality: Nationality,
        profileImageUrl: String?,
        introduction: String?,
        languages: List<Language>,
        interestSchool: String,
        interestMajor: String,
    ) {
        super.updateBasicInfo(
            name = name,
            nationality = nationality,
            profileImageUrl = profileImageUrl,
            introduction = introduction,
            languages = languages,
        )
        this.interest = interest.update(
            interestSchool = interestSchool,
            interestMajor = interestMajor,
        )
        checkProfileCompleted()
    }
}
