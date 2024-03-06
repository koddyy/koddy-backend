package com.koddy.server.coffeechat.domain.repository.query

import com.koddy.server.coffeechat.domain.model.CoffeeChat
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus
import com.koddy.server.coffeechat.domain.repository.query.response.CoffeeChatCountPerCategory
import com.koddy.server.coffeechat.domain.repository.query.response.MenteeCoffeeChatScheduleData
import com.koddy.server.coffeechat.domain.repository.query.response.MentorCoffeeChatScheduleData
import com.koddy.server.coffeechat.domain.repository.query.spec.MenteeCoffeeChatQueryCondition
import com.koddy.server.coffeechat.domain.repository.query.spec.MentorCoffeeChatQueryCondition
import com.koddy.server.global.annotation.KoddyReadOnlyTransactional
import com.koddy.server.global.query.CoffeeChatDsl
import com.koddy.server.member.domain.model.mentee.Mentee
import com.koddy.server.member.domain.model.mentor.Mentor
import com.linecorp.kotlinjdsl.dsl.jpql.Jpql
import com.linecorp.kotlinjdsl.dsl.jpql.jpql
import com.linecorp.kotlinjdsl.querymodel.jpql.expression.Expression
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
class CoffeeChatScheduleQueryRepositoryImpl(
    private val entityManager: EntityManager,
    private val context: JpqlRenderContext,
) : CoffeeChatScheduleQueryRepository {
    override fun fetchMentorCoffeeChatCountPerCategory(mentorId: Long): CoffeeChatCountPerCategory {
        val targetQuery: SelectQuery<CoffeeChatCountPerCategory> = jpql {
            selectNew<CoffeeChatCountPerCategory>(
                fetchCountWithStatus(status = CoffeeChatStatus.withWaitingCategory(), alias = "waiting"),
                fetchCountWithStatus(status = CoffeeChatStatus.withSuggstedCategory(), alias = "suggested"),
                fetchCountWithStatus(status = CoffeeChatStatus.withScheduledCategory(), alias = "scheduled"),
                fetchCountWithStatus(status = CoffeeChatStatus.withPassedCategory(), alias = "passed"),
            ).from(
                entity(CoffeeChat::class),
            ).where(
                path(CoffeeChat::mentorId).equal(mentorId),
            )
        }

        val targetResult: List<CoffeeChatCountPerCategory> = entityManager.createQuery(targetQuery, context).resultList
        return checkResult(targetResult)
    }

    override fun fetchMenteeCoffeeChatCountPerCategory(menteeId: Long): CoffeeChatCountPerCategory {
        val targetQuery: SelectQuery<CoffeeChatCountPerCategory> = jpql {
            selectNew<CoffeeChatCountPerCategory>(
                fetchCountWithStatus(status = CoffeeChatStatus.withWaitingCategory(), alias = "waiting"),
                fetchCountWithStatus(status = CoffeeChatStatus.withSuggstedCategory(), alias = "suggested"),
                fetchCountWithStatus(status = CoffeeChatStatus.withScheduledCategory(), alias = "scheduled"),
                fetchCountWithStatus(status = CoffeeChatStatus.withPassedCategory(), alias = "passed"),
            ).from(
                entity(CoffeeChat::class),
            ).where(
                path(CoffeeChat::menteeId).equal(menteeId),
            )
        }

        val targetResult: List<CoffeeChatCountPerCategory> = entityManager.createQuery(targetQuery, context).resultList
        return checkResult(targetResult)
    }

    private fun Jpql.fetchCountWithStatus(
        status: List<CoffeeChatStatus>,
        alias: String,
    ): Expression<Long> {
        return select<Long>(count(CoffeeChat::id))
            .from(entity(CoffeeChat::class))
            .where(path(CoffeeChat::status).`in`(status))
            .asSubquery()
            .toExpression()
            .`as`(expression(alias))
    }

    private fun checkResult(targetResult: List<CoffeeChatCountPerCategory>): CoffeeChatCountPerCategory {
        return if (targetResult.isEmpty()) CoffeeChatCountPerCategory.zero() else targetResult.first()
    }

    override fun fetchMentorCoffeeChatSchedules(
        condition: MentorCoffeeChatQueryCondition,
        pageable: Pageable,
    ): Slice<MentorCoffeeChatScheduleData> {
        val targetQuery: SelectQuery<MentorCoffeeChatScheduleData> = jpql(CoffeeChatDsl) {
            selectNew<MentorCoffeeChatScheduleData>(
                path(CoffeeChat::id),
                path(CoffeeChat::status),
                path(Mentee::id),
                path(Mentee::name),
                path(Mentee::profileImageUrl),
                path(Mentee::interest),
            ).from(
                entity(CoffeeChat::class),
                innerJoin(Mentee::class).on(path(Mentee::id).equal(path(CoffeeChat::menteeId))),
            ).whereAnd(
                path(CoffeeChat::mentorId).equal(condition.mentorId),
                entity(CoffeeChat::class).statusIn(condition.status),
            ).orderBy(
                path(CoffeeChat::lastModifiedAt).desc(),
                path(CoffeeChat::id).desc(),
            )
        }

        val targetResult: List<MentorCoffeeChatScheduleData> = entityManager.createQuery(targetQuery, context)
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

    override fun fetchMenteeCoffeeChatSchedules(
        condition: MenteeCoffeeChatQueryCondition,
        pageable: Pageable,
    ): Slice<MenteeCoffeeChatScheduleData> {
        val targetQuery: SelectQuery<MenteeCoffeeChatScheduleData> = jpql(CoffeeChatDsl) {
            selectNew<MenteeCoffeeChatScheduleData>(
                path(CoffeeChat::id),
                path(CoffeeChat::status),
                path(Mentor::id),
                path(Mentor::name),
                path(Mentor::profileImageUrl),
                path(Mentor::universityProfile),
            ).from(
                entity(CoffeeChat::class),
                innerJoin(Mentor::class).on(path(Mentor::id).equal(path(CoffeeChat::mentorId))),
            ).whereAnd(
                path(CoffeeChat::menteeId).equal(condition.menteeId),
                entity(CoffeeChat::class).statusIn(condition.status),
            ).orderBy(
                path(CoffeeChat::lastModifiedAt).desc(),
                path(CoffeeChat::id).desc(),
            )
        }

        val targetResult: List<MenteeCoffeeChatScheduleData> = entityManager.createQuery(targetQuery, context)
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
