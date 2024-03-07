package com.koddy.server.member.domain.model

import com.koddy.server.global.base.BaseTimeEntity
import com.koddy.server.member.domain.model.Member.Status.ACTIVE
import com.koddy.server.member.exception.MemberException
import com.koddy.server.member.exception.MemberExceptionCode.AVAILABLE_LANGUAGE_MUST_EXISTS
import com.koddy.server.member.exception.MemberExceptionCode.MAIN_LANGUAGE_MUST_BE_ONLY_ONE
import jakarta.persistence.CascadeType.PERSIST
import jakarta.persistence.CascadeType.REMOVE
import jakarta.persistence.Column
import jakarta.persistence.DiscriminatorColumn
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.EnumType.STRING
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType.IDENTITY
import jakarta.persistence.Id
import jakarta.persistence.Inheritance
import jakarta.persistence.InheritanceType.JOINED
import jakarta.persistence.Lob
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import org.hibernate.annotations.SQLRestriction

@Entity
@Table(name = "member")
@Inheritance(strategy = JOINED)
@DiscriminatorColumn(name = "type")
@SQLRestriction("status = 'ACTIVE'")
abstract class Member<T : Member<T>> private constructor(
    id: Long = 0L,
    platform: SocialPlatform,
    name: String,
    nationality: Nationality,
    role: Role,
) : BaseTimeEntity() {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    val id: Long = id

    @Embedded
    var platform: SocialPlatform = platform
        protected set

    @Column(name = "name", nullable = false)
    var name: String = name
        protected set

    @Enumerated(STRING)
    @Column(name = "nationality", nullable = false, columnDefinition = "VARCHAR(50)")
    var nationality: Nationality = nationality
        protected set

    @Lob
    @Column(name = "introduction", columnDefinition = "TEXT")
    var introduction: String? = null
        protected set

    @Column(name = "profile_image_url")
    var profileImageUrl: String? = null
        protected set

    @Column(name = "profile_complete", nullable = false, columnDefinition = "TINYINT")
    var isProfileComplete: Boolean = false
        protected set

    @Enumerated(STRING)
    @Column(name = "status", nullable = false, columnDefinition = "VARCHAR(20)")
    val status: Status = ACTIVE

    @Enumerated(STRING)
    @Column(name = "role", nullable = false, columnDefinition = "VARCHAR(30)")
    var role: Role = role
        protected set

    @OneToMany(mappedBy = "member", cascade = [PERSIST, REMOVE], orphanRemoval = true)
    val availableLanguages: MutableList<AvailableLanguage> = mutableListOf()

    val languages: List<Language>
        get() = availableLanguages.map(AvailableLanguage::language)

    val authority: String
        get() = role.authority

    protected constructor(
        id: Long = 0L,
        platform: SocialPlatform,
        name: String,
        nationality: Nationality,
        role: Role,
        languages: List<Language>,
    ) : this(
        id = id,
        platform = platform,
        name = name,
        nationality = nationality,
        role = role,
    ) {
        applyLanguages(languages)
    }

    private fun applyLanguages(languages: List<Language>) {
        validateEmpty(languages)
        validateMainLanguageIsOnlyOne(languages)

        availableLanguages.clear()
        availableLanguages.addAll(languages.map { AvailableLanguage(member = this, language = it) })
    }

    private fun validateEmpty(languages: List<Language>) {
        if (languages.isEmpty()) {
            throw MemberException(AVAILABLE_LANGUAGE_MUST_EXISTS)
        }
    }

    private fun validateMainLanguageIsOnlyOne(languages: List<Language>) {
        if (languages.count { it.type == Language.Type.MAIN } != 1) {
            throw MemberException(MAIN_LANGUAGE_MUST_BE_ONLY_ONE)
        }
    }

    protected fun completeProfile(
        introduction: String?,
        profileImageUrl: String?,
    ) {
        this.introduction = introduction
        this.profileImageUrl = profileImageUrl
    }

    protected fun updateBasicInfo(
        name: String,
        nationality: Nationality,
        profileImageUrl: String?,
        introduction: String?,
        languages: List<Language>,
    ) {
        this.name = name
        this.nationality = nationality
        this.profileImageUrl = profileImageUrl
        this.introduction = introduction
        applyLanguages(languages)
    }

    fun syncEmail(email: Email) {
        this.platform = platform.syncEmail(email)
    }

    abstract fun checkProfileCompleted()

    enum class Status {
        ACTIVE,
        INACTIVE,
        BAN,
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Member<*>
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}
