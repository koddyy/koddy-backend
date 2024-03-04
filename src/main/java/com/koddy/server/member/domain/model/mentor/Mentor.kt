package com.koddy.server.member.domain.model.mentor

import com.koddy.server.coffeechat.domain.model.Reservation
import com.koddy.server.member.domain.model.Language
import com.koddy.server.member.domain.model.Member
import com.koddy.server.member.domain.model.Nationality
import com.koddy.server.member.domain.model.Role
import com.koddy.server.member.domain.model.SocialPlatform
import com.koddy.server.member.domain.model.mentor.UniversityAuthentication.Companion.attemptMail
import com.koddy.server.member.domain.model.mentor.UniversityAuthentication.Companion.attemptProofData
import com.koddy.server.member.exception.MemberException
import com.koddy.server.member.exception.MemberExceptionCode.CANNOT_RESERVATION
import com.koddy.server.member.exception.MemberExceptionCode.MENTOR_NOT_FILL_IN_SCHEDULE
import jakarta.persistence.CascadeType.PERSIST
import jakarta.persistence.CascadeType.REMOVE
import jakarta.persistence.DiscriminatorValue
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.OneToMany
import jakarta.persistence.Table

@Entity
@Table(name = "mentor")
@DiscriminatorValue(value = Role.MENTOR_VALUE)
class Mentor(
    id: Long = 0L,
    platform: SocialPlatform,
    name: String,
    languages: List<Language>,
    universityProfile: UniversityProfile,
) : Member<Mentor>(
    id = id,
    platform = platform,
    name = name,
    nationality = Nationality.KOREA,
    role = Role.MENTOR,
    languages = languages,
) {
    @Embedded
    var universityProfile: UniversityProfile = universityProfile
        protected set

    @Embedded
    var universityAuthentication: UniversityAuthentication? = null
        protected set

    @Embedded
    var mentoringPeriod: MentoringPeriod? = null
        protected set

    @OneToMany(mappedBy = "mentor", cascade = [PERSIST, REMOVE], orphanRemoval = true)
    val schedules: MutableList<Schedule> = mutableListOf()

    val isAuthenticated: Boolean
        get() = when (universityAuthentication) {
            null -> false
            else -> universityAuthentication!!.isAuthenticated
        }

    val mentoringTimeUnit: Int?
        get() = when (mentoringPeriod) {
            null -> null
            else -> mentoringPeriod!!.timeUnit.value
        }

    override fun checkProfileCompleted() {
        super.profileComplete = isCompleted
    }

    private val isCompleted: Boolean
        get() = !introduction.isNullOrBlank()
                && !profileImageUrl.isNullOrBlank()
                && mentoringPeriod != null
                && schedules.isNotEmpty()

    fun completeProfile(
        introduction: String?,
        profileImageUrl: String?,
        mentoringPeriod: MentoringPeriod?,
        timelines: List<Timeline>,
    ) {
        super.completeProfile(
            introduction = introduction,
            profileImageUrl = profileImageUrl,
        )
        this.mentoringPeriod = mentoringPeriod
        applySchedules(timelines)
        checkProfileCompleted()
    }

    fun updateBasicInfo(
        name: String,
        profileImageUrl: String?,
        introduction: String?,
        languages: List<Language>,
        school: String,
        major: String,
        enteredIn: Int,
    ) {
        super.updateBasicInfo(
            name = name,
            nationality = Nationality.KOREA,
            profileImageUrl = profileImageUrl,
            introduction = introduction,
            languages = languages,
        )
        this.universityProfile = universityProfile.update(
            school = school,
            major = major,
            enteredIn = enteredIn,
        )
        checkProfileCompleted()
    }

    fun updateSchedules(
        mentoringPeriod: MentoringPeriod?,
        timelines: List<Timeline>,
    ) {
        this.mentoringPeriod = mentoringPeriod
        applySchedules(timelines)
        checkProfileCompleted()
    }

    private fun applySchedules(timelines: List<Timeline>) {
        schedules.clear()
        if (timelines.isNotEmpty()) {
            schedules.addAll(timelines.map { Schedule(timeline = it, mentor = this) })
        }
    }

    fun authWithMail(schoolMail: String) {
        universityAuthentication = attemptMail(schoolMail)
    }

    fun authWithProofData(proofDataUploadUrl: String) {
        universityAuthentication = attemptProofData(proofDataUploadUrl)
    }

    fun authComplete() {
        universityAuthentication = universityAuthentication!!.complete()
    }

    fun validateReservationData(reservation: Reservation) {
        // 1. 멘토 스케줄 정보 입력 완료 확인
        if (mentoringPeriod == null || schedules.isEmpty()) {
            throw MemberException(MENTOR_NOT_FILL_IN_SCHEDULE)
        }

        // 2. 멘토링 진행 기간에 포함되는지 확인
        if (isOutOfDate(reservation)) {
            throw MemberException(CANNOT_RESERVATION)
        }

        // 3. 멘토의 각 멘토링 진행 시간(TimeUnit)과 일치하는지 확인
        if (nowAllowedTimeUnit(reservation)) {
            throw MemberException(CANNOT_RESERVATION)
        }

        // 4. 요일별 스케줄 시간대에 포함되는지 확인
        if (notAllowedSchedule(reservation)) {
            throw MemberException(CANNOT_RESERVATION)
        }
    }

    private fun isOutOfDate(reservation: Reservation): Boolean {
        return when (mentoringPeriod) {
            null -> true
            else -> mentoringPeriod!!.isDateIncluded(reservation.start.toLocalDate()).not() || mentoringPeriod!!.isDateIncluded(reservation.end.toLocalDate()).not()
        }
    }

    private fun nowAllowedTimeUnit(reservation: Reservation): Boolean {
        return when (mentoringPeriod) {
            null -> true
            else -> mentoringPeriod!!.allowedTimeUnit(reservation.start, reservation.end).not()
        }
    }

    private fun notAllowedSchedule(reservation: Reservation): Boolean {
        val timelines: List<Timeline> = schedules.map { it.timeline }
        if (timelines.isEmpty()) {
            return true
        }

        val dayOfWeek: DayOfWeek = DayOfWeek.of(
            year = reservation.start.year,
            month = reservation.start.monthValue,
            day = reservation.start.dayOfMonth,
        )

        val filteringWithStart: List<Timeline> = timelines.filter { it.dayOfWeek == dayOfWeek && it.isTimeIncluded(reservation.start.toLocalTime()) }
        val filteringWithEnd: List<Timeline> = timelines.filter { it.dayOfWeek == dayOfWeek && it.isTimeIncluded(reservation.end.toLocalTime()) }
        return filteringWithStart.isEmpty() || filteringWithEnd.isEmpty()
    }
}
