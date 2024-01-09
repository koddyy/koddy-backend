package com.koddy.server.member.domain.model.mentor;

import com.koddy.server.member.domain.model.Email;
import com.koddy.server.member.domain.model.Language;
import com.koddy.server.member.domain.model.Member;
import com.koddy.server.member.domain.model.Role;
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
            final List<Language> languages,
            final UniversityProfile universityProfile
    ) {
        super(email, name, profileImageUrl, KOREA, languages, List.of(Role.Type.MENTOR));
        this.universityProfile = universityProfile;
    }

    public void completeInfo(final String introduction, final List<Timeline> timelines) {
        super.completeInfo(introduction);
        applySchedules(timelines);
        super.checkProfileCompleted();
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
        super.checkProfileCompleted();
    }

    public void updateSchedules(final List<Timeline> timelines) {
        applySchedules(timelines);
        super.checkProfileCompleted();
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

    @Override
    public boolean isProfileComplete() {
        return StringUtils.hasText(introduction) && !CollectionUtils.isEmpty(schedules);
    }
}
