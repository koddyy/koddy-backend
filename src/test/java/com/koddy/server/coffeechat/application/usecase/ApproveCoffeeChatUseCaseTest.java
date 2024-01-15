package com.koddy.server.coffeechat.application.usecase;

import com.koddy.server.coffeechat.application.usecase.command.ApproveMenteeApplyCommand;
import com.koddy.server.coffeechat.application.usecase.command.ApproveMentorSuggestCommand;
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
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.PENDING;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_1;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static com.koddy.server.common.utils.EncryptorFactory.getEncryptor;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("CoffeeChat -> ApproveCoffeeChatUseCase 테스트")
class ApproveCoffeeChatUseCaseTest extends UnitTest {
    private final CoffeeChatRepository coffeeChatRepository = mock(CoffeeChatRepository.class);
    private final Encryptor encryptor = getEncryptor();
    private final ApproveCoffeeChatUseCase sut = new ApproveCoffeeChatUseCase(coffeeChatRepository, encryptor);

    private final Mentee mentee = MENTEE_1.toDomain().apply(1L);
    private final Mentor mentor = MENTOR_1.toDomain().apply(2L);
    private final String applyReason = "신청 이유...";

    @Test
    @DisplayName("멘토의 커피챗 제안을 수락한다")
    void suggestByMentor() {
        // given
        final CoffeeChat coffeeChat = CoffeeChat.suggestCoffeeChat(mentor, mentee, applyReason).apply(1L);

        final Reservation start = new Reservation(LocalDateTime.of(2024, 2, 1, 9, 0));
        final Reservation end = new Reservation(LocalDateTime.of(2024, 2, 1, 10, 0));
        final ApproveMentorSuggestCommand command = new ApproveMentorSuggestCommand(coffeeChat.getId(), start, end);
        given(coffeeChatRepository.getAppliedCoffeeChat(command.coffeeChatId())).willReturn(coffeeChat);

        // when
        sut.suggestByMentor(command);

        // then
        assertAll(
                () -> verify(coffeeChatRepository, times(1)).getAppliedCoffeeChat(command.coffeeChatId()),
                () -> assertThat(coffeeChat.getApplier()).isEqualTo(mentor),
                () -> assertThat(coffeeChat.getTarget()).isEqualTo(mentee),
                () -> assertThat(coffeeChat.getApplyReason()).isEqualTo(applyReason),
                () -> assertThat(coffeeChat.getRejectReason()).isNull(),
                () -> assertThat(coffeeChat.getStatus()).isEqualTo(PENDING),
                () -> assertThat(coffeeChat.getStart()).isEqualTo(start),
                () -> assertThat(coffeeChat.getEnd()).isEqualTo(end),
                () -> assertThat(coffeeChat.getStrategy()).isNull()
        );
    }

    @Test
    @DisplayName("멘티의 커피챗 신청을 수락한다")
    void applyByMentee() {
        // given
        final Reservation start = new Reservation(LocalDateTime.of(2024, 2, 1, 9, 0));
        final Reservation end = new Reservation(LocalDateTime.of(2024, 2, 1, 10, 0));
        final CoffeeChat coffeeChat = CoffeeChat.applyCoffeeChat(mentee, mentor, applyReason, start, end).apply(1L);

        final Strategy.Type type = Strategy.Type.KAKAO_ID;
        final String value = "sjiwon";
        final ApproveMenteeApplyCommand command = new ApproveMenteeApplyCommand(coffeeChat.getId(), type, value);
        given(coffeeChatRepository.getAppliedCoffeeChat(command.coffeeChatId())).willReturn(coffeeChat);

        // when
        sut.applyByMentee(command);

        // then
        assertAll(
                () -> verify(coffeeChatRepository, times(1)).getAppliedCoffeeChat(command.coffeeChatId()),
                () -> assertThat(coffeeChat.getApplier()).isEqualTo(mentee),
                () -> assertThat(coffeeChat.getTarget()).isEqualTo(mentor),
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
