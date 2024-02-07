package com.koddy.server.coffeechat.domain.model;

import com.koddy.server.coffeechat.exception.CoffeeChatException;
import com.koddy.server.global.base.BaseEntity;
import com.koddy.server.member.domain.model.mentee.Mentee;
import com.koddy.server.member.domain.model.mentor.Mentor;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTEE_APPLY;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTEE_PENDING;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTEE_REJECT;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_APPROVE;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_FINALLY_APPROVE;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_FINALLY_REJECT;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_REJECT;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_SUGGEST;
import static com.koddy.server.coffeechat.exception.CoffeeChatExceptionCode.CANNOT_APPROVE_STATUS;
import static com.koddy.server.coffeechat.exception.CoffeeChatExceptionCode.CANNOT_CANCEL_STATUS;
import static com.koddy.server.coffeechat.exception.CoffeeChatExceptionCode.CANNOT_COMPLETE_STATUS;
import static com.koddy.server.coffeechat.exception.CoffeeChatExceptionCode.CANNOT_FINALLY_DECIDE_STATUS;
import static com.koddy.server.coffeechat.exception.CoffeeChatExceptionCode.CANNOT_REJECT_STATUS;
import static jakarta.persistence.EnumType.STRING;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(name = "coffee_chat")
public class CoffeeChat extends BaseEntity<CoffeeChat> {
    @Column(name = "source_member_id", nullable = false)
    private Long sourceMemberId;

    @Column(name = "target_member_id", nullable = false)
    private Long targetMemberId;

    @Enumerated(STRING)
    @Column(name = "status", nullable = false, columnDefinition = "VARCHAR(30)")
    private CoffeeChatStatus status;

    @Lob
    @Column(name = "apply_reason", nullable = false, columnDefinition = "TEXT")
    private String applyReason;

    @Lob
    @Column(name = "question", columnDefinition = "TEXT")
    private String question;

    @Lob
    @Column(name = "reject_reason", columnDefinition = "TEXT")
    private String rejectReason;

    @Embedded
    private Reservation reservation;

    @Embedded
    private Strategy strategy;

    private CoffeeChat(
            final Long sourceMemberId,
            final Long targetMemberId,
            final String applyReason,
            final String question,
            final String rejectReason,
            final CoffeeChatStatus status,
            final Reservation reservation,
            final Strategy strategy
    ) {
        this.sourceMemberId = sourceMemberId;
        this.targetMemberId = targetMemberId;
        this.applyReason = applyReason;
        this.question = question;
        this.rejectReason = rejectReason;
        this.status = status;
        this.reservation = reservation;
        this.strategy = strategy;
    }

    public static CoffeeChat apply(
            final Mentee mentee,
            final Mentor mentor,
            final String applyReason,
            final Reservation reservation
    ) {
        return new CoffeeChat(
                mentee.getId(),
                mentor.getId(),
                applyReason,
                null,
                null,
                MENTEE_APPLY,
                reservation,
                null
        );
    }

    public static CoffeeChat suggest(final Mentor mentor, final Mentee mentee, final String applyReason) {
        return new CoffeeChat(
                mentor.getId(),
                mentee.getId(),
                applyReason,
                null,
                null,
                MENTOR_SUGGEST,
                null,
                null
        );
    }

    /**
     * 멘티 신청 커피챗 or 멘토 제안 커피챗을 취소 (Self)
     */
    public void cancel(final CoffeeChatStatus status) {
        if (this.status != MENTEE_APPLY && this.status != MENTOR_SUGGEST) {
            throw new CoffeeChatException(CANNOT_CANCEL_STATUS);
        }

        this.status = status;
    }

    /**
     * 멘티의 신청 -> 멘토가 거절
     */
    public void rejectFromMenteeApply(final String rejectReason) {
        if (this.status != MENTEE_APPLY) {
            throw new CoffeeChatException(CANNOT_REJECT_STATUS);
        }

        this.rejectReason = rejectReason;
        this.status = MENTOR_REJECT;
    }

    /**
     * 멘티의 신청 -> 멘토가 수락
     */
    public void approveFromMenteeApply(final Strategy strategy) {
        if (this.status != MENTEE_APPLY) {
            throw new CoffeeChatException(CANNOT_APPROVE_STATUS);
        }

        this.strategy = strategy;
        this.status = MENTOR_APPROVE;
    }

    /**
     * 멘토의 제안 -> 멘티가 거절
     */
    public void rejectFromMentorSuggest(final String rejectReason) {
        if (this.status != MENTOR_SUGGEST) {
            throw new CoffeeChatException(CANNOT_REJECT_STATUS);
        }

        this.rejectReason = rejectReason;
        this.status = MENTEE_REJECT;
    }

    /**
     * 멘토의 제안 -> 멘티의 1차 수락
     */
    public void pendingFromMentorSuggest(final String question, final Reservation reservation) {
        if (this.status != MENTOR_SUGGEST) {
            throw new CoffeeChatException(CANNOT_APPROVE_STATUS);
        }

        this.question = question;
        this.reservation = reservation;
        this.status = MENTEE_PENDING;
    }

    /**
     * 멘토의 제안 & 멘티의 1차 수락 -> 멘토가 최종 거절
     */
    public void rejectPendingCoffeeChat(final String rejectReason) {
        if (this.status != MENTEE_PENDING) {
            throw new CoffeeChatException(CANNOT_FINALLY_DECIDE_STATUS);
        }

        this.rejectReason = rejectReason;
        this.status = MENTOR_FINALLY_REJECT;
    }

    /**
     * 멘토의 제안 & 멘티의 1차 수락 -> 멘토가 최종 수락
     */
    public void approvePendingCoffeeChat(final Strategy strategy) {
        if (this.status != MENTEE_PENDING) {
            throw new CoffeeChatException(CANNOT_FINALLY_DECIDE_STATUS);
        }

        this.strategy = strategy;
        this.status = MENTOR_FINALLY_APPROVE;
    }

    /**
     * 멘티 신청 커피챗 or 멘토 제안 커피챗 진행을 완료했을 경우
     */
    public void complete(final CoffeeChatStatus status) {
        if (this.status != MENTOR_APPROVE && this.status != MENTOR_FINALLY_APPROVE) {
            throw new CoffeeChatException(CANNOT_COMPLETE_STATUS);
        }

        this.status = status;
    }

    public boolean isRequestReservationIncludedSchedules(final Reservation target) {
        return reservation.isDateTimeIncluded(target.getStart())
                || reservation.isDateTimeIncluded(target.getEnd());
    }
}
