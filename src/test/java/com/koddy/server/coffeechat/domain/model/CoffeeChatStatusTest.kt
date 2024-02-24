package com.koddy.server.coffeechat.domain.model

import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.CANCEL_FROM_MENTEE_FLOW
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.CANCEL_FROM_MENTOR_FLOW
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.Category.PASSED
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.Category.SCHEDULED
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.Category.SUGGESTED
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.Category.WAITING
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.Detail.APPLY
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.Detail.APPROVE
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.Detail.CANCEL
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.Detail.COMPLETE
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.Detail.PENDING
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.Detail.REJECT
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTEE_APPLY
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTEE_APPLY_COFFEE_CHAT_COMPLETE
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTEE_PENDING
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTEE_REJECT
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_APPROVE
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_FINALLY_APPROVE
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_FINALLY_CANCEL
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_REJECT
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_SUGGEST
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_SUGGEST_COFFEE_CHAT_COMPLETE
import io.kotest.assertions.assertSoftly
import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.shouldBe

@DisplayName("CoffeeChat -> 도메인 [CoffeeChatStatus] 테스트")
internal class CoffeeChatStatusTest : DescribeSpec({
    describe("CoffeeChatStatus's isCancelable") {
        context("CoffeeChatStatus에 대해서") {
            it("취소 가능 상태인지 확인한다") {
                assertSoftly {
                    MENTEE_APPLY.isCancelable() shouldBe true
                    MENTOR_REJECT.isCancelable() shouldBe false
                    MENTOR_APPROVE.isCancelable() shouldBe true
                    MENTEE_APPLY_COFFEE_CHAT_COMPLETE.isCancelable() shouldBe false
                    CANCEL_FROM_MENTEE_FLOW.isCancelable() shouldBe false
                    MENTOR_SUGGEST.isCancelable() shouldBe true
                    MENTEE_REJECT.isCancelable() shouldBe false
                    MENTEE_PENDING.isCancelable() shouldBe true
                    MENTOR_FINALLY_CANCEL.isCancelable() shouldBe false
                    MENTOR_FINALLY_APPROVE.isCancelable() shouldBe true
                    MENTOR_SUGGEST_COFFEE_CHAT_COMPLETE.isCancelable() shouldBe false
                    CANCEL_FROM_MENTOR_FLOW.isCancelable() shouldBe false
                }
            }
        }
    }

    describe("CoffeeChatStatus's isMenteeFlow") {
        context("CoffeeChatStatus에 대해서") {
            it("MenteeFlow인지 확인한다") {
                assertSoftly {
                    MENTEE_APPLY.isMenteeFlow() shouldBe true
                    MENTOR_REJECT.isMenteeFlow() shouldBe true
                    MENTOR_APPROVE.isMenteeFlow() shouldBe true
                    MENTEE_APPLY_COFFEE_CHAT_COMPLETE.isMenteeFlow() shouldBe true
                    CANCEL_FROM_MENTEE_FLOW.isMenteeFlow() shouldBe true
                    MENTOR_SUGGEST.isMenteeFlow() shouldBe false
                    MENTEE_REJECT.isMenteeFlow() shouldBe false
                    MENTEE_PENDING.isMenteeFlow() shouldBe false
                    MENTOR_FINALLY_CANCEL.isMenteeFlow() shouldBe false
                    MENTOR_FINALLY_APPROVE.isMenteeFlow() shouldBe false
                    MENTOR_SUGGEST_COFFEE_CHAT_COMPLETE.isMenteeFlow() shouldBe false
                    CANCEL_FROM_MENTOR_FLOW.isMenteeFlow() shouldBe false
                }
            }
        }
    }

    describe("CoffeeChatStatus's fromCategory") {
        context("[대기 상태 = waiting] 카테고리를 적용해서") {
            val expected: List<CoffeeChatStatus> = listOf(
                MENTEE_APPLY,
                MENTEE_PENDING,
            )

            it("List<CoffeeChatStatus>를 조회한다") {
                assertSoftly {
                    CoffeeChatStatus.fromCategory(WAITING) shouldContainAll expected
                    CoffeeChatStatus.withWaitingCategory() shouldContainAll expected
                }
            }
        }

        context("[제안 상태 = suggested] 카테고리를 적용해서") {
            val expected: List<CoffeeChatStatus> = listOf(MENTOR_SUGGEST)

            it("List<CoffeeChatStatus>를 조회한다") {
                assertSoftly {
                    CoffeeChatStatus.fromCategory(SUGGESTED) shouldContainAll expected
                    CoffeeChatStatus.withSuggstedCategory() shouldContainAll expected
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
                    CoffeeChatStatus.fromCategory(SCHEDULED) shouldContainAll expected
                    CoffeeChatStatus.withScheduledCategory() shouldContainAll expected
                }
            }
        }

        context("[지나간 상태 = passed] 카테고리를 적용해서") {
            val expected: List<CoffeeChatStatus> = listOf(
                CANCEL_FROM_MENTEE_FLOW,
                MENTOR_REJECT,
                MENTEE_APPLY_COFFEE_CHAT_COMPLETE,
                CANCEL_FROM_MENTOR_FLOW,
                MENTEE_REJECT,
                MENTOR_FINALLY_CANCEL,
                MENTOR_SUGGEST_COFFEE_CHAT_COMPLETE,
            )

            it("List<CoffeeChatStatus>를 조회한다") {
                assertSoftly {
                    CoffeeChatStatus.fromCategory(PASSED) shouldContainAll expected
                    CoffeeChatStatus.withPassedCategory() shouldContainAll expected
                }
            }
        }
    }

    describe("CoffeeChatStatus's fromCategoryDetail") {
        context("카테고리 + 상세 필터를 적용해서") {
            it("List<CoffeeChatStatus>를 조회한다") {
                assertSoftly {
                    CoffeeChatStatus.fromCategoryDetail(WAITING, APPLY) shouldContainAll listOf(MENTEE_APPLY)
                    CoffeeChatStatus.fromCategoryDetail(WAITING, PENDING) shouldContainAll listOf(MENTEE_PENDING)
                    CoffeeChatStatus.fromCategoryDetail(SCHEDULED, APPROVE) shouldContainAll listOf(
                        MENTOR_APPROVE,
                        MENTOR_FINALLY_APPROVE,
                    )
                    CoffeeChatStatus.fromCategoryDetail(PASSED, CANCEL) shouldContainAll listOf(
                        CANCEL_FROM_MENTEE_FLOW,
                        CANCEL_FROM_MENTOR_FLOW,
                        MENTOR_FINALLY_CANCEL,
                    )
                    CoffeeChatStatus.fromCategoryDetail(PASSED, REJECT) shouldContainAll listOf(
                        MENTOR_REJECT,
                        MENTEE_REJECT,
                    )
                    CoffeeChatStatus.fromCategoryDetail(PASSED, COMPLETE) shouldContainAll listOf(
                        MENTEE_APPLY_COFFEE_CHAT_COMPLETE,
                        MENTOR_SUGGEST_COFFEE_CHAT_COMPLETE,
                    )
                }
            }
        }
    }
})
