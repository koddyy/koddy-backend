package com.koddy.server.member.domain.model.mentor;

import com.koddy.server.common.ParallelTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Member/Mentor -> 도메인 [Day] 테스트")
class DayTest extends ParallelTest {
    @ParameterizedTest
    @CsvSource(
            value = {
                    "2023:12:25:MON",
                    "2023:12:26:TUE",
                    "2023:12:27:WED",
                    "2023:12:28:THU",
                    "2023:12:29:FRI",
                    "2023:12:30:SAT",
                    "2023:12:31:SUN",
            },
            delimiter = ':'
    )
    @DisplayName("Year/Month/Day 정보를 기준으로 Day를 가져온다")
    void getDay(final int year, final int month, final int day, final Day expected) {
        assertThat(Day.of(year, month, day)).isEqualTo(expected);
    }
}
