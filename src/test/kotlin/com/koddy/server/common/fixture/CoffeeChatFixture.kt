package com.koddy.server.common.fixture

import com.koddy.server.coffeechat.domain.model.CoffeeChat
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.CANCEL_FROM_MENTEE_FLOW
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.CANCEL_FROM_MENTOR_FLOW
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTEE_APPLY_COFFEE_CHAT_COMPLETE
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_SUGGEST_COFFEE_CHAT_COMPLETE
import com.koddy.server.coffeechat.domain.model.Reservation
import com.koddy.server.coffeechat.domain.model.Strategy
import com.koddy.server.common.fixture.StrategyFixture.KAKAO_ID
import com.koddy.server.member.domain.model.mentee.Mentee
import com.koddy.server.member.domain.model.mentor.Mentor
import java.time.LocalDateTime

private fun mentoring(day: Int, hour: Int, minute: Int): LocalDateTime {
    return LocalDateTime.of(2024, 1, day, hour, minute)
}

enum class CoffeeChatFixture(
    val start: LocalDateTime,
    val end: LocalDateTime,
    val strategy: Strategy,
) {
    월요일_1주차_20_00_시작(mentoring(1, 20, 0), mentoring(1, 20, 30), KAKAO_ID.toDomain()),
    월요일_1주차_20_30_시작(mentoring(1, 20, 30), mentoring(1, 21, 0), KAKAO_ID.toDomain()),
    월요일_1주차_21_00_시작(mentoring(1, 21, 0), mentoring(1, 21, 30), KAKAO_ID.toDomain()),
    월요일_1주차_21_30_시작(mentoring(1, 21, 30), mentoring(1, 22, 0), KAKAO_ID.toDomain()),
    월요일_2주차_20_00_시작(mentoring(8, 20, 0), mentoring(8, 20, 30), KAKAO_ID.toDomain()),
    월요일_2주차_20_30_시작(mentoring(8, 20, 30), mentoring(8, 21, 0), KAKAO_ID.toDomain()),
    월요일_2주차_21_00_시작(mentoring(8, 21, 0), mentoring(8, 21, 30), KAKAO_ID.toDomain()),
    월요일_2주차_21_30_시작(mentoring(8, 21, 30), mentoring(8, 22, 0), KAKAO_ID.toDomain()),
    월요일_3주차_20_00_시작(mentoring(15, 20, 0), mentoring(15, 20, 30), KAKAO_ID.toDomain()),
    월요일_3주차_20_30_시작(mentoring(15, 20, 30), mentoring(15, 21, 0), KAKAO_ID.toDomain()),
    월요일_3주차_21_00_시작(mentoring(15, 21, 0), mentoring(15, 21, 30), KAKAO_ID.toDomain()),
    월요일_3주차_21_30_시작(mentoring(15, 21, 30), mentoring(15, 22, 0), KAKAO_ID.toDomain()),
    월요일_4주차_20_00_시작(mentoring(22, 20, 0), mentoring(22, 20, 30), KAKAO_ID.toDomain()),
    월요일_4주차_20_30_시작(mentoring(22, 20, 30), mentoring(22, 21, 0), KAKAO_ID.toDomain()),
    월요일_4주차_21_00_시작(mentoring(22, 21, 0), mentoring(22, 21, 30), KAKAO_ID.toDomain()),
    월요일_4주차_21_30_시작(mentoring(22, 21, 30), mentoring(22, 22, 0), KAKAO_ID.toDomain()),

    화요일_1주차_20_00_시작(mentoring(2, 20, 0), mentoring(2, 20, 30), KAKAO_ID.toDomain()),
    화요일_1주차_20_30_시작(mentoring(2, 20, 30), mentoring(2, 21, 0), KAKAO_ID.toDomain()),
    화요일_1주차_21_00_시작(mentoring(2, 21, 0), mentoring(2, 21, 30), KAKAO_ID.toDomain()),
    화요일_1주차_21_30_시작(mentoring(2, 21, 30), mentoring(2, 22, 0), KAKAO_ID.toDomain()),
    화요일_2주차_20_00_시작(mentoring(9, 20, 0), mentoring(9, 20, 30), KAKAO_ID.toDomain()),
    화요일_2주차_20_30_시작(mentoring(9, 20, 30), mentoring(9, 21, 0), KAKAO_ID.toDomain()),
    화요일_2주차_21_00_시작(mentoring(9, 21, 0), mentoring(9, 21, 30), KAKAO_ID.toDomain()),
    화요일_2주차_21_30_시작(mentoring(9, 21, 30), mentoring(9, 22, 0), KAKAO_ID.toDomain()),
    화요일_3주차_20_00_시작(mentoring(16, 20, 0), mentoring(16, 20, 30), KAKAO_ID.toDomain()),
    화요일_3주차_20_30_시작(mentoring(16, 20, 30), mentoring(16, 21, 0), KAKAO_ID.toDomain()),
    화요일_3주차_21_00_시작(mentoring(16, 21, 0), mentoring(16, 21, 30), KAKAO_ID.toDomain()),
    화요일_3주차_21_30_시작(mentoring(16, 21, 30), mentoring(16, 22, 0), KAKAO_ID.toDomain()),
    화요일_4주차_20_00_시작(mentoring(23, 20, 0), mentoring(23, 20, 30), KAKAO_ID.toDomain()),
    화요일_4주차_20_30_시작(mentoring(23, 20, 30), mentoring(23, 21, 0), KAKAO_ID.toDomain()),
    화요일_4주차_21_00_시작(mentoring(23, 21, 0), mentoring(23, 21, 30), KAKAO_ID.toDomain()),
    화요일_4주차_21_30_시작(mentoring(23, 21, 30), mentoring(23, 22, 0), KAKAO_ID.toDomain()),

    수요일_1주차_20_00_시작(mentoring(3, 20, 0), mentoring(3, 20, 30), KAKAO_ID.toDomain()),
    수요일_1주차_20_30_시작(mentoring(3, 20, 30), mentoring(3, 21, 0), KAKAO_ID.toDomain()),
    수요일_1주차_21_00_시작(mentoring(3, 21, 0), mentoring(3, 21, 30), KAKAO_ID.toDomain()),
    수요일_1주차_21_30_시작(mentoring(3, 21, 30), mentoring(3, 22, 0), KAKAO_ID.toDomain()),
    수요일_2주차_20_00_시작(mentoring(10, 20, 0), mentoring(10, 20, 30), KAKAO_ID.toDomain()),
    수요일_2주차_20_30_시작(mentoring(10, 20, 30), mentoring(10, 21, 0), KAKAO_ID.toDomain()),
    수요일_2주차_21_00_시작(mentoring(10, 21, 0), mentoring(10, 21, 30), KAKAO_ID.toDomain()),
    수요일_2주차_21_30_시작(mentoring(10, 21, 30), mentoring(10, 22, 0), KAKAO_ID.toDomain()),
    수요일_3주차_20_00_시작(mentoring(17, 20, 0), mentoring(17, 20, 30), KAKAO_ID.toDomain()),
    수요일_3주차_20_30_시작(mentoring(17, 20, 30), mentoring(17, 21, 0), KAKAO_ID.toDomain()),
    수요일_3주차_21_00_시작(mentoring(17, 21, 0), mentoring(17, 21, 30), KAKAO_ID.toDomain()),
    수요일_3주차_21_30_시작(mentoring(17, 21, 30), mentoring(17, 22, 0), KAKAO_ID.toDomain()),
    수요일_4주차_20_00_시작(mentoring(24, 20, 0), mentoring(24, 20, 30), KAKAO_ID.toDomain()),
    수요일_4주차_20_30_시작(mentoring(24, 20, 30), mentoring(24, 21, 0), KAKAO_ID.toDomain()),
    수요일_4주차_21_00_시작(mentoring(24, 21, 0), mentoring(24, 21, 30), KAKAO_ID.toDomain()),
    수요일_4주차_21_30_시작(mentoring(24, 21, 30), mentoring(24, 22, 0), KAKAO_ID.toDomain()),

    목요일_1주차_20_00_시작(mentoring(4, 20, 0), mentoring(4, 20, 30), KAKAO_ID.toDomain()),
    목요일_1주차_20_30_시작(mentoring(4, 20, 30), mentoring(4, 21, 0), KAKAO_ID.toDomain()),
    목요일_1주차_21_00_시작(mentoring(4, 21, 0), mentoring(4, 21, 30), KAKAO_ID.toDomain()),
    목요일_1주차_21_30_시작(mentoring(4, 21, 30), mentoring(4, 22, 0), KAKAO_ID.toDomain()),
    목요일_2주차_20_00_시작(mentoring(11, 20, 0), mentoring(11, 20, 30), KAKAO_ID.toDomain()),
    목요일_2주차_20_30_시작(mentoring(11, 20, 30), mentoring(11, 21, 0), KAKAO_ID.toDomain()),
    목요일_2주차_21_00_시작(mentoring(11, 21, 0), mentoring(11, 21, 30), KAKAO_ID.toDomain()),
    목요일_2주차_21_30_시작(mentoring(11, 21, 30), mentoring(11, 22, 0), KAKAO_ID.toDomain()),
    목요일_3주차_20_00_시작(mentoring(18, 20, 0), mentoring(18, 20, 30), KAKAO_ID.toDomain()),
    목요일_3주차_20_30_시작(mentoring(18, 20, 30), mentoring(18, 21, 0), KAKAO_ID.toDomain()),
    목요일_3주차_21_00_시작(mentoring(18, 21, 0), mentoring(18, 21, 30), KAKAO_ID.toDomain()),
    목요일_3주차_21_30_시작(mentoring(18, 21, 30), mentoring(18, 22, 0), KAKAO_ID.toDomain()),
    목요일_4주차_20_00_시작(mentoring(25, 20, 0), mentoring(25, 20, 30), KAKAO_ID.toDomain()),
    목요일_4주차_20_30_시작(mentoring(25, 20, 30), mentoring(25, 21, 0), KAKAO_ID.toDomain()),
    목요일_4주차_21_00_시작(mentoring(25, 21, 0), mentoring(25, 21, 30), KAKAO_ID.toDomain()),
    목요일_4주차_21_30_시작(mentoring(25, 21, 30), mentoring(25, 22, 0), KAKAO_ID.toDomain()),

    금요일_1주차_20_00_시작(mentoring(5, 20, 0), mentoring(5, 20, 30), KAKAO_ID.toDomain()),
    금요일_1주차_20_30_시작(mentoring(5, 20, 30), mentoring(5, 21, 0), KAKAO_ID.toDomain()),
    금요일_1주차_21_00_시작(mentoring(5, 21, 0), mentoring(5, 21, 30), KAKAO_ID.toDomain()),
    금요일_1주차_21_30_시작(mentoring(5, 21, 30), mentoring(5, 22, 0), KAKAO_ID.toDomain()),
    금요일_2주차_20_00_시작(mentoring(12, 20, 0), mentoring(12, 20, 30), KAKAO_ID.toDomain()),
    금요일_2주차_20_30_시작(mentoring(12, 20, 30), mentoring(12, 21, 0), KAKAO_ID.toDomain()),
    금요일_2주차_21_00_시작(mentoring(12, 21, 0), mentoring(12, 21, 30), KAKAO_ID.toDomain()),
    금요일_2주차_21_30_시작(mentoring(12, 21, 30), mentoring(12, 22, 0), KAKAO_ID.toDomain()),
    금요일_3주차_20_00_시작(mentoring(19, 20, 0), mentoring(19, 20, 30), KAKAO_ID.toDomain()),
    금요일_3주차_20_30_시작(mentoring(19, 20, 30), mentoring(19, 21, 0), KAKAO_ID.toDomain()),
    금요일_3주차_21_00_시작(mentoring(19, 21, 0), mentoring(19, 21, 30), KAKAO_ID.toDomain()),
    금요일_3주차_21_30_시작(mentoring(19, 21, 30), mentoring(19, 22, 0), KAKAO_ID.toDomain()),
    금요일_4주차_20_00_시작(mentoring(26, 20, 0), mentoring(26, 20, 30), KAKAO_ID.toDomain()),
    금요일_4주차_20_30_시작(mentoring(26, 20, 30), mentoring(26, 21, 0), KAKAO_ID.toDomain()),
    금요일_4주차_21_00_시작(mentoring(26, 21, 0), mentoring(26, 21, 30), KAKAO_ID.toDomain()),
    금요일_4주차_21_30_시작(mentoring(26, 21, 30), mentoring(26, 22, 0), KAKAO_ID.toDomain()),

    토요일_1주차_20_00_시작(mentoring(6, 20, 0), mentoring(6, 20, 30), KAKAO_ID.toDomain()),
    토요일_1주차_20_30_시작(mentoring(6, 20, 30), mentoring(6, 21, 0), KAKAO_ID.toDomain()),
    토요일_1주차_21_00_시작(mentoring(6, 21, 0), mentoring(6, 21, 30), KAKAO_ID.toDomain()),
    토요일_1주차_21_30_시작(mentoring(6, 21, 30), mentoring(6, 22, 0), KAKAO_ID.toDomain()),
    토요일_2주차_20_00_시작(mentoring(13, 20, 0), mentoring(13, 20, 30), KAKAO_ID.toDomain()),
    토요일_2주차_20_30_시작(mentoring(13, 20, 30), mentoring(13, 21, 0), KAKAO_ID.toDomain()),
    토요일_2주차_21_00_시작(mentoring(13, 21, 0), mentoring(13, 21, 30), KAKAO_ID.toDomain()),
    토요일_2주차_21_30_시작(mentoring(13, 21, 30), mentoring(13, 22, 0), KAKAO_ID.toDomain()),
    토요일_3주차_20_00_시작(mentoring(20, 20, 0), mentoring(20, 20, 30), KAKAO_ID.toDomain()),
    토요일_3주차_20_30_시작(mentoring(20, 20, 30), mentoring(20, 21, 0), KAKAO_ID.toDomain()),
    토요일_3주차_21_00_시작(mentoring(20, 21, 0), mentoring(20, 21, 30), KAKAO_ID.toDomain()),
    토요일_3주차_21_30_시작(mentoring(20, 21, 30), mentoring(20, 22, 0), KAKAO_ID.toDomain()),
    토요일_4주차_20_00_시작(mentoring(27, 20, 0), mentoring(27, 20, 30), KAKAO_ID.toDomain()),
    토요일_4주차_20_30_시작(mentoring(27, 20, 30), mentoring(27, 21, 0), KAKAO_ID.toDomain()),
    토요일_4주차_21_00_시작(mentoring(27, 21, 0), mentoring(27, 21, 30), KAKAO_ID.toDomain()),
    토요일_4주차_21_30_시작(mentoring(27, 21, 30), mentoring(27, 22, 0), KAKAO_ID.toDomain()),

    일요일_2주차_20_00_시작(mentoring(7, 20, 0), mentoring(7, 20, 30), KAKAO_ID.toDomain()),
    일요일_2주차_20_30_시작(mentoring(7, 20, 30), mentoring(7, 21, 0), KAKAO_ID.toDomain()),
    일요일_2주차_21_00_시작(mentoring(7, 21, 0), mentoring(7, 21, 30), KAKAO_ID.toDomain()),
    일요일_2주차_21_30_시작(mentoring(7, 21, 30), mentoring(7, 22, 0), KAKAO_ID.toDomain()),
    일요일_3주차_20_00_시작(mentoring(14, 20, 0), mentoring(14, 20, 30), KAKAO_ID.toDomain()),
    일요일_3주차_20_30_시작(mentoring(14, 20, 30), mentoring(14, 21, 0), KAKAO_ID.toDomain()),
    일요일_3주차_21_00_시작(mentoring(14, 21, 0), mentoring(14, 21, 30), KAKAO_ID.toDomain()),
    일요일_3주차_21_30_시작(mentoring(14, 21, 30), mentoring(14, 22, 0), KAKAO_ID.toDomain()),
    일요일_4주차_20_00_시작(mentoring(21, 20, 0), mentoring(21, 20, 30), KAKAO_ID.toDomain()),
    일요일_4주차_20_30_시작(mentoring(21, 20, 30), mentoring(21, 21, 0), KAKAO_ID.toDomain()),
    일요일_4주차_21_00_시작(mentoring(21, 21, 0), mentoring(21, 21, 30), KAKAO_ID.toDomain()),
    일요일_4주차_21_30_시작(mentoring(21, 21, 30), mentoring(21, 22, 0), KAKAO_ID.toDomain()),
    ;
}

