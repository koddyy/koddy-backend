package com.koddy.server.coffeechat.domain.model;

import com.koddy.server.coffeechat.exception.CoffeeChatException;
import com.koddy.server.common.UnitTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static com.koddy.server.coffeechat.exception.CoffeeChatExceptionCode.RESERVATION_INFO_MUST_EXISTS;
import static com.koddy.server.coffeechat.exception.CoffeeChatExceptionCode.RESERVATION_MUST_ALIGN;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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
}
