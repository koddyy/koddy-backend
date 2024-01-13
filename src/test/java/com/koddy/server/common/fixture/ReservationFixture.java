package com.koddy.server.common.fixture;

import com.koddy.server.coffeechat.domain.model.Reservation;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

@Getter
@RequiredArgsConstructor
public enum ReservationFixture {
    TARGET_2024_01_01(LocalDateTime.of(2024, 1, 1, 0, 0)),
    TARGET_2024_01_15(LocalDateTime.of(2024, 1, 15, 0, 0)),
    TARGET_2024_01_31(LocalDateTime.of(2024, 1, 31, 0, 0)),

    TARGET_2024_02_01(LocalDateTime.of(2024, 2, 1, 0, 0)),
    TARGET_2024_02_15(LocalDateTime.of(2024, 2, 15, 0, 0)),
    TARGET_2024_02_25(LocalDateTime.of(2024, 2, 25, 0, 0)),

    TARGET_2024_03_01(LocalDateTime.of(2024, 3, 1, 0, 0)),
    TARGET_2024_03_15(LocalDateTime.of(2024, 3, 15, 0, 0)),
    TARGET_2024_03_31(LocalDateTime.of(2024, 3, 31, 0, 0)),
    ;

    private final LocalDateTime target;

    public Reservation toDomain() {
        return new Reservation(target);
    }
}
