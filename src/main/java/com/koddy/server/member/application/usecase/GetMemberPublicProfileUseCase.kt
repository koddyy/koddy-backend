package com.koddy.server.member.application.usecase

import com.koddy.server.global.annotation.KoddyReadOnlyTransactional
import com.koddy.server.global.annotation.UseCase
import com.koddy.server.member.application.usecase.query.response.MenteePublicProfile
import com.koddy.server.member.application.usecase.query.response.MentorPublicProfile
import com.koddy.server.member.domain.model.mentee.Mentee
import com.koddy.server.member.domain.model.mentor.Mentor
import com.koddy.server.member.domain.repository.MenteeRepository
import com.koddy.server.member.domain.repository.MentorRepository

@UseCase
class GetMemberPublicProfileUseCase(
    private val mentorRepository: MentorRepository,
    private val menteeRepository: MenteeRepository,
) {
    @KoddyReadOnlyTransactional
    fun getMentorProfile(mentorId: Long): MentorPublicProfile {
        val mentor: Mentor = mentorRepository.getProfile(mentorId)
        return MentorPublicProfile.from(mentor)
    }

    @KoddyReadOnlyTransactional
    fun getMenteeProfile(menteeId: Long): MenteePublicProfile {
        val mentee: Mentee = menteeRepository.getProfile(menteeId)
        return MenteePublicProfile.from(mentee)
    }
}
