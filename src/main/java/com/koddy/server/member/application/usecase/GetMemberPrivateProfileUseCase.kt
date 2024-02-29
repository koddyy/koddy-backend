package com.koddy.server.member.application.usecase

import com.koddy.server.global.annotation.KoddyReadOnlyTransactional
import com.koddy.server.global.annotation.UseCase
import com.koddy.server.member.application.usecase.query.response.MenteePrivateProfile
import com.koddy.server.member.application.usecase.query.response.MentorPrivateProfile
import com.koddy.server.member.domain.model.mentee.Mentee
import com.koddy.server.member.domain.model.mentor.Mentor
import com.koddy.server.member.domain.repository.MenteeRepository
import com.koddy.server.member.domain.repository.MentorRepository

@UseCase
class GetMemberPrivateProfileUseCase(
    private val mentorRepository: MentorRepository,
    private val menteeRepository: MenteeRepository,
) {
    @KoddyReadOnlyTransactional
    fun getMentorProfile(mentorId: Long): MentorPrivateProfile {
        val mentor: Mentor = mentorRepository.getProfile(mentorId)
        return MentorPrivateProfile.from(mentor)
    }

    @KoddyReadOnlyTransactional
    fun getMenteeProfile(menteeId: Long): MenteePrivateProfile {
        val mentee: Mentee = menteeRepository.getProfile(menteeId)
        return MenteePrivateProfile.from(mentee)
    }
}
