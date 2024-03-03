package com.koddy.server.coffeechat.domain.model

import jakarta.persistence.Column
import jakarta.persistence.Embeddable
import jakarta.persistence.Lob

@Embeddable
data class Reason(
    @Lob
    @Column(name = "apply_reason", columnDefinition = "TEXT")
    val applyReason: String? = null,

    @Lob
    @Column(name = "suggest_reason", columnDefinition = "TEXT")
    val suggestReason: String? = null,

    @Lob
    @Column(name = "cancel_reason", columnDefinition = "TEXT")
    val cancelReason: String? = null,

    @Lob
    @Column(name = "reject_reason", columnDefinition = "TEXT")
    val rejectReason: String? = null,
) {

    fun applyCancelReason(value: String): Reason {
        return Reason(
            applyReason = applyReason,
            suggestReason = suggestReason,
            cancelReason = value,
            rejectReason = rejectReason,
        )
    }

    fun applyRejectReason(value: String): Reason {
        return Reason(
            applyReason = applyReason,
            suggestReason = suggestReason,
            cancelReason = cancelReason,
            rejectReason = value,
        )
    }

    companion object {
        fun apply(value: String): Reason {
            return Reason(applyReason = value)
        }

        fun suggest(value: String): Reason {
            return Reason(suggestReason = value)
        }
    }
}
