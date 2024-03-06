package com.koddy.server.coffeechat.domain.repository.query

import com.koddy.server.coffeechat.domain.model.CoffeeChat
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTEE_APPLY
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTEE_PENDING
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_APPROVE
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_FINALLY_APPROVE
import com.koddy.server.coffeechat.domain.model.Reservation
import com.koddy.server.global.annotation.KoddyReadOnlyTransactional
import com.linecorp.kotlinjdsl.dsl.jpql.Jpql
import com.linecorp.kotlinjdsl.dsl.jpql.JpqlDsl
import com.linecorp.kotlinjdsl.dsl.jpql.jpql
import com.linecorp.kotlinjdsl.querymodel.jpql.entity.Entity
import com.linecorp.kotlinjdsl.querymodel.jpql.path.Path
import com.linecorp.kotlinjdsl.querymodel.jpql.predicate.Predicate
import com.linecorp.kotlinjdsl.querymodel.jpql.select.SelectQuery
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderContext
import com.linecorp.kotlinjdsl.support.spring.data.jpa.extension.createQuery
import jakarta.persistence.EntityManager
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
@KoddyReadOnlyTransactional
class MentorReservedScheduleQueryRepositoryImpl(
    private val entityManager: EntityManager,
    private val context: JpqlRenderContext,
) : MentorReservedScheduleQueryRepository {
    override fun fetchReservedCoffeeChat(
        mentorId: Long,
        year: Int,
        month: Int,
    ): List<CoffeeChat> {
        val suggestedByMentorQuery: SelectQuery<CoffeeChat> = createQueryByCoffeeChatStatus(
            mentorId = mentorId,
            year = year,
            month = month,
            status = listOf(MENTEE_PENDING, MENTOR_FINALLY_APPROVE),
        )
        val appliedToMentorQuery: SelectQuery<CoffeeChat> = createQueryByCoffeeChatStatus(
            mentorId = mentorId,
            year = year,
            month = month,
            status = listOf(MENTEE_APPLY, MENTOR_APPROVE),
        )

        val suggestedByMentorResult: List<CoffeeChat> = entityManager.createQuery(suggestedByMentorQuery, context).resultList
        val appliedToMentorResult: List<CoffeeChat> = entityManager.createQuery(appliedToMentorQuery, context).resultList

        return listOf(*suggestedByMentorResult.toTypedArray(), *appliedToMentorResult.toTypedArray())
            .sortedBy { it.reservation!!.start }
    }

    private fun createQueryByCoffeeChatStatus(
        mentorId: Long,
        year: Int,
        month: Int,
        status: List<CoffeeChatStatus>,
    ): SelectQuery<CoffeeChat> {
        return jpql(CoffeeChatDsl) {
            select(
                entity(CoffeeChat::class),
            ).from(
                entity(CoffeeChat::class),
            ).whereAnd(
                path(CoffeeChat::mentorId).equal(mentorId),
                entity(CoffeeChat::class).reservationStartBetween(year, month),
                entity(CoffeeChat::class).statusIn(status),
            )
        }
    }
}

private class CoffeeChatDsl : Jpql() {
    companion object Constructor : JpqlDsl.Constructor<CoffeeChatDsl> {
        override fun newInstance(): CoffeeChatDsl = CoffeeChatDsl()
    }

    fun Entity<CoffeeChat>.reservationStartBetween(
        year: Int,
        month: Int,
    ): Predicate {
        val start: LocalDateTime = LocalDateTime.of(year, month, 1, 0, 0)
        val end: LocalDateTime = start.plusMonths(1)

        val reservationStartPath: Path<LocalDateTime> = path(CoffeeChat::reservation)(Reservation::start)
        return reservationStartPath.greaterThanOrEqualTo(start)
            .and(reservationStartPath.lessThan(end))
    }

    fun Entity<CoffeeChat>.statusIn(status: List<CoffeeChatStatus>): Predicate? {
        return when (status.isEmpty()) {
            true -> null
            false -> path(CoffeeChat::status).`in`(status)
        }
    }
}
