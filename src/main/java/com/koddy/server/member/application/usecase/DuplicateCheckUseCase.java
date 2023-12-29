package com.koddy.server.member.application.usecase;

import com.koddy.server.global.annotation.UseCase;
import com.koddy.server.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class DuplicateCheckUseCase {
    private final MemberRepository memberRepository;

    public boolean checkEmail(final String email) {
        // TODO
        return true;
    }
}
