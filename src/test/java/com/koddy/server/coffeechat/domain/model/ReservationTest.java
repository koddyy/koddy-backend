package com.koddy.server.coffeechat.domain.model;

import com.koddy.server.coffeechat.exception.CoffeeChatException;
import com.koddy.server.common.UnitTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static com.koddy.server.coffeechat.exception.CoffeeChatExceptionCode.RESERVATION_INFO_MUST_EXISTS;
import static com.koddy.server.coffeechat.exception.CoffeeChatExceptionCode.RESERVATION_MUST_ALIGN;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DisplayName("CoffeeChat -> 도메인 [Reservation] 테스트")
class ReservationTest extends UnitTest {
    private final LocalDateTime start = LocalDateTime.of(2024, 1, 1, 18, 0);
    private final LocalDateTime end = LocalDateTime.of(2024, 1, 1, 18, 30);

    @Nested
    @DisplayName("Reservation 생성")
    class Construct {
        @Test
        @DisplayName("예약 날짜가 입력되지 않으면 Reservation 생성에 실패한다")
        void throwExceptionByDateTimeNotExists() {
            assertThatThrownBy(() -> Reservation.of(start, null))
                    .isInstanceOf(CoffeeChatException.class)
                    .hasMessage(RESERVATION_INFO_MUST_EXISTS.getMessage());
        }

        @Test
        @DisplayName("예약 종료날짜가 시작날짜 이전이면 Reservation 생성에 실패한다")
        void throwExceptionByStartEndNotAlign() {
            assertThatThrownBy(() -> Reservation.of(end, start))
                    .isInstanceOf(CoffeeChatException.class)
                    .hasMessage(RESERVATION_MUST_ALIGN.getMessage());
        }

        @Test
        @DisplayName("Reservation을 생성한다")
        void construct() {
            assertDoesNotThrow(() -> Reservation.of(start, end));
        }
    }

    @Test
    @DisplayName("주어진 시간이 예약 시간에 포함되는지 확인한다")
    void isDateTimeIncluded() {
        // given
        final LocalDateTime start = LocalDateTime.of(2024, 2, 27, 16, 0);
        final LocalDateTime end = LocalDateTime.of(2024, 2, 27, 16, 30);
        final Reservation reservation = Reservation.of(start, end);

        // when
        final LocalDateTime target1 = LocalDateTime.of(2024, 2, 27, 15, 30);
        final LocalDateTime target2 = LocalDateTime.of(2024, 2, 27, 15, 50);
        final LocalDateTime target3 = LocalDateTime.of(2024, 2, 27, 16, 0);
        final LocalDateTime target4 = LocalDateTime.of(2024, 2, 27, 16, 20);
        final LocalDateTime target5 = LocalDateTime.of(2024, 2, 27, 16, 30);

        final boolean actual1 = reservation.isDateTimeIncluded(Reservation.of(target1, target1.plusMinutes(30)));
        final boolean actual2 = reservation.isDateTimeIncluded(Reservation.of(target2, target2.plusMinutes(30)));
        final boolean actual3 = reservation.isDateTimeIncluded(Reservation.of(target3, target3.plusMinutes(30)));
        final boolean actual4 = reservation.isDateTimeIncluded(Reservation.of(target4, target4.plusMinutes(30)));
        final boolean actual5 = reservation.isDateTimeIncluded(Reservation.of(target5, target5.plusMinutes(30)));

        // then
        assertAll(
                () -> assertThat(actual1).isFalse(),
                () -> assertThat(actual2).isTrue(),
                () -> assertThat(actual3).isTrue(),
                () -> assertThat(actual4).isTrue(),
                () -> assertThat(actual5).isFalse()
        );
    }
}
