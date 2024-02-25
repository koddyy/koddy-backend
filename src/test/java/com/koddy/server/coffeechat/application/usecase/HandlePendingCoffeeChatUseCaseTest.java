package com.koddy.server.coffeechat.application.usecase;

import com.koddy.server.coffeechat.application.usecase.command.FinallyApprovePendingCoffeeChatCommand;
import com.koddy.server.coffeechat.application.usecase.command.FinallyCancelPendingCoffeeChatCommand;
import com.koddy.server.coffeechat.domain.event.BothNotification;
import com.koddy.server.coffeechat.domain.event.MenteeNotification;
import com.koddy.server.coffeechat.domain.model.CoffeeChat;
import com.koddy.server.coffeechat.domain.model.Strategy;
import com.koddy.server.coffeechat.domain.repository.CoffeeChatRepository;
import com.koddy.server.coffeechat.domain.service.CoffeeChatNotificationEventPublisher;
import com.koddy.server.common.UnitTest;
import com.koddy.server.common.fixture.CoffeeChatFixture;
import com.koddy.server.common.mock.fake.FakeEncryptor;
import com.koddy.server.global.utils.encrypt.Encryptor;
import com.koddy.server.member.domain.model.mentee.Mentee;
import com.koddy.server.member.domain.model.mentor.Mentor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationEventPublisher;

import java.time.LocalDateTime;

import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_FINALLY_APPROVE;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_FINALLY_CANCEL;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_1;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("CoffeeChat -> HandlePendingCoffeeChatUseCase 테스트")
class HandlePendingCoffeeChatUseCaseTest extends UnitTest {
    private final CoffeeChatRepository coffeeChatRepository = mock(CoffeeChatRepository.class);
    private final Encryptor encryptor = new FakeEncryptor();
    private final ApplicationEventPublisher applicationEventPublisher = mock(ApplicationEventPublisher.class);
    private final CoffeeChatNotificationEventPublisher eventPublisher = new CoffeeChatNotificationEventPublisher(applicationEventPublisher);
    private final HandlePendingCoffeeChatUseCase sut = new HandlePendingCoffeeChatUseCase(
            coffeeChatRepository,
            encryptor,
            eventPublisher
    );

    private final Mentee mentee = MENTEE_1.toDomain().apply(1L);
    private final Mentor mentor = MENTOR_1.toDomain().apply(2L);

    @Test
    @DisplayName("Pending 상태인 CoffeeChat에 대해서 멘토는 최종 취소한다")
    void finallyCancel() {
        // given
        final LocalDateTime start = LocalDateTime.of(2024, 2, 1, 9, 0);
        final CoffeeChat coffeeChat = CoffeeChatFixture.MentorFlow.suggestAndPending(start, start.plusMinutes(30), mentor, mentee).apply(1L);

        final FinallyCancelPendingCoffeeChatCommand command = new FinallyCancelPendingCoffeeChatCommand(mentor.getId(), coffeeChat.getId(), "거절...");
        given(coffeeChatRepository.getByIdAndMentorId(command.coffeeChatId(), command.mentorId())).willReturn(coffeeChat);

        // when
        sut.finallyCancel(command);

        // then
        assertAll(
                () -> verify(coffeeChatRepository, times(1)).getByIdAndMentorId(command.coffeeChatId(), command.mentorId()),
                () -> verify(applicationEventPublisher, times(1)).publishEvent(any(MenteeNotification.MentorFinallyCanceledFromMentorFlowEvent.class)),
                () -> assertThat(coffeeChat.getMentorId()).isEqualTo(mentor.getId()),
                () -> assertThat(coffeeChat.getMenteeId()).isEqualTo(mentee.getId()),
                () -> assertThat(coffeeChat.getStatus()).isEqualTo(MENTOR_FINALLY_CANCEL),
                () -> assertThat(coffeeChat.getReason().getApplyReason()).isNull(),
                () -> assertThat(coffeeChat.getReason().getSuggestReason()).isNotNull(),
                () -> assertThat(coffeeChat.getReason().getCancelReason()).isNotNull(),
                () -> assertThat(coffeeChat.getReason().getRejectReason()).isNull(),
                () -> assertThat(coffeeChat.getQuestion()).isNotNull(),
                () -> assertThat(coffeeChat.getReservation().getStart()).isEqualTo(start),
                () -> assertThat(coffeeChat.getReservation().getEnd()).isEqualTo(start.plusMinutes(30)),
                () -> assertThat(coffeeChat.getStrategy()).isNull()
        );
    }

    @Test
    @DisplayName("Pending 상태인 CoffeeChat에 대해서 멘토는 최종 수락한다")
    void finallyApprove() {
        // given
        final LocalDateTime start = LocalDateTime.of(2024, 2, 1, 9, 0);
        final CoffeeChat coffeeChat = CoffeeChatFixture.MentorFlow.suggestAndPending(start, start.plusMinutes(30), mentor, mentee).apply(1L);

        final FinallyApprovePendingCoffeeChatCommand command = new FinallyApprovePendingCoffeeChatCommand(
                mentor.getId(),
                coffeeChat.getId(),
                Strategy.Type.KAKAO_ID,
                "sjiwon"
        );
        given(coffeeChatRepository.getByIdAndMentorId(command.coffeeChatId(), command.mentorId())).willReturn(coffeeChat);

        // when
        sut.finallyApprove(command);

        // then
        assertAll(
                () -> verify(coffeeChatRepository, times(1)).getByIdAndMentorId(command.coffeeChatId(), command.mentorId()),
                () -> verify(applicationEventPublisher, times(1)).publishEvent(any(BothNotification.FinallyApprovedFromMentorFlowEvent.class)),
                () -> assertThat(coffeeChat.getMentorId()).isEqualTo(mentor.getId()),
                () -> assertThat(coffeeChat.getMenteeId()).isEqualTo(mentee.getId()),
                () -> assertThat(coffeeChat.getStatus()).isEqualTo(MENTOR_FINALLY_APPROVE),
                () -> assertThat(coffeeChat.getReason().getApplyReason()).isNull(),
                () -> assertThat(coffeeChat.getReason().getSuggestReason()).isNotNull(),
                () -> assertThat(coffeeChat.getReason().getCancelReason()).isNull(),
                () -> assertThat(coffeeChat.getReason().getRejectReason()).isNull(),
                () -> assertThat(coffeeChat.getQuestion()).isNotNull(),
                () -> assertThat(coffeeChat.getReservation().getStart()).isEqualTo(start),
                () -> assertThat(coffeeChat.getReservation().getEnd()).isEqualTo(start.plusMinutes(30)),
                () -> assertThat(coffeeChat.getStrategy().getType()).isEqualTo(command.type()),
                () -> assertThat(coffeeChat.getStrategy().getValue()).isNotEqualTo(command.value()),
                () -> assertThat(encryptor.decrypt(coffeeChat.getStrategy().getValue())).isEqualTo(command.value())
        );
    }
}
