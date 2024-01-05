package com.koddy.server.global.utils;

import com.koddy.server.common.ParallelTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Global -> DateTimeUtils 테스트")
class DateTimeUtilsTest extends ParallelTest {
    @Test
    @DisplayName("시작/종료 시간 차이를 Minute 기준으로 응답한다")
    void calculateDurationByMinutes() {
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
        final long duration = DateTimeUtils.calculateDurationByMinutes(start, end);

        // then
        assertThat(duration).isEqualTo(105); // 60 X 1 + 45
    }

    @Test
    @DisplayName("KST 시간을 UTC 시간으로 변환한다")
    void kstToUtc() {
        // given
        final ZonedDateTime kst = ZonedDateTime.of(
                LocalDateTime.of(2024, 1, 5, 15, 0),
                ZoneId.of("Asia/Seoul")
        );

        // when
        final ZonedDateTime utc = DateTimeUtils.kstToUtc(kst);

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
