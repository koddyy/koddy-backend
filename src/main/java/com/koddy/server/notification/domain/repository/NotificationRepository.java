package com.koddy.server.notification.domain.repository;

import com.koddy.server.global.annotation.KoddyWritableTransactional;
import com.koddy.server.notification.domain.model.Notification;
import com.koddy.server.notification.exception.NotificationException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

import static com.koddy.server.notification.exception.NotificationExceptionCode.NOTIFICATION_NOT_FOUND;

public interface NotificationRepository extends JpaRepository<Notification, Long> {
    Optional<Notification> findByIdAndTargetId(final long id, final long targetId);

    default Notification getByIdAndTargetId(final long id, final long targetId) {
        return findByIdAndTargetId(id, targetId)
                .orElseThrow(() -> new NotificationException(NOTIFICATION_NOT_FOUND));
    }

    @KoddyWritableTransactional
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
            UPDATE Notification n
            SET n.read = true
            WHERE n.targetId = :targetId
            """)
    void readAll(@Param("targetId") final long targetId);
}
