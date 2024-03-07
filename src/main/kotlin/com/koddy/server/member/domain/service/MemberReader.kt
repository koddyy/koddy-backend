package com.koddy.server.member.domain.service

import com.koddy.server.member.domain.model.AvailableLanguage
import com.koddy.server.member.domain.model.Email
import com.koddy.server.member.domain.model.Member
import com.koddy.server.member.domain.model.mentee.Mentee
import com.koddy.server.member.domain.model.mentor.Mentor
import com.koddy.server.member.domain.repository.AvailableLanguageRepository
import com.koddy.server.member.domain.repository.MemberRepository
import com.koddy.server.member.domain.repository.MenteeRepository
import com.koddy.server.member.domain.repository.MentorRepository
import com.koddy.server.member.exception.MemberException
import com.koddy.server.member.exception.MemberExceptionCode.MEMBER_NOT_FOUND
import com.koddy.server.member.exception.MemberExceptionCode.MENTEE_NOT_FOUND
import com.koddy.server.member.exception.MemberExceptionCode.MENTOR_NOT_FOUND
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

@Service
class MemberReader(
    private val memberRepository: MemberRepository,
    private val mentorRepository: MentorRepository,
    private val menteeRepository: MenteeRepository,
    private val availableLanguageRepository: AvailableLanguageRepository,
) {
    // Member
    fun existsByid(id: Long): Boolean {
        return memberRepository.existsById(id)
    }

    fun existsByPlatformSocialId(socialId: String): Boolean {
        return memberRepository.existsByPlatformSocialId(socialId)
    }

    fun findByPlatformSocialId(socialId: String): Member<*>? {
        return memberRepository.findByPlatformSocialId(socialId)
    }

    fun findByPlatformEmail(email: Email): Member<*>? {
        return memberRepository.findByPlatformEmail(email)
    }

    fun getMember(mentorId: Long): Member<*> {
        return memberRepository.findByIdOrNull(mentorId)
            ?: throw MemberException(MEMBER_NOT_FOUND)
    }

    // Mentor
    fun getMentor(mentorId: Long): Mentor {
        return mentorRepository.findByIdOrNull(mentorId)
            ?: throw MemberException(MENTOR_NOT_FOUND)
    }

    fun getMentorWithNative(mentorId: Long): Mentor {
        return mentorRepository.findByIdWithNative(mentorId)
            ?: throw MemberException(MENTOR_NOT_FOUND)
    }

    fun getMentorWithSchedules(mentorId: Long): Mentor {
        return mentorRepository.findByIdWithSchedules(mentorId)
            ?: throw MemberException(MENTOR_NOT_FOUND)
    }

    fun getMentorWithLanguages(mentorId: Long): Mentor {
        return mentorRepository.findByIdWithLanguages(mentorId)
            ?: throw MemberException(MENTOR_NOT_FOUND)
    }

    // Mentee
    fun getMentee(menteeId: Long): Mentee {
        return menteeRepository.findByIdOrNull(menteeId)
            ?: throw MemberException(MENTEE_NOT_FOUND)
    }

    fun getMenteeWithNative(menteeId: Long): Mentee {
        return menteeRepository.findByIdWithNative(menteeId)
            ?: throw MemberException(MENTEE_NOT_FOUND)
    }

    fun getMenteeWithLanguages(menteeId: Long): Mentee {
        return menteeRepository.findByIdWithLanguages(menteeId)
            ?: throw MemberException(MENTEE_NOT_FOUND)
    }

    // AvailableLanguage
    fun getMemberAvailableLanguagesWithNative(memberId: Long): List<AvailableLanguage> {
        return availableLanguageRepository.findByMemberIdWithNative(memberId)
    }
}
