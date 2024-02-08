package com.koddy.server.coffeechat.domain.model;

import com.koddy.server.coffeechat.exception.CoffeeChatException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

import static com.koddy.server.coffeechat.exception.CoffeeChatExceptionCode.RESERVATION_INFO_MUST_EXISTS;
import static com.koddy.server.coffeechat.exception.CoffeeChatExceptionCode.RESERVATION_MUST_ALIGN;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Embeddable
public class Reservation {
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
        if (start.isAfter(end)) {
            throw new CoffeeChatException(RESERVATION_MUST_ALIGN);
        }
    }

    public boolean isDateTimeIncluded(final LocalDateTime target) {
        return !target.isBefore(start) && target.isBefore(end);
    }
}
