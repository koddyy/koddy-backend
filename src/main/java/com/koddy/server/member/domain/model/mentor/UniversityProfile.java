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

    @Column(name = "entered_in", nullable = false)
    private int enteredIn;

    public UniversityProfile(final String school, final String major, final int enteredIn) {
        this.school = school;
        this.major = major;
        this.enteredIn = enteredIn;
    }

    public UniversityProfile update(final String school, final String major, final int enteredIn) {
        return new UniversityProfile(school, major, enteredIn);
    }
}