object MenteeFlow {
    fun apply(
        id: Long = 0L,
        fixture: CoffeeChatFixture,
        mentee: Mentee,
        mentor: Mentor,
    ): CoffeeChat {
        return CoffeeChat.applyFixture(
            id = id,
            mentee = mentee,
            mentor = mentor,
            applyReason = "신청..",
            reservation = Reservation(
                start = fixture.start,
                end = fixture.end,
            ),
        )
    }

    fun apply(
        id: Long = 0L,
        start: LocalDateTime,
        end: LocalDateTime,
        mentee: Mentee,
        mentor: Mentor,
    ): CoffeeChat {
        return CoffeeChat.applyFixture(
            id = id,
            mentee = mentee,
            mentor = mentor,
            applyReason = "신청..",
            reservation = Reservation(
                start = start,
                end = end,
            ),
        )
    }

    fun applyAndCancel(
        id: Long = 0L,
        fixture: CoffeeChatFixture,
        mentee: Mentee,
        mentor: Mentor,
    ): CoffeeChat {
        val coffeeChat: CoffeeChat = apply(id = id, fixture = fixture, mentee = mentee, mentor = mentor)
        coffeeChat.cancel(status = CANCEL_FROM_MENTEE_FLOW, cancelBy = mentee.id, cancelReason = "취소..")
        return coffeeChat
    }

