package com.koddy.server.member.domain.model.mentor;

import com.koddy.server.member.domain.model.Email;
import com.koddy.server.member.domain.model.Language;
import com.koddy.server.member.domain.model.Member;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static com.koddy.server.member.domain.model.Nationality.KOREA;
import static com.koddy.server.member.domain.model.RoleType.MENTOR;
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

    public Mentor(
            final Email email,
            final String name,
            final String profileImageUrl,
            final String introduction,
            final List<Language> languages,
            final UniversityProfile universityProfile,
            final String meetingUrl,
            final List<Schedule> schedules
    ) {
        super(email, name, profileImageUrl, KOREA, introduction, languages, List.of(MENTOR));
        this.universityProfile = universityProfile;
        this.meetingUrl = checkEmptyValue(meetingUrl);
        applySchedules(schedules);
    }

    private void applySchedules(final List<Schedule> schedules) {
        this.chatTimes.clear();
        if (!CollectionUtils.isEmpty(schedules)) {
            this.chatTimes.addAll(
                    schedules.stream()
                            .map(it -> new ChatTime(this, it))
                            .toList()
            );
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
        super.updateBasicInfo(name, KOREA, profileImageUrl, introduction, languages);
        this.universityProfile = this.universityProfile.update(school, major, grade);
        this.meetingUrl = meetingUrl;
    }

    public void updateSchedules(final List<Schedule> schedules) {
        applySchedules(schedules);
    }

    @Override
    public boolean isProfileComplete() {
        return !Objects.equals(this.meetingUrl, EMPTY)
                && !this.chatTimes.isEmpty();
    }
}
