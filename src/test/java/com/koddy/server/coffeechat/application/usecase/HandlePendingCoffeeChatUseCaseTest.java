package com.koddy.server.coffeechat.application.usecase;

import com.koddy.server.coffeechat.application.usecase.command.ApprovePendingCoffeeChatCommand;
import com.koddy.server.coffeechat.application.usecase.command.RejectPendingCoffeeChatCommand;
import com.koddy.server.coffeechat.domain.model.CoffeeChat;
import com.koddy.server.coffeechat.domain.model.Strategy;
import com.koddy.server.coffeechat.domain.repository.CoffeeChatRepository;
import com.koddy.server.common.UnitTest;
import com.koddy.server.common.fixture.CoffeeChatFixture;
import com.koddy.server.global.utils.encrypt.Encryptor;
import com.koddy.server.member.domain.model.mentee.Mentee;
import com.koddy.server.member.domain.model.mentor.Mentor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_FINALLY_APPROVE;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_FINALLY_REJECT;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_1;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static com.koddy.server.common.utils.EncryptorFactory.getEncryptor;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("CoffeeChat -> HandlePendingCoffeeChatUseCase 테스트")
class HandlePendingCoffeeChatUseCaseTest extends UnitTest {
    private final CoffeeChatRepository coffeeChatRepository = mock(CoffeeChatRepository.class);
    private final Encryptor encryptor = getEncryptor();
    private final HandlePendingCoffeeChatUseCase sut = new HandlePendingCoffeeChatUseCase(coffeeChatRepository, encryptor);

    private final Mentee mentee = MENTEE_1.toDomain().apply(1L);
    private final Mentor mentor = MENTOR_1.toDomain().apply(2L);

    @Test
    @DisplayName("최종 결정 대기 상태인 CoffeeChat에 대해서 멘토는 거절한다")
    void reject() {
        // given
        final LocalDateTime start = LocalDateTime.of(2024, 2, 1, 9, 0);
        final CoffeeChat coffeeChat = CoffeeChatFixture.MentorFlow.suggestAndPending(start, start.plusMinutes(30), mentor, mentee).apply(1L);

        final RejectPendingCoffeeChatCommand command = new RejectPendingCoffeeChatCommand(mentor.getId(), coffeeChat.getId(), "거절...");
        given(coffeeChatRepository.getByIdAndMentorId(command.coffeeChatId(), command.mentorId())).willReturn(coffeeChat);

        // when
        sut.reject(command);

        // then
        assertAll(
                () -> verify(coffeeChatRepository, times(1)).getByIdAndMentorId(command.coffeeChatId(), command.mentorId()),
                () -> assertThat(coffeeChat.getMentorId()).isEqualTo(mentor.getId()),
                () -> assertThat(coffeeChat.getMenteeId()).isEqualTo(mentee.getId()),
                () -> assertThat(coffeeChat.getStatus()).isEqualTo(MENTOR_FINALLY_REJECT),
                () -> assertThat(coffeeChat.getReason().getApplyReason()).isNull(),
                () -> assertThat(coffeeChat.getReason().getSuggestReason()).isNotNull(),
                () -> assertThat(coffeeChat.getReason().getCancelReason()).isNull(),
                () -> assertThat(coffeeChat.getReason().getRejectReason()).isNotNull(),
                () -> assertThat(coffeeChat.getQuestion()).isNotNull(),
                () -> assertThat(coffeeChat.getReservation().getStart()).isEqualTo(start),
                () -> assertThat(coffeeChat.getReservation().getEnd()).isEqualTo(start.plusMinutes(30)),
                () -> assertThat(coffeeChat.getStrategy()).isNull()
        );
    }

    @Test
    @DisplayName("최종 결정 대기 상태인 CoffeeChat에 대해서 멘토는 수락한다")
    void approve() {
        // given
        final LocalDateTime start = LocalDateTime.of(2024, 2, 1, 9, 0);
        final CoffeeChat coffeeChat = CoffeeChatFixture.MentorFlow.suggestAndPending(start, start.plusMinutes(30), mentor, mentee).apply(1L);

        final ApprovePendingCoffeeChatCommand command = new ApprovePendingCoffeeChatCommand(
                mentor.getId(),
                coffeeChat.getId(),
                Strategy.Type.KAKAO_ID,
                "sjiwon"
        );
        given(coffeeChatRepository.getByIdAndMentorId(command.coffeeChatId(), command.mentorId())).willReturn(coffeeChat);

        // when
        sut.approve(command);

        // then
        assertAll(
                () -> verify(coffeeChatRepository, times(1)).getByIdAndMentorId(command.coffeeChatId(), command.mentorId()),
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
