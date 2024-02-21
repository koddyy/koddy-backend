package com.koddy.server.coffeechat.domain.service;

import com.koddy.server.coffeechat.domain.model.CoffeeChat;
import com.koddy.server.coffeechat.domain.model.Reservation;
import com.koddy.server.coffeechat.domain.repository.query.MentorReservedScheduleQueryRepository;
import com.koddy.server.common.UnitTest;
import com.koddy.server.common.fixture.CoffeeChatFixture.MenteeFlow;
import com.koddy.server.member.domain.model.mentee.Mentee;
import com.koddy.server.member.domain.model.mentor.Mentor;
import com.koddy.server.member.domain.model.mentor.MentoringPeriod;
import com.koddy.server.member.domain.model.mentor.Timeline;
import com.koddy.server.member.exception.MemberException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_1;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static com.koddy.server.member.domain.model.mentor.DayOfWeek.FRI;
import static com.koddy.server.member.domain.model.mentor.DayOfWeek.THU;
import static com.koddy.server.member.domain.model.mentor.DayOfWeek.TUE;
import static com.koddy.server.member.domain.model.mentor.DayOfWeek.WED;
import static com.koddy.server.member.exception.MemberExceptionCode.CANNOT_RESERVATION;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DisplayName("CoffeeChat -> ReservationAvailabilityChecker 테스트")
class ReservationAvailabilityCheckerTest extends UnitTest {
    private final MentorReservedScheduleQueryRepository mentorReservedScheduleQueryRepository = mock(MentorReservedScheduleQueryRepository.class);
    private final ReservationAvailabilityChecker sut = new ReservationAvailabilityChecker(mentorReservedScheduleQueryRepository);

    private final Mentee mentee = MENTEE_1.toDomain().apply(1L);

    @Test
    @DisplayName("예약 날짜가 멘토링 진행 기간에 포함되지 않으면 예외가 발생한다")
    void throwExceptionByOutOfDate() {
        // given
        final MentoringPeriod mentoringPeriod = MentoringPeriod.of(
                LocalDate.of(2024, 2, 6),
                LocalDate.of(2024, 3, 1)
        );
        final Mentor mentor = MENTOR_1.toDomainWithMentoringInfo(mentoringPeriod, MENTOR_1.getTimelines()).apply(2L);

        // when - then
        final LocalDateTime target1 = LocalDateTime.of(2024, 2, 5, 18, 0);
        final LocalDateTime target2 = LocalDateTime.of(2024, 3, 2, 18, 0);

        assertAll(
                () -> assertThatThrownBy(() -> sut.check(mentor, Reservation.of(target1, target1.plusMinutes(30))))
                        .isInstanceOf(MemberException.class)
                        .hasMessage(CANNOT_RESERVATION.getMessage()),
                () -> assertThatThrownBy(() -> sut.check(mentor, Reservation.of(target2, target2.plusMinutes(30))))
                        .isInstanceOf(MemberException.class)
                        .hasMessage(CANNOT_RESERVATION.getMessage()),
                () -> verify(mentorReservedScheduleQueryRepository, times(0)).fetchReservedCoffeeChat(mentor.getId(), 2024, 2)
        );
    }

