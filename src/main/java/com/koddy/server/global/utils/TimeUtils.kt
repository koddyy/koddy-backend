package com.koddy.server.global.utils

import com.koddy.server.global.exception.GlobalException
import com.koddy.server.global.exception.GlobalExceptionCode
import java.time.DateTimeException
import java.time.LocalDate
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

    fun toLocalTime(value: String): LocalTime {
        try {
            return LocalTime.parse(value, timeFormatter)
        } catch (e: DateTimeException) {
            throw GlobalException(GlobalExceptionCode.INVALID_TIME_DATA)
        }
    }

    fun toLocalDateTime(value: String): LocalDateTime {
        try {
            return LocalDateTime.parse(value, dateTimeFormatter)
        } catch (e: DateTimeException) {
            throw GlobalException(GlobalExceptionCode.INVALID_TIME_DATA)
        }
    }

    fun calculateDurationByMinutes(
        start: LocalDateTime,
        end: LocalDateTime,
    ): Long = ChronoUnit.MINUTES.between(start, end).absoluteValue

    fun kstToUtc(kst: LocalDateTime): LocalDateTime {
        return ZonedDateTime.of(kst, kstZoneId)
            .withZoneSameInstant(utcZoneId)
            .toLocalDateTime()
    }

    fun utcToKst(utc: LocalDateTime): LocalDateTime {
        return ZonedDateTime.of(utc, utcZoneId)
            .withZoneSameInstant(kstZoneId)
            .toLocalDateTime()
    }

    /**
     * targetA < targetB
     */
    fun isLower(
        targetA: LocalDate,
        targetB: LocalDate,
    ): Boolean = targetA < targetB

    /**
     * targetA <= targetB
     */
    @JvmStatic
    fun isLowerOrEqual(
        targetA: LocalDate,
        targetB: LocalDate,
    ): Boolean = targetA <= targetB

    /**
     * targetA > targetB
     */
    @JvmStatic
    fun isGreator(
        targetA: LocalDate,
        targetB: LocalDate,
    ): Boolean = targetA > targetB

    /**
     * targetA >= targetB
     */
    fun isGreatorOrEqual(
        targetA: LocalDate,
        targetB: LocalDate,
    ): Boolean = targetA >= targetB

    /**
     * targetA < targetB
     */
    fun isLower(
        targetA: LocalTime,
        targetB: LocalTime,
    ): Boolean = targetA < targetB

    /**
     * targetA <= targetB
     */
    @JvmStatic
    fun isLowerOrEqual(
        targetA: LocalTime,
        targetB: LocalTime,
    ): Boolean = targetA <= targetB

    /**
     * targetA > targetB
     */
    @JvmStatic
    fun isGreator(
        targetA: LocalTime,
        targetB: LocalTime,
    ): Boolean = targetA > targetB

    /**
     * targetA >= targetB
     */
    fun isGreatorOrEqual(
        targetA: LocalTime,
        targetB: LocalTime,
    ): Boolean = targetA >= targetB

    /**
     * targetA < targetB
     */
    fun isLower(
        targetA: LocalDateTime,
        targetB: LocalDateTime,
    ): Boolean = targetA < targetB

    /**
     * targetA <= targetB
     */
    fun isLowerOrEqual(
        targetA: LocalDateTime,
        targetB: LocalDateTime,
    ): Boolean = targetA <= targetB

    /**
     * targetA > targetB
     */
    fun isGreator(
        targetA: LocalDateTime,
        targetB: LocalDateTime,
    ): Boolean = targetA > targetB

    /**
     * targetA >= targetB
     */
    fun isGreatorOrEqual(
        targetA: LocalDateTime,
        targetB: LocalDateTime,
    ): Boolean = targetA >= targetB
}
