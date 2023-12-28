package com.koddy.server.member.domain.model.mentor;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Embeddable
public class UniversityProfile {
    @Column(name = "school", nullable = false)
    private String school;

    @Column(name = "major", nullable = false)
    private String major;

    @Column(name = "grade", nullable = false)
    private int grade;

    public UniversityProfile(final String school, final String major, final int grade) {
        this.school = school;
        this.major = major;
        this.grade = grade;
    }
}
