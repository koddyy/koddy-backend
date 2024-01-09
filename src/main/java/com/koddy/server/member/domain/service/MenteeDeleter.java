package com.koddy.server.member.domain.service;

import com.koddy.server.auth.domain.repository.TokenRepository;
import com.koddy.server.global.annotation.KoddyWritableTransactional;
import com.koddy.server.member.domain.repository.AvailableLanguageRepository;
import com.koddy.server.member.domain.repository.MemberRepository;
import com.koddy.server.member.domain.repository.MenteeRepository;
import com.koddy.server.member.domain.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MenteeDeleter {
    private final TokenRepository tokenRepository;
    private final RoleRepository roleRepository;
    private final AvailableLanguageRepository availableLanguageRepository;
    private final MenteeRepository menteeRepository;
    private final MemberRepository memberRepository;

    @KoddyWritableTransactional
    public void execute(final long menteeId) {
        tokenRepository.deleteRefreshToken(menteeId);
        roleRepository.deleteMemberRole(menteeId);
        availableLanguageRepository.deleteMemberLanguage(menteeId);
        menteeRepository.deleteMentee(menteeId);
    }
}
