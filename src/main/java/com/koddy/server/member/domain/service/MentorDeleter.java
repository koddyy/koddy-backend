package com.koddy.server.member.domain.service;

import com.koddy.server.auth.domain.repository.TokenRepository;
import com.koddy.server.global.annotation.KoddyWritableTransactional;
import com.koddy.server.member.domain.repository.AvailableLanguageRepository;
import com.koddy.server.member.domain.repository.MemberRepository;
import com.koddy.server.member.domain.repository.MentorRepository;
import com.koddy.server.member.domain.repository.MentorScheduleRepository;
import com.koddy.server.member.domain.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MentorDeleter {
    private final TokenRepository tokenRepository;
    private final RoleRepository roleRepository;
    private final AvailableLanguageRepository availableLanguageRepository;
    private final MentorScheduleRepository mentorScheduleRepository;
    private final MentorRepository mentorRepository;
    private final MemberRepository memberRepository;

    @KoddyWritableTransactional
    public void execute(final long mentorId) {
        tokenRepository.deleteRefreshToken(mentorId);
        roleRepository.deleteMemberRole(mentorId);
        availableLanguageRepository.deleteMemberLanguage(mentorId);
        mentorScheduleRepository.deleteMentorSchedule(mentorId);
        mentorRepository.deleteMentor(mentorId);
    }
}