    fun applyAndCancel(
        id: Long = 0L,
        start: LocalDateTime,
        end: LocalDateTime,
        mentee: Mentee,
        mentor: Mentor,
    ): CoffeeChat {
        val coffeeChat: CoffeeChat = apply(id = id, start = start, end = end, mentee = mentee, mentor = mentor)
        coffeeChat.cancel(status = CANCEL_FROM_MENTEE_FLOW, cancelBy = mentee.id, cancelReason = "취소..")
        return coffeeChat
    }

    fun applyAndApprove(
        id: Long = 0L,
        fixture: CoffeeChatFixture,
        mentee: Mentee,
        mentor: Mentor,
    ): CoffeeChat {
        val coffeeChat: CoffeeChat = apply(id = id, fixture = fixture, mentee = mentee, mentor = mentor)
        coffeeChat.approveFromMenteeApply(question = "질문..", strategy = fixture.strategy)
        return coffeeChat
    }

    fun applyAndApprove(
        id: Long = 0L,
        start: LocalDateTime,
        end: LocalDateTime,
        mentee: Mentee,
        mentor: Mentor,
    ): CoffeeChat {
        val coffeeChat: CoffeeChat = apply(id = id, start = start, end = end, mentee = mentee, mentor = mentor)
        coffeeChat.approveFromMenteeApply(question = "질문..", strategy = KAKAO_ID.toDomain())
        return coffeeChat
    }

