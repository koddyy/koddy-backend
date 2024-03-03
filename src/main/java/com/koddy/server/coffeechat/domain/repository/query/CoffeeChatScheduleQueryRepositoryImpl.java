//package com.koddy.server.coffeechat.domain.repository.query;
//
//import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus;
//import com.koddy.server.coffeechat.domain.repository.query.response.CoffeeChatCountPerCategory;
//import com.koddy.server.coffeechat.domain.repository.query.response.MenteeCoffeeChatScheduleData;
//import com.koddy.server.coffeechat.domain.repository.query.response.MentorCoffeeChatScheduleData;
//import com.koddy.server.coffeechat.domain.repository.query.response.QCoffeeChatCountPerCategory;
//import com.koddy.server.coffeechat.domain.repository.query.response.QMenteeCoffeeChatScheduleData;
//import com.koddy.server.coffeechat.domain.repository.query.response.QMentorCoffeeChatScheduleData;
//import com.koddy.server.coffeechat.domain.repository.query.spec.MenteeCoffeeChatQueryCondition;
//import com.koddy.server.coffeechat.domain.repository.query.spec.MentorCoffeeChatQueryCondition;
//import com.koddy.server.global.annotation.KoddyReadOnlyTransactional;
//import com.querydsl.core.types.Expression;
//import com.querydsl.core.types.ExpressionUtils;
//import com.querydsl.core.types.dsl.BooleanExpression;
//import com.querydsl.jpa.JPQLQuery;
//import com.querydsl.jpa.impl.JPAQueryFactory;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Slice;
//import org.springframework.data.domain.SliceImpl;
//import org.springframework.stereotype.Repository;
//import org.springframework.util.CollectionUtils;
//
//import java.util.List;
//
//import static com.koddy.server.coffeechat.domain.model.QCoffeeChat.coffeeChat;
//import static com.koddy.server.member.domain.model.mentee.QMentee.mentee;
//import static com.koddy.server.member.domain.model.mentor.QMentor.mentor;
//
//@Repository
//@KoddyReadOnlyTransactional
//public class CoffeeChatScheduleQueryRepositoryImpl implements CoffeeChatScheduleQueryRepository {
//    private final JPAQueryFactory query;
//
//    public CoffeeChatScheduleQueryRepositoryImpl(final JPAQueryFactory query) {
//        this.query = query;
//    }
//
//    @Override
//    public CoffeeChatCountPerCategory fetchMentorCoffeeChatCountPerCategory(final long mentorId) {
//        final CoffeeChatCountPerCategory result = query
//                .selectDistinct(fetchCoffeeChatCountPerCategory())
//                .from(coffeeChat)
//                .where(coffeeChat.mentorId.eq(mentorId))
//                .fetchOne();
//        return checkResult(result);
//    }
//
//    @Override
//    public CoffeeChatCountPerCategory fetchMenteeCoffeeChatCountPerCategory(final long menteeId) {
//        final CoffeeChatCountPerCategory result = query
//                .selectDistinct(fetchCoffeeChatCountPerCategory())
//                .from(coffeeChat)
//                .where(coffeeChat.menteeId.eq(menteeId))
//                .fetchOne();
//        return checkResult(result);
//    }
//
//    private QCoffeeChatCountPerCategory fetchCoffeeChatCountPerCategory() {
//        return new QCoffeeChatCountPerCategory(
//                fetchCountWithStatus(CoffeeChatStatus.withWaitingCategory(), "waiting"),
//                fetchCountWithStatus(CoffeeChatStatus.withSuggstedCategory(), "suggested"),
//                fetchCountWithStatus(CoffeeChatStatus.withScheduledCategory(), "scheduled"),
//                fetchCountWithStatus(CoffeeChatStatus.withPassedCategory(), "passed")
//        );
//    }
//
//    private Expression<Long> fetchCountWithStatus(
//            final List<CoffeeChatStatus> status,
//            final String alias
//    ) {
//        final JPQLQuery<Long> subQuery = select(coffeeChat.count())
//                .from(coffeeChat)
//                .where(coffeeChat.status.in(status));
//        return ExpressionUtils.as(subQuery, alias);
//    }
//
//    private CoffeeChatCountPerCategory checkResult(final CoffeeChatCountPerCategory result) {
//        return result == null ? CoffeeChatCountPerCategory.zero() : result;
//    }
//
//    @Override
//    public Slice<MentorCoffeeChatScheduleData> fetchMentorCoffeeChatSchedules(
//            final MentorCoffeeChatQueryCondition condition,
//            final Pageable pageable
//    ) {
//        final List<MentorCoffeeChatScheduleData> result = query
//                .select(new QMentorCoffeeChatScheduleData(
//                        coffeeChat.id,
//                        coffeeChat.status,
//                        mentee.id,
//                        mentee.name,
//                        mentee.profileImageUrl,
//                        mentee.interest
//                ))
//                .from(coffeeChat)
//                .innerJoin(mentee).on(mentee.id.eq(coffeeChat.menteeId))
//                .where(
//                        coffeeChat.mentorId.eq(condition.mentorId()),
//                        statusIn(condition.status())
//                )
//                .orderBy(coffeeChat.lastModifiedAt.desc(), coffeeChat.id.desc())
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
//    public Slice<MenteeCoffeeChatScheduleData> fetchMenteeCoffeeChatSchedules(
//            final MenteeCoffeeChatQueryCondition condition,
//            final Pageable pageable
//    ) {
//        final List<MenteeCoffeeChatScheduleData> result = query
//                .select(new QMenteeCoffeeChatScheduleData(
//                        coffeeChat.id,
//                        coffeeChat.status,
//                        mentor.id,
//                        mentor.name,
//                        mentor.profileImageUrl,
//                        mentor.universityProfile
//                ))
//                .from(coffeeChat)
//                .innerJoin(mentor).on(mentor.id.eq(coffeeChat.mentorId))
//                .where(
//                        coffeeChat.menteeId.eq(condition.menteeId()),
//                        statusIn(condition.status())
//                )
//                .orderBy(coffeeChat.lastModifiedAt.desc(), coffeeChat.id.desc())
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
//    private BooleanExpression statusIn(final List<CoffeeChatStatus> status) {
//        if (CollectionUtils.isEmpty(status)) {
//            return null;
//        }
//        return coffeeChat.status.in(status);
//    }
//}
