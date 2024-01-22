package com.koddy.server.coffeechat.domain.model;

import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Embeddable
public class Reservation {
    private int year;
    private int month;
    private int day;
    private LocalTime time;

    public Reservation(final LocalDateTime target) {
        this.year = target.getYear();
        this.month = target.getMonthValue();
        this.day = target.getDayOfMonth();
        this.time = target.toLocalTime();
    }

    public LocalDate toLocalDate() {
        return LocalDate.of(year, month, day);
    }

    public LocalDateTime toLocalDateTime() {
        return LocalDateTime.of(year, month, day, time.getHour(), time.getMinute());
    }

    public boolean isSameDate(final Reservation compare) {
        return this.year == compare.getYear()
                && this.month == compare.getMonth()
                && this.day == compare.getDay();
    }
}
