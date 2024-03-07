package com.koddy.server.member.application.usecase

import com.koddy.server.global.annotation.KoddyReadOnlyTransactional
import com.koddy.server.global.annotation.UseCase
import com.koddy.server.member.application.usecase.query.response.MenteePrivateProfile
import com.koddy.server.member.application.usecase.query.response.MentorPrivateProfile
import com.koddy.server.member.domain.model.mentee.Mentee
import com.koddy.server.member.domain.model.mentor.Mentor
import com.koddy.server.member.domain.service.MemberReader

@UseCase
class GetMemberPrivateProfileUseCase(
    private val memberReader: MemberReader,
) {
    @KoddyReadOnlyTransactional
    fun getMentorProfile(mentorId: Long): MentorPrivateProfile {
        val mentor: Mentor = memberReader.getMentorWithLanguages(mentorId)
        return MentorPrivateProfile.from(mentor)
    }

    @KoddyReadOnlyTransactional
    fun getMenteeProfile(menteeId: Long): MenteePrivateProfile {
        val mentee: Mentee = memberReader.getMenteeWithLanguages(menteeId)
        return MenteePrivateProfile.from(mentee)
    }
}
