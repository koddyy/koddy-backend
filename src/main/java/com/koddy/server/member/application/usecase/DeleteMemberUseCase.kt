package com.koddy.server.member.application.usecase

import com.koddy.server.global.annotation.KoddyWritableTransactional
import com.koddy.server.global.annotation.UseCase
import com.koddy.server.member.domain.model.Member
import com.koddy.server.member.domain.model.mentee.Mentee
import com.koddy.server.member.domain.model.mentor.Mentor
import com.koddy.server.member.domain.repository.MemberRepository
import com.koddy.server.member.domain.service.MenteeDeleter
import com.koddy.server.member.domain.service.MentorDeleter

@UseCase
class DeleteMemberUseCase(
    private val memberRepository: MemberRepository,
    private val mentorDeleter: MentorDeleter,
    private val menteeDeleter: MenteeDeleter,
) {
    @KoddyWritableTransactional
    fun invoke(memberId: Long) {
        when (val member: Member<*> = memberRepository.getById(memberId)) {
            is Mentor -> mentorDeleter.execute(member.id)
            is Mentee -> menteeDeleter.execute(member.id)
        }
    }
}
