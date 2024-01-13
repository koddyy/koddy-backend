package com.koddy.server.coffeechat.application.usecase;

import com.koddy.server.coffeechat.application.usecase.command.MenteeApplyCoffeeChatCommand;
import com.koddy.server.coffeechat.application.usecase.command.MentorSuggestCoffeeChatCommand;
import com.koddy.server.coffeechat.domain.model.CoffeeChat;
import com.koddy.server.coffeechat.domain.repository.CoffeeChatRepository;
import com.koddy.server.global.annotation.UseCase;
import com.koddy.server.member.domain.model.mentee.Mentee;
import com.koddy.server.member.domain.model.mentor.Mentor;
import com.koddy.server.member.domain.repository.MenteeRepository;
import com.koddy.server.member.domain.repository.MentorRepository;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class ApplyCoffeeChatUseCase {
    private final MentorRepository mentorRepository;
    private final MenteeRepository menteeRepository;
    private final CoffeeChatRepository coffeeChatRepository;

    public Long applyMentorToMentee(final MentorSuggestCoffeeChatCommand command) {
        final Mentor applier = mentorRepository.getById(command.mentorId());
        final Mentee target = menteeRepository.getById(command.menteeId());

        return coffeeChatRepository.save(CoffeeChat.applyMentorToMentee(
                applier,
                target,
                command.applyReason()
        )).getId();
    }

    public Long applyMenteeToMentor(final MenteeApplyCoffeeChatCommand command) {
        final Mentee applier = menteeRepository.getById(command.menteeId());
        final Mentor target = mentorRepository.getById(command.mentorId());

        return coffeeChatRepository.save(CoffeeChat.applyMenteeToMentor(
                applier,
                target,
                command.applyReason(),
                command.start(),
                command.end()
        )).getId();
    }
}
