package com.koddy.server.member.domain.model.mentor;

import com.koddy.server.global.encrypt.Encryptor;
import com.koddy.server.member.domain.model.Email;
import com.koddy.server.member.domain.model.Language;
import com.koddy.server.member.domain.model.Member;
import com.koddy.server.member.domain.model.Nationality;
import com.koddy.server.member.domain.model.Password;
import com.koddy.server.member.exception.MemberException;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

import static com.koddy.server.member.domain.model.RoleType.MENTOR;
import static com.koddy.server.member.exception.MemberExceptionCode.SCHEDULE_MUST_BE_EXISTS;
import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.CascadeType.REMOVE;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(name = "mentor")
public class Mentor extends Member<Mentor> {
    @Embedded
    private UniversityProfile universityProfile;

    @Column(name = "meeting_url", nullable = false)
    private String meetingUrl;

    @OneToMany(mappedBy = "mentor", cascade = {PERSIST, REMOVE}, orphanRemoval = true)
    private final List<ChatTime> chatTimes = new ArrayList<>();

    public Mentor(final Email email, final Password password) {
        super(email, password, List.of(MENTOR));
        this.universityProfile = new UniversityProfile(EMPTY, EMPTY, 0);
        this.meetingUrl = EMPTY;
    }

    public void complete(
            final String name,
            final Nationality nationality,
            final String profileImageUrl,
            final String introduction,
            final List<Language> languages,
            final UniversityProfile universityProfile,
            final String meetingUrl,
            final List<Schedule> schedules
    ) {
        super.complete(name, nationality, profileImageUrl, introduction, languages);
        this.universityProfile = universityProfile;
        this.meetingUrl = meetingUrl;
        applySchedules(schedules);
    }

    private void applySchedules(final List<Schedule> schedules) {
        validateScheduleCount(schedules);
        this.chatTimes.clear();
        this.chatTimes.addAll(
                schedules.stream()
                        .map(it -> new ChatTime(this, it))
                        .toList()
        );
    }

    private void validateScheduleCount(final List<Schedule> schedules) {
        if (schedules.isEmpty()) {
            throw new MemberException(SCHEDULE_MUST_BE_EXISTS);
        }
    }

    public void updateBasicInfo(
            final String name,
            final String profileImageUrl,
            final String introduction,
            final List<Language> languages,
            final String school,
            final String major,
            final int grade,
            final String meetingUrl
    ) {
        super.updateBasicInfo(name, profileImageUrl, introduction, languages);
        this.universityProfile = this.universityProfile.update(school, major, grade);
        this.meetingUrl = meetingUrl;
    }

    public void updatePassword(final String currentPassword, final String updatePassword, final Encryptor encryptor) {
        super.updatePassword(currentPassword, updatePassword, encryptor);
    }

    public void updateSchedules(final List<Schedule> schedules) {
        applySchedules(schedules);
    }
}
