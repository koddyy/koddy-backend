package com.koddy.server.global.query

import com.koddy.server.coffeechat.domain.model.CoffeeChat
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus
import com.koddy.server.coffeechat.domain.model.Reservation
import com.linecorp.kotlinjdsl.dsl.jpql.Jpql
import com.linecorp.kotlinjdsl.dsl.jpql.JpqlDsl
import com.linecorp.kotlinjdsl.querymodel.jpql.entity.Entity
import com.linecorp.kotlinjdsl.querymodel.jpql.path.Path
import com.linecorp.kotlinjdsl.querymodel.jpql.predicate.Predicate
import java.time.LocalDateTime

class MemberDsl : Jpql() {
    companion object Constructor : JpqlDsl.Constructor<MemberDsl> {
        override fun newInstance(): MemberDsl = MemberDsl()
    }
}

class CoffeeChatDsl : Jpql() {
    companion object Constructor : JpqlDsl.Constructor<CoffeeChatDsl> {
        override fun newInstance(): CoffeeChatDsl = CoffeeChatDsl()
    }

    fun Entity<CoffeeChat>.statusIn(status: List<CoffeeChatStatus>): Predicate? {
        return when (status.isEmpty()) {
            true -> null
            false -> path(CoffeeChat::status).`in`(status)
        }
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
}

class NotificationDsl : Jpql() {
    companion object Constructor : JpqlDsl.Constructor<NotificationDsl> {
        override fun newInstance(): NotificationDsl = NotificationDsl()
    }
}
