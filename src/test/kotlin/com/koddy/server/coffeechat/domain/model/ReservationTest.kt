package com.koddy.server.coffeechat.domain.model

import com.koddy.server.coffeechat.exception.CoffeeChatException
import com.koddy.server.coffeechat.exception.CoffeeChatExceptionCode.RESERVATION_MUST_ALIGN
import com.koddy.server.common.UnitTestKt
import com.koddy.server.common.toLocalDateTime
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage

@UnitTestKt
@DisplayName("CoffeeChat -> 도메인 [Reservation] 테스트")
internal class ReservationTest : FeatureSpec({
    feature("Reservation 생성") {
        scenario("Start가 End보다 나중 시각으로 설정되면 Reservation을 생성할 수 없다") {
            shouldThrow<CoffeeChatException> {
                Reservation(
                    start = "2024/3/1-18:30".toLocalDateTime(),
                    end = "2024/3/1-18:00".toLocalDateTime(),
                )
            } shouldHaveMessage RESERVATION_MUST_ALIGN.message
        }

        scenario("Start가 End보다 같거나 느린 시각으로 설정되면 Reservation을 생성할 수 있다") {
            val table = mapOf(
                "2024/3/1-18:00".toLocalDateTime() to "2024/3/1-18:00".toLocalDateTime(),
                "2024/3/1-18:00".toLocalDateTime() to "2024/3/1-18:30".toLocalDateTime(),
            )

            table.forEach { (start, end) -> shouldNotThrowAny { Reservation(start = start, end = end) } }
        }
    }

    feature("Reservation's isDateTimeIncluded") {
        scenario("주어진 시각이 예약된 시간과 겹치는지 확인한다") {
            val reservation = Reservation(
                start = "2024/3/1-16:00".toLocalDateTime(),
                end = "2024/3/1-16:30".toLocalDateTime(),
            )

            mapOf(
                "2024/3/1-15:30".toLocalDateTime() to false,
                "2024/3/1-15:50".toLocalDateTime() to true,
                "2024/3/1-16:00".toLocalDateTime() to true,
                "2024/3/1-16:20".toLocalDateTime() to true,
                "2024/3/1-16:30".toLocalDateTime() to false,
            ).forEach { (start, result) ->
                val target = Reservation(
                    start = start,
                    end = start.plusMinutes(30),
                )
                reservation.isDateTimeIncluded(target) shouldBe result
            }
        }
    }
})
