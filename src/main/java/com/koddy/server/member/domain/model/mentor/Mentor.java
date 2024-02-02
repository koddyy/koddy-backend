package com.koddy.server.member.domain.model.mentor;

import com.koddy.server.coffeechat.domain.model.Reservation;
import com.koddy.server.member.domain.model.Language;
import com.koddy.server.member.domain.model.Member;
import com.koddy.server.member.domain.model.Role;
import com.koddy.server.member.domain.model.SocialPlatform;
import com.koddy.server.member.exception.MemberException;
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
import static com.koddy.server.member.domain.model.Role.MENTOR;
import static com.koddy.server.member.exception.MemberExceptionCode.CANNOT_RESERVATION;
import static com.koddy.server.member.exception.MemberExceptionCode.MENTOR_NOT_FILL_IN_SCHEDULE;
import static jakarta.persistence.CascadeType.PERSIST;
import static jakarta.persistence.CascadeType.REMOVE;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(name = "mentor")
@DiscriminatorValue(value = Role.Value.MENTOR)
public class Mentor extends Member<Mentor> {
    @Embedded
    private UniversityProfile universityProfile;

    @Embedded
    private UniversityAuthentication universityAuthentication;

    @Embedded
    private MentoringPeriod mentoringPeriod;

    @OneToMany(mappedBy = "mentor", cascade = {PERSIST, REMOVE}, orphanRemoval = true)
    private final List<Schedule> schedules = new ArrayList<>();

    public Mentor(
            final SocialPlatform platform,
            final String name,
            final String profileImageUrl,
            final List<Language> languages,
            final UniversityProfile universityProfile
    ) {
        super(platform, name, profileImageUrl, KOREA, MENTOR, languages);
        this.universityProfile = universityProfile;
    }

    public void completeInfo(
            final String introduction,
            final MentoringPeriod mentoringPeriod,
            final List<Timeline> timelines
    ) {
        super.completeInfo(introduction);
        this.mentoringPeriod = mentoringPeriod;
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

    public void updateSchedules(final MentoringPeriod mentoringPeriod, final List<Timeline> timelines) {
        this.mentoringPeriod = mentoringPeriod;
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

    public void authWithMail(final String schoolMail) {
        universityAuthentication = UniversityAuthentication.attemptMail(schoolMail);
    }

    public void authWithProofData(final String proofDataUploadUrl) {
        universityAuthentication = UniversityAuthentication.attemptProofData(proofDataUploadUrl);
    }

    public void authComplete() {
        universityAuthentication.complete();
    }

    public void validateReservationData(final Reservation start, final Reservation end) {
        if (mentoringPeriod == null || schedules.isEmpty()) {
            throw new MemberException(MENTOR_NOT_FILL_IN_SCHEDULE);
        }

        if (isOutOfDate(start)) {
            throw new MemberException(CANNOT_RESERVATION);
        }

        if (notAllowedTime(start, end)) {
            throw new MemberException(CANNOT_RESERVATION);
        }
    }

    private boolean isOutOfDate(final Reservation start) {
        return !mentoringPeriod.isDateIncluded(start.toLocalDate());
    }

    private boolean notAllowedTime(final Reservation start, final Reservation end) {
        final DayOfWeek dayOfWeek = DayOfWeek.of(start.getYear(), start.getMonth(), start.getDay());

        final List<Timeline> filteringWithStart = schedules.stream()
                .map(Schedule::getTimeline)
                .filter(it -> it.getDayOfWeek() == dayOfWeek)
                .filter(it -> it.isTimeIncluded(start.getTime()))
                .toList();
        final List<Timeline> filteringWithEnd = schedules.stream()
                .map(Schedule::getTimeline)
                .filter(it -> it.getDayOfWeek() == dayOfWeek)
                .filter(it -> it.isTimeIncluded(end.getTime()))
                .toList();
        return filteringWithStart.isEmpty() || filteringWithEnd.isEmpty();
    }

    @Override
    public boolean isProfileComplete() {
        return StringUtils.hasText(introduction)
                && mentoringPeriod != null
                && !CollectionUtils.isEmpty(schedules);
    }

    public boolean isAuthenticated() {
        if (universityAuthentication == null) {
            return false;
        }
        return universityAuthentication.isAuthenticated();
    }

    public Integer getMentoringTimeUnit() {
        if (mentoringPeriod == null) {
            return null;
        }
        return mentoringPeriod.getTimeUnit().getValue();
    }
}
