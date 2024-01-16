package com.koddy.server.coffeechat.application.usecase;

import com.koddy.server.coffeechat.application.usecase.command.CancelCoffeeChatCommand;
import com.koddy.server.coffeechat.domain.model.CoffeeChat;
import com.koddy.server.coffeechat.domain.model.Reservation;
import com.koddy.server.coffeechat.domain.repository.CoffeeChatRepository;
import com.koddy.server.common.UnitTest;
import com.koddy.server.member.domain.model.mentee.Mentee;
import com.koddy.server.member.domain.model.mentor.Mentor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.CANCEL;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_1;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("CoffeeChat -> CancelCoffeeChatUseCase 테스트")
class CancelCoffeeChatUseCaseTest extends UnitTest {
    private final CoffeeChatRepository coffeeChatRepository = mock(CoffeeChatRepository.class);
    private final CancelCoffeeChatUseCase sut = new CancelCoffeeChatUseCase(coffeeChatRepository);

    private final Mentee mentee = MENTEE_1.toDomain().apply(1L);
    private final Mentor mentor = MENTOR_1.toDomain().apply(2L);
    private final String applyReason = "신청 이유...";

    @Test
    @DisplayName("멘티는 자신이 신청한 커피챗을 취소한다")
    void cancelAppliedCoffeeChat() {
        // given
        final Reservation start = new Reservation(LocalDateTime.of(2024, 2, 1, 9, 0));
        final Reservation end = new Reservation(LocalDateTime.of(2024, 2, 1, 10, 0));
        final CoffeeChat coffeeChat = CoffeeChat.applyCoffeeChat(mentee, mentor, applyReason, start, end).apply(1L);

        final CancelCoffeeChatCommand command = new CancelCoffeeChatCommand(mentee.getId(), coffeeChat.getId());
        given(coffeeChatRepository.getAppliedOrSuggestedCoffeeChat(command.coffeeChatId(), command.memberId())).willReturn(coffeeChat);

        // when
        sut.invoke(command);

        // then
        assertAll(
                () -> verify(coffeeChatRepository, times(1)).getAppliedOrSuggestedCoffeeChat(command.coffeeChatId(), command.memberId()),
                () -> assertThat(coffeeChat.getApplier()).isEqualTo(mentee),
                () -> assertThat(coffeeChat.getTarget()).isEqualTo(mentor),
                () -> assertThat(coffeeChat.getApplyReason()).isEqualTo(applyReason),
                () -> assertThat(coffeeChat.getRejectReason()).isNull(),
                () -> assertThat(coffeeChat.getStatus()).isEqualTo(CANCEL),
                () -> assertThat(coffeeChat.getStart()).isEqualTo(start),
                () -> assertThat(coffeeChat.getEnd()).isEqualTo(end),
                () -> assertThat(coffeeChat.getStrategy()).isNull()
        );
    }

    @Test
    @DisplayName("멘토는 자신이 제안한 커피챗을 취소한다")
    void cancelSuggestedCoffeeChat() {
        // given
        final CoffeeChat coffeeChat = CoffeeChat.suggestCoffeeChat(mentor, mentee, applyReason).apply(1L);

        final CancelCoffeeChatCommand command = new CancelCoffeeChatCommand(mentor.getId(), coffeeChat.getId());
        given(coffeeChatRepository.getAppliedOrSuggestedCoffeeChat(command.coffeeChatId(), command.memberId())).willReturn(coffeeChat);

        // when
        sut.invoke(command);

        // then
        assertAll(
                () -> verify(coffeeChatRepository, times(1)).getAppliedOrSuggestedCoffeeChat(command.coffeeChatId(), command.memberId()),
                () -> assertThat(coffeeChat.getApplier()).isEqualTo(mentor),
                () -> assertThat(coffeeChat.getTarget()).isEqualTo(mentee),
                () -> assertThat(coffeeChat.getApplyReason()).isEqualTo(applyReason),
                () -> assertThat(coffeeChat.getRejectReason()).isNull(),
                () -> assertThat(coffeeChat.getStatus()).isEqualTo(CANCEL),
                () -> assertThat(coffeeChat.getStart()).isNull(),
                () -> assertThat(coffeeChat.getEnd()).isNull(),
                () -> assertThat(coffeeChat.getStrategy()).isNull()
        );
    }
}
