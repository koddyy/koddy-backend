package com.koddy.server.member.domain.model

import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.FetchType.LAZY
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType.IDENTITY
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "member_language")
class AvailableLanguage(
    id: Long = 0L,
    language: Language,
    member: Member<*>,
) {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    val id: Long = id

    @Embedded
    val language: Language = language

    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "member_id", referencedColumnName = "id", nullable = false)
    val member: Member<*> = member

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as AvailableLanguage
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}
