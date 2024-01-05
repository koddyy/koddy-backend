package com.koddy.server.global.utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.temporal.ChronoUnit;

public class DateTimeUtils {
    public static long calculateDurationByMinutes(final ZonedDateTime start, final ZonedDateTime end) {
        return ChronoUnit.MINUTES.between(start, end);
    }

    public static long calculateDurationByMinutes(final LocalDateTime start, final LocalDateTime end) {
        return ChronoUnit.MINUTES.between(start, end);
    }

    public static ZonedDateTime kstToUtc(final ZonedDateTime kst) {
        return kst.withZoneSameInstant(ZoneId.of("UTC"));
    }

    public static LocalDateTime kstToUtc(final LocalDateTime kst) {
        return ZonedDateTime.of(kst, ZoneId.of("Asia/Seoul"))
                .withZoneSameInstant(ZoneId.of("UTC"))
                .toLocalDateTime();
    }
}
