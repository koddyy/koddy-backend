package com.koddy.server.coffeechat.domain.model;

import com.koddy.server.global.base.BaseEntity;
import com.koddy.server.member.domain.model.Member;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.APPLY;
import static jakarta.persistence.EnumType.STRING;
import static jakarta.persistence.FetchType.LAZY;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(name = "coffee_chat")
public class CoffeeChat extends BaseEntity<CoffeeChat> {
    @Lob
    @Column(name = "apply_reason", nullable = false, columnDefinition = "TEXT")
    private String applyReason;

    @Enumerated(STRING)
    @Column(name = "status", nullable = false, columnDefinition = "VARCHAR(30)")
    private CoffeeChatStatus status;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "year", column = @Column(name = "start_year")),
            @AttributeOverride(name = "month", column = @Column(name = "start_month")),
            @AttributeOverride(name = "day", column = @Column(name = "start_day")),
            @AttributeOverride(name = "time", column = @Column(name = "start_time"))
    })
    private Reservation start;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "year", column = @Column(name = "end_year")),
            @AttributeOverride(name = "month", column = @Column(name = "end_month")),
            @AttributeOverride(name = "day", column = @Column(name = "end_day")),
            @AttributeOverride(name = "time", column = @Column(name = "end_time"))
    })
    private Reservation end;

    @Embedded
    private Strategy strategy;

    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "applier_id", referencedColumnName = "id", nullable = false)
    private Member<?> applier;

    @ManyToOne(fetch = LAZY, optional = false)
    @JoinColumn(name = "target_id", referencedColumnName = "id", nullable = false)
    private Member<?> target;

    private CoffeeChat(
            final Member<?> applier,
            final Member<?> target,
            final String applyReason,
            final CoffeeChatStatus status,
            final Reservation start,
            final Reservation end,
            final Strategy strategy
    ) {
        this.applier = applier;
        this.target = target;
        this.applyReason = applyReason;
        this.status = status;
        this.start = start;
        this.end = end;
        this.strategy = strategy;
    }

    public static CoffeeChat applyMenteeToMentor(
            final Member<?> applier,
            final Member<?> target,
            final String applyReason,
            final Reservation start,
            final Reservation end
    ) {
        return new CoffeeChat(
                applier,
                target,
                applyReason,
                APPLY,
                start,
                end,
                null
        );
    }

    public static CoffeeChat suggestMentorToMentee(
            final Member<?> applier,
            final Member<?> target,
            final String applyReason
    ) {
        return new CoffeeChat(
                applier,
                target,
                applyReason,
                APPLY,
                null,
                null,
                null
        );
    }
}
