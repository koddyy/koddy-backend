package com.koddy.server.member.application.usecase

import com.koddy.server.global.annotation.KoddyWritableTransactional
import com.koddy.server.global.annotation.UseCase
import com.koddy.server.member.application.usecase.command.CompleteMenteeProfileCommand
import com.koddy.server.member.application.usecase.command.CompleteMentorProfileCommand
import com.koddy.server.member.domain.model.mentee.Mentee
import com.koddy.server.member.domain.model.mentor.Mentor
import com.koddy.server.member.domain.service.MemberReader

@UseCase
class CompleteProfileUseCase(
    private val memberReader: MemberReader,
) {
    @KoddyWritableTransactional
    fun completeMentor(command: CompleteMentorProfileCommand) {
        val mentor: Mentor = memberReader.getMentor(command.mentorId)
        mentor.completeProfile(
            command.introduction,
            command.profileImageUrl,
            command.mentoringPeriod,
            command.timelines,
        )
    }

    @KoddyWritableTransactional
    fun completeMentee(command: CompleteMenteeProfileCommand) {
        val mentee: Mentee = memberReader.getMentee(command.menteeId)
        mentee.completeProfile(
            command.introduction,
            command.profileImageUrl,
        )
    }
}
