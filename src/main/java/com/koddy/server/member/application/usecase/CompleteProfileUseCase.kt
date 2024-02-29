package com.koddy.server.member.application.usecase

import com.koddy.server.global.annotation.KoddyWritableTransactional
import com.koddy.server.global.annotation.UseCase
import com.koddy.server.member.application.usecase.command.CompleteMenteeProfileCommand
import com.koddy.server.member.application.usecase.command.CompleteMentorProfileCommand
import com.koddy.server.member.domain.model.mentee.Mentee
import com.koddy.server.member.domain.model.mentor.Mentor
import com.koddy.server.member.domain.repository.MenteeRepository
import com.koddy.server.member.domain.repository.MentorRepository

@UseCase
class CompleteProfileUseCase(
    private val mentorRepository: MentorRepository,
    private val menteeRepository: MenteeRepository,
) {
    @KoddyWritableTransactional
    fun completeMentor(command: CompleteMentorProfileCommand) {
        val mentor: Mentor = mentorRepository.getById(command.mentorId)
        mentor.completeInfo(
            command.introduction,
            command.profileImageUrl,
            command.mentoringPeriod,
            command.timelines,
        )
    }

    @KoddyWritableTransactional
    fun completeMentee(command: CompleteMenteeProfileCommand) {
        val mentee: Mentee = menteeRepository.getById(command.menteeId)
        mentee.completeInfo(
            command.introduction,
            command.profileImageUrl,
        )
    }
}
