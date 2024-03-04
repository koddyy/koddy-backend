package com.koddy.server.member.application.usecase

import com.koddy.server.global.annotation.KoddyReadOnlyTransactional
import com.koddy.server.global.annotation.UseCase
import com.koddy.server.member.application.usecase.query.response.MenteePublicProfile
import com.koddy.server.member.application.usecase.query.response.MentorPublicProfile
import com.koddy.server.member.domain.model.mentee.Mentee
import com.koddy.server.member.domain.model.mentor.Mentor
import com.koddy.server.member.domain.service.MemberReader

@UseCase
class GetMemberPublicProfileUseCase(
    private val memberReader: MemberReader,
) {
    @KoddyReadOnlyTransactional
    fun getMentorProfile(mentorId: Long): MentorPublicProfile {
        val mentor: Mentor = memberReader.getMentorWithLanguages(mentorId)
        return MentorPublicProfile.from(mentor)
    }

    @KoddyReadOnlyTransactional
    fun getMenteeProfile(menteeId: Long): MenteePublicProfile {
        val mentee: Mentee = memberReader.getMenteeWithLanguages(menteeId)
        return MenteePublicProfile.from(mentee)
    }
}
