package com.koddy.server.notification.domain.model;

import com.koddy.server.global.base.BaseEntity;
import com.koddy.server.member.domain.model.Member;
import com.koddy.server.member.domain.model.mentee.Mentee;
import com.koddy.server.member.domain.model.mentor.Mentor;
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
    @Column(name = "target_id", nullable = false, updatable = false)
    private Long targetId;

    @Column(name = "mentor_id", nullable = false, updatable = false)
    private Long mentorId;

    @Column(name = "mentee_id", nullable = false, updatable = false)
    private Long menteeId;

    @Enumerated(STRING)
    @Column(name = "notification_type", nullable = false, updatable = false, columnDefinition = "VARCHAR(50)")
    private NotificationType type;

    @Lob
    @Column(name = "message", nullable = false, updatable = false, columnDefinition = "TEXT")
    private String message;

    @Column(name = "is_read", nullable = false, columnDefinition = "TINYINT")
    private boolean read;

    private Notification(
            final Member<?> target,
            final Mentor mentor,
            final Mentee mentee,
            final NotificationType type,
            final String message,
            final boolean read
    ) {
        this.targetId = target.getId();
        this.mentorId = mentor.getId();
        this.menteeId = mentee.getId();
        this.type = type;
        this.message = message;
        this.read = read;
    }

    public static Notification create(
            final Member<?> target,
            final Mentor mentor,
            final Mentee mentee,
            final NotificationType type,
            final String message
    ) {
        return new Notification(target, mentor, mentee, type, message, false);
    }

    public void read() {
        this.read = true;
    }
}
