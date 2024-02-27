package com.koddy.server.member.domain.model.mentor

import com.koddy.server.member.exception.MemberException
import com.koddy.server.member.exception.MemberExceptionCode.INVALID_DAY
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldContainInOrder
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage

@DisplayName("Member/Mentor -> 도메인 [DayOfWeek] 테스트")
internal class DayOfWeekTest : DescribeSpec({
    describe("DayOfWeek's from") {
        context("인식할 수 없는 KOR 요일 정보는") {
            it("INVALID_DAY 예외가 발생한다") {
                shouldThrow<MemberException> {
                    DayOfWeek.from("??")
                } shouldHaveMessage INVALID_DAY.message
            }
        }

        context("인식할 수 있는 KOR 요일 정보로") {
            it("DayOfWeek을 조회한다") {
                assertSoftly {
                    DayOfWeek.from("월") shouldBe DayOfWeek.MON
                    DayOfWeek.from("화") shouldBe DayOfWeek.TUE
                    DayOfWeek.from("수") shouldBe DayOfWeek.WED
                    DayOfWeek.from("목") shouldBe DayOfWeek.THU
                    DayOfWeek.from("금") shouldBe DayOfWeek.FRI
                    DayOfWeek.from("토") shouldBe DayOfWeek.SAT
                    DayOfWeek.from("일") shouldBe DayOfWeek.SUN
                }
            }
        }
    }

    describe("DayOfWeek's of(List<String>)") {
        context("인식할 수 있는 KOR 요일 리스트 정보로") {
            it("List<DayOfWeek을>를 조회한다") {
                DayOfWeek.of(listOf("월", "화")) shouldContainInOrder listOf(
                    DayOfWeek.MON,
                    DayOfWeek.TUE,
                )
            }
        }
    }

    describe("DayOfWeek's of(int, int, int)") {
        context("Year, Month, Day 정보를 기준으로") {
            it("DayOfWeek을 조회한다") {
                DayOfWeek.of(2023, 12, 25) shouldBe DayOfWeek.MON
                DayOfWeek.of(2023, 12, 26) shouldBe DayOfWeek.TUE
                DayOfWeek.of(2023, 12, 27) shouldBe DayOfWeek.WED
                DayOfWeek.of(2023, 12, 28) shouldBe DayOfWeek.THU
                DayOfWeek.of(2023, 12, 29) shouldBe DayOfWeek.FRI
                DayOfWeek.of(2023, 12, 30) shouldBe DayOfWeek.SAT
                DayOfWeek.of(2023, 12, 31) shouldBe DayOfWeek.SUN
            }
        }
    }
})
