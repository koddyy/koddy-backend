package com.koddy.server.coffeechat.application.usecase;

import com.koddy.server.coffeechat.application.usecase.command.PendingSuggestedCoffeeChatCommand;
import com.koddy.server.coffeechat.application.usecase.command.RejectSuggestedCoffeeChatCommand;
import com.koddy.server.coffeechat.domain.model.CoffeeChat;
import com.koddy.server.coffeechat.domain.model.Reservation;
import com.koddy.server.coffeechat.domain.repository.CoffeeChatRepository;
import com.koddy.server.coffeechat.domain.service.ReservationAvailabilityChecker;
import com.koddy.server.common.UnitTest;
import com.koddy.server.member.domain.model.mentee.Mentee;
import com.koddy.server.member.domain.model.mentor.Mentor;
import com.koddy.server.member.domain.repository.MentorRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.PENDING;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.REJECT;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_1;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("CoffeeChat -> HandleSuggestedCoffeeChatUseCase 테스트")
class HandleSuggestedCoffeeChatUseCaseTest extends UnitTest {
    private final CoffeeChatRepository coffeeChatRepository = mock(CoffeeChatRepository.class);
    private final MentorRepository mentorRepository = mock(MentorRepository.class);
    private final ReservationAvailabilityChecker reservationAvailabilityChecker = mock(ReservationAvailabilityChecker.class);
    private final HandleSuggestedCoffeeChatUseCase sut = new HandleSuggestedCoffeeChatUseCase(
            coffeeChatRepository,
            mentorRepository,
            reservationAvailabilityChecker
    );

    private final Mentee mentee = MENTEE_1.toDomain().apply(1L);
    private final Mentor mentor = MENTOR_1.toDomain().apply(2L);
    private final String applyReason = "신청 이유...";

    @Test
    @DisplayName("멘토의 커피챗 제안을 거절한다")
    void reject() {
        // given
        final CoffeeChat coffeeChat = CoffeeChat.suggestCoffeeChat(mentor, mentee, applyReason).apply(1L);

        final String rejectReason = "거절...";
        final RejectSuggestedCoffeeChatCommand command = new RejectSuggestedCoffeeChatCommand(coffeeChat.getId(), rejectReason);
        given(coffeeChatRepository.getAppliedCoffeeChat(command.coffeeChatId())).willReturn(coffeeChat);

        // when
        sut.reject(command);

        // then
        assertAll(
                () -> verify(coffeeChatRepository, times(1)).getAppliedCoffeeChat(command.coffeeChatId()),
                () -> verify(mentorRepository, times(0)).getByIdWithSchedules(coffeeChat.getSourceMemberId()),
                () -> verify(reservationAvailabilityChecker, times(0)).check(any(), any(), any()),
                () -> assertThat(coffeeChat.getSourceMemberId()).isEqualTo(mentor.getId()),
                () -> assertThat(coffeeChat.getTargetMemberId()).isEqualTo(mentee.getId()),
                () -> assertThat(coffeeChat.getApplyReason()).isEqualTo(applyReason),
                () -> assertThat(coffeeChat.getRejectReason()).isEqualTo(rejectReason),
                () -> assertThat(coffeeChat.getStatus()).isEqualTo(REJECT),
                () -> assertThat(coffeeChat.getStart()).isNull(),
                () -> assertThat(coffeeChat.getEnd()).isNull(),
                () -> assertThat(coffeeChat.getStrategy()).isNull()
        );
    }

    @Test
    @DisplayName("멘토의 커피챗 제안을 1차 수락한다 (멘토 최종 수락 대기)")
    void pending() {
        // given
        final CoffeeChat coffeeChat = CoffeeChat.suggestCoffeeChat(mentor, mentee, applyReason).apply(1L);

        final String question = "질문..";
        final Reservation start = new Reservation(LocalDateTime.of(2024, 2, 1, 9, 0));
        final Reservation end = new Reservation(LocalDateTime.of(2024, 2, 1, 10, 0));
        final PendingSuggestedCoffeeChatCommand command = new PendingSuggestedCoffeeChatCommand(coffeeChat.getId(), question, start, end);
        given(coffeeChatRepository.getAppliedCoffeeChat(command.coffeeChatId())).willReturn(coffeeChat);
        given(mentorRepository.getByIdWithSchedules(coffeeChat.getSourceMemberId())).willReturn(mentor);
        doNothing()
                .when(reservationAvailabilityChecker)
                .check(mentor, command.start(), command.end());

        // when
        sut.pending(command);

        // then
        assertAll(
                () -> verify(coffeeChatRepository, times(1)).getAppliedCoffeeChat(command.coffeeChatId()),
                () -> verify(mentorRepository, times(1)).getByIdWithSchedules(coffeeChat.getSourceMemberId()),
                () -> verify(reservationAvailabilityChecker, times(1)).check(mentor, command.start(), command.end()),
                () -> assertThat(coffeeChat.getSourceMemberId()).isEqualTo(mentor.getId()),
                () -> assertThat(coffeeChat.getTargetMemberId()).isEqualTo(mentee.getId()),
                () -> assertThat(coffeeChat.getApplyReason()).isEqualTo(applyReason),
                () -> assertThat(coffeeChat.getQuestion()).isEqualTo(question),
                () -> assertThat(coffeeChat.getRejectReason()).isNull(),
                () -> assertThat(coffeeChat.getStatus()).isEqualTo(PENDING),
                () -> assertThat(coffeeChat.getStart()).isEqualTo(start),
                () -> assertThat(coffeeChat.getEnd()).isEqualTo(end),
                () -> assertThat(coffeeChat.getStrategy()).isNull()
        );
    }
}
