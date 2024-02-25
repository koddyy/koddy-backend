package com.koddy.server.coffeechat.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.Lob;

@Embeddable
public class Reason {
    protected Reason() {
    }

    @Lob
    @Column(name = "apply_reason", columnDefinition = "TEXT")
    private String applyReason;

    @Lob
    @Column(name = "suggest_reason", columnDefinition = "TEXT")
    private String suggestReason;

    @Lob
    @Column(name = "cancel_reason", columnDefinition = "TEXT")
    private String cancelReason;

    @Lob
    @Column(name = "reject_reason", columnDefinition = "TEXT")
    private String rejectReason;

    private Reason(
            final String applyReason,
            final String suggestReason,
            final String cancelReason,
            final String rejectReason
    ) {
        this.applyReason = applyReason;
        this.suggestReason = suggestReason;
        this.cancelReason = cancelReason;
        this.rejectReason = rejectReason;
    }

    public static Reason apply(final String value) {
        return new Reason(value, null, null, null);
    }

    public static Reason suggest(final String value) {
        return new Reason(null, value, null, null);
    }

    public Reason applyCancelReason(final String value) {
        return new Reason(applyReason, suggestReason, value, rejectReason);
    }

    public Reason applyRejectReason(final String value) {
        return new Reason(applyReason, suggestReason, cancelReason, value);
    }

    public String getApplyReason() {
        return applyReason;
    }

    public String getSuggestReason() {
        return suggestReason;
    }

    public String getCancelReason() {
        return cancelReason;
    }

    public String getRejectReason() {
        return rejectReason;
    }
}
