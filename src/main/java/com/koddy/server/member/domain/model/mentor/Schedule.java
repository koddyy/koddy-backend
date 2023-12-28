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
public class Schedule {
    @Enumerated(STRING)
    @Column(name = "day", nullable = false, columnDefinition = "VARCHAR(20)")
    private Day day;

    @Embedded
    private Period period;

    public Schedule(final Day day, final Period period) {
        this.day = day;
        this.period = period;
    }
}
