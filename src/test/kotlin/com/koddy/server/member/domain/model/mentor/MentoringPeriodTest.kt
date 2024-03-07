package com.koddy.server.member.domain.model.mentor

import com.koddy.server.common.UnitTestKt
import com.koddy.server.common.toLocalDate
import com.koddy.server.common.toLocalDateTime
import com.koddy.server.member.exception.MemberException
import com.koddy.server.member.exception.MemberExceptionCode.SCHEDULE_PERIOD_TIME_MUST_ALIGN
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage
import java.time.LocalDateTime

@UnitTestKt
@DisplayName("Member/Mentor -> 도메인 [MentoringPeriod] 테스트")
internal class MentoringPeriodTest : FeatureSpec({
    feature("MentoringPeriod 생성") {
        scenario("Start가 End보다 나중 날짜로 설정되면 MentoringPeriod을 생성할 수 없다") {
            shouldThrow<MemberException> {
                MentoringPeriod(
                    startDate = "2024/3/2".toLocalDate(),
                    endDate = "2024/3/1".toLocalDate(),
                )
            } shouldHaveMessage SCHEDULE_PERIOD_TIME_MUST_ALIGN.message
        }

        scenario("Start가 End보다 같거나 느린 날짜로 설정되면 MentoringPeriod을 생성할 수 있다") {
            val table = mapOf(
                "2024/3/1".toLocalDate() to "2024/3/1".toLocalDate(),
                "2024/3/1".toLocalDate() to "2024/3/2".toLocalDate(),
            )

            table.forEach { (startDate, endDate) ->
                shouldNotThrowAny { MentoringPeriod(startDate = startDate, endDate = endDate) }
            }
        }
    }

    feature("MentoringPeriod's isDateIncluded") {
        scenario("주어진 날짜가 멘토링 진행 기간에 포함되는지 확인한다") {
            val mentoringPeriod = MentoringPeriod(
                startDate = "2024/3/1".toLocalDate(),
                endDate = "2024/3/10".toLocalDate(),
            )

            mapOf(
                "2024/2/26".toLocalDate() to false,
                "2024/3/1".toLocalDate() to true,
                "2024/3/4".toLocalDate() to true,
                "2024/3/10".toLocalDate() to true,
                "2024/3/11".toLocalDate() to false,
            ).forEach { (target, result) ->
                mentoringPeriod.isDateIncluded(target) shouldBe result
            }
        }
    }

    feature("MentoringPeriod's allowedTimeUnit") {
        scenario("멘토링 진행 시간이 멘토의 TimeUnit이랑 일치하는지 확인한다") {
            val timeUnit30 = MentoringPeriod(
                startDate = "2024/3/1".toLocalDate(),
                endDate = "2024/3/10".toLocalDate(),
            )
            val timeUnit60 = MentoringPeriod(
                startDate = "2024/3/1".toLocalDate(),
                endDate = "2024/3/10".toLocalDate(),
                timeUnit = MentoringPeriod.TimeUnit.ONE_HOUR,
            )

            val start: LocalDateTime = "2024/3/1-18:00".toLocalDateTime()
            assertSoftly {
                timeUnit30.allowedTimeUnit(start, start.plusMinutes(30)) shouldBe true
                timeUnit30.allowedTimeUnit(start, start.plusMinutes(60)) shouldBe false
                timeUnit60.allowedTimeUnit(start, start.plusMinutes(30)) shouldBe false
                timeUnit60.allowedTimeUnit(start, start.plusMinutes(60)) shouldBe true
            }
        }
    }
})
