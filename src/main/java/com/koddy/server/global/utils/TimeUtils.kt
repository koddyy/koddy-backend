package com.koddy.server.global.utils

import com.koddy.server.global.exception.GlobalException
import com.koddy.server.global.exception.GlobalExceptionCode
import java.time.DateTimeException
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import kotlin.math.absoluteValue

object TimeUtils {
    private val timeFormatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_TIME
    private val dateTimeFormatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    private val kstZoneId: ZoneId = ZoneId.of("Asia/Seoul")
    private val utcZoneId: ZoneId = ZoneId.of("UTC")

    @JvmStatic
    fun toLocalTime(
        hour: Int,
        minute: Int,
    ): LocalTime {
        try {
            return LocalTime.of(hour, minute)
        } catch (e: DateTimeException) {
            throw GlobalException(GlobalExceptionCode.INVALID_TIME_DATA)
        }
    }

    @JvmStatic
    fun toLocalTime(value: String): LocalTime {
        try {
            return LocalTime.parse(value, timeFormatter)
        } catch (e: DateTimeException) {
            throw GlobalException(GlobalExceptionCode.INVALID_TIME_DATA)
        }
    }

    @JvmStatic
    fun toLocalDateTime(value: String): LocalDateTime {
        try {
            return LocalDateTime.parse(value, dateTimeFormatter)
        } catch (e: DateTimeException) {
            throw GlobalException(GlobalExceptionCode.INVALID_TIME_DATA)
        }
    }

    @JvmStatic
    fun calculateDurationByMinutes(
        start: LocalDateTime,
        end: LocalDateTime,
    ): Long = ChronoUnit.MINUTES.between(start, end).absoluteValue

    @JvmStatic
    fun kstToUtc(kst: LocalDateTime): LocalDateTime =
        ZonedDateTime.of(kst, kstZoneId)
            .withZoneSameInstant(utcZoneId)
            .toLocalDateTime()

    @JvmStatic
    fun utcToKst(utc: LocalDateTime): LocalDateTime =
        ZonedDateTime.of(utc, utcZoneId)
            .withZoneSameInstant(kstZoneId)
            .toLocalDateTime()
}
