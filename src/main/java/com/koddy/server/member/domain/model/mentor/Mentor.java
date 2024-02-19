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
@DiscriminatorValue(value = Role.MENTOR_VALUE)
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
            final List<Language> languages,
            final UniversityProfile universityProfile
    ) {
        super(
                platform,
                name,
                KOREA,
                MENTOR,
                languages
        );
        this.universityProfile = universityProfile;
    }

    public void completeInfo(
            final String introduction,
            final String profileImageUrl,
            final MentoringPeriod mentoringPeriod,
            final List<Timeline> timelines
    ) {
        super.completeInfo(introduction, profileImageUrl);
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

    public void validateReservationData(final Reservation reservation) {
        // 1. 멘토 스케줄 정보 입력 완료 확인
        if (mentoringPeriod == null || schedules.isEmpty()) {
            throw new MemberException(MENTOR_NOT_FILL_IN_SCHEDULE);
        }

        // 2. 멘토링 진행 기간에 포함되는지 확인
        if (isOutOfDate(reservation)) {
            throw new MemberException(CANNOT_RESERVATION);
        }

        // 3. 멘토의 각 멘토링 진행 시간(TimeUnit)과 일치하는지 확인
        if (nowAllowedTimeUnit(reservation)) {
            throw new MemberException(CANNOT_RESERVATION);
        }

        // 4. 요일별 스케줄 시간대에 포함되는지 확인
        if (notAllowedSchedule(reservation)) {
            throw new MemberException(CANNOT_RESERVATION);
        }
    }

    private boolean isOutOfDate(final Reservation reservation) {
        return !mentoringPeriod.isDateIncluded(reservation.getStart().toLocalDate())
                || !mentoringPeriod.isDateIncluded(reservation.getEnd().toLocalDate());
    }

    private boolean nowAllowedTimeUnit(final Reservation reservation) {
        return !mentoringPeriod.allowedTimeUnit(reservation.getStart(), reservation.getEnd());
    }

    private boolean notAllowedSchedule(final Reservation reservation) {
        final DayOfWeek dayOfWeek = DayOfWeek.of(
                reservation.getStart().getYear(),
                reservation.getStart().getMonthValue(),
                reservation.getStart().getDayOfMonth()
        );

        final List<Timeline> filteringWithStart = schedules.stream()
                .map(Schedule::getTimeline)
                .filter(it -> it.getDayOfWeek() == dayOfWeek)
                .filter(it -> it.isTimeIncluded(reservation.getStart().toLocalTime()))
                .toList();
        final List<Timeline> filteringWithEnd = schedules.stream()
                .map(Schedule::getTimeline)
                .filter(it -> it.getDayOfWeek() == dayOfWeek)
                .filter(it -> it.isTimeIncluded(reservation.getEnd().toLocalTime()))
                .toList();
        return filteringWithStart.isEmpty() || filteringWithEnd.isEmpty();
    }

    @Override
    public boolean isProfileComplete() {
        return StringUtils.hasText(introduction)
                && StringUtils.hasText(profileImageUrl)
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
