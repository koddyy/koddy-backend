package com.koddy.server.member.application.usecase;

import com.koddy.server.coffeechat.domain.model.CoffeeChat;
import com.koddy.server.coffeechat.domain.repository.query.MentorReservedScheduleQueryRepository;
import com.koddy.server.global.annotation.KoddyReadOnlyTransactional;
import com.koddy.server.global.annotation.UseCase;
import com.koddy.server.member.application.usecase.query.GetMentorReservedSchedule;
import com.koddy.server.member.application.usecase.query.response.MentorReservedSchedule;
import com.koddy.server.member.domain.model.mentor.Mentor;
import com.koddy.server.member.domain.repository.MentorRepository;

import java.util.List;

@UseCase
public class GetMentorReservedScheduleUseCase {
    private final MentorRepository mentorRepository;
    private final MentorReservedScheduleQueryRepository mentorReservedScheduleQueryRepository;

    public GetMentorReservedScheduleUseCase(
            final MentorRepository mentorRepository,
            final MentorReservedScheduleQueryRepository mentorReservedScheduleQueryRepository
    ) {
        this.mentorRepository = mentorRepository;
        this.mentorReservedScheduleQueryRepository = mentorReservedScheduleQueryRepository;
    }

    @KoddyReadOnlyTransactional
    public MentorReservedSchedule invoke(final GetMentorReservedSchedule query) {
        final Mentor mentor = mentorRepository.getByIdWithSchedules(query.mentorId());
        final List<CoffeeChat> reservedCoffeeChat = mentorReservedScheduleQueryRepository.fetchReservedCoffeeChat(
                query.mentorId(),
                query.year(),
                query.month()
        );
        return MentorReservedSchedule.of(mentor, reservedCoffeeChat);
    }
}