    fun applyAndReject(
        id: Long = 0L,
        fixture: CoffeeChatFixture,
        mentee: Mentee,
        mentor: Mentor,
    ): CoffeeChat {
        val coffeeChat: CoffeeChat = apply(id = id, fixture = fixture, mentee = mentee, mentor = mentor)
        coffeeChat.rejectFromMenteeApply(rejectReason = "거절..")
        return coffeeChat
    }

    fun applyAndReject(
        id: Long = 0L,
        start: LocalDateTime,
        end: LocalDateTime,
        mentee: Mentee,
        mentor: Mentor,
    ): CoffeeChat {
        val coffeeChat: CoffeeChat = apply(id = id, start = start, end = end, mentee = mentee, mentor = mentor)
        coffeeChat.rejectFromMenteeApply(rejectReason = "거절..")
        return coffeeChat
    }

    fun applyAndComplete(
        id: Long = 0L,
        fixture: CoffeeChatFixture,
        mentee: Mentee,
        mentor: Mentor,
    ): CoffeeChat {
        val coffeeChat: CoffeeChat = apply(id = id, fixture = fixture, mentee = mentee, mentor = mentor)
        coffeeChat.approveFromMenteeApply(question = "질문..", strategy = fixture.strategy)
        coffeeChat.complete(status = MENTEE_APPLY_COFFEE_CHAT_COMPLETE)
        return coffeeChat
    }

