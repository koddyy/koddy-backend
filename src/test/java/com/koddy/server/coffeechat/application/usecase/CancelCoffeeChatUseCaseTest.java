package com.koddy.server.coffeechat.application.usecase;

import com.koddy.server.auth.domain.model.Authenticated;
import com.koddy.server.coffeechat.application.usecase.command.CancelCoffeeChatCommand;
import com.koddy.server.coffeechat.domain.model.CoffeeChat;
import com.koddy.server.coffeechat.domain.repository.CoffeeChatRepository;
import com.koddy.server.common.UnitTest;
import com.koddy.server.common.fixture.CoffeeChatFixture.MenteeFlow;
import com.koddy.server.common.fixture.CoffeeChatFixture.MentorFlow;
import com.koddy.server.member.domain.model.mentee.Mentee;
import com.koddy.server.member.domain.model.mentor.Mentor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTEE_CANCEL;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_CANCEL;
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
    private final Authenticated menteeAuthenticated = new Authenticated(mentee.getId(), mentee.getAuthority());
    private final Authenticated mentorAuthenticated = new Authenticated(mentor.getId(), mentor.getAuthority());

    @Test
    @DisplayName("멘티는 자신이 신청한 커피챗을 취소한다")
    void cancelAppliedCoffeeChat() {
        // given
        final LocalDateTime start = LocalDateTime.of(2024, 2, 1, 9, 0);
        final CoffeeChat coffeeChat = MenteeFlow.apply(start, start.plusMinutes(30), mentee, mentor).apply(1L);

        final CancelCoffeeChatCommand command = new CancelCoffeeChatCommand(menteeAuthenticated, coffeeChat.getId());
        given(coffeeChatRepository.getAppliedOrSuggestedCoffeeChat(command.coffeeChatId(), command.authenticated().id())).willReturn(coffeeChat);

        // when
        sut.invoke(command);

        // then
        assertAll(
                () -> verify(coffeeChatRepository, times(1)).getAppliedOrSuggestedCoffeeChat(command.coffeeChatId(), command.authenticated().id()),
                () -> assertThat(coffeeChat.getSourceMemberId()).isEqualTo(mentee.getId()),
                () -> assertThat(coffeeChat.getTargetMemberId()).isEqualTo(mentor.getId()),
                () -> assertThat(coffeeChat.getApplyReason()).isNotNull(),
                () -> assertThat(coffeeChat.getRejectReason()).isNull(),
                () -> assertThat(coffeeChat.getStatus()).isEqualTo(MENTEE_CANCEL),
                () -> assertThat(coffeeChat.getReservation().getStart()).isEqualTo(start),
                () -> assertThat(coffeeChat.getReservation().getEnd()).isEqualTo(start.plusMinutes(30)),
                () -> assertThat(coffeeChat.getStrategy()).isNull()
        );
    }

    @Test
    @DisplayName("멘토는 자신이 제안한 커피챗을 취소한다")
    void cancelSuggestedCoffeeChat() {
        // given
        final CoffeeChat coffeeChat = MentorFlow.suggest(mentor, mentee).apply(1L);

        final CancelCoffeeChatCommand command = new CancelCoffeeChatCommand(mentorAuthenticated, coffeeChat.getId());
        given(coffeeChatRepository.getAppliedOrSuggestedCoffeeChat(command.coffeeChatId(), command.authenticated().id())).willReturn(coffeeChat);

        // when
        sut.invoke(command);

        // then
        assertAll(
                () -> verify(coffeeChatRepository, times(1)).getAppliedOrSuggestedCoffeeChat(command.coffeeChatId(), command.authenticated().id()),
                () -> assertThat(coffeeChat.getSourceMemberId()).isEqualTo(mentor.getId()),
                () -> assertThat(coffeeChat.getTargetMemberId()).isEqualTo(mentee.getId()),
                () -> assertThat(coffeeChat.getApplyReason()).isNotNull(),
                () -> assertThat(coffeeChat.getRejectReason()).isNull(),
                () -> assertThat(coffeeChat.getStatus()).isEqualTo(MENTOR_CANCEL),
                () -> assertThat(coffeeChat.getReservation()).isNull(),
                () -> assertThat(coffeeChat.getStrategy()).isNull()
        );
    }
}
