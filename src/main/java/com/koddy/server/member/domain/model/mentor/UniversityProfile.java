package com.koddy.server.member.domain.model.mentor;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

@Embeddable
public class UniversityProfile {
    protected UniversityProfile() {
    }

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

    public String getSchool() {
        return school;
    }

    public String getMajor() {
        return major;
    }

    public int getEnteredIn() {
        return enteredIn;
    }
}
