package com.koddy.server.member.domain.model.mentor

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
data class UniversityProfile(
    @Column(name = "school", nullable = false)
    val school: String,

    @Column(name = "major", nullable = false)
    val major: String,

    @Column(name = "entered_in", nullable = false)
    val enteredIn: Int,
) {
    fun update(
        school: String,
        major: String,
        enteredIn: Int,
    ): UniversityProfile {
        return UniversityProfile(school, major, enteredIn)
    }
}