    @Test
    @DisplayName("예약 날짜가 멘토링 가능 시간에 포함되지 않으면 예외가 발생한다")
    void throwExceptionByNotAllowedTime() {
        // given
        final MentoringPeriod mentoringPeriod = MentoringPeriod.of(
                LocalDate.of(2024, 2, 1),
                LocalDate.of(2024, 3, 1)
        );
        final LocalTime time = LocalTime.of(19, 0);
        final List<Timeline> timelines = List.of(
                Timeline.of(TUE, time, time.plusHours(3)),
                Timeline.of(WED, time, time.plusHours(3)),
                Timeline.of(THU, time, time.plusHours(3)),
                Timeline.of(FRI, time, time.plusHours(3))
        );
        final Mentor mentor = MENTOR_1.toDomainWithMentoringInfo(mentoringPeriod, timelines).apply(2L);

        // when - then
        final LocalDateTime target1 = LocalDateTime.of(2024, 2, 5, 18, 0);
        final LocalDateTime target2 = LocalDateTime.of(2024, 2, 5, 18, 30);
        final LocalDateTime target3 = LocalDateTime.of(2024, 2, 5, 18, 50);
        final LocalDateTime target4 = LocalDateTime.of(2024, 2, 5, 19, 30);
        final LocalDateTime target5 = LocalDateTime.of(2024, 2, 5, 21, 50);
        final LocalDateTime target6 = LocalDateTime.of(2024, 2, 5, 22, 0);
        final LocalDateTime target7 = LocalDateTime.of(2024, 2, 5, 22, 30);

        assertAll(
                () -> assertThatThrownBy(() -> sut.check(mentor, Reservation.of(target1, target1.plusMinutes(30))))
                        .isInstanceOf(MemberException.class)
                        .hasMessage(CANNOT_RESERVATION.getMessage()),
                () -> assertThatThrownBy(() -> sut.check(mentor, Reservation.of(target2, target2.plusMinutes(30))))
                        .isInstanceOf(MemberException.class)
                        .hasMessage(CANNOT_RESERVATION.getMessage()),
                () -> assertThatThrownBy(() -> sut.check(mentor, Reservation.of(target3, target3.plusMinutes(30))))
                        .isInstanceOf(MemberException.class)
                        .hasMessage(CANNOT_RESERVATION.getMessage()),
                () -> assertThatThrownBy(() -> sut.check(mentor, Reservation.of(target4, target4.plusMinutes(30))))
                        .isInstanceOf(MemberException.class)
                        .hasMessage(CANNOT_RESERVATION.getMessage()),
                () -> assertThatThrownBy(() -> sut.check(mentor, Reservation.of(target5, target5.plusMinutes(30))))
                        .isInstanceOf(MemberException.class)
                        .hasMessage(CANNOT_RESERVATION.getMessage()),
                () -> assertThatThrownBy(() -> sut.check(mentor, Reservation.of(target6, target6.plusMinutes(30))))
                        .isInstanceOf(MemberException.class)
                        .hasMessage(CANNOT_RESERVATION.getMessage()),
                () -> assertThatThrownBy(() -> sut.check(mentor, Reservation.of(target7, target7.plusMinutes(30))))
                        .isInstanceOf(MemberException.class)
                        .hasMessage(CANNOT_RESERVATION.getMessage()),
                () -> verify(mentorReservedScheduleQueryRepository, times(0)).fetchReservedCoffeeChat(mentor.getId(), 2024, 2)
        );
    }

    @Test
    @DisplayName("멘토링 진행 시간이 멘토가 정한 TimeUnit이랑 일치하지 않으면 예외가 발생한다")
    void throwExceptionByNotAllowedTimeUnit() {
        // given
        final MentoringPeriod mentoringPeriod = MentoringPeriod.of(
                LocalDate.of(2024, 2, 1),
                LocalDate.of(2024, 3, 1)
        );
        final LocalTime time = LocalTime.of(19, 0);
        final List<Timeline> timelines = List.of(
                Timeline.of(TUE, time, time.plusHours(3)),
                Timeline.of(WED, time, time.plusHours(3)),
                Timeline.of(THU, time, time.plusHours(3)),
                Timeline.of(FRI, time, time.plusHours(3))
        );
        final Mentor mentor = MENTOR_1.toDomainWithMentoringInfo(mentoringPeriod, timelines).apply(2L);

        // when - then
        final LocalDateTime start = LocalDateTime.of(2024, 2, 5, 18, 0);

        assertAll(
                () -> assertThatThrownBy(() -> sut.check(mentor, Reservation.of(start, start.plusMinutes(10))))
                        .isInstanceOf(MemberException.class)
                        .hasMessage(CANNOT_RESERVATION.getMessage()),
                () -> assertThatThrownBy(() -> sut.check(mentor, Reservation.of(start, start.plusMinutes(20))))
                        .isInstanceOf(MemberException.class)
                        .hasMessage(CANNOT_RESERVATION.getMessage()),
                () -> assertThatThrownBy(() -> sut.check(mentor, Reservation.of(start, start.plusMinutes(29))))
                        .isInstanceOf(MemberException.class)
                        .hasMessage(CANNOT_RESERVATION.getMessage()),
                () -> assertThatThrownBy(() -> sut.check(mentor, Reservation.of(start, start.plusMinutes(31))))
                        .isInstanceOf(MemberException.class)
                        .hasMessage(CANNOT_RESERVATION.getMessage()),
                () -> assertThatThrownBy(() -> sut.check(mentor, Reservation.of(start, start.plusMinutes(40))))
                        .isInstanceOf(MemberException.class)
                        .hasMessage(CANNOT_RESERVATION.getMessage()),
                () -> verify(mentorReservedScheduleQueryRepository, times(0)).fetchReservedCoffeeChat(mentor.getId(), 2024, 2)
        );
    }

