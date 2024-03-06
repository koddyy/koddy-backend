package com.koddy.server.global.query

import com.koddy.server.coffeechat.domain.model.CoffeeChat
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus
import com.koddy.server.coffeechat.domain.model.Reservation
import com.koddy.server.member.domain.model.AvailableLanguage
import com.koddy.server.member.domain.model.Language
import com.koddy.server.member.domain.model.Nationality
import com.koddy.server.member.domain.model.mentee.Mentee
import com.koddy.server.member.domain.model.mentor.Mentor
import com.koddy.server.notification.domain.model.Notification
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

    fun Entity<Mentor>.mentorIdIn(ids: List<Long>): Predicate {
        return path(Mentor::id).`in`(ids)
    }

    fun Entity<Mentee>.menteeIdIn(ids: List<Long>): Predicate {
        return path(Mentee::id).`in`(ids)
    }

    fun Entity<Mentor>.mentorNationalityAnyMatches(values: List<Nationality>): Predicate {
        return or(*values.map { path(Mentor::nationality).equal(it) }.toTypedArray())
    }

    fun Entity<Mentee>.menteeNationalityAnyMatches(values: List<Nationality>): Predicate {
        return or(*values.map { path(Mentee::nationality).equal(it) }.toTypedArray())
    }

    fun Entity<AvailableLanguage>.languageCategoryIn(values: List<Language.Category>): Predicate {
        return path(AvailableLanguage::language)(Language::category).`in`(values)
    }
}

class CoffeeChatDsl : Jpql() {
    companion object Constructor : JpqlDsl.Constructor<CoffeeChatDsl> {
        override fun newInstance(): CoffeeChatDsl = CoffeeChatDsl()
    }

    fun Entity<CoffeeChat>.mentorIdEq(mentorId: Long): Predicate {
        return path(CoffeeChat::mentorId).equal(mentorId)
    }

    fun Entity<CoffeeChat>.menteeIdEq(menteeId: Long): Predicate {
        return path(CoffeeChat::menteeId).equal(menteeId)
    }

    fun Entity<CoffeeChat>.statusEq(status: CoffeeChatStatus): Predicate {
        return path(CoffeeChat::status).equal(status)
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

    fun Entity<Notification>.targetIdEq(targetId: Long): Predicate {
        return path(Notification::targetId).equal(targetId)
    }
}
