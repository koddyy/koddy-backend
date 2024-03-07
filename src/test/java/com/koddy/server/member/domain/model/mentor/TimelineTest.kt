package com.koddy.server.member.domain.model.mentor

import com.koddy.server.common.UnitTestKt
import com.koddy.server.common.toLocalTime
import com.koddy.server.member.exception.MemberException
import com.koddy.server.member.exception.MemberExceptionCode.SCHEDULE_PERIOD_TIME_MUST_ALIGN
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage

@UnitTestKt
@DisplayName("Member/Mentor -> 도메인 [Timeline] 테스트")
internal class TimelineTest : FeatureSpec({
    feature("Timeline 생성") {
        scenario("멘토링 시작 시간이 종료 시간과 같거나 이후면 Timeline을 생성할 수 없다") {
            mapOf(
                "18:00".toLocalTime() to "18:00".toLocalTime(),
                "18:00".toLocalTime() to "17:59".toLocalTime(),
            ).forEach { (startTime, endTime) ->
                shouldThrow<MemberException> {
                    Timeline(
                        dayOfWeek = DayOfWeek.MON,
                        startTime = startTime,
                        endTime = endTime,
                    )
                } shouldHaveMessage SCHEDULE_PERIOD_TIME_MUST_ALIGN.message
            }
        }

        scenario("멘토링 시작 시간이 종료 시간 이전이면 Timeline을 생성할 수 있다") {
            mapOf(
                "18:00".toLocalTime() to "18:01".toLocalTime(),
                "18:00".toLocalTime() to "18:30".toLocalTime(),
            ).forEach { (startTime, endTime) ->
                shouldNotThrowAny {
                    Timeline(
                        dayOfWeek = DayOfWeek.MON,
                        startTime = startTime,
                        endTime = endTime,
                    )
                }
            }
        }
    }

    feature("Timeline's isTimeIncluded") {
        scenario("주어진 시간이 멘토링 가능 시간에 포함되는지 확인한다") {
            val timeline = Timeline(
                dayOfWeek = DayOfWeek.MON,
                startTime = "09:00".toLocalTime(),
                endTime = "13:00".toLocalTime(),
            )

            mapOf(
                "08:30".toLocalTime() to false,
                "08:59".toLocalTime() to false,
                "09:00".toLocalTime() to true,
                "12:50".toLocalTime() to true,
                "13:00".toLocalTime() to true,
                "13:01".toLocalTime() to false,
            ).forEach { (target, result) ->
                timeline.isTimeIncluded(target) shouldBe result
            }
        }
    }
})