    fun applyAndComplete(
        id: Long = 0L,
        start: LocalDateTime,
        end: LocalDateTime,
        mentee: Mentee,
        mentor: Mentor,
    ): CoffeeChat {
        val coffeeChat: CoffeeChat = apply(id = id, start = start, end = end, mentee = mentee, mentor = mentor)
        coffeeChat.approveFromMenteeApply(question = "질문..", strategy = KAKAO_ID.toDomain())
        coffeeChat.complete(status = MENTEE_APPLY_COFFEE_CHAT_COMPLETE)
        return coffeeChat
    }
}

object MentorFlow {
    fun suggest(
        id: Long = 0L,
        mentor: Mentor,
        mentee: Mentee,
    ): CoffeeChat {
        return CoffeeChat.suggestFixture(
            id = id,
            mentor = mentor,
            mentee = mentee,
            suggestReason = "제안..",
        )
    }

    fun suggestAndCancel(
        id: Long = 0L,
        mentor: Mentor,
        mentee: Mentee,
    ): CoffeeChat {
        val coffeeChat: CoffeeChat = suggest(id, mentor, mentee)
        coffeeChat.cancel(status = CANCEL_FROM_MENTOR_FLOW, cancelBy = mentor.id, cancelReason = "취소..")
        return coffeeChat
    }

