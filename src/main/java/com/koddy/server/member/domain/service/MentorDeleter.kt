package com.koddy.server.member.domain.service

import com.koddy.server.auth.application.adapter.TokenStore
import com.koddy.server.global.annotation.KoddyWritableTransactional
import com.koddy.server.member.domain.model.Member
import com.koddy.server.member.domain.repository.MemberRepository
import com.koddy.server.member.domain.repository.MentorScheduleRepository
import org.springframework.stereotype.Service

@Service
class MentorDeleter(
    private val memberRepository: MemberRepository,
    private val tokenStore: TokenStore,
    private val mentorScheduleRepository: MentorScheduleRepository,
) {
    @KoddyWritableTransactional
    fun execute(mentorId: Long) {
        val mentor: Member<*> = memberRepository.getById(mentorId)
        tokenStore.deleteRefreshToken(mentor.id)
        mentorScheduleRepository.deleteMentorSchedule(mentor.id)
        memberRepository.deleteMember(mentor.id)
    }
}
