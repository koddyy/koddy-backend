package com.koddy.server.coffeechat.application.usecase;

import com.koddy.server.coffeechat.application.usecase.command.PendingSuggestedCoffeeChatCommand;
import com.koddy.server.coffeechat.application.usecase.command.RejectSuggestedCoffeeChatCommand;
import com.koddy.server.coffeechat.domain.model.CoffeeChat;
import com.koddy.server.coffeechat.domain.model.Reservation;
import com.koddy.server.coffeechat.domain.repository.CoffeeChatRepository;
import com.koddy.server.coffeechat.domain.service.ReservationAvailabilityChecker;
import com.koddy.server.common.UnitTest;
import com.koddy.server.common.fixture.CoffeeChatFixture.MentorFlow;
import com.koddy.server.member.domain.model.mentee.Mentee;
import com.koddy.server.member.domain.model.mentor.Mentor;
import com.koddy.server.member.domain.repository.MentorRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTEE_PENDING;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTEE_REJECT;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_1;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("CoffeeChat -> HandleMentorSuggestedCoffeeChatUseCase 테스트")
class HandleMentorSuggestedCoffeeChatUseCaseTest extends UnitTest {
    private final CoffeeChatRepository coffeeChatRepository = mock(CoffeeChatRepository.class);
    private final MentorRepository mentorRepository = mock(MentorRepository.class);
    private final ReservationAvailabilityChecker reservationAvailabilityChecker = mock(ReservationAvailabilityChecker.class);
    private final HandleMentorSuggestedCoffeeChatUseCase sut = new HandleMentorSuggestedCoffeeChatUseCase(
            coffeeChatRepository,
            mentorRepository,
            reservationAvailabilityChecker
    );

    private final Mentee mentee = MENTEE_1.toDomain().apply(1L);
    private final Mentor mentor = MENTOR_1.toDomain().apply(2L);

    @Test
    @DisplayName("멘토의 커피챗 제안을 거절한다")
    void reject() {
        // given
        final CoffeeChat coffeeChat = MentorFlow.suggest(mentor, mentee).apply(1L);

        final RejectSuggestedCoffeeChatCommand command = new RejectSuggestedCoffeeChatCommand(mentee.getId(), coffeeChat.getId(), "거절...");
        given(coffeeChatRepository.getByIdAndMenteeId(command.coffeeChatId(), command.menteeId())).willReturn(coffeeChat);

        // when
        sut.reject(command);

        // then
        assertAll(
                () -> verify(coffeeChatRepository, times(1)).getByIdAndMenteeId(command.coffeeChatId(), command.menteeId()),
                () -> assertThat(coffeeChat.getMentorId()).isEqualTo(mentor.getId()),
                () -> assertThat(coffeeChat.getMenteeId()).isEqualTo(mentee.getId()),
                () -> assertThat(coffeeChat.getStatus()).isEqualTo(MENTEE_REJECT),
                () -> assertThat(coffeeChat.getApplyReason()).isNull(),
                () -> assertThat(coffeeChat.getSuggestReason()).isNotNull(),
                () -> assertThat(coffeeChat.getRejectReason()).isEqualTo(command.rejectReason()),
                () -> assertThat(coffeeChat.getReservation()).isNull(),
                () -> assertThat(coffeeChat.getStrategy()).isNull()
        );
    }

    @Test
    @DisplayName("멘토의 커피챗 제안을 1차 수락한다 (멘토 최종 수락 대기)")
    void pending() {
        // given
        final CoffeeChat coffeeChat = MentorFlow.suggest(mentor, mentee).apply(1L);

        final LocalDateTime start = LocalDateTime.of(2024, 2, 1, 10, 0);
        final PendingSuggestedCoffeeChatCommand command = new PendingSuggestedCoffeeChatCommand(
                mentee.getId(),
                coffeeChat.getId(),
                "질문..",
                Reservation.of(start, start.plusMinutes(30))
        );
        given(coffeeChatRepository.getByIdAndMenteeId(command.coffeeChatId(), command.menteeId())).willReturn(coffeeChat);
        given(mentorRepository.getByIdWithSchedules(coffeeChat.getMentorId())).willReturn(mentor);
        doNothing()
                .when(reservationAvailabilityChecker)
                .check(mentor, command.reservation());

        // when
        sut.pending(command);

        // then
        assertAll(
                () -> verify(coffeeChatRepository, times(1)).getByIdAndMenteeId(command.coffeeChatId(), command.menteeId()),
                () -> verify(mentorRepository, times(1)).getByIdWithSchedules(coffeeChat.getMentorId()),
                () -> verify(reservationAvailabilityChecker, times(1)).check(mentor, command.reservation()),
                () -> assertThat(coffeeChat.getMentorId()).isEqualTo(mentor.getId()),
                () -> assertThat(coffeeChat.getMenteeId()).isEqualTo(mentee.getId()),
                () -> assertThat(coffeeChat.getStatus()).isEqualTo(MENTEE_PENDING),
                () -> assertThat(coffeeChat.getApplyReason()).isNull(),
                () -> assertThat(coffeeChat.getSuggestReason()).isNotNull(),
                () -> assertThat(coffeeChat.getQuestion()).isEqualTo(command.question()),
                () -> assertThat(coffeeChat.getRejectReason()).isNull(),
                () -> assertThat(coffeeChat.getReservation().getStart()).isEqualTo(start),
                () -> assertThat(coffeeChat.getReservation().getEnd()).isEqualTo(start.plusMinutes(30)),
                () -> assertThat(coffeeChat.getStrategy()).isNull()
        );
    }
}
