package com.koddy.server.global.utils

import com.koddy.server.common.UnitTestKt
import com.koddy.server.global.exception.GlobalException
import com.koddy.server.global.exception.GlobalExceptionCode
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage
import java.time.LocalDateTime

@UnitTestKt
@DisplayName("Global/Utils -> TimeUtils 테스트")
internal class TimeUtilsTest : DescribeSpec({
    describe("TimeUtils's toLocalTime(Int, Int)") {
        context("Hour 정보가 0..23 범위가 아니면") {
            it("INVALID_TIME_DATA 예외가 발생한다") {
                shouldThrow<GlobalException> {
                    TimeUtils.toLocalTime(24, 0)
                } shouldHaveMessage GlobalExceptionCode.INVALID_TIME_DATA.message
            }
        }

        context("Minute 정보가 0..59 범위가 아니면") {
            it("INVALID_TIME_DATA 예외가 발생한다") {
                shouldThrow<GlobalException> {
                    TimeUtils.toLocalTime(23, 60)
                } shouldHaveMessage GlobalExceptionCode.INVALID_TIME_DATA.message
            }
        }

        context("Hour 정보가 0..23 범위이고 Minute 정보가 0..59 범위이면") {
            it("LocalTime으로 변환된다") {
                assertSoftly {
                    shouldNotThrowAny { TimeUtils.toLocalTime(0, 0) }
                    shouldNotThrowAny { TimeUtils.toLocalTime(23, 59) }
                }
            }
        }
    }

    describe("TimeUtils's toLocalTime(String)") {
        context("시간 정보가 00:00:00 ~ 23:59:59 범위가 아니면") {
            it("INVALID_TIME_DATA 예외가 발생한다") {
                shouldThrow<GlobalException> {
                    TimeUtils.toLocalTime("24:00:00")
                } shouldHaveMessage GlobalExceptionCode.INVALID_TIME_DATA.message
            }
        }

        context("시간 정보가 00:00:00 ~ 23:59:59 범위면") {
            it("LocalTime으로 변환된다") {
                assertSoftly {
                    shouldNotThrowAny { TimeUtils.toLocalTime("00:00:00") }
                    shouldNotThrowAny { TimeUtils.toLocalTime("23:59:59") }
                }
            }
        }
    }

    describe("TimeUtils's toLocalDateTime(String)") {
        context("시간 정보가 00:00:00 ~ 23:59:59 범위가 아니면") {
            it("INVALID_TIME_DATA 예외가 발생한다") {
                shouldThrow<GlobalException> {
                    TimeUtils.toLocalDateTime("2024-02-01T24:00:00")
                } shouldHaveMessage GlobalExceptionCode.INVALID_TIME_DATA.message
            }
        }

        context("시간 정보가 00:00:00 ~ 23:59:59 범위면") {
            it("LocalDateTime으로 변환된다") {
                assertSoftly {
                    shouldNotThrowAny { TimeUtils.toLocalDateTime("2024-02-01T00:00:00") }
                    shouldNotThrowAny { TimeUtils.toLocalDateTime("2024-02-01T23:59:59") }
                }
            }
        }
    }

    describe("TimeUtils's calculateDurationByMinutes(LocalDateTime, LocalDateTime)") {
        context("두 LocalDateTime의 차이를") {
            val start = LocalDateTime.of(2024, 1, 5, 15, 0)
            val end = LocalDateTime.of(2024, 1, 5, 16, 45)

            it("Minute 기준으로 변환해서 응답한다") {
                assertSoftly {
                    TimeUtils.calculateDurationByMinutes(start, end) shouldBe 105
                    TimeUtils.calculateDurationByMinutes(end, start) shouldBe 105
                }
            }
        }
    }

    describe("TimeUtils's kstToUtc(LocalDateTime)") {
        context("KST 기준 시각을 제공하면") {
            val kst = LocalDateTime.of(2024, 1, 5, 15, 0)

            it("UTC 기준 시각으로 변환해서 응답한다") {
                val result: LocalDateTime = TimeUtils.kstToUtc(kst)

                assertSoftly(result) {
                    year shouldBe 2024
                    monthValue shouldBe 1
                    dayOfMonth shouldBe 5
                    hour shouldBe (15 - 9) % 24
                    minute shouldBe 0
                }
            }
        }
    }

    describe("TimeUtils's utcToKst(LocalDateTime)") {
        context("UTC 기준 시각을 제공하면") {
            val utc = LocalDateTime.of(2024, 1, 5, 15, 0)

            it("KST 기준 시각으로 변환해서 응답한다") {
                val result: LocalDateTime = TimeUtils.utcToKst(utc)

                assertSoftly(result) {
                    year shouldBe 2024
                    monthValue shouldBe 1
                    dayOfMonth shouldBe 6
                    hour shouldBe (15 + 9) % 24
                    minute shouldBe 0
                }
            }
        }
    }
})
