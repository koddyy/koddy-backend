package com.koddy.server.member.domain.service

import com.koddy.server.auth.application.adapter.TokenStore
import com.koddy.server.global.annotation.KoddyWritableTransactional
import com.koddy.server.member.domain.model.Member
import org.springframework.stereotype.Service

@Service
class MentorDeleter(
    private val memberReader: MemberReader,
    private val memberWriter: MemberWriter,
    private val tokenStore: TokenStore,
) {
    @KoddyWritableTransactional
    fun execute(mentorId: Long) {
        val mentor: Member<*> = memberReader.getMentor(mentorId)
        tokenStore.deleteRefreshToken(mentor.id)
        memberWriter.deleteMentorSchedule(mentor.id)
        memberWriter.deleteMember(mentor.id)
    }
}
