package com.koddy.server.coffeechat.domain.model;

import com.koddy.server.coffeechat.exception.CoffeeChatException;
import com.koddy.server.global.utils.TimeUtils;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.time.LocalDateTime;

import static com.koddy.server.coffeechat.exception.CoffeeChatExceptionCode.RESERVATION_INFO_MUST_EXISTS;
import static com.koddy.server.coffeechat.exception.CoffeeChatExceptionCode.RESERVATION_MUST_ALIGN;

@Embeddable
public class Reservation {
    protected Reservation() {
    }

    @Column(name = "start")
    private LocalDateTime start;

    @Column(name = "end")
    private LocalDateTime end;

    private Reservation(final LocalDateTime start, final LocalDateTime end) {
        this.start = start;
        this.end = end;
    }

    public static Reservation of(final LocalDateTime start, final LocalDateTime end) {
        validateDateTimeExists(start, end);
        validateStartIsBeforeEnd(start, end);
        return new Reservation(start, end);
    }

    private static void validateDateTimeExists(final LocalDateTime start, final LocalDateTime end) {
        if (start == null || end == null) {
            throw new CoffeeChatException(RESERVATION_INFO_MUST_EXISTS);
        }
    }

    private static void validateStartIsBeforeEnd(final LocalDateTime start, final LocalDateTime end) {
        if (TimeUtils.isGreator(start, end)) {
            throw new CoffeeChatException(RESERVATION_MUST_ALIGN);
        }
    }

    /**
     * 1. target.end가 진행중인 시간에 포함 <br>
     * -> (target.start < this.start) && (this.start < target.end <= this.end) <br><br>
     * <p>
     * 2. target's start, end가 진행중인 시간에 포함 <br>
     * -> (this.start <= target.start <= this.end) && (this.start <= target.end <= this.end) <br><br>
     * <p>
     * 3. target.start가 진행중인 시간에 포함 <br>
     * ->  (this.start <= target.start < this.end) && (this.end < target.end) <br><br>
     * <p>
     * 4. target's start, end가 진행중인 시간대 전역을 커버 <br>
     * -> (target.start <= this.start) && (this.end <= target.end)
     */
    public boolean isDateTimeIncluded(final Reservation target) {
        if (TimeUtils.isLower(target.start, this.start) && TimeUtils.isLower(this.start, target.end) && TimeUtils.isLowerOrEqual(target.end, this.end)) {
            return true;
        }

        if (isBetween(target.start) && isBetween(target.end)) {
            return true;
        }

        if (TimeUtils.isLowerOrEqual(this.start, target.start) && TimeUtils.isLower(target.start, this.end) && TimeUtils.isLower(this.end, target.end)) {
            return true;
        }

        return TimeUtils.isLowerOrEqual(target.start, this.start) && TimeUtils.isLowerOrEqual(this.end, target.end);
    }

    private boolean isBetween(final LocalDateTime target) {
        return TimeUtils.isLowerOrEqual(this.start, target) && TimeUtils.isLowerOrEqual(target, this.end);
    }

    public LocalDateTime getStart() {
        return start;
    }

    public LocalDateTime getEnd() {
        return end;
    }
}
