package com.koddy.server.global.utils;

import com.koddy.server.global.exception.GlobalException;

import java.time.DateTimeException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

import static com.koddy.server.global.exception.GlobalExceptionCode.INVALID_TIME_DATA;

public class TimeUtils {
    private static final DateTimeFormatter timeFormatter = DateTimeFormatter.ISO_LOCAL_TIME;
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public static LocalTime toLocalTime(final String value) {
        try {
            return LocalTime.parse(value, timeFormatter);
        } catch (final DateTimeException e) {
            throw new GlobalException(INVALID_TIME_DATA);
        }
    }

    public static LocalDateTime toLocalDateTime(final String value) {
        try {
            return LocalDateTime.parse(value, dateTimeFormatter);
        } catch (final DateTimeException e) {
            throw new GlobalException(INVALID_TIME_DATA);
        }
    }

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
