package com.koddy.server.notification.domain.repository.query

import com.koddy.server.coffeechat.domain.model.CoffeeChat
import com.koddy.server.global.annotation.KoddyReadOnlyTransactional
import com.koddy.server.global.query.NotificationDsl
import com.koddy.server.member.domain.model.mentee.Mentee
import com.koddy.server.member.domain.model.mentor.Mentor
import com.koddy.server.notification.domain.model.Notification
import com.koddy.server.notification.domain.repository.query.response.NotificationDetails
import com.linecorp.kotlinjdsl.dsl.jpql.jpql
import com.linecorp.kotlinjdsl.querymodel.jpql.select.SelectQuery
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderContext
import com.linecorp.kotlinjdsl.support.spring.data.jpa.extension.createQuery
import jakarta.persistence.EntityManager
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.domain.SliceImpl
import org.springframework.stereotype.Repository

@Repository
@KoddyReadOnlyTransactional
class NotificationQueryRepositoryImpl(
    private val entityManager: EntityManager,
    private val context: JpqlRenderContext,
) : NotificationQueryRepository {
    override fun fetchMentorNotifications(
        mentorId: Long,
        pageable: Pageable,
    ): Slice<NotificationDetails> {
        val targetQuery: SelectQuery<NotificationDetails> = jpql(NotificationDsl) {
            selectNew<NotificationDetails>(
                path(Notification::id),
                path(Notification::isRead),
                path(Notification::coffeeChatStatusSnapshot),
                path(Notification::type),
                path(Notification::createdAt),
                path(Mentee::id),
                path(Mentee::name),
                path(Mentee::profileImageUrl),
                path(CoffeeChat::id),
                path(CoffeeChat::reason),
                path(CoffeeChat::reservation),
            ).from(
                entity(Notification::class),
                innerJoin(CoffeeChat::class).on(path(CoffeeChat::id).equal(path(Notification::coffeeChatId))),
                innerJoin(Mentee::class).on(path(Mentee::id).equal(path(CoffeeChat::menteeId))),
            ).where(
                entity(Notification::class).targetIdEq(mentorId),
            ).orderBy(
                path(Notification::id).desc(),
            )
        }

        val targetResult: List<NotificationDetails> = entityManager.createQuery(targetQuery, context)
            .setFirstResult(pageable.offset.toInt())
            .setMaxResults(pageable.pageSize + 1)
            .resultList
        val hasNext: Boolean = targetResult.size > pageable.pageSize

        return SliceImpl(
            takeIf { hasNext }?.let { targetResult.dropLast(1) } ?: targetResult,
            pageable,
            targetResult.size > pageable.pageSize,
        )
    }

    override fun fetchMenteeNotifications(
        menteeId: Long,
        pageable: Pageable,
    ): Slice<NotificationDetails> {
        val targetQuery: SelectQuery<NotificationDetails> = jpql(NotificationDsl) {
            selectNew<NotificationDetails>(
                path(Notification::id),
                path(Notification::isRead),
                path(Notification::coffeeChatStatusSnapshot),
                path(Notification::type),
                path(Notification::createdAt),
                path(Mentor::id),
                path(Mentor::name),
                path(Mentor::profileImageUrl),
                path(CoffeeChat::id),
                path(CoffeeChat::reason),
                path(CoffeeChat::reservation),
            ).from(
                entity(Notification::class),
                innerJoin(CoffeeChat::class).on(path(CoffeeChat::id).equal(path(Notification::coffeeChatId))),
                innerJoin(Mentor::class).on(path(Mentor::id).equal(path(CoffeeChat::mentorId))),
            ).where(
                entity(Notification::class).targetIdEq(menteeId),
            ).orderBy(
                path(Notification::id).desc(),
            )
        }

        val targetResult: List<NotificationDetails> = entityManager.createQuery(targetQuery, context)
            .setFirstResult(pageable.offset.toInt())
            .setMaxResults(pageable.pageSize + 1)
            .resultList
        val hasNext: Boolean = targetResult.size > pageable.pageSize

        return SliceImpl(
            takeIf { hasNext }?.let { targetResult.dropLast(1) } ?: targetResult,
            pageable,
            targetResult.size > pageable.pageSize,
        )
    }
}
