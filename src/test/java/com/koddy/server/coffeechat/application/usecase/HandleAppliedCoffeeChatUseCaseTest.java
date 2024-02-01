package com.koddy.server.coffeechat.application.usecase;

import com.koddy.server.coffeechat.application.usecase.command.ApproveAppliedCoffeeChatCommand;
import com.koddy.server.coffeechat.application.usecase.command.RejectAppliedCoffeeChatCommand;
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

import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.APPROVE;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.REJECT;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_1;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static com.koddy.server.common.utils.EncryptorFactory.getEncryptor;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("CoffeeChat -> HandleAppliedCoffeeChatUseCase 테스트")
class HandleAppliedCoffeeChatUseCaseTest extends UnitTest {
    private final CoffeeChatRepository coffeeChatRepository = mock(CoffeeChatRepository.class);
    private final Encryptor encryptor = getEncryptor();
    private final HandleAppliedCoffeeChatUseCase sut = new HandleAppliedCoffeeChatUseCase(coffeeChatRepository, encryptor);

    private final Mentee mentee = MENTEE_1.toDomain().apply(1L);
    private final Mentor mentor = MENTOR_1.toDomain().apply(2L);

    @Test
    @DisplayName("멘티의 커피챗 신청을 거절한다")
    void reject() {
        // given
        final LocalDateTime start = LocalDateTime.of(2024, 2, 1, 9, 0);
        final CoffeeChat coffeeChat = CoffeeChatFixture.MenteeFlow.apply(start, start.plusMinutes(30), mentee, mentor).apply(1L);

        final RejectAppliedCoffeeChatCommand command = new RejectAppliedCoffeeChatCommand(coffeeChat.getId(), "거절...");
        given(coffeeChatRepository.getAppliedCoffeeChat(command.coffeeChatId())).willReturn(coffeeChat);

        // when
        sut.reject(command);

        // then
        assertAll(
                () -> verify(coffeeChatRepository, times(1)).getAppliedCoffeeChat(command.coffeeChatId()),
                () -> assertThat(coffeeChat.getSourceMemberId()).isEqualTo(mentee.getId()),
                () -> assertThat(coffeeChat.getTargetMemberId()).isEqualTo(mentor.getId()),
                () -> assertThat(coffeeChat.getApplyReason()).isNotNull(),
                () -> assertThat(coffeeChat.getRejectReason()).isEqualTo(command.rejectReason()),
                () -> assertThat(coffeeChat.getStatus()).isEqualTo(REJECT),
                () -> assertThat(coffeeChat.getStart().toLocalDateTime()).isEqualTo(start),
                () -> assertThat(coffeeChat.getEnd().toLocalDateTime()).isEqualTo(start.plusMinutes(30)),
                () -> assertThat(coffeeChat.getStrategy()).isNull()
        );
    }

    @Test
    @DisplayName("멘티의 커피챗 신청을 수락한다")
    void approve() {
        // given
        final LocalDateTime start = LocalDateTime.of(2024, 2, 1, 9, 0);
        final CoffeeChat coffeeChat = CoffeeChatFixture.MenteeFlow.apply(start, start.plusMinutes(30), mentee, mentor).apply(1L);

        final ApproveAppliedCoffeeChatCommand command = new ApproveAppliedCoffeeChatCommand(coffeeChat.getId(), Strategy.Type.from("kakao"), "sjiwon");
        given(coffeeChatRepository.getAppliedCoffeeChat(command.coffeeChatId())).willReturn(coffeeChat);

        // when
        sut.approve(command);

        // then
        assertAll(
                () -> verify(coffeeChatRepository, times(1)).getAppliedCoffeeChat(command.coffeeChatId()),
                () -> assertThat(coffeeChat.getSourceMemberId()).isEqualTo(mentee.getId()),
                () -> assertThat(coffeeChat.getTargetMemberId()).isEqualTo(mentor.getId()),
                () -> assertThat(coffeeChat.getApplyReason()).isNotNull(),
                () -> assertThat(coffeeChat.getRejectReason()).isNull(),
                () -> assertThat(coffeeChat.getStatus()).isEqualTo(APPROVE),
                () -> assertThat(coffeeChat.getStart().toLocalDateTime()).isEqualTo(start),
                () -> assertThat(coffeeChat.getEnd().toLocalDateTime()).isEqualTo(start.plusMinutes(30)),
                () -> assertThat(coffeeChat.getStrategy().getType()).isEqualTo(command.type()),
                () -> assertThat(coffeeChat.getStrategy().getValue()).isNotEqualTo(command.value()),
                () -> assertThat(encryptor.symmetricDecrypt(coffeeChat.getStrategy().getValue())).isEqualTo(command.value())
        );
    }
}
