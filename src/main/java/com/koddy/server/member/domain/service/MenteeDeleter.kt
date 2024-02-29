package com.koddy.server.member.domain.service

import com.koddy.server.auth.application.adapter.TokenStore
import com.koddy.server.global.annotation.KoddyWritableTransactional
import com.koddy.server.member.domain.model.Member
import com.koddy.server.member.domain.repository.MemberRepository
import org.springframework.stereotype.Service

@Service
class MenteeDeleter(
    private val memberRepository: MemberRepository,
    private val tokenStore: TokenStore,
) {
    @KoddyWritableTransactional
    fun execute(menteeId: Long) {
        val mentee: Member<*> = memberRepository.getById(menteeId)
        tokenStore.deleteRefreshToken(mentee.id)
        memberRepository.deleteMember(mentee.id)
    }
}
