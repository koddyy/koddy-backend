package com.koddy.server.notification.domain.model

import com.koddy.server.coffeechat.domain.model.CoffeeChat
import com.koddy.server.global.base.BaseTimeEntity
import com.koddy.server.member.domain.model.Member
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType.STRING
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType.IDENTITY
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "notification")
class Notification(
    id: Long = 0L,
    target: Member<*>,
    coffeeChat: CoffeeChat,
    type: NotificationType,
) : BaseTimeEntity() {
    @Id
    @GeneratedValue(strategy = IDENTITY)
    val id: Long = id

    @Column(name = "target_id", nullable = false, updatable = false)
    val targetId: Long = target.id

    @Column(name = "coffee_chat_id", nullable = false, updatable = false)
    val coffeeChatId: Long = coffeeChat.id

    @Column(name = "coffee_chat_status_snapshot", nullable = false, updatable = false)
    val coffeeChatStatusSnapshot: String = coffeeChat.status.name

    @Enumerated(STRING)
    @Column(name = "notification_type", nullable = false, updatable = false, columnDefinition = "VARCHAR(50)")
    val type: NotificationType = type

    @Column(name = "is_read", nullable = false, columnDefinition = "TINYINT")
    var isRead: Boolean = false
        protected set

    fun read() {
        isRead = true
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Notification
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()
}
