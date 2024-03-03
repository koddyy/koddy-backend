package com.koddy.server.notification.domain.model

import com.koddy.server.coffeechat.domain.model.CoffeeChat
import com.koddy.server.global.base.BaseTimeEntity
import com.koddy.server.member.domain.model.Member
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "notification")
class Notification(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0L,

    @Column(name = "target_id", nullable = false, updatable = false)
    val targetId: Long,

    @Column(name = "coffee_chat_id", nullable = false, updatable = false)
    val coffeeChatId: Long,

    @Column(name = "coffee_chat_status_snapshot", nullable = false, updatable = false)
    val coffeeChatStatusSnapshot: String,

    @Enumerated(EnumType.STRING)
    @Column(name = "notification_type", nullable = false, updatable = false, columnDefinition = "VARCHAR(50)")
    val type: NotificationType,

    @Column(name = "is_read", nullable = false, columnDefinition = "TINYINT")
    var read: Boolean = false,
) : BaseTimeEntity() {
    fun read() {
        read = true
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Notification
        return id == other.id
    }

    override fun hashCode(): Int = id.hashCode()

    companion object {
        fun create(
            target: Member<*>,
            coffeeChat: CoffeeChat,
            type: NotificationType,
        ): Notification {
            return Notification(
                targetId = target.id,
                coffeeChatId = coffeeChat.id,
                coffeeChatStatusSnapshot = coffeeChat.status.name,
                type = type,
            )
        }

        fun fixture(
            id: Long = 0L,
            target: Member<*>,
            coffeeChat: CoffeeChat,
            type: NotificationType,
        ): Notification {
            return Notification(
                id = id,
                targetId = target.id,
                coffeeChatId = coffeeChat.id,
                coffeeChatStatusSnapshot = coffeeChat.status.name,
                type = type,
            )
        }
    }
}
