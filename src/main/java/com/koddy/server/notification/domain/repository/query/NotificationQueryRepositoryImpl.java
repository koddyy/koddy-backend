//package com.koddy.server.notification.domain.repository.query;
//
//import com.koddy.server.global.annotation.KoddyReadOnlyTransactional;
//import com.koddy.server.notification.domain.repository.query.response.NotificationDetails;
//import com.koddy.server.notification.domain.repository.query.response.QNotificationDetails;
//import com.querydsl.jpa.impl.JPAQueryFactory;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Slice;
//import org.springframework.data.domain.SliceImpl;
//import org.springframework.stereotype.Repository;
//
//import java.util.List;
//
//import static com.koddy.server.coffeechat.domain.model.QCoffeeChat.coffeeChat;
//import static com.koddy.server.member.domain.model.mentee.QMentee.mentee;
//import static com.koddy.server.member.domain.model.mentor.QMentor.mentor;
//import static com.koddy.server.notification.domain.model.QNotification.notification;
//
//@Repository
//@KoddyReadOnlyTransactional
//public class NotificationQueryRepositoryImpl implements NotificationQueryRepository {
//    private final JPAQueryFactory query;
//
//    public NotificationQueryRepositoryImpl(final JPAQueryFactory query) {
//        this.query = query;
//    }
//
//    @Override
//    public Slice<NotificationDetails> fetchMentorNotifications(
//            final long mentorId,
//            final Pageable pageable
//    ) {
//        final List<NotificationDetails> result = query
//                .select(new QNotificationDetails(
//                        notification.id,
//                        notification.read,
//                        notification.coffeeChatStatusSnapshot,
//                        notification.type,
//                        notification.createdAt,
//                        mentee.id,
//                        mentee.name,
//                        mentee.profileImageUrl,
//                        coffeeChat.id,
//                        coffeeChat.reason,
//                        coffeeChat.reservation
//                ))
//                .from(notification)
//                .innerJoin(coffeeChat).on(coffeeChat.id.eq(notification.coffeeChatId))
//                .innerJoin(mentee).on(mentee.id.eq(coffeeChat.menteeId))
//                .where(notification.targetId.eq(mentorId))
//                .orderBy(notification.id.desc())
//                .offset(pageable.getOffset())
//                .limit(pageable.getPageSize() + 1)
//                .fetch();
//
//        return new SliceImpl<>(
//                result.stream().limit(pageable.getPageSize()).toList(),
//                pageable,
//                result.size() > pageable.getPageSize()
//        );
//    }
//
//    @Override
//    public Slice<NotificationDetails> fetchMenteeNotifications(
//            final long menteeId,
//            final Pageable pageable
//    ) {
//        final List<NotificationDetails> result = query
//                .select(new QNotificationDetails(
//                        notification.id,
//                        notification.read,
//                        notification.coffeeChatStatusSnapshot,
//                        notification.type,
//                        notification.createdAt,
//                        mentor.id,
//                        mentor.name,
//                        mentor.profileImageUrl,
//                        coffeeChat.id,
//                        coffeeChat.reason,
//                        coffeeChat.reservation
//                ))
//                .from(notification)
//                .innerJoin(coffeeChat).on(coffeeChat.id.eq(notification.coffeeChatId))
//                .innerJoin(mentor).on(mentor.id.eq(coffeeChat.mentorId))
//                .where(notification.targetId.eq(menteeId))
//                .orderBy(notification.id.desc())
//                .offset(pageable.getOffset())
//                .limit(pageable.getPageSize() + 1)
//                .fetch();
//
//        return new SliceImpl<>(
//                result.stream().limit(pageable.getPageSize()).toList(),
//                pageable,
//                result.size() > pageable.getPageSize()
//        );
//    }
//}
