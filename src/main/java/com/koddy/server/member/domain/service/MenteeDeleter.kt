package com.koddy.server.member.domain.service

import com.koddy.server.auth.application.adapter.TokenStore
import com.koddy.server.global.annotation.KoddyWritableTransactional
import com.koddy.server.member.domain.model.Member
import org.springframework.stereotype.Service

@Service
class MenteeDeleter(
    private val memberReader: MemberReader,
    private val memberWriter: MemberWriter,
    private val tokenStore: TokenStore,
) {
    @KoddyWritableTransactional
    fun execute(menteeId: Long) {
        val mentee: Member<*> = memberReader.getMentee(menteeId)
        tokenStore.deleteRefreshToken(mentee.id)
        memberWriter.deleteMember(mentee.id)
    }
}
