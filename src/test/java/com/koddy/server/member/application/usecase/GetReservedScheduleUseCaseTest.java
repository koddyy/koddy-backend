package com.koddy.server.member.application.usecase;

import com.koddy.server.coffeechat.domain.model.CoffeeChat;
import com.koddy.server.coffeechat.domain.model.Reservation;
import com.koddy.server.coffeechat.domain.repository.CoffeeChatRepository;
import com.koddy.server.common.UnitTest;
import com.koddy.server.member.application.usecase.query.GetReservedSchedule;
import com.koddy.server.member.application.usecase.query.response.ReservedSchedule;
import com.koddy.server.member.domain.model.mentee.Mentee;
import com.koddy.server.member.domain.model.mentor.Mentor;
import com.koddy.server.member.domain.model.response.ScheduleResponse;
import com.koddy.server.member.domain.repository.MentorRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_1;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_2;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_3;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("Member -> GetReservedScheduleUseCase 테스트")
class GetReservedScheduleUseCaseTest extends UnitTest {
    private final MentorRepository mentorRepository = mock(MentorRepository.class);
    private final CoffeeChatRepository coffeeChatRepository = mock(CoffeeChatRepository.class);
    private final GetReservedScheduleUseCase sut = new GetReservedScheduleUseCase(mentorRepository, coffeeChatRepository);

    private final Mentor mentor = MENTOR_1.toDomain().apply(1L);
    private final Mentee menteeA = MENTEE_1.toDomain().apply(2L);
    private final Mentee menteeB = MENTEE_2.toDomain().apply(3L);
    private final Mentee menteeC = MENTEE_3.toDomain().apply(4L);

    @Test
    @DisplayName("특정 Year-Month에 대해서 멘토의 예약된 스케줄 정보를 조회한다")
    void success() {
        // given
        final GetReservedSchedule query = new GetReservedSchedule(mentor.getId(), 2024, 2);
        given(mentorRepository.getByIdWithSchedules(query.mentorId())).willReturn(mentor);

        final CoffeeChat coffeeChatA = CoffeeChat.suggest(mentor, menteeA, "제안..").apply(1L);
        final CoffeeChat coffeeChatB = CoffeeChat.suggest(mentor, menteeB, "제안..").apply(2L);
        final CoffeeChat coffeeChatC = CoffeeChat.suggest(mentor, menteeC, "제안..").apply(3L);
        coffeeChatA.rejectFromMentorSuggest("거절..");
        coffeeChatB.pendingFromMentorSuggest(
                "질문..",
                new Reservation(LocalDateTime.of(2024, 2, 18, 18, 0)),
                new Reservation(LocalDateTime.of(2024, 2, 18, 18, 30))
        );
        coffeeChatC.pendingFromMentorSuggest(
                "질문..",
                new Reservation(LocalDateTime.of(2024, 2, 15, 18, 0)),
                new Reservation(LocalDateTime.of(2024, 2, 15, 18, 30))
        );
        given(coffeeChatRepository.getReservedCoffeeChat(query.mentorId(), query.year(), query.month())).willReturn(List.of(coffeeChatC, coffeeChatB));

        // when
        final ReservedSchedule result = sut.invoke(query);

        // then
        assertAll(
                () -> verify(mentorRepository, times(1)).getByIdWithSchedules(query.mentorId()),
                () -> verify(coffeeChatRepository, times(1)).getReservedCoffeeChat(query.mentorId(), query.year(), query.month()),
                () -> assertThat(result.period().startDate()).isEqualTo(mentor.getMentoringPeriod().getStartDate()),
                () -> assertThat(result.period().endDate()).isEqualTo(mentor.getMentoringPeriod().getEndDate()),
                () -> assertThat(result.schedules()).containsExactlyInAnyOrderElementsOf(
                        mentor.getSchedules()
                                .stream()
                                .map(it -> ScheduleResponse.of(it.getTimeline()))
                                .toList()
                ),
                () -> assertThat(result.timeUnit()).isEqualTo(mentor.getMentoringTimeUnit()),
                () -> assertThat(result.reserved())
                        .map(ReservedSchedule.Reserved::start)
                        .containsExactly(coffeeChatC.getStart().toLocalDateTime(), coffeeChatB.getStart().toLocalDateTime()),
                () -> assertThat(result.reserved())
                        .map(ReservedSchedule.Reserved::end)
                        .containsExactly(coffeeChatC.getEnd().toLocalDateTime(), coffeeChatB.getEnd().toLocalDateTime())
        );
    }
}
