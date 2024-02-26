package com.koddy.server.member.application.usecase;

import com.koddy.server.coffeechat.domain.model.CoffeeChat;
import com.koddy.server.coffeechat.domain.repository.query.MentorReservedScheduleQueryRepository;
import com.koddy.server.common.UnitTest;
import com.koddy.server.common.fixture.CoffeeChatFixture.MentorFlow;
import com.koddy.server.member.application.usecase.query.GetMentorReservedSchedule;
import com.koddy.server.member.application.usecase.query.response.MentorReservedSchedule;
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

@DisplayName("Member -> GetMentorReservedScheduleUseCase 테스트")
class GetMentorReservedScheduleUseCaseTest extends UnitTest {
    private final MentorRepository mentorRepository = mock(MentorRepository.class);
    private final MentorReservedScheduleQueryRepository mentorReservedScheduleQueryRepository = mock(MentorReservedScheduleQueryRepository.class);
    private final GetMentorReservedScheduleUseCase sut = new GetMentorReservedScheduleUseCase(mentorRepository, mentorReservedScheduleQueryRepository);

    private final Mentor mentor = MENTOR_1.toDomain().apply(1L);
    private final Mentee menteeA = MENTEE_1.toDomain().apply(2L);
    private final Mentee menteeB = MENTEE_2.toDomain().apply(3L);
    private final Mentee menteeC = MENTEE_3.toDomain().apply(4L);

    @Test
    @DisplayName("특정 Year-Month에 대해서 멘토의 예약된 스케줄 정보를 조회한다")
    void success() {
        // given
        final GetMentorReservedSchedule query = new GetMentorReservedSchedule(mentor.getId(), 2024, 2);
        given(mentorRepository.getByIdWithSchedules(query.mentorId())).willReturn(mentor);

        final LocalDateTime startB = LocalDateTime.of(2024, 2, 18, 18, 0);
        final LocalDateTime startC = LocalDateTime.of(2024, 2, 15, 18, 0);

        final CoffeeChat coffeeChatA = MentorFlow.suggestAndReject(mentor, menteeA).apply(1L);
        final CoffeeChat coffeeChatB = MentorFlow.suggestAndPending(startB, startB.plusMinutes(30), mentor, menteeB).apply(2L);
        final CoffeeChat coffeeChatC = MentorFlow.suggestAndPending(startC, startC.plusMinutes(30), mentor, menteeC).apply(2L);
        given(mentorReservedScheduleQueryRepository.fetchReservedCoffeeChat(query.mentorId(), query.year(), query.month())).willReturn(List.of(coffeeChatC, coffeeChatB));

        // when
        final MentorReservedSchedule result = sut.invoke(query);

        // then
        assertAll(
                () -> verify(mentorRepository, times(1)).getByIdWithSchedules(query.mentorId()),
                () -> verify(mentorReservedScheduleQueryRepository, times(1)).fetchReservedCoffeeChat(query.mentorId(), query.year(), query.month()),
                () -> assertThat(result.period().startDate()).isEqualTo(mentor.getMentoringPeriod().getStartDate()),
                () -> assertThat(result.period().endDate()).isEqualTo(mentor.getMentoringPeriod().getEndDate()),
                () -> assertThat(result.schedules()).containsExactlyInAnyOrderElementsOf(
                        mentor.getSchedules()
                                .stream()
                                .map(it -> ScheduleResponse.from(it.getTimeline()))
                                .toList()
                ),
                () -> assertThat(result.timeUnit()).isEqualTo(mentor.getMentoringTimeUnit()),
                () -> assertThat(result.reserved())
                        .map(MentorReservedSchedule.Reserved::start)
                        .containsExactly(coffeeChatC.getReservation().getStart(), coffeeChatB.getReservation().getStart()),
                () -> assertThat(result.reserved())
                        .map(MentorReservedSchedule.Reserved::end)
                        .containsExactly(coffeeChatC.getReservation().getEnd(), coffeeChatB.getReservation().getEnd())
        );
    }
}
