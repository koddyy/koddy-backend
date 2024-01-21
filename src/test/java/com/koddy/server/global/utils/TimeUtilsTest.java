package com.koddy.server.global.utils;

import com.koddy.server.common.UnitTest;
import com.koddy.server.global.exception.GlobalException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static com.koddy.server.global.exception.GlobalExceptionCode.INVALID_TIME_DATA;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Global -> TimeUtils 테스트")
class TimeUtilsTest extends UnitTest {
    @Nested
    @DisplayName("String -> LocalTime 변환")
    class ToLocalTime {
        @Test
        @DisplayName("시간 정보가 00:00:00 ~ 23:59:59 범위가 아니면 예외가 발생한다")
        void throwExceptionByInvalidTimeData() {
            assertThatThrownBy(() -> TimeUtils.toLocalTime("24:00:00"))
                    .isInstanceOf(GlobalException.class)
                    .hasMessage(INVALID_TIME_DATA.getMessage());
        }

        @Test
        @DisplayName("시간 정보를 변환한다 (LocalTime)")
        void success() {
            assertAll(
                    () -> assertThat(TimeUtils.toLocalTime("00:00:00")).isEqualTo(LocalTime.of(0, 0, 0)),
                    () -> assertThat(TimeUtils.toLocalTime("23:59:59")).isEqualTo(LocalTime.of(23, 59, 59))
            );
        }
    }

    @Nested
    @DisplayName("String -> LocalDateTime 변환")
    class ToLocalDateTime {
        @Test
        @DisplayName("시간 정보가 00:00:00 ~ 23:59:59 범위가 아니면 예외가 발생한다")
        void throwExceptionByInvalidTimeData() {
            assertThatThrownBy(() -> TimeUtils.toLocalDateTime("2024-02-01T24:00:00"))
                    .isInstanceOf(GlobalException.class)
                    .hasMessage(INVALID_TIME_DATA.getMessage());
        }

        @Test
        @DisplayName("시간 정보를 변환한다 (LocalDateTime)")
        void success() {
            assertAll(
                    () -> assertThat(TimeUtils.toLocalDateTime("2024-02-01T00:00:00")).isEqualTo(LocalDateTime.of(2024, 2, 1, 0, 0)),
                    () -> assertThat(TimeUtils.toLocalDateTime("2024-02-01T23:59:59")).isEqualTo(LocalDateTime.of(2024, 2, 1, 23, 59, 59))
            );
        }
    }

    @Test
    @DisplayName("시작/종료 시간 차이를 Minute 기준으로 응답한다 (ZonedDateTime)")
    void calculateDurationByMinutes1() {
        // given
        final ZonedDateTime start = ZonedDateTime.of(
                LocalDateTime.of(2024, 1, 5, 15, 0),
                ZoneId.of("Asia/Seoul")
        );

        final ZonedDateTime end = ZonedDateTime.of(
                LocalDateTime.of(2024, 1, 5, 16, 45),
                ZoneId.of("Asia/Seoul")
        );

        // when
        final long duration = TimeUtils.calculateDurationByMinutes(start, end);

        // then
        assertThat(duration).isEqualTo(105); // 60 X 1 + 45
    }

    @Test
    @DisplayName("시작/종료 시간 차이를 Minute 기준으로 응답한다 (LocalDateTime)")
    void calculateDurationByMinutes2() {
        // given
        final LocalDateTime start = LocalDateTime.of(2024, 1, 5, 15, 0);
        final LocalDateTime end = LocalDateTime.of(2024, 1, 5, 16, 45);

        // when
        final long duration = TimeUtils.calculateDurationByMinutes(start, end);

        // then
        assertThat(duration).isEqualTo(105); // 60 X 1 + 45
    }

    @Test
    @DisplayName("KST 시간을 UTC 시간으로 변환한다 (ZonedDateTime)")
    void kstToUtc1() {
        // given
        final ZonedDateTime kst = ZonedDateTime.of(
                LocalDateTime.of(2024, 1, 5, 15, 0),
                ZoneId.of("Asia/Seoul")
        );

        // when
        final ZonedDateTime utc = TimeUtils.kstToUtc(kst);

        // then
        assertAll(
                () -> assertThat(utc.getYear()).isEqualTo(2024),
                () -> assertThat(utc.getMonthValue()).isEqualTo(1),
                () -> assertThat(utc.getDayOfMonth()).isEqualTo(5),
                () -> assertThat(utc.getHour()).isEqualTo(15 - 9),
                () -> assertThat(utc.getMinute()).isEqualTo(0)
        );
    }

    @Test
    @DisplayName("KST 시간을 UTC 시간으로 변환한다 (LocalDateTime)")
    void kstToUtc2() {
        // given
        final LocalDateTime kst = LocalDateTime.of(2024, 1, 5, 15, 0);

        // when
        final LocalDateTime utc = TimeUtils.kstToUtc(kst);

        // then
        assertAll(
                () -> assertThat(utc.getYear()).isEqualTo(2024),
                () -> assertThat(utc.getMonthValue()).isEqualTo(1),
                () -> assertThat(utc.getDayOfMonth()).isEqualTo(5),
                () -> assertThat(utc.getHour()).isEqualTo(15 - 9),
                () -> assertThat(utc.getMinute()).isEqualTo(0)
        );
    }
}
