package com.koddy.server.coffeechat.domain.model

import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTEE_APPLY
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTEE_PENDING
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTEE_REJECT
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_APPROVE
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_FINALLY_APPROVE
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_FINALLY_CANCEL
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_REJECT
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_SUGGEST
import com.koddy.server.coffeechat.exception.CoffeeChatException
import com.koddy.server.coffeechat.exception.CoffeeChatExceptionCode.CANNOT_APPROVE_STATUS
import com.koddy.server.coffeechat.exception.CoffeeChatExceptionCode.CANNOT_CANCEL_STATUS
import com.koddy.server.coffeechat.exception.CoffeeChatExceptionCode.CANNOT_COMPLETE_STATUS
import com.koddy.server.coffeechat.exception.CoffeeChatExceptionCode.CANNOT_FINALLY_DECIDE_STATUS
import com.koddy.server.coffeechat.exception.CoffeeChatExceptionCode.CANNOT_REJECT_STATUS
import com.koddy.server.global.base.BaseTimeEntity
import com.koddy.server.member.domain.model.mentee.Mentee
import com.koddy.server.member.domain.model.mentor.Mentor
import jakarta.persistence.Column
import jakarta.persistence.Embedded
import jakarta.persistence.Entity
import jakarta.persistence.EnumType.STRING
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType.IDENTITY
import jakarta.persistence.Id
import jakarta.persistence.Lob
import jakarta.persistence.Table

@Entity
@Table(name = "coffee_chat")
class CoffeeChat(
    id: Long = 0L,
    mentor: Mentor,
    mentee: Mentee,
    status: CoffeeChatStatus,
    reason: Reason,
    reservation: Reservation? = null,
) : BaseTimeEntity() {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    val id: Long = id

    @Column(name = "mentor_id", nullable = false)
    val mentorId: Long = mentor.id

    @Column(name = "mentee_id", nullable = false)
    val menteeId: Long = mentee.id

    @Enumerated(STRING)
    @Column(name = "status", nullable = false, columnDefinition = "VARCHAR(30)")
    var status: CoffeeChatStatus = status
        protected set

    /**
     * 언제든 취소 가능하기 때문에 실제로 취소한 사용자 추적 용도
     */
    @Column(name = "cancel_by")
    var cancelBy: Long? = null
        protected set

    @Embedded
    var reason: Reason = reason
        protected set

    @Lob
    @Column(name = "question", columnDefinition = "TEXT")
    var question: String? = null
        protected set

    @Embedded
    var reservation: Reservation? = reservation
        protected set

    @Embedded
    var strategy: Strategy? = null
        protected set

    val isMenteeFlow: Boolean
        get() = status.isMenteeFlow()

    /**
     * 커피챗 취소 -> 완료 상태가 아닌 경우 언제든지 가능
     */
    fun cancel(
        status: CoffeeChatStatus,
        cancelBy: Long,
        cancelReason: String,
    ) {
        if (currentCannotCancelable()) {
            throw CoffeeChatException(CANNOT_CANCEL_STATUS)
        }

        this.status = status
        this.cancelBy = cancelBy
        this.reason = this.reason.applyCancelReason(cancelReason)
    }

    private fun currentCannotCancelable(): Boolean = this.status.isCancelable().not()

    /**
     * 멘티의 신청 -> 멘토가 거절
     */
    fun rejectFromMenteeApply(rejectReason: String) {
        if (this.status != MENTEE_APPLY) {
            throw CoffeeChatException(CANNOT_REJECT_STATUS)
        }

        this.reason = reason.applyRejectReason(rejectReason)
        this.status = MENTOR_REJECT
    }

    /**
     * 멘티의 신청 -> 멘토가 수락
     */
    fun approveFromMenteeApply(
        question: String,
        strategy: Strategy,
    ) {
        if (this.status != MENTEE_APPLY) {
            throw CoffeeChatException(CANNOT_APPROVE_STATUS)
        }

        this.question = question
        this.strategy = strategy
        this.status = MENTOR_APPROVE
    }

    /**
     * 멘토의 제안 -> 멘티가 거절
     */
    fun rejectFromMentorSuggest(rejectReason: String) {
        if (this.status != MENTOR_SUGGEST) {
            throw CoffeeChatException(CANNOT_REJECT_STATUS)
        }

        this.reason = reason.applyRejectReason(rejectReason)
        this.status = MENTEE_REJECT
    }

    /**
     * 멘토의 제안 -> 멘티의 1차 수락
     */
    fun pendingFromMentorSuggest(
        question: String,
        reservation: Reservation,
    ) {
        if (this.status != MENTOR_SUGGEST) {
            throw CoffeeChatException(CANNOT_APPROVE_STATUS)
        }

        this.question = question
        this.reservation = reservation
        this.status = MENTEE_PENDING
    }

    /**
     * 멘토의 제안 & 멘티의 1차 수락 -> 멘토가 최종 취소
     */
    fun finallyCancelPendingCoffeeChat(cancelReason: String) {
        if (this.status != MENTEE_PENDING) {
            throw CoffeeChatException(CANNOT_FINALLY_DECIDE_STATUS)
        }

        this.reason = reason.applyCancelReason(cancelReason)
        this.status = MENTOR_FINALLY_CANCEL
    }

    /**
     * 멘토의 제안 & 멘티의 1차 수락 -> 멘토가 최종 수락
     */
    fun finallyApprovePendingCoffeeChat(strategy: Strategy) {
        if (this.status != MENTEE_PENDING) {
            throw CoffeeChatException(CANNOT_FINALLY_DECIDE_STATUS)
        }

        this.strategy = strategy
        this.status = MENTOR_FINALLY_APPROVE
    }

    /**
     * 커피챗 진행 완료
     */
    fun complete(status: CoffeeChatStatus) {
        if (this.status != MENTOR_APPROVE && this.status != MENTOR_FINALLY_APPROVE) {
            throw CoffeeChatException(CANNOT_COMPLETE_STATUS)
        }

        this.status = status
    }

    fun isRequestReservationIncludedSchedules(target: Reservation): Boolean {
        if (reservation == null) {
            return false
        }

        return reservation!!.isDateTimeIncluded(target)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as CoffeeChat
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    companion object {
        fun apply(
            mentee: Mentee,
            mentor: Mentor,
            applyReason: String,
            reservation: Reservation,
        ): CoffeeChat {
            return CoffeeChat(
                mentor = mentor,
                mentee = mentee,
                status = MENTEE_APPLY,
                reason = Reason.apply(applyReason),
                reservation = reservation,
            )
        }

        fun suggest(
            mentor: Mentor,
            mentee: Mentee,
            suggestReason: String,
        ): CoffeeChat {
            return CoffeeChat(
                mentor = mentor,
                mentee = mentee,
                status = MENTOR_SUGGEST,
                reason = Reason.suggest(suggestReason),
            )
        }

        fun applyFixture(
            id: Long = 0L,
            mentee: Mentee,
            mentor: Mentor,
            applyReason: String,
            reservation: Reservation,
        ): CoffeeChat {
            return CoffeeChat(
                id = id,
                mentee = mentee,
                mentor = mentor,
                status = MENTEE_APPLY,
                reason = Reason.apply(applyReason),
                reservation = reservation,
            )
        }

        fun suggestFixture(
            id: Long = 0L,
            mentor: Mentor,
            mentee: Mentee,
            suggestReason: String,
        ): CoffeeChat {
            return CoffeeChat(
                id = id,
                mentor = mentor,
                mentee = mentee,
                status = MENTOR_SUGGEST,
                reason = Reason.suggest(suggestReason),
            )
        }
    }
}
