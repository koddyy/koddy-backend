package com.koddy.server.member.domain.service;

import com.koddy.server.auth.application.adapter.TokenStore;
import com.koddy.server.global.annotation.KoddyWritableTransactional;
import com.koddy.server.member.domain.model.Member;
import com.koddy.server.member.domain.repository.AvailableLanguageRepository;
import com.koddy.server.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MenteeDeleter {
    private final MemberRepository memberRepository;
    private final TokenStore tokenStore;
    private final AvailableLanguageRepository availableLanguageRepository;

    @KoddyWritableTransactional
    public void execute(final long menteeId) {
        final Member<?> mentee = memberRepository.getById(menteeId);
        tokenStore.deleteRefreshToken(mentee.getId());
        availableLanguageRepository.deleteMemberLanguage(mentee.getId());
        memberRepository.deleteMember(mentee.getId());
    }
}
