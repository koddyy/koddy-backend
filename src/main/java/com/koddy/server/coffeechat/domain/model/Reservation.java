package com.koddy.server.coffeechat.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.LocalTime;

import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Embeddable
public class Reservation {
    @Column(name = "year", nullable = false)
    private int year;

    @Column(name = "month", nullable = false)
    private int month;

    @Column(name = "day", nullable = false)
    private int day;

    @Column(name = "time", nullable = false)
    private LocalTime time;

    public Reservation(final LocalDateTime target) {
        this.year = target.getYear();
        this.month = target.getMonthValue();
        this.day = target.getDayOfMonth();
        this.time = target.toLocalTime();
    }
}
