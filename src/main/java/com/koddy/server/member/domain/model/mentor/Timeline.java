package com.koddy.server.member.domain.model.mentor;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Embedded;
import jakarta.persistence.Enumerated;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.EnumType.STRING;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Embeddable
public class Timeline {
    @Enumerated(STRING)
    @Column(name = "day_of_week", nullable = false, columnDefinition = "VARCHAR(20)")
    private DayOfWeek dayOfWeek;

    @Embedded
    private Period period;

    public Timeline(final DayOfWeek dayOfWeek, final Period period) {
        this.dayOfWeek = dayOfWeek;
        this.period = period;
    }
}
