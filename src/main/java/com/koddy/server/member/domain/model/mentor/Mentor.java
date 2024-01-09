package com.koddy.server.member.domain.model.mentor;

import com.koddy.server.member.domain.model.Email;
import com.koddy.server.member.domain.model.Language;
import com.koddy.server.member.domain.model.Member;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static com.koddy.server.member.domain.model.Nationality.KOREA;
import static com.koddy.server.member.domain.model.RoleType.MENTOR;
import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.CascadeType.REMOVE;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(name = "mentor")
@DiscriminatorValue(value = Member.MemberType.Value.MENTOR)
public class Mentor extends Member<Mentor> {
    @Embedded
    private UniversityProfile universityProfile;

    @OneToMany(mappedBy = "mentor", cascade = {PERSIST, REMOVE}, orphanRemoval = true)
    private final List<Schedule> schedules = new ArrayList<>();

    public Mentor(
            final Email email,
            final String name,
            final String profileImageUrl,
            final String introduction,
            final List<Language> languages,
            final UniversityProfile universityProfile,
            final List<Timeline> timelines
    ) {
        super(email, name, profileImageUrl, KOREA, introduction, languages, List.of(MENTOR));
        this.universityProfile = universityProfile;
        applySchedules(timelines);
    }

    private void applySchedules(final List<Timeline> timelines) {
        this.schedules.clear();
        if (!CollectionUtils.isEmpty(timelines)) {
            this.schedules.addAll(
                    timelines.stream()
                            .map(it -> new Schedule(this, it))
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
            final int enteredIn
    ) {
        super.updateBasicInfo(name, KOREA, profileImageUrl, introduction, languages);
        this.universityProfile = this.universityProfile.update(school, major, enteredIn);
    }

    public void updateSchedules(final List<Timeline> timelines) {
        applySchedules(timelines);
    }

    @Override
    public boolean isProfileComplete() {
        return StringUtils.hasText(introduction) && !this.schedules.isEmpty();
    }
}
