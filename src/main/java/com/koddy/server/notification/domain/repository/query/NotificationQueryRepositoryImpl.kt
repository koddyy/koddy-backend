package com.koddy.server.notification.domain.repository.query

import com.koddy.server.coffeechat.domain.model.CoffeeChat
import com.koddy.server.global.annotation.KoddyReadOnlyTransactional
import com.koddy.server.member.domain.model.mentee.Mentee
import com.koddy.server.member.domain.model.mentor.Mentor
import com.koddy.server.notification.domain.model.Notification
import com.koddy.server.notification.domain.repository.query.response.NotificationDetails
import com.linecorp.kotlinjdsl.support.spring.data.jpa.repository.KotlinJdslJpqlExecutor
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.stereotype.Repository

@Repository
@KoddyReadOnlyTransactional
class NotificationQueryRepositoryImpl(
    private val executor: KotlinJdslJpqlExecutor,
) : NotificationQueryRepository {
    override fun fetchMentorNotifications(
        mentorId: Long,
        pageable: Pageable,
    ): Slice<NotificationDetails> {
        return executor.findSlice(pageable) {
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
                path(Notification::targetId).equal(mentorId),
            ).orderBy(
                path(Notification::id).desc(),
            )
        }.map { it }
    }

    override fun fetchMenteeNotifications(
        menteeId: Long,
        pageable: Pageable,
    ): Slice<NotificationDetails> {
        return executor.findSlice(pageable) {
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
                path(Notification::targetId).equal(menteeId),
            ).orderBy(
                path(Notification::id).desc(),
            )
        }.map { it }
    }
    // TODO (KotlinJdslJpqlExecutor + map iterating) vs (EntityManager Directly Handling) Performance
}
