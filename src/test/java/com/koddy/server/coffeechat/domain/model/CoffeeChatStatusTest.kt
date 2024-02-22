package com.koddy.server.coffeechat.domain.model

import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTEE_APPLY
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTEE_APPLY_COFFEE_CHAT_COMPLETE
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTEE_CANCEL
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTEE_PENDING
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTEE_REJECT
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_APPROVE
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_CANCEL
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_FINALLY_APPROVE
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_FINALLY_CANCEL
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_REJECT
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_SUGGEST
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_SUGGEST_COFFEE_CHAT_COMPLETE
import com.koddy.server.coffeechat.exception.CoffeeChatException
import com.koddy.server.coffeechat.exception.CoffeeChatExceptionCode
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.throwable.shouldHaveMessage

@DisplayName("CoffeeChat -> 도메인 [CoffeeChatStatus] 테스트")
internal class CoffeeChatStatusTest : DescribeSpec({
    describe("CoffeeChatStatus's fromCategory") {
        context("제공하지 않는 카테고리를 적용하면") {
            it("INVALID_COFFEECHAT_STATUS 예외가 발생한다") {
                shouldThrow<CoffeeChatException> {
                    CoffeeChatStatus.fromCategory("anonymous")
                } shouldHaveMessage CoffeeChatExceptionCode.INVALID_COFFEECHAT_STATUS.message
            }
        }

        context("[대기 상태 = waiting] 카테고리를 적용해서") {
            val expected: List<CoffeeChatStatus> = listOf(
                MENTEE_APPLY,
                MENTEE_PENDING,
            )

            it("List<CoffeeChatStatus>를 조회한다") {
                assertSoftly {
                    CoffeeChatStatus.fromCategory("waiting") shouldContainExactly expected
                    CoffeeChatStatus.withWaitingCategory() shouldContainExactly expected
                }
            }
        }

        context("[제안 상태 = suggested] 카테고리를 적용해서") {
            val expected: List<CoffeeChatStatus> = listOf(MENTOR_SUGGEST)

            it("List<CoffeeChatStatus>를 조회한다") {
                assertSoftly {
                    CoffeeChatStatus.fromCategory("suggested") shouldContainExactly expected
                    CoffeeChatStatus.withSuggstedCategory() shouldContainExactly expected
                }
            }
        }

        context("[예정 상태 = scheduled] 카테고리를 적용해서") {
            val expected: List<CoffeeChatStatus> = listOf(
                MENTOR_APPROVE,
                MENTOR_FINALLY_APPROVE,
            )

            it("List<CoffeeChatStatus>를 조회한다") {
                assertSoftly {
                    CoffeeChatStatus.fromCategory("scheduled") shouldContainExactly expected
                    CoffeeChatStatus.withScheduledCategory() shouldContainExactly expected
                }
            }
        }

        context("[지나간 상태 = passed] 카테고리를 적용해서") {
            val expected: List<CoffeeChatStatus> = listOf(
                MENTEE_CANCEL,
                MENTOR_REJECT,
                MENTEE_APPLY_COFFEE_CHAT_COMPLETE,
                MENTOR_CANCEL,
                MENTEE_REJECT,
                MENTOR_FINALLY_CANCEL,
                MENTOR_SUGGEST_COFFEE_CHAT_COMPLETE,
            )

            it("List<CoffeeChatStatus>를 조회한다") {
                assertSoftly {
                    CoffeeChatStatus.fromCategory("passed") shouldContainExactly expected
                    CoffeeChatStatus.withPassedCategory() shouldContainExactly expected
                }
            }
        }
    }

    describe("CoffeeChatStatus's fromCategoryDetail") {
        context("제공하지 않는 카테고리 + 상세 필터를 적용하면") {
            it("INVALID_COFFEECHAT_STATUS 예외가 발생한다") {
                assertSoftly {
                    shouldThrow<CoffeeChatException> {
                        CoffeeChatStatus.fromCategoryDetail("anonymous", "anonymous")
                    } shouldHaveMessage CoffeeChatExceptionCode.INVALID_COFFEECHAT_STATUS.message
                    shouldThrow<CoffeeChatException> {
                        CoffeeChatStatus.fromCategoryDetail("waiting", "anonymous")
                    } shouldHaveMessage CoffeeChatExceptionCode.INVALID_COFFEECHAT_STATUS.message
                    shouldThrow<CoffeeChatException> {
                        CoffeeChatStatus.fromCategoryDetail("scheduled", "anonymous")
                    } shouldHaveMessage CoffeeChatExceptionCode.INVALID_COFFEECHAT_STATUS.message
                    shouldThrow<CoffeeChatException> {
                        CoffeeChatStatus.fromCategoryDetail("passed", "anonymous")
                    } shouldHaveMessage CoffeeChatExceptionCode.INVALID_COFFEECHAT_STATUS.message
                }
            }
        }

        context("카테고리 + 상세 필터를 적용해서") {
            it("List<CoffeeChatStatus>를 조회한다") {
                assertSoftly {
                    CoffeeChatStatus.fromCategoryDetail("waiting", "apply") shouldContainExactly listOf(MENTEE_APPLY)
                    CoffeeChatStatus.fromCategoryDetail("waiting", "pending") shouldContainExactly listOf(MENTEE_PENDING)
                    CoffeeChatStatus.fromCategoryDetail("scheduled", "approve") shouldContainExactly listOf(
                        MENTOR_APPROVE,
                        MENTOR_FINALLY_APPROVE,
                    )
                    CoffeeChatStatus.fromCategoryDetail("passed", "cancel") shouldContainExactly listOf(
                        MENTEE_CANCEL,
                        MENTOR_CANCEL,
                        MENTOR_FINALLY_CANCEL,
                    )
                    CoffeeChatStatus.fromCategoryDetail("passed", "reject") shouldContainExactly listOf(
                        MENTOR_REJECT,
                        MENTEE_REJECT,
                    )
                    CoffeeChatStatus.fromCategoryDetail("passed", "complete") shouldContainExactly listOf(
                        MENTEE_APPLY_COFFEE_CHAT_COMPLETE,
                        MENTOR_SUGGEST_COFFEE_CHAT_COMPLETE,
                    )
                }
            }
        }
    }
})
