package com.koddy.server.member.domain.model.mentor;

import com.koddy.server.common.ParallelTest;
import com.koddy.server.member.exception.MemberException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import static com.koddy.server.member.domain.model.mentor.DayOfWeek.FRI;
import static com.koddy.server.member.domain.model.mentor.DayOfWeek.MON;
import static com.koddy.server.member.domain.model.mentor.DayOfWeek.SAT;
import static com.koddy.server.member.domain.model.mentor.DayOfWeek.SUN;
import static com.koddy.server.member.domain.model.mentor.DayOfWeek.THU;
import static com.koddy.server.member.domain.model.mentor.DayOfWeek.TUE;
import static com.koddy.server.member.domain.model.mentor.DayOfWeek.WED;
import static com.koddy.server.member.exception.MemberExceptionCode.INVALID_DAY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Member/Mentor -> 도메인 [DayOfWeek] 테스트")
class DayOfWeekTest extends ParallelTest {
    @Test
    @DisplayName("kor 정보를 토대로 DayOfWeek을 가져온다")
    void from() {
        assertAll(
                () -> assertThat(DayOfWeek.from("월")).isEqualTo(MON),
                () -> assertThat(DayOfWeek.from("화")).isEqualTo(TUE),
                () -> assertThat(DayOfWeek.from("수")).isEqualTo(WED),
                () -> assertThat(DayOfWeek.from("목")).isEqualTo(THU),
                () -> assertThat(DayOfWeek.from("금")).isEqualTo(FRI),
                () -> assertThat(DayOfWeek.from("토")).isEqualTo(SAT),
                () -> assertThat(DayOfWeek.from("일")).isEqualTo(SUN),
                () -> assertThatThrownBy(() -> DayOfWeek.from("anonymous"))
                        .isInstanceOf(MemberException.class)
                        .hasMessage(INVALID_DAY.getMessage())
        );
    }

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
    @DisplayName("Year/Month/Day 정보를 기준으로 DayOfWeek을 가져온다")
    void getDay(final int year, final int month, final int day, final DayOfWeek expected) {
        assertThat(DayOfWeek.of(year, month, day)).isEqualTo(expected);
    }
}
