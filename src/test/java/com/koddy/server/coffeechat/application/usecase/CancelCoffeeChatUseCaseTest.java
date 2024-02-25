package com.koddy.server.coffeechat.application.usecase;

import com.koddy.server.auth.domain.model.Authenticated;
import com.koddy.server.coffeechat.application.usecase.command.CancelCoffeeChatCommand;
import com.koddy.server.coffeechat.domain.event.MenteeNotification;
import com.koddy.server.coffeechat.domain.event.MentorNotification;
import com.koddy.server.coffeechat.domain.model.CoffeeChat;
import com.koddy.server.coffeechat.domain.repository.CoffeeChatRepository;
import com.koddy.server.coffeechat.domain.service.CoffeeChatNotificationEventPublisher;
import com.koddy.server.common.UnitTest;
import com.koddy.server.common.fixture.CoffeeChatFixture.MenteeFlow;
import com.koddy.server.common.fixture.CoffeeChatFixture.MentorFlow;
import com.koddy.server.member.domain.model.mentee.Mentee;
import com.koddy.server.member.domain.model.mentor.Mentor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;

import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.CANCEL_FROM_MENTEE_FLOW;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.CANCEL_FROM_MENTOR_FLOW;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_1;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("CoffeeChat -> CancelCoffeeChatUseCase 테스트")
class CancelCoffeeChatUseCaseTest extends UnitTest {
    private final CoffeeChatRepository coffeeChatRepository = mock(CoffeeChatRepository.class);
    private final ApplicationEventPublisher applicationEventPublisher = mock(ApplicationEventPublisher.class);
    private final CoffeeChatNotificationEventPublisher eventPublisher = new CoffeeChatNotificationEventPublisher(applicationEventPublisher);
    private final CancelCoffeeChatUseCase sut = new CancelCoffeeChatUseCase(
            coffeeChatRepository,
            eventPublisher
    );

    private final Mentor mentor = MENTOR_1.toDomain().apply(1L);
    private final Mentee mentee = MENTEE_1.toDomain().apply(2L);

    @Nested
    @DisplayName("멘토가 커피챗 취소")
    class CancelByMentor {
        private final Authenticated authenticated = new Authenticated(mentor.getId(), mentor.getAuthority());

        @Test
        @DisplayName("멘토가 MenteeFlow로 진행되는 커피챗을 취소한다")
        void cancelMenteeFlowCoffeeChat() {
            /// given
            final LocalDateTime start = LocalDateTime.of(2024, 2, 1, 9, 0);
            final CoffeeChat coffeeChat = MenteeFlow.apply(start, start.plusMinutes(30), mentee, mentor).apply(1L);

            final CancelCoffeeChatCommand command = new CancelCoffeeChatCommand(authenticated, coffeeChat.getId(), "취소..");
            given(coffeeChatRepository.getByIdAndMentorId(command.coffeeChatId(), command.authenticated().id)).willReturn(coffeeChat);

            // when
            sut.invoke(command);

            // then
            assertAll(
                    () -> verify(coffeeChatRepository, times(1)).getByIdAndMentorId(command.coffeeChatId(), command.authenticated().id),
                    () -> verify(applicationEventPublisher, times(1)).publishEvent(any(MenteeNotification.MentorCanceledFromMenteeFlowEvent.class)),
                    () -> assertThat(coffeeChat.getMentorId()).isEqualTo(mentor.getId()),
                    () -> assertThat(coffeeChat.getMenteeId()).isEqualTo(mentee.getId()),
                    () -> assertThat(coffeeChat.getStatus()).isEqualTo(CANCEL_FROM_MENTEE_FLOW),
                    () -> assertThat(coffeeChat.getReason().getApplyReason()).isNotNull(),
                    () -> assertThat(coffeeChat.getReason().getSuggestReason()).isNull(),
                    () -> assertThat(coffeeChat.getReason().getCancelReason()).isNotNull(),
                    () -> assertThat(coffeeChat.getReason().getRejectReason()).isNull(),
                    () -> assertThat(coffeeChat.getCancelBy()).isEqualTo(mentor.getId()),
                    () -> assertThat(coffeeChat.getQuestion()).isNull(),
                    () -> assertThat(coffeeChat.getReservation().getStart()).isEqualTo(start),
                    () -> assertThat(coffeeChat.getReservation().getEnd()).isEqualTo(start.plusMinutes(30)),
                    () -> assertThat(coffeeChat.getStrategy()).isNull()
            );
        }