    @Test
    @DisplayName("이미 예약된 CoffeeChat 시간대와 겹치면 예외가 발생한다")
    void throwExceptionByCoffeeChatReservationTimeIncluded() {
        // given
        final MentoringPeriod mentoringPeriod = MentoringPeriod.of(
                LocalDate.of(2024, 2, 1),
                LocalDate.of(2024, 3, 1)
        );
        final LocalTime time = LocalTime.of(13, 0);
        final List<Timeline> timelines = List.of(
                Timeline.of(TUE, time, time.plusHours(9)),
                Timeline.of(WED, time, time.plusHours(9)),
                Timeline.of(THU, time, time.plusHours(9))
        );
        final Mentor mentor = MENTOR_1.toDomainWithMentoringInfo(mentoringPeriod, timelines).apply(2L);

        final LocalDateTime start1 = LocalDateTime.of(2024, 2, 6, 18, 0);
        final LocalDateTime start2 = LocalDateTime.of(2024, 2, 7, 18, 0);
        final LocalDateTime start3 = LocalDateTime.of(2024, 2, 8, 18, 0);
        final CoffeeChat coffeeChat1 = MenteeFlow.apply(start1, start1.plusHours(2), mentee, mentor).apply(1L);
        final CoffeeChat coffeeChat2 = MenteeFlow.apply(start2, start2.plusHours(2), mentee, mentor).apply(2L);
        final CoffeeChat coffeeChat3 = MenteeFlow.apply(start3, start3.plusHours(2), mentee, mentor).apply(3L);
        given(mentorReservedScheduleQueryRepository.fetchReservedCoffeeChat(mentor.getId(), 2024, 2)).willReturn(List.of(coffeeChat1, coffeeChat2, coffeeChat3));

        // when - then
        final LocalDateTime target1 = LocalDateTime.of(2024, 2, 6, 17, 50);
        final LocalDateTime target2 = LocalDateTime.of(2024, 2, 6, 18, 0);
        final LocalDateTime target3 = LocalDateTime.of(2024, 2, 6, 19, 50);
        final LocalDateTime target4 = LocalDateTime.of(2024, 2, 7, 17, 50);
        final LocalDateTime target5 = LocalDateTime.of(2024, 2, 7, 18, 0);
        final LocalDateTime target6 = LocalDateTime.of(2024, 2, 7, 19, 50);
        final LocalDateTime target7 = LocalDateTime.of(2024, 2, 8, 17, 50);
        final LocalDateTime target8 = LocalDateTime.of(2024, 2, 8, 18, 0);
        final LocalDateTime target9 = LocalDateTime.of(2024, 2, 8, 19, 50);

        assertAll(
                () -> assertThatThrownBy(() -> sut.check(mentor, Reservation.of(target1, target1.plusMinutes(30))))
                        .isInstanceOf(MemberException.class)
                        .hasMessage(CANNOT_RESERVATION.getMessage()),
                () -> assertThatThrownBy(() -> sut.check(mentor, Reservation.of(target2, target2.plusMinutes(30))))
                        .isInstanceOf(MemberException.class)
                        .hasMessage(CANNOT_RESERVATION.getMessage()),
                () -> assertThatThrownBy(() -> sut.check(mentor, Reservation.of(target3, target3.plusMinutes(30))))
                        .isInstanceOf(MemberException.class)
                        .hasMessage(CANNOT_RESERVATION.getMessage()),
                () -> assertThatThrownBy(() -> sut.check(mentor, Reservation.of(target4, target4.plusMinutes(30))))
                        .isInstanceOf(MemberException.class)
                        .hasMessage(CANNOT_RESERVATION.getMessage()),
                () -> assertThatThrownBy(() -> sut.check(mentor, Reservation.of(target5, target5.plusMinutes(30))))
                        .isInstanceOf(MemberException.class)
                        .hasMessage(CANNOT_RESERVATION.getMessage()),
                () -> assertThatThrownBy(() -> sut.check(mentor, Reservation.of(target6, target6.plusMinutes(30))))
                        .isInstanceOf(MemberException.class)
                        .hasMessage(CANNOT_RESERVATION.getMessage()),
                () -> assertThatThrownBy(() -> sut.check(mentor, Reservation.of(target7, target7.plusMinutes(30))))
                        .isInstanceOf(MemberException.class)
                        .hasMessage(CANNOT_RESERVATION.getMessage()),
                () -> assertThatThrownBy(() -> sut.check(mentor, Reservation.of(target8, target8.plusMinutes(30))))
                        .isInstanceOf(MemberException.class)
                        .hasMessage(CANNOT_RESERVATION.getMessage()),
                () -> assertThatThrownBy(() -> sut.check(mentor, Reservation.of(target9, target9.plusMinutes(30))))
                        .isInstanceOf(MemberException.class)
                        .hasMessage(CANNOT_RESERVATION.getMessage())
        );
    }

