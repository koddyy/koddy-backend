package com.koddy.server.coffeechat.domain.model;

import com.koddy.server.coffeechat.exception.CoffeeChatException;
import com.koddy.server.global.base.BaseEntity;
import com.koddy.server.member.domain.model.Member;
import com.koddy.server.member.domain.model.mentee.Mentee;
import com.koddy.server.member.domain.model.mentor.Mentor;
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
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.APPROVE;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.PENDING;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.REJECT;
import static com.koddy.server.coffeechat.exception.CoffeeChatExceptionCode.CANNOT_APPROVE_STATUS;
import static com.koddy.server.coffeechat.exception.CoffeeChatExceptionCode.CANNOT_REJECT_STATUS;
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

    @Lob
    @Column(name = "reject_reason", columnDefinition = "TEXT")
    private String rejectReason;

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
            final String rejectReason,
            final CoffeeChatStatus status,
            final Reservation start,
            final Reservation end,
            final Strategy strategy
    ) {
        this.applier = applier;
        this.target = target;
        this.applyReason = applyReason;
        this.rejectReason = rejectReason;
        this.status = status;
        this.start = start;
        this.end = end;
        this.strategy = strategy;
    }

    public static CoffeeChat applyCoffeeChat(
            final Mentee mentee,
            final Mentor mentor,
            final String applyReason,
            final Reservation start,
            final Reservation end
    ) {
        return new CoffeeChat(
                mentee,
                mentor,
                applyReason,
                null,
                APPLY,
                start,
                end,
                null
        );
    }

    public static CoffeeChat suggestCoffeeChat(
            final Mentor mentor,
            final Mentee mentee,
            final String applyReason
    ) {
        return new CoffeeChat(
                mentor,
                mentee,
                applyReason,
                null,
                APPLY,
                null,
                null,
                null
        );
    }

    public void rejectFromMenteeApply(final String rejectReason) {
        if (this.status != APPLY) {
            throw new CoffeeChatException(CANNOT_REJECT_STATUS);
        }

        this.rejectReason = rejectReason;
        this.status = REJECT;
    }

    public void approveFromMenteeApply(final Strategy strategy) {
        if (this.status != APPLY) {
            throw new CoffeeChatException(CANNOT_APPROVE_STATUS);
        }

        this.strategy = strategy;
        this.status = APPROVE;
    }

    public void rejectFromMentorSuggest(final String rejectReason) {
        if (this.status != APPLY) {
            throw new CoffeeChatException(CANNOT_REJECT_STATUS);
        }

        this.rejectReason = rejectReason;
        this.status = REJECT;
    }

    public void pendingFromMentorSuggest(final Reservation start, final Reservation end) {
        if (this.status != APPLY) {
            throw new CoffeeChatException(CANNOT_APPROVE_STATUS);
        }

        this.start = start;
        this.end = end;
        this.status = PENDING;
    }
}