        @Test
        @DisplayName("멘토가 MentorFlow로 진행되는 커피챗을 취소한다")
        void cancelMentorFlowCoffeeChat() {
            /// given
            final CoffeeChat coffeeChat = MentorFlow.suggest(mentor, mentee).apply(1L);

            final CancelCoffeeChatCommand command = new CancelCoffeeChatCommand(authenticated, coffeeChat.getId(), "취소..");
            given(coffeeChatRepository.getByIdAndMentorId(command.coffeeChatId(), command.authenticated().id)).willReturn(coffeeChat);

            // when
            sut.invoke(command);

            // then
            assertAll(
                    () -> verify(coffeeChatRepository, times(1)).getByIdAndMentorId(command.coffeeChatId(), command.authenticated().id),
                    () -> verify(applicationEventPublisher, times(1)).publishEvent(any(MenteeNotification.MentorCanceledFromMentorFlowEvent.class)),
                    () -> assertThat(coffeeChat.getMentorId()).isEqualTo(mentor.getId()),
                    () -> assertThat(coffeeChat.getMenteeId()).isEqualTo(mentee.getId()),
                    () -> assertThat(coffeeChat.getStatus()).isEqualTo(CANCEL_FROM_MENTOR_FLOW),
                    () -> assertThat(coffeeChat.getReason().getApplyReason()).isNull(),
                    () -> assertThat(coffeeChat.getReason().getSuggestReason()).isNotNull(),
                    () -> assertThat(coffeeChat.getReason().getCancelReason()).isNotNull(),
                    () -> assertThat(coffeeChat.getReason().getRejectReason()).isNull(),
                    () -> assertThat(coffeeChat.getCancelBy()).isEqualTo(mentor.getId()),
                    () -> assertThat(coffeeChat.getQuestion()).isNull(),
                    () -> assertThat(coffeeChat.getReservation()).isNull(),
                    () -> assertThat(coffeeChat.getStrategy()).isNull()
            );
        }
    }

    @Nested
    @DisplayName("멘티가 커피챗 취소")
    class CancelByMentee {
        private final Authenticated authenticated = new Authenticated(mentee.getId(), mentee.getAuthority());

        @Test
        @DisplayName("멘티가 MenteeFlow로 진행되는 커피챗을 취소한다")
        void cancelMenteeFlowCoffeeChat() {
            // given
            final LocalDateTime start = LocalDateTime.of(2024, 2, 1, 9, 0);
            final CoffeeChat coffeeChat = MenteeFlow.apply(start, start.plusMinutes(30), mentee, mentor).apply(1L);

            final CancelCoffeeChatCommand command = new CancelCoffeeChatCommand(authenticated, coffeeChat.getId(), "취소..");
            given(coffeeChatRepository.getByIdAndMenteeId(command.coffeeChatId(), command.authenticated().id)).willReturn(coffeeChat);

            // when
            sut.invoke(command);

            // then
            assertAll(
                    () -> verify(coffeeChatRepository, times(1)).getByIdAndMenteeId(command.coffeeChatId(), command.authenticated().id),
                    () -> verify(applicationEventPublisher, times(1)).publishEvent(any(MentorNotification.MenteeCanceledFromMenteeFlowEvent.class)),
                    () -> assertThat(coffeeChat.getMentorId()).isEqualTo(mentor.getId()),
                    () -> assertThat(coffeeChat.getMenteeId()).isEqualTo(mentee.getId()),
                    () -> assertThat(coffeeChat.getStatus()).isEqualTo(CANCEL_FROM_MENTEE_FLOW),
                    () -> assertThat(coffeeChat.getReason().getApplyReason()).isNotNull(),
                    () -> assertThat(coffeeChat.getReason().getSuggestReason()).isNull(),
                    () -> assertThat(coffeeChat.getReason().getCancelReason()).isNotNull(),
                    () -> assertThat(coffeeChat.getReason().getRejectReason()).isNull(),
                    () -> assertThat(coffeeChat.getCancelBy()).isEqualTo(mentee.getId()),
                    () -> assertThat(coffeeChat.getQuestion()).isNull(),
                    () -> assertThat(coffeeChat.getReservation().getStart()).isEqualTo(start),
                    () -> assertThat(coffeeChat.getReservation().getEnd()).isEqualTo(start.plusMinutes(30)),
                    () -> assertThat(coffeeChat.getStrategy()).isNull()
            );
        }

        @Test
        @DisplayName("멘티가 MentorFlow로 진행되는 커피챗을 취소한다")
        void cancelMentorFlowCoffeeChat() {
            // given
            final CoffeeChat coffeeChat = MentorFlow.suggest(mentor, mentee).apply(1L);

            final CancelCoffeeChatCommand command = new CancelCoffeeChatCommand(authenticated, coffeeChat.getId(), "취소..");
            given(coffeeChatRepository.getByIdAndMenteeId(command.coffeeChatId(), command.authenticated().id)).willReturn(coffeeChat);

            // when
            sut.invoke(command);

            // then
            assertAll(
                    () -> verify(coffeeChatRepository, times(1)).getByIdAndMenteeId(command.coffeeChatId(), command.authenticated().id),
                    () -> verify(applicationEventPublisher, times(1)).publishEvent(any(MentorNotification.MenteeCanceledFromMentorFlowEvent.class)),
                    () -> assertThat(coffeeChat.getMentorId()).isEqualTo(mentor.getId()),
                    () -> assertThat(coffeeChat.getMenteeId()).isEqualTo(mentee.getId()),
                    () -> assertThat(coffeeChat.getStatus()).isEqualTo(CANCEL_FROM_MENTOR_FLOW),
                    () -> assertThat(coffeeChat.getReason().getApplyReason()).isNull(),
                    () -> assertThat(coffeeChat.getReason().getSuggestReason()).isNotNull(),
                    () -> assertThat(coffeeChat.getReason().getCancelReason()).isNotNull(),
                    () -> assertThat(coffeeChat.getReason().getRejectReason()).isNull(),
                    () -> assertThat(coffeeChat.getCancelBy()).isEqualTo(mentee.getId()),
                    () -> assertThat(coffeeChat.getQuestion()).isNull(),
                    () -> assertThat(coffeeChat.getReservation()).isNull(),
                    () -> assertThat(coffeeChat.getStrategy()).isNull()
            );
        }
    }
}
