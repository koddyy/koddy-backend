package com.koddy.server.member.domain.service;

import com.koddy.server.auth.application.adapter.TokenStore;
import com.koddy.server.global.annotation.KoddyWritableTransactional;
import com.koddy.server.member.domain.model.Member;
import com.koddy.server.member.domain.repository.MemberRepository;
import com.koddy.server.member.domain.repository.MentorScheduleRepository;
import org.springframework.stereotype.Service;

@Service
public class MentorDeleter {
    private final MemberRepository memberRepository;
    private final TokenStore tokenStore;
    private final MentorScheduleRepository mentorScheduleRepository;

    public MentorDeleter(
            final MemberRepository memberRepository,
            final TokenStore tokenStore,
            final MentorScheduleRepository mentorScheduleRepository
    ) {
        this.memberRepository = memberRepository;
        this.tokenStore = tokenStore;
        this.mentorScheduleRepository = mentorScheduleRepository;
    }

    @KoddyWritableTransactional
    public void execute(final long mentorId) {
        final Member<?> mentor = memberRepository.getById(mentorId);
        tokenStore.deleteRefreshToken(mentor.getId());
        mentorScheduleRepository.deleteMentorSchedule(mentor.getId());
        memberRepository.deleteMember(mentor.getId());
    }
}