    @Test
    @DisplayName("멘토 스케줄 예약 관련 검증을 통과한다")
    void success() {
        // given
        final MentoringPeriod mentoringPeriod = MentoringPeriod.of(
                LocalDate.of(2024, 2, 1),
                LocalDate.of(2024, 3, 1)
        );
        final LocalTime time = LocalTime.of(13, 0);
        final List<Timeline> timelines = List.of(
                Timeline.of(TUE, time, time.plusHours(9)),
                Timeline.of(WED, time, time.plusHours(9)),
                Timeline.of(THU, time, time.plusHours(9))
        );
        final Mentor mentor = MENTOR_1.toDomainWithMentoringInfo(mentoringPeriod, timelines).apply(2L);

        final LocalDateTime start1 = LocalDateTime.of(2024, 2, 6, 18, 0);
        final LocalDateTime start2 = LocalDateTime.of(2024, 2, 7, 18, 0);
        final LocalDateTime start3 = LocalDateTime.of(2024, 2, 8, 18, 0);
        final CoffeeChat coffeeChat1 = MenteeFlow.apply(start1, start1.plusMinutes(30), mentee, mentor).apply(1L);
        final CoffeeChat coffeeChat2 = MenteeFlow.apply(start2, start2.plusMinutes(30), mentee, mentor).apply(2L);
        final CoffeeChat coffeeChat3 = MenteeFlow.apply(start3, start3.plusMinutes(30), mentee, mentor).apply(3L);
        given(mentorReservedScheduleQueryRepository.fetchReservedCoffeeChat(mentor.getId(), 2024, 2)).willReturn(List.of(coffeeChat1, coffeeChat2, coffeeChat3));

        // when - then
        final LocalDateTime target1 = LocalDateTime.of(2024, 2, 6, 17, 0);
        final LocalDateTime target2 = LocalDateTime.of(2024, 2, 6, 17, 30);
        final LocalDateTime target3 = LocalDateTime.of(2024, 2, 6, 18, 30);
        final LocalDateTime target4 = LocalDateTime.of(2024, 2, 7, 17, 0);
        final LocalDateTime target5 = LocalDateTime.of(2024, 2, 7, 17, 30);
        final LocalDateTime target6 = LocalDateTime.of(2024, 2, 7, 18, 30);
        final LocalDateTime target7 = LocalDateTime.of(2024, 2, 8, 17, 0);
        final LocalDateTime target8 = LocalDateTime.of(2024, 2, 8, 17, 30);
        final LocalDateTime target9 = LocalDateTime.of(2024, 2, 8, 18, 30);

        assertAll(
                () -> assertDoesNotThrow(() -> sut.check(mentor, Reservation.of(target1, target1.plusMinutes(30)))),
                () -> assertDoesNotThrow(() -> sut.check(mentor, Reservation.of(target2, target2.plusMinutes(30)))),
                () -> assertDoesNotThrow(() -> sut.check(mentor, Reservation.of(target3, target3.plusMinutes(30)))),
                () -> assertDoesNotThrow(() -> sut.check(mentor, Reservation.of(target4, target4.plusMinutes(30)))),
                () -> assertDoesNotThrow(() -> sut.check(mentor, Reservation.of(target5, target5.plusMinutes(30)))),
                () -> assertDoesNotThrow(() -> sut.check(mentor, Reservation.of(target6, target6.plusMinutes(30)))),
                () -> assertDoesNotThrow(() -> sut.check(mentor, Reservation.of(target7, target7.plusMinutes(30)))),
                () -> assertDoesNotThrow(() -> sut.check(mentor, Reservation.of(target8, target8.plusMinutes(30)))),
                () -> assertDoesNotThrow(() -> sut.check(mentor, Reservation.of(target9, target9.plusMinutes(30))))
        );
    }
}
