package com.koddy.server.member.application.usecase;

import com.koddy.server.coffeechat.domain.model.CoffeeChat;
import com.koddy.server.coffeechat.domain.repository.CoffeeChatRepository;
import com.koddy.server.global.annotation.KoddyReadOnlyTransactional;
import com.koddy.server.global.annotation.UseCase;
import com.koddy.server.member.application.usecase.query.GetReservedSchedule;
import com.koddy.server.member.application.usecase.query.response.ReservedSchedule;
import com.koddy.server.member.domain.model.mentor.Mentor;
import com.koddy.server.member.domain.repository.MentorRepository;
import lombok.RequiredArgsConstructor;

import java.util.List;

@UseCase
@RequiredArgsConstructor
public class GetReservedScheduleUseCase {
    private final MentorRepository mentorRepository;
    private final CoffeeChatRepository coffeeChatRepository;

    @KoddyReadOnlyTransactional
    public ReservedSchedule invoke(final GetReservedSchedule query) {
        final Mentor mentor = mentorRepository.getById(query.mentorId());
        final List<CoffeeChat> reservedCoffeeChat = coffeeChatRepository.getReservedCoffeeChat(query.mentorId(), query.year(), query.month());
        return new ReservedSchedule(
                mentor.getMentoringTimeUnit(),
                reservedCoffeeChat.stream()
                        .map(it -> ReservedSchedule.Period.of(it.getStart(), it.getEnd()))
                        .toList()
        );
    }
}
