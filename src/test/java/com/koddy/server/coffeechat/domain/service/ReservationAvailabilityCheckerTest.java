package com.koddy.server.coffeechat.domain.service;

import com.koddy.server.coffeechat.domain.model.CoffeeChat;
import com.koddy.server.coffeechat.domain.model.Reservation;
import com.koddy.server.coffeechat.domain.repository.CoffeeChatRepository;
import com.koddy.server.common.UnitTest;
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
    private final CoffeeChatRepository coffeeChatRepository = mock(CoffeeChatRepository.class);
    private final ReservationAvailabilityChecker sut = new ReservationAvailabilityChecker(coffeeChatRepository);

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
                () -> assertThatThrownBy(() -> sut.check(mentor, new Reservation(target1), new Reservation(target1.plusMinutes(30))))
                        .isInstanceOf(MemberException.class)
                        .hasMessage(CANNOT_RESERVATION.getMessage()),
                () -> assertThatThrownBy(() -> sut.check(mentor, new Reservation(target2), new Reservation(target2.plusMinutes(30))))
                        .isInstanceOf(MemberException.class)
                        .hasMessage(CANNOT_RESERVATION.getMessage()),
                () -> verify(coffeeChatRepository, times(0)).getReservedCoffeeChat(mentor.getId(), 2024, 2)
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
                () -> assertThatThrownBy(() -> sut.check(mentor, new Reservation(target1), new Reservation(target1.plusMinutes(30))))
                        .isInstanceOf(MemberException.class)
                        .hasMessage(CANNOT_RESERVATION.getMessage()),
                () -> assertThatThrownBy(() -> sut.check(mentor, new Reservation(target2), new Reservation(target2.plusMinutes(30))))
                        .isInstanceOf(MemberException.class)
                        .hasMessage(CANNOT_RESERVATION.getMessage()),
                () -> assertThatThrownBy(() -> sut.check(mentor, new Reservation(target3), new Reservation(target3.plusMinutes(30))))
                        .isInstanceOf(MemberException.class)
                        .hasMessage(CANNOT_RESERVATION.getMessage()),
                () -> assertThatThrownBy(() -> sut.check(mentor, new Reservation(target4), new Reservation(target4.plusMinutes(30))))
                        .isInstanceOf(MemberException.class)
                        .hasMessage(CANNOT_RESERVATION.getMessage()),
                () -> assertThatThrownBy(() -> sut.check(mentor, new Reservation(target5), new Reservation(target5.plusMinutes(30))))
                        .isInstanceOf(MemberException.class)
                        .hasMessage(CANNOT_RESERVATION.getMessage()),
                () -> assertThatThrownBy(() -> sut.check(mentor, new Reservation(target6), new Reservation(target6.plusMinutes(30))))
                        .isInstanceOf(MemberException.class)
                        .hasMessage(CANNOT_RESERVATION.getMessage()),
                () -> assertThatThrownBy(() -> sut.check(mentor, new Reservation(target7), new Reservation(target7.plusMinutes(30))))
                        .isInstanceOf(MemberException.class)
                        .hasMessage(CANNOT_RESERVATION.getMessage()),
                () -> verify(coffeeChatRepository, times(0)).getReservedCoffeeChat(mentor.getId(), 2024, 2)
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
        final CoffeeChat coffeeChat1 = CoffeeChat.applyCoffeeChat(mentee, mentor, "신청..", new Reservation(start1), new Reservation(start1.plusHours(2))).apply(1L);
        final CoffeeChat coffeeChat2 = CoffeeChat.applyCoffeeChat(mentee, mentor, "신청..", new Reservation(start2), new Reservation(start2.plusHours(2))).apply(2L);
        final CoffeeChat coffeeChat3 = CoffeeChat.applyCoffeeChat(mentee, mentor, "신청..", new Reservation(start3), new Reservation(start3.plusHours(2))).apply(3L);
        given(coffeeChatRepository.getReservedCoffeeChat(mentor.getId(), 2024, 2)).willReturn(List.of(coffeeChat1, coffeeChat2, coffeeChat3));

        // when - then
        final LocalDateTime target1 = LocalDateTime.of(2024, 2, 6, 17, 30);
        final LocalDateTime target2 = LocalDateTime.of(2024, 2, 6, 18, 0);
        final LocalDateTime target3 = LocalDateTime.of(2024, 2, 6, 19, 50);
        final LocalDateTime target4 = LocalDateTime.of(2024, 2, 7, 17, 30);
        final LocalDateTime target5 = LocalDateTime.of(2024, 2, 7, 18, 0);
        final LocalDateTime target6 = LocalDateTime.of(2024, 2, 7, 19, 50);
        final LocalDateTime target7 = LocalDateTime.of(2024, 2, 8, 17, 30);
        final LocalDateTime target8 = LocalDateTime.of(2024, 2, 8, 18, 0);
        final LocalDateTime target9 = LocalDateTime.of(2024, 2, 8, 19, 50);

        assertAll(
                () -> assertThatThrownBy(() -> sut.check(mentor, new Reservation(target1), new Reservation(target1.plusMinutes(30))))
                        .isInstanceOf(MemberException.class)
                        .hasMessage(CANNOT_RESERVATION.getMessage()),
                () -> assertThatThrownBy(() -> sut.check(mentor, new Reservation(target2), new Reservation(target2.plusMinutes(30))))
                        .isInstanceOf(MemberException.class)
                        .hasMessage(CANNOT_RESERVATION.getMessage()),
                () -> assertThatThrownBy(() -> sut.check(mentor, new Reservation(target3), new Reservation(target3.plusMinutes(30))))
                        .isInstanceOf(MemberException.class)
                        .hasMessage(CANNOT_RESERVATION.getMessage()),
                () -> assertThatThrownBy(() -> sut.check(mentor, new Reservation(target4), new Reservation(target4.plusMinutes(30))))
                        .isInstanceOf(MemberException.class)
                        .hasMessage(CANNOT_RESERVATION.getMessage()),
                () -> assertThatThrownBy(() -> sut.check(mentor, new Reservation(target5), new Reservation(target5.plusMinutes(30))))
                        .isInstanceOf(MemberException.class)
                        .hasMessage(CANNOT_RESERVATION.getMessage()),
                () -> assertThatThrownBy(() -> sut.check(mentor, new Reservation(target6), new Reservation(target6.plusMinutes(30))))
                        .isInstanceOf(MemberException.class)
                        .hasMessage(CANNOT_RESERVATION.getMessage()),
                () -> assertThatThrownBy(() -> sut.check(mentor, new Reservation(target7), new Reservation(target7.plusMinutes(30))))
                        .isInstanceOf(MemberException.class)
                        .hasMessage(CANNOT_RESERVATION.getMessage()),
                () -> assertThatThrownBy(() -> sut.check(mentor, new Reservation(target8), new Reservation(target8.plusMinutes(30))))
                        .isInstanceOf(MemberException.class)
                        .hasMessage(CANNOT_RESERVATION.getMessage()),
                () -> assertThatThrownBy(() -> sut.check(mentor, new Reservation(target9), new Reservation(target9.plusMinutes(30))))
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
        final CoffeeChat coffeeChat1 = CoffeeChat.applyCoffeeChat(mentee, mentor, "신청..", new Reservation(start1), new Reservation(start1.plusHours(2))).apply(1L);
        final CoffeeChat coffeeChat2 = CoffeeChat.applyCoffeeChat(mentee, mentor, "신청..", new Reservation(start2), new Reservation(start2.plusHours(2))).apply(2L);
        final CoffeeChat coffeeChat3 = CoffeeChat.applyCoffeeChat(mentee, mentor, "신청..", new Reservation(start3), new Reservation(start3.plusHours(2))).apply(3L);
        given(coffeeChatRepository.getReservedCoffeeChat(mentor.getId(), 2024, 2)).willReturn(List.of(coffeeChat1, coffeeChat2, coffeeChat3));

        // when - then
        final LocalDateTime target1 = LocalDateTime.of(2024, 2, 6, 17, 0);
        final LocalDateTime target2 = LocalDateTime.of(2024, 2, 6, 17, 29);
        final LocalDateTime target3 = LocalDateTime.of(2024, 2, 6, 20, 0);
        final LocalDateTime target4 = LocalDateTime.of(2024, 2, 7, 17, 0);
        final LocalDateTime target5 = LocalDateTime.of(2024, 2, 7, 17, 29);
        final LocalDateTime target6 = LocalDateTime.of(2024, 2, 7, 20, 0);
        final LocalDateTime target7 = LocalDateTime.of(2024, 2, 8, 17, 0);
        final LocalDateTime target8 = LocalDateTime.of(2024, 2, 8, 17, 29);
        final LocalDateTime target9 = LocalDateTime.of(2024, 2, 8, 20, 0);

        assertAll(
                () -> assertDoesNotThrow(() -> sut.check(mentor, new Reservation(target1), new Reservation(target1.plusMinutes(30)))),
                () -> assertDoesNotThrow(() -> sut.check(mentor, new Reservation(target2), new Reservation(target2.plusMinutes(30)))),
                () -> assertDoesNotThrow(() -> sut.check(mentor, new Reservation(target3), new Reservation(target3.plusMinutes(30)))),
                () -> assertDoesNotThrow(() -> sut.check(mentor, new Reservation(target4), new Reservation(target4.plusMinutes(30)))),
                () -> assertDoesNotThrow(() -> sut.check(mentor, new Reservation(target5), new Reservation(target5.plusMinutes(30)))),
                () -> assertDoesNotThrow(() -> sut.check(mentor, new Reservation(target6), new Reservation(target6.plusMinutes(30)))),
                () -> assertDoesNotThrow(() -> sut.check(mentor, new Reservation(target7), new Reservation(target7.plusMinutes(30)))),
                () -> assertDoesNotThrow(() -> sut.check(mentor, new Reservation(target8), new Reservation(target8.plusMinutes(30)))),
                () -> assertDoesNotThrow(() -> sut.check(mentor, new Reservation(target9), new Reservation(target9.plusMinutes(30))))
        );
    }
}
