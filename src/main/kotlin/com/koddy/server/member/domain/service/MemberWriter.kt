package com.koddy.server.member.domain.service

import com.koddy.server.member.domain.model.mentee.Mentee
import com.koddy.server.member.domain.model.mentor.Mentor
import com.koddy.server.member.domain.repository.MemberRepository
import com.koddy.server.member.domain.repository.MenteeRepository
import com.koddy.server.member.domain.repository.MentorRepository
import com.koddy.server.member.domain.repository.MentorScheduleRepository
import org.springframework.stereotype.Service

@Service
class MemberWriter(
    private val memberRepository: MemberRepository,
    private val mentorRepository: MentorRepository,
    private val menteeRepository: MenteeRepository,
    private val mentorScheduleRepository: MentorScheduleRepository,
) {
    fun saveMentor(mentor: Mentor): Mentor {
        return mentorRepository.save(mentor)
    }

    fun saveMentee(mentee: Mentee): Mentee {
        return menteeRepository.save(mentee)
    }

    fun deleteMentorSchedule(mentorId: Long) {
        mentorScheduleRepository.deleteMentorSchedule(mentorId)
    }

    fun deleteMember(id: Long) {
        memberRepository.deleteMember(id)
    }
}
