package com.koddy.server.member.domain.model.mentee;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Embeddable
public class Interest {
    @Column(name = "interest_school", nullable = false)
    private String school;

    @Column(name = "interest_major", nullable = false)
    private String major;

    public Interest(final String school, final String major) {
        this.school = school;
        this.major = major;
    }

    public Interest update(final String interestSchool, final String interestMajor) {
        return new Interest(interestSchool, interestMajor);
    }
}
