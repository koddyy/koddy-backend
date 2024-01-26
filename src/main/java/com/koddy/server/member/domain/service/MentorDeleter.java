package com.koddy.server.member.domain.service;

import com.koddy.server.auth.application.adapter.TokenStore;
import com.koddy.server.global.annotation.KoddyWritableTransactional;
import com.koddy.server.member.domain.model.Member;
import com.koddy.server.member.domain.repository.AvailableLanguageRepository;
import com.koddy.server.member.domain.repository.MemberRepository;
import com.koddy.server.member.domain.repository.MentorScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MentorDeleter {
    private final MemberRepository memberRepository;
    private final TokenStore tokenStore;
    private final AvailableLanguageRepository availableLanguageRepository;
    private final MentorScheduleRepository mentorScheduleRepository;

    @KoddyWritableTransactional
    public void execute(final long mentorId) {
        final Member<?> mentor = memberRepository.getById(mentorId);
        tokenStore.deleteRefreshToken(mentor.getId());
        availableLanguageRepository.deleteMemberLanguage(mentor.getId());
        mentorScheduleRepository.deleteMentorSchedule(mentor.getId());
        memberRepository.deleteMember(mentor.getId());
    }
}