    fun suggestAndPending(
        id: Long = 0L,
        fixture: CoffeeChatFixture,
        mentor: Mentor,
        mentee: Mentee,
    ): CoffeeChat {
        val coffeeChat: CoffeeChat = suggest(id, mentor, mentee)
        coffeeChat.pendingFromMentorSuggest(question = "질문..", reservation = Reservation(fixture.start, fixture.end))
        return coffeeChat
    }

    fun suggestAndPending(
        id: Long = 0L,
        start: LocalDateTime,
        end: LocalDateTime,
        mentor: Mentor,
        mentee: Mentee,
    ): CoffeeChat {
        val coffeeChat: CoffeeChat = suggest(id, mentor, mentee)
        coffeeChat.pendingFromMentorSuggest(question = "질문..", reservation = Reservation(start, end))
        return coffeeChat
    }

    fun suggestAndReject(
        id: Long = 0L,
        mentor: Mentor,
        mentee: Mentee,
    ): CoffeeChat {
        val coffeeChat: CoffeeChat = suggest(id, mentor, mentee)
        coffeeChat.rejectFromMentorSuggest(rejectReason = "거절..")
        return coffeeChat
    }

    fun suggestAndFinallyApprove(
        id: Long = 0L,
        fixture: CoffeeChatFixture,
        mentor: Mentor,
        mentee: Mentee,
    ): CoffeeChat {
        val coffeeChat: CoffeeChat = suggest(id, mentor, mentee)
        coffeeChat.pendingFromMentorSuggest(question = "질문..", reservation = Reservation(fixture.start, fixture.end))
        coffeeChat.finallyApprovePendingCoffeeChat(strategy = fixture.strategy)
        return coffeeChat
    }

    fun suggestAndFinallyApprove(
        id: Long = 0L,
        start: LocalDateTime,
        end: LocalDateTime,
        mentor: Mentor,
        mentee: Mentee,
    ): CoffeeChat {
        val coffeeChat: CoffeeChat = suggest(id, mentor, mentee)
        coffeeChat.pendingFromMentorSuggest(question = "질문..", reservation = Reservation(start, end))
        coffeeChat.finallyApprovePendingCoffeeChat(strategy = KAKAO_ID.toDomain())
        return coffeeChat
    }

    fun suggestAndFinallyCancel(
        id: Long = 0L,
        fixture: CoffeeChatFixture,
        mentor: Mentor,
        mentee: Mentee,
    ): CoffeeChat {
        val coffeeChat: CoffeeChat = suggest(id, mentor, mentee)
        coffeeChat.pendingFromMentorSuggest(question = "질문..", reservation = Reservation(fixture.start, fixture.end))
        coffeeChat.finallyCancelPendingCoffeeChat(cancelReason = "최종 취소..")
        return coffeeChat
    }

    fun suggestAndFinallyCancel(
        id: Long = 0L,
        start: LocalDateTime,
        end: LocalDateTime,
        mentor: Mentor,
        mentee: Mentee,
    ): CoffeeChat {
        val coffeeChat: CoffeeChat = suggest(id, mentor, mentee)
        coffeeChat.pendingFromMentorSuggest(question = "질문..", reservation = Reservation(start, end))
        coffeeChat.finallyCancelPendingCoffeeChat(cancelReason = "최종 취소..")
        return coffeeChat
    }

    fun suggestAndComplete(
        id: Long = 0L,
        fixture: CoffeeChatFixture,
        mentor: Mentor,
        mentee: Mentee,
    ): CoffeeChat {
        val coffeeChat: CoffeeChat = suggest(id, mentor, mentee)
        coffeeChat.pendingFromMentorSuggest(question = "질문..", reservation = Reservation(fixture.start, fixture.end))
        coffeeChat.finallyApprovePendingCoffeeChat(strategy = fixture.strategy)
        coffeeChat.complete(status = MENTOR_SUGGEST_COFFEE_CHAT_COMPLETE)
        return coffeeChat
    }

    fun suggestAndComplete(
        id: Long = 0L,
        start: LocalDateTime,
        end: LocalDateTime,
        mentor: Mentor,
        mentee: Mentee,
    ): CoffeeChat {
        val coffeeChat: CoffeeChat = suggest(id, mentor, mentee)
        coffeeChat.pendingFromMentorSuggest(question = "질문..", reservation = Reservation(start, end))
        coffeeChat.finallyApprovePendingCoffeeChat(strategy = KAKAO_ID.toDomain())
        coffeeChat.complete(status = MENTOR_SUGGEST_COFFEE_CHAT_COMPLETE)
        return coffeeChat
    }
}
