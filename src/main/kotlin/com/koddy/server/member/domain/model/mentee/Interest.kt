package com.koddy.server.member.domain.model.mentee

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
data class Interest(
    @Column(name = "interest_school", nullable = false)
    val school: String,

    @Column(name = "interest_major", nullable = false)
    val major: String,
) {
    fun update(
        interestSchool: String,
        interestMajor: String,
    ): Interest {
        return Interest(interestSchool, interestMajor)
    }
}
