package com.koddy.server.notification.domain.model;

import com.koddy.server.global.base.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;

import static jakarta.persistence.EnumType.STRING;
import static lombok.AccessLevel.PROTECTED;

@Getter
@NoArgsConstructor(access = PROTECTED)
@Entity
@Table(name = "notification")
public class Notification extends BaseEntity<Notification> {
    @Column(name = "source_member_id", nullable = false, updatable = false)
    private Long sourceMemberId;

    @Column(name = "target_member_id", nullable = false, updatable = false)
    private Long targetMemberId;

    @Enumerated(STRING)
    @Column(name = "notification_type", nullable = false, updatable = false, columnDefinition = "VARCHAR(50)")
    private NotificationType type;

    @Lob
    @Column(name = "message", nullable = false, updatable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "is_read", nullable = false, columnDefinition = "TINYINT")
    private boolean isRead;

    private Notification(
            final Long sourceMemberId,
            final Long targetMemberId,
            final NotificationType type,
            final String message,
            final boolean isRead
    ) {
        this.sourceMemberId = sourceMemberId;
        this.targetMemberId = targetMemberId;
        this.type = type;
        this.message = message;
        this.isRead = isRead;
    }

    public static Notification create(
            final Long sourceMemberId,
            final Long targetMemberId,
            final NotificationType type,
            final String message
    ) {
        return new Notification(sourceMemberId, targetMemberId, type, message, false);
    }

    public void read() {
        this.isRead = true;
    }
}
