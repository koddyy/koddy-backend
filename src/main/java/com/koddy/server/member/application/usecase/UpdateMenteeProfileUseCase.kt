package com.koddy.server.member.application.usecase

import com.koddy.server.global.annotation.KoddyWritableTransactional
import com.koddy.server.global.annotation.UseCase
import com.koddy.server.member.application.usecase.command.UpdateMenteeBasicInfoCommand
import com.koddy.server.member.domain.model.mentee.Mentee
import com.koddy.server.member.domain.repository.MenteeRepository

@UseCase
class UpdateMenteeProfileUseCase(
    private val menteeRepository: MenteeRepository,
) {
    @KoddyWritableTransactional
    fun updateBasicInfo(command: UpdateMenteeBasicInfoCommand) {
        val mentee: Mentee = menteeRepository.getById(command.menteeId)
        mentee.updateBasicInfo(
            command.name,
            command.nationality,
            command.profileImageUrl,
            command.introduction,
            command.languages,
            command.interestSchool,
            command.interestMajor,
        )
    }
}
