package com.koddy.server.notification.domain.repository

import com.koddy.server.global.annotation.KoddyWritableTransactional
import com.koddy.server.notification.domain.model.Notification
import com.koddy.server.notification.exception.NotificationException
import com.koddy.server.notification.exception.NotificationExceptionCode.NOTIFICATION_NOT_FOUND
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface NotificationRepository : JpaRepository<Notification, Long> {
    fun findByIdAndTargetId(
        id: Long,
        targetId: Long,
    ): Notification?

    fun getByIdAndTargetId(
        id: Long,
        targetId: Long,
    ): Notification {
        return findByIdAndTargetId(id, targetId)
            ?: throw NotificationException(NOTIFICATION_NOT_FOUND)
    }

    fun findByTargetId(targetId: Long): List<Notification>

    @KoddyWritableTransactional
    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query(
        """
        UPDATE Notification n
        SET n.isRead = true
        WHERE n.targetId = :targetId
        """,
    )
    fun readAll(@Param("targetId") targetId: Long)
}
