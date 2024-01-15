package com.koddy.server.coffeechat.application.usecase;

import com.koddy.server.coffeechat.application.usecase.command.ApprovePendingCoffeeChatCommand;
import com.koddy.server.coffeechat.application.usecase.command.RejectPendingCoffeeChatCommand;
import com.koddy.server.coffeechat.domain.model.CoffeeChat;
import com.koddy.server.coffeechat.domain.model.Reservation;
import com.koddy.server.coffeechat.domain.model.Strategy;
import com.koddy.server.coffeechat.domain.repository.CoffeeChatRepository;
import com.koddy.server.common.UnitTest;
import com.koddy.server.global.encrypt.Encryptor;
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

@DisplayName("CoffeeChat -> HandlePendingCoffeeChatUseCase 테스트")
class HandlePendingCoffeeChatUseCaseTest extends UnitTest {
    private final CoffeeChatRepository coffeeChatRepository = mock(CoffeeChatRepository.class);
    private final Encryptor encryptor = getEncryptor();
    private final HandlePendingCoffeeChatUseCase sut = new HandlePendingCoffeeChatUseCase(coffeeChatRepository, encryptor);

    private final Mentee mentee = MENTEE_1.toDomain().apply(1L);
    private final Mentor mentor = MENTOR_1.toDomain().apply(2L);
    private final String applyReason = "신청 이유...";
    private final Reservation start = new Reservation(LocalDateTime.of(2024, 2, 1, 9, 0));
    private final Reservation end = new Reservation(LocalDateTime.of(2024, 2, 1, 10, 0));

    @Test
    @DisplayName("최종 결정 대기 상태인 CoffeeChat에 대해서 멘토는 거절한다")
    void reject() {
        // given
        final CoffeeChat coffeeChat = CoffeeChat.suggestCoffeeChat(mentor, mentee, applyReason).apply(1L);
        coffeeChat.pendingFromMentorSuggest(start, end);

        final String rejectReason = "거절...";
        final RejectPendingCoffeeChatCommand command = new RejectPendingCoffeeChatCommand(coffeeChat.getId(), rejectReason);
        given(coffeeChatRepository.getPendingCoffeeChat(command.coffeeChatId())).willReturn(coffeeChat);

        // when
        sut.reject(command);

        // then
        assertAll(
                () -> verify(coffeeChatRepository, times(1)).getPendingCoffeeChat(command.coffeeChatId()),
                () -> assertThat(coffeeChat.getApplier()).isEqualTo(mentor),
                () -> assertThat(coffeeChat.getTarget()).isEqualTo(mentee),
                () -> assertThat(coffeeChat.getApplyReason()).isEqualTo(applyReason),
                () -> assertThat(coffeeChat.getRejectReason()).isEqualTo(rejectReason),
                () -> assertThat(coffeeChat.getStatus()).isEqualTo(REJECT),
                () -> assertThat(coffeeChat.getStart()).isEqualTo(start),
                () -> assertThat(coffeeChat.getEnd()).isEqualTo(end),
                () -> assertThat(coffeeChat.getStrategy()).isNull()
        );
    }

    @Test
    @DisplayName("최종 결정 대기 상태인 CoffeeChat에 대해서 멘토는 수락한다")
    void approve() {
        // given
        final CoffeeChat coffeeChat = CoffeeChat.suggestCoffeeChat(mentor, mentee, applyReason).apply(1L);
        coffeeChat.pendingFromMentorSuggest(start, end);

        final Strategy.Type type = Strategy.Type.from("kakao");
        final String value = "sjiwon";
        final ApprovePendingCoffeeChatCommand command = new ApprovePendingCoffeeChatCommand(coffeeChat.getId(), type, value);
        given(coffeeChatRepository.getPendingCoffeeChat(command.coffeeChatId())).willReturn(coffeeChat);

        // when
        sut.approve(command);

        // then
        assertAll(
                () -> verify(coffeeChatRepository, times(1)).getPendingCoffeeChat(command.coffeeChatId()),
                () -> assertThat(coffeeChat.getApplier()).isEqualTo(mentor),
                () -> assertThat(coffeeChat.getTarget()).isEqualTo(mentee),
                () -> assertThat(coffeeChat.getApplyReason()).isEqualTo(applyReason),
                () -> assertThat(coffeeChat.getRejectReason()).isNull(),
                () -> assertThat(coffeeChat.getStatus()).isEqualTo(APPROVE),
                () -> assertThat(coffeeChat.getStart()).isEqualTo(start),
                () -> assertThat(coffeeChat.getEnd()).isEqualTo(end),
                () -> assertThat(coffeeChat.getStrategy().getType()).isEqualTo(type),
                () -> assertThat(coffeeChat.getStrategy().getValue()).isNotEqualTo(value),
                () -> assertThat(encryptor.symmetricDecrypt(coffeeChat.getStrategy().getValue())).isEqualTo(value)
        );
    }
}
