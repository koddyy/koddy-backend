package com.koddy.server.member.application.usecase

import com.koddy.server.global.annotation.KoddyWritableTransactional
import com.koddy.server.global.annotation.UseCase
import com.koddy.server.member.application.usecase.command.UpdateMentorBasicInfoCommand
import com.koddy.server.member.application.usecase.command.UpdateMentorScheduleCommand
import com.koddy.server.member.domain.model.mentor.Mentor
import com.koddy.server.member.domain.repository.MentorRepository

@UseCase
class UpdateMentorProfileUseCase(
    private val mentorRepository: MentorRepository,
) {
    @KoddyWritableTransactional
    fun updateBasicInfo(command: UpdateMentorBasicInfoCommand) {
        val mentor: Mentor = mentorRepository.getById(command.mentorId)
        mentor.updateBasicInfo(
            command.name,
            command.profileImageUrl,
            command.introduction,
            command.languages,
            command.school,
            command.major,
            command.enteredIn,
        )
    }

    @KoddyWritableTransactional
    fun updateSchedule(command: UpdateMentorScheduleCommand) {
        val mentor: Mentor = mentorRepository.getById(command.mentorId)
        mentor.updateSchedules(
            command.mentoringPeriod,
            command.timelines,
        )
    }
}
