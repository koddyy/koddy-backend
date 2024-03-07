package com.koddy.server.member.domain.model.mentor

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
@Table(name = "mentor_schedule")
class Schedule(
    id: Long = 0L,
    timeline: Timeline,
    mentor: Mentor,
) {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    val id: Long = id

    @Embedded
    val timeline: Timeline = timeline

    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "mentor_id", referencedColumnName = "id", nullable = false)
    val mentor: Mentor = mentor

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Schedule
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}
