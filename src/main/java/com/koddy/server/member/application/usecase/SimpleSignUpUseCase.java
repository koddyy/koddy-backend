package com.koddy.server.member.application.usecase;

import com.koddy.server.global.annotation.UseCase;
import com.koddy.server.global.encrypt.Encryptor;
import com.koddy.server.member.application.usecase.command.SimpleSignUpCommand;
import com.koddy.server.member.domain.model.Password;
import com.koddy.server.member.domain.model.mentee.Mentee;
import com.koddy.server.member.domain.model.mentor.Mentor;
import com.koddy.server.member.domain.repository.MemberRepository;
import lombok.RequiredArgsConstructor;

import static com.koddy.server.member.domain.model.MemberType.MENTOR;

@UseCase
@RequiredArgsConstructor
public class SimpleSignUpUseCase {
    private final Encryptor encryptor;
    private final MemberRepository memberRepository;

    public Long invoke(final SimpleSignUpCommand command) {
        final Password password = Password.encrypt(command.password(), encryptor);

        if (command.type() == MENTOR) {
            return memberRepository.save(new Mentor(command.email(), password)).getId();
        }
        return memberRepository.save(new Mentee(command.email(), password)).getId();
    }
}
