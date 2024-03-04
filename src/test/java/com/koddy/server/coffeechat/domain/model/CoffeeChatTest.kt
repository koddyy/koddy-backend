package com.koddy.server.coffeechat.domain.model

import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.CANCEL_FROM_MENTEE_FLOW
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.CANCEL_FROM_MENTOR_FLOW
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
import com.koddy.server.coffeechat.exception.CoffeeChatException
import com.koddy.server.coffeechat.exception.CoffeeChatExceptionCode.CANNOT_APPROVE_STATUS
import com.koddy.server.coffeechat.exception.CoffeeChatExceptionCode.CANNOT_CANCEL_STATUS
import com.koddy.server.coffeechat.exception.CoffeeChatExceptionCode.CANNOT_COMPLETE_STATUS
import com.koddy.server.coffeechat.exception.CoffeeChatExceptionCode.CANNOT_FINALLY_DECIDE_STATUS
import com.koddy.server.coffeechat.exception.CoffeeChatExceptionCode.CANNOT_REJECT_STATUS
import com.koddy.server.common.UnitTestKt
import com.koddy.server.common.fixture.CoffeeChatFixture.월요일_1주차_20_00_시작
import com.koddy.server.common.fixture.CoffeeChatFixture.월요일_2주차_20_00_시작
import com.koddy.server.common.fixture.CoffeeChatFixture.월요일_3주차_20_00_시작
import com.koddy.server.common.fixture.CoffeeChatFixture.월요일_4주차_20_00_시작
import com.koddy.server.common.fixture.MenteeFixtureStore.menteeFixture
import com.koddy.server.common.fixture.MenteeFlow
import com.koddy.server.common.fixture.MentorFixtureStore.mentorFixture
import com.koddy.server.common.fixture.MentorFlow
import com.koddy.server.common.fixture.StrategyFixture.KAKAO_ID
import com.koddy.server.common.mock.fake.FakeEncryptor
import com.koddy.server.common.toLocalDateTime
import com.koddy.server.member.domain.model.mentee.Mentee
import com.koddy.server.member.domain.model.mentor.Mentor
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.throwable.shouldHaveMessage
import java.time.LocalDateTime

private val encryptor = FakeEncryptor()

@UnitTestKt
@DisplayName("CoffeeChat -> 도메인 Aggregate [CoffeeChat] 생성 테스트")
internal class CoffeeChatCreateTest : FeatureSpec({
    val mentee: Mentee = menteeFixture(id = 1L).toDomain()
    val mentor: Mentor = mentorFixture(id = 2L).toDomain()

    feature("CoffeeChat's apply") {
        scenario("멘티가 멘토에게 커피챗을 신청한다") {
            val start: LocalDateTime = "2024/3/1-18:00".toLocalDateTime()
            val coffeeChat: CoffeeChat = CoffeeChat.apply(
                mentee = mentee,
                mentor = mentor,
                applyReason = "신청..",
                reservation = Reservation(
                    start = start,
                    end = start.plusMinutes(30),
                ),
            )

            assertSoftly(coffeeChat) {
                // Effected
                mentorId shouldBe mentor.id
                menteeId shouldBe mentee.id
                status shouldBe MENTEE_APPLY
                reason.applyReason shouldBe "신청.."
                reservation!!.start shouldBe start
                reservation!!.end shouldBe start.plusMinutes(30)

                // Not Effected
                cancelBy shouldBe null
                reason.suggestReason shouldBe null
                reason.cancelReason shouldBe null
                reason.rejectReason shouldBe null
                question shouldBe null
                strategy shouldBe null
            }
        }
    }

    feature("CoffeeChat's suggest") {
        scenario("멘토가 멘티에게 커피챗을 제안한다") {
            val coffeeChat: CoffeeChat = CoffeeChat.suggest(
                mentor = mentor,
                mentee = mentee,
                suggestReason = "제안..",
            )

            assertSoftly(coffeeChat) {
                // Effected
                mentorId shouldBe mentor.id
                menteeId shouldBe mentee.id
                status shouldBe MENTOR_SUGGEST
                reason.suggestReason shouldBe "제안.."

                // Not Effected
                cancelBy shouldBe null
                reason.applyReason shouldBe null
                reason.cancelReason shouldBe null
                reason.rejectReason shouldBe null
                question shouldBe null
                reservation shouldBe null
                strategy shouldBe null
            }
        }
    }
})

@UnitTestKt
@DisplayName("CoffeeChat -> 도메인 Aggregate [CoffeeChat] MenteeFlow 테스트")
internal class CoffeeChatMenteeFlowTest : FeatureSpec({
    val mentee: Mentee = menteeFixture(id = 1L).toDomain()
    val mentor: Mentor = mentorFixture(id = 2L).toDomain()

    feature("CoffeeChat's cancel") {
        scenario("Cancelable 상태가 아니면 취소할 수 없다") {
            listOf(
                MenteeFlow.applyAndReject(id = 1L, fixture = 월요일_1주차_20_00_시작, mentee = mentee, mentor = mentor),
                MenteeFlow.applyAndComplete(id = 2L, fixture = 월요일_2주차_20_00_시작, mentee = mentee, mentor = mentor),
            ).forEach {
                shouldThrow<CoffeeChatException> {
                    it.cancel(
                        status = CANCEL_FROM_MENTEE_FLOW,
                        cancelBy = mentee.id,
                        cancelReason = "취소..",
                    )
                } shouldHaveMessage CANNOT_CANCEL_STATUS.message
            }
        }

        scenario("관련된 멘티가 커피챗을 취소한다") {
            val coffeeChat: CoffeeChat = MenteeFlow.apply(id = 1L, fixture = 월요일_1주차_20_00_시작, mentee = mentee, mentor = mentor)

            coffeeChat.cancel(
                status = CANCEL_FROM_MENTEE_FLOW,
                cancelBy = mentee.id,
                cancelReason = "취소..",
            )
            assertSoftly(coffeeChat) {
                // Effected
                status shouldBe CANCEL_FROM_MENTEE_FLOW
                cancelBy shouldBe mentee.id
                reason.cancelReason shouldBe "취소.."

                // Not Effected
                mentorId shouldBe mentor.id
                menteeId shouldBe mentee.id
                reason.applyReason shouldNotBe null
                reason.suggestReason shouldBe null
                reason.rejectReason shouldBe null
                question shouldBe null
                reservation!!.start shouldBe 월요일_1주차_20_00_시작.start
                reservation!!.end shouldBe 월요일_1주차_20_00_시작.end
                strategy shouldBe null
            }
        }

        scenario("관련된 멘토가 커피챗을 취소한다") {
            val coffeeChat: CoffeeChat = MenteeFlow.apply(id = 1L, fixture = 월요일_1주차_20_00_시작, mentee = mentee, mentor = mentor)

            coffeeChat.cancel(
                status = CANCEL_FROM_MENTEE_FLOW,
                cancelBy = mentor.id,
                cancelReason = "취소..",
            )
            assertSoftly(coffeeChat) {
                // Effected
                status shouldBe CANCEL_FROM_MENTEE_FLOW
                cancelBy shouldBe mentor.id
                reason.cancelReason shouldBe "취소.."

                // Not Effected
                mentorId shouldBe mentor.id
                menteeId shouldBe mentee.id
                reason.applyReason shouldNotBe null
                reason.suggestReason shouldBe null
                reason.rejectReason shouldBe null
                question shouldBe null
                reservation!!.start shouldBe 월요일_1주차_20_00_시작.start
                reservation!!.end shouldBe 월요일_1주차_20_00_시작.end
                strategy shouldBe null
            }
        }
    }

    feature("CoffeeChat's rejectFromMenteeApply & approveFromMenteeApply") {
        scenario("MENTEE_APPLY 상태가 아니면 거절 또는 수락할 수 없다") {
            val coffeeChats: List<CoffeeChat> = listOf(
                MenteeFlow.applyAndReject(id = 1L, fixture = 월요일_1주차_20_00_시작, mentee = mentee, mentor = mentor),
                MenteeFlow.applyAndApprove(id = 2L, fixture = 월요일_2주차_20_00_시작, mentee = mentee, mentor = mentor),
                MenteeFlow.applyAndComplete(id = 3L, fixture = 월요일_3주차_20_00_시작, mentee = mentee, mentor = mentor),
            )

            coffeeChats.forEach {
                shouldThrow<CoffeeChatException> {
                    it.rejectFromMenteeApply(rejectReason = "거절..")
                } shouldHaveMessage CANNOT_REJECT_STATUS.message
            }
            coffeeChats.forEach {
                shouldThrow<CoffeeChatException> {
                    it.approveFromMenteeApply(
                        question = "궁금한점..",
                        strategy = KAKAO_ID.toDomain(),
                    )
                } shouldHaveMessage CANNOT_APPROVE_STATUS.message
            }
        }

        scenario("멘티가 신청한 커피챗을 멘토가 거절한다") {
            val coffeeChat: CoffeeChat = MenteeFlow.apply(id = 1L, fixture = 월요일_1주차_20_00_시작, mentee = mentee, mentor = mentor)

            coffeeChat.rejectFromMenteeApply(rejectReason = "거절..")
            assertSoftly(coffeeChat) {
                // Effected
                status shouldBe MENTOR_REJECT
                reason.rejectReason shouldBe "거절.."

                // Not Effected
                mentorId shouldBe mentor.id
                menteeId shouldBe mentee.id
                cancelBy shouldBe null
                reason.applyReason shouldNotBe null
                reason.suggestReason shouldBe null
                reason.cancelReason shouldBe null
                question shouldBe null
                reservation!!.start shouldBe 월요일_1주차_20_00_시작.start
                reservation!!.end shouldBe 월요일_1주차_20_00_시작.end
                strategy shouldBe null
            }
        }

        scenario("멘티가 신청한 커피챗을 멘토가 수락한다") {
            val coffeeChat: CoffeeChat = MenteeFlow.apply(id = 1L, fixture = 월요일_1주차_20_00_시작, mentee = mentee, mentor = mentor)

            coffeeChat.approveFromMenteeApply(
                question = "궁금한점..",
                strategy = Strategy.of(
                    type = KAKAO_ID.type,
                    value = KAKAO_ID.value,
                    encryptor = encryptor,
                ),
            )
            assertSoftly(coffeeChat) {
                // Effected
                status shouldBe MENTOR_APPROVE
                question shouldBe "궁금한점.."
                strategy!!.type shouldBe KAKAO_ID.type
                strategy!!.value shouldNotBe KAKAO_ID.value
                encryptor.decrypt(strategy!!.value) shouldBe KAKAO_ID.value

                // Not Effected
                mentorId shouldBe mentor.id
                menteeId shouldBe mentee.id
                cancelBy shouldBe null
                reason.applyReason shouldNotBe null
                reason.suggestReason shouldBe null
                reason.cancelReason shouldBe null
                reason.rejectReason shouldBe null
                reservation!!.start shouldBe 월요일_1주차_20_00_시작.start
                reservation!!.end shouldBe 월요일_1주차_20_00_시작.end
            }
        }
    }

    feature("CoffeeChat's complete") {
        scenario("MENTOR_APPROVE 상태가 아니면 완료 상태로 전환할 수 없다") {
            listOf(
                MenteeFlow.apply(id = 1L, fixture = 월요일_1주차_20_00_시작, mentee = mentee, mentor = mentor),
                MenteeFlow.applyAndCancel(id = 2L, fixture = 월요일_2주차_20_00_시작, mentee = mentee, mentor = mentor),
                MenteeFlow.applyAndReject(id = 3L, fixture = 월요일_3주차_20_00_시작, mentee = mentee, mentor = mentor),
                MenteeFlow.applyAndComplete(id = 4L, fixture = 월요일_4주차_20_00_시작, mentee = mentee, mentor = mentor),
            ).forEach {
                shouldThrow<CoffeeChatException> {
                    it.complete(status = MENTEE_APPLY_COFFEE_CHAT_COMPLETE)
                } shouldHaveMessage CANNOT_COMPLETE_STATUS.message
            }
        }

        scenario("진행한 커피챗을 완료 상태로 전환한다") {
            val coffeeChat: CoffeeChat = MenteeFlow.applyAndApprove(id = 1L, fixture = 월요일_1주차_20_00_시작, mentee = mentee, mentor = mentor)

            coffeeChat.complete(status = MENTEE_APPLY_COFFEE_CHAT_COMPLETE)
            assertSoftly(coffeeChat) {
                // Effected
                status shouldBe MENTEE_APPLY_COFFEE_CHAT_COMPLETE

                // Not Effected
                mentorId shouldBe mentor.id
                menteeId shouldBe mentee.id
                cancelBy shouldBe null
                reason.applyReason shouldNotBe null
                reason.suggestReason shouldBe null
                reason.cancelReason shouldBe null
                reason.rejectReason shouldBe null
                question shouldNotBe null
                reservation!!.start shouldBe 월요일_1주차_20_00_시작.start
                reservation!!.end shouldBe 월요일_1주차_20_00_시작.end
                strategy!!.type shouldNotBe null
                strategy!!.value shouldNotBe null
            }
        }
    }
})

@UnitTestKt
@DisplayName("CoffeeChat -> 도메인 Aggregate [CoffeeChat] MentorFlow 테스트")
internal class CoffeeChatMentorFlowTest : FeatureSpec({
    val mentee: Mentee = menteeFixture(id = 1L).toDomain()
    val mentor: Mentor = mentorFixture(id = 2L).toDomain()

    feature("CoffeeChat's cancel") {
        scenario("Cancelable 상태가 아니면 취소할 수 없다") {
            listOf(
                MentorFlow.suggestAndReject(id = 1L, mentor = mentor, mentee = mentee),
                MentorFlow.suggestAndFinallyCancel(id = 2L, fixture = 월요일_1주차_20_00_시작, mentor = mentor, mentee = mentee),
                MentorFlow.suggestAndComplete(id = 3L, fixture = 월요일_2주차_20_00_시작, mentor = mentor, mentee = mentee),
            ).forEach {
                shouldThrow<CoffeeChatException> {
                    it.cancel(
                        status = CANCEL_FROM_MENTOR_FLOW,
                        cancelBy = mentor.id,
                        cancelReason = "취소..",
                    )
                } shouldHaveMessage CANNOT_CANCEL_STATUS.message
            }
        }

        scenario("관련된 멘티가 커피챗을 취소한다") {
            val coffeeChat: CoffeeChat = MentorFlow.suggest(id = 1L, mentee = mentee, mentor = mentor)

            coffeeChat.cancel(
                status = CANCEL_FROM_MENTOR_FLOW,
                cancelBy = mentee.id,
                cancelReason = "취소..",
            )
            assertSoftly(coffeeChat) {
                // Effected
                status shouldBe CANCEL_FROM_MENTOR_FLOW
                cancelBy shouldBe mentee.id
                reason.cancelReason shouldBe "취소.."

                // Not Effected
                mentorId shouldBe mentor.id
                menteeId shouldBe mentee.id
                reason.applyReason shouldBe null
                reason.suggestReason shouldNotBe null
                reason.rejectReason shouldBe null
                question shouldBe null
                reservation shouldBe null
                strategy shouldBe null
            }
        }

        scenario("관련된 멘토가 커피챗을 취소한다") {
            val coffeeChat: CoffeeChat = MentorFlow.suggest(id = 1L, mentee = mentee, mentor = mentor)

            coffeeChat.cancel(
                status = CANCEL_FROM_MENTOR_FLOW,
                cancelBy = mentor.id,
                cancelReason = "취소..",
            )
            assertSoftly(coffeeChat) {
                // Effected
                status shouldBe CANCEL_FROM_MENTOR_FLOW
                cancelBy shouldBe mentor.id
                reason.cancelReason shouldBe "취소.."

                // Not Effected
                mentorId shouldBe mentor.id
                menteeId shouldBe mentee.id
                reason.applyReason shouldBe null
                reason.suggestReason shouldNotBe null
                reason.rejectReason shouldBe null
                question shouldBe null
                reservation shouldBe null
                strategy shouldBe null
            }
        }
    }

    feature("CoffeeChat's rejectFromMentorSuggest & pendingFromMentorSuggest") {
        scenario("MENTOR_SUGGEST 상태가 아니면 거절 또는 1차 수락할 수 없다") {
            val start: LocalDateTime = "2024/3/1-18:00".toLocalDateTime()
            val coffeeChats: List<CoffeeChat> = listOf(
                MentorFlow.suggestAndCancel(id = 1L, mentor = mentor, mentee = mentee),
                MentorFlow.suggestAndReject(id = 2L, mentor = mentor, mentee = mentee),
                MentorFlow.suggestAndPending(id = 3L, fixture = 월요일_1주차_20_00_시작, mentor = mentor, mentee = mentee),
                MentorFlow.suggestAndFinallyCancel(id = 4L, fixture = 월요일_2주차_20_00_시작, mentor = mentor, mentee = mentee),
                MentorFlow.suggestAndFinallyApprove(id = 5L, fixture = 월요일_3주차_20_00_시작, mentor = mentor, mentee = mentee),
                MentorFlow.suggestAndComplete(id = 6L, fixture = 월요일_4주차_20_00_시작, mentor = mentor, mentee = mentee),
            )

            coffeeChats.forEach {
                shouldThrow<CoffeeChatException> {
                    it.rejectFromMentorSuggest(rejectReason = "거절..")
                } shouldHaveMessage CANNOT_REJECT_STATUS.message
            }
            coffeeChats.forEach {
                shouldThrow<CoffeeChatException> {
                    it.pendingFromMentorSuggest(
                        question = "궁금한점..",
                        reservation = Reservation(
                            start = start,
                            end = start.plusMinutes(30),
                        ),
                    )
                } shouldHaveMessage CANNOT_APPROVE_STATUS.message
            }
        }

        scenario("멘토가 제안한 커피챗을 멘티가 거절한다") {
            val coffeeChat: CoffeeChat = MentorFlow.suggest(id = 1L, mentee = mentee, mentor = mentor)

            coffeeChat.rejectFromMentorSuggest(rejectReason = "거절..")
            assertSoftly(coffeeChat) {
                // Effected
                status shouldBe MENTEE_REJECT
                reason.rejectReason shouldBe "거절.."

                // Not Effected
                mentorId shouldBe mentor.id
                menteeId shouldBe mentee.id
                cancelBy shouldBe null
                reason.applyReason shouldBe null
                reason.suggestReason shouldNotBe null
                reason.cancelReason shouldBe null
                question shouldBe null
                reservation shouldBe null
                strategy shouldBe null
            }
        }

        scenario("멘토가 제안한 커피챗을 멘티가 1차 수락한다") {
            val coffeeChat: CoffeeChat = MentorFlow.suggest(id = 1L, mentee = mentee, mentor = mentor)

            coffeeChat.pendingFromMentorSuggest(
                question = "궁금한점..",
                reservation = Reservation(
                    start = 월요일_1주차_20_00_시작.start,
                    end = 월요일_1주차_20_00_시작.end,
                ),
            )
            assertSoftly(coffeeChat) {
                // Effected
                status shouldBe MENTEE_PENDING
                question shouldBe "궁금한점.."
                reservation!!.start shouldBe 월요일_1주차_20_00_시작.start
                reservation!!.end shouldBe 월요일_1주차_20_00_시작.end

                // Not Effected
                mentorId shouldBe mentor.id
                menteeId shouldBe mentee.id
                cancelBy shouldBe null
                reason.applyReason shouldBe null
                reason.suggestReason shouldNotBe null
                reason.cancelReason shouldBe null
                reason.rejectReason shouldBe null
                strategy shouldBe null
            }
        }
    }

    feature("CoffeeChat's finallyCancelPendingCoffeeChat & finallyApprovePendingCoffeeChat") {
        scenario("MENTEE_PENDING 상태가 아니면 최종 취소 또는 최종 수락할 수 없다") {
            val coffeeChats: List<CoffeeChat> = listOf(
                MentorFlow.suggestAndCancel(id = 1L, mentor = mentor, mentee = mentee),
                MentorFlow.suggestAndReject(id = 2L, mentor = mentor, mentee = mentee),
                MentorFlow.suggestAndFinallyCancel(id = 3L, fixture = 월요일_1주차_20_00_시작, mentor = mentor, mentee = mentee),
                MentorFlow.suggestAndFinallyApprove(id = 4L, fixture = 월요일_2주차_20_00_시작, mentor = mentor, mentee = mentee),
                MentorFlow.suggestAndComplete(id = 5L, fixture = 월요일_3주차_20_00_시작, mentor = mentor, mentee = mentee),
            )

            coffeeChats.forEach {
                shouldThrow<CoffeeChatException> {
                    it.finallyCancelPendingCoffeeChat(cancelReason = "최종 취소..")
                } shouldHaveMessage CANNOT_FINALLY_DECIDE_STATUS.message
            }
            coffeeChats.forEach {
                shouldThrow<CoffeeChatException> {
                    it.finallyApprovePendingCoffeeChat(
                        strategy = Strategy.of(
                            type = KAKAO_ID.type,
                            value = KAKAO_ID.value,
                            encryptor = encryptor,
                        ),
                    )
                } shouldHaveMessage CANNOT_FINALLY_DECIDE_STATUS.message
            }
        }

        scenario("멘토가 제안 & 멘티가 1차 수락한 커피챗을 멘토가 최종 취소한다") {
            val coffeeChat: CoffeeChat = MentorFlow.suggestAndPending(id = 1L, fixture = 월요일_1주차_20_00_시작, mentee = mentee, mentor = mentor)

            coffeeChat.finallyCancelPendingCoffeeChat(cancelReason = "최종 취소..")
            assertSoftly(coffeeChat) {
                // Effected
                status shouldBe MENTOR_FINALLY_CANCEL
                reason.cancelReason shouldBe "최종 취소.."

                // Not Effected
                mentorId shouldBe mentor.id
                menteeId shouldBe mentee.id
                cancelBy shouldBe null
                reason.applyReason shouldBe null
                reason.suggestReason shouldNotBe null
                reason.rejectReason shouldBe null
                question shouldNotBe null
                reservation!!.start shouldBe 월요일_1주차_20_00_시작.start
                reservation!!.end shouldBe 월요일_1주차_20_00_시작.end
                strategy shouldBe null
            }
        }

        scenario("멘토가 제안 & 멘티가 1차 수락한 커피챗을 멘토가 최종 수락한다") {
            val coffeeChat: CoffeeChat = MentorFlow.suggestAndPending(id = 1L, fixture = 월요일_1주차_20_00_시작, mentee = mentee, mentor = mentor)

            coffeeChat.finallyApprovePendingCoffeeChat(
                strategy = Strategy.of(
                    type = KAKAO_ID.type,
                    value = KAKAO_ID.value,
                    encryptor = encryptor,
                ),
            )
            assertSoftly(coffeeChat) {
                // Effected
                status shouldBe MENTOR_FINALLY_APPROVE
                strategy!!.type shouldBe KAKAO_ID.type
                strategy!!.value shouldNotBe KAKAO_ID.value
                encryptor.decrypt(strategy!!.value) shouldBe KAKAO_ID.value

                // Not Effected
                mentorId shouldBe mentor.id
                menteeId shouldBe mentee.id
                cancelBy shouldBe null
                reason.applyReason shouldBe null
                reason.suggestReason shouldNotBe null
                reason.cancelReason shouldBe null
                reason.rejectReason shouldBe null
                question shouldNotBe null
                reservation!!.start shouldBe 월요일_1주차_20_00_시작.start
                reservation!!.end shouldBe 월요일_1주차_20_00_시작.end
            }
        }
    }

    feature("CoffeeChat's complete") {
        scenario("MENTOR_FINALLY_APPROVE 상태가 아니면 완료 상태로 전환할 수 없다") {
            listOf(
                MentorFlow.suggest(id = 1L, mentor = mentor, mentee = mentee),
                MentorFlow.suggestAndCancel(id = 2L, mentor = mentor, mentee = mentee),
                MentorFlow.suggestAndReject(id = 3L, mentor = mentor, mentee = mentee),
                MentorFlow.suggestAndPending(id = 4L, fixture = 월요일_1주차_20_00_시작, mentor = mentor, mentee = mentee),
                MentorFlow.suggestAndFinallyCancel(id = 5L, fixture = 월요일_2주차_20_00_시작, mentor = mentor, mentee = mentee),
                MentorFlow.suggestAndComplete(id = 6L, fixture = 월요일_3주차_20_00_시작, mentor = mentor, mentee = mentee),
            ).forEach {
                shouldThrow<CoffeeChatException> {
                    it.complete(status = MENTOR_SUGGEST_COFFEE_CHAT_COMPLETE)
                } shouldHaveMessage CANNOT_COMPLETE_STATUS.message
            }
        }

        scenario("진행한 커피챗을 완료 상태로 전환한다") {
            val coffeeChat: CoffeeChat = MentorFlow.suggestAndFinallyApprove(id = 1L, fixture = 월요일_1주차_20_00_시작, mentee = mentee, mentor = mentor)

            coffeeChat.complete(status = MENTOR_SUGGEST_COFFEE_CHAT_COMPLETE)
            assertSoftly(coffeeChat) {
                // Effected
                status shouldBe MENTOR_SUGGEST_COFFEE_CHAT_COMPLETE

                // Not Effected
                mentorId shouldBe mentor.id
                menteeId shouldBe mentee.id
                cancelBy shouldBe null
                reason.applyReason shouldBe null
                reason.suggestReason shouldNotBe null
                reason.cancelReason shouldBe null
                reason.rejectReason shouldBe null
                question shouldNotBe null
                reservation!!.start shouldBe 월요일_1주차_20_00_시작.start
                reservation!!.end shouldBe 월요일_1주차_20_00_시작.end
                strategy!!.type shouldNotBe null
                strategy!!.value shouldNotBe null
            }
        }
    }
})

@UnitTestKt
@DisplayName("CoffeeChat -> 도메인 Aggregate [CoffeeChat] Reservation Validity 테스트")
internal class CoffeeChatReservationValidityTest : FeatureSpec({
    val mentee: Mentee = menteeFixture(id = 1L).toDomain()
    val mentor: Mentor = mentorFixture(id = 2L).toDomain()

    feature("CoffeeChat's isRequestReservationIncludedSchedules") {
        scenario("예약 예정인 시간대가 현재 예약된 시간대와 겹치는지 확인한다 [reservation == null]") {
            val coffeeChat: CoffeeChat = MentorFlow.suggest(id = 1L, mentee = mentee, mentor = mentor)

            coffeeChat.isRequestReservationIncludedSchedules(
                Reservation(
                    start = "2024/03/01-18:00".toLocalDateTime(),
                    end = "2024/03/01-18:30".toLocalDateTime(),
                )
            ) shouldBe false
        }

        scenario("예약 예정인 시간대가 현재 예약된 시간대와 겹치는지 확인한다 [reservation != null]") {
            val coffeeChat: CoffeeChat = MenteeFlow.apply(
                id = 1L,
                start = "2024/03/01-18:00".toLocalDateTime(),
                end = "2024/03/01-18:30".toLocalDateTime(),
                mentee = mentee,
                mentor = mentor,
            )

            listOf(
                "2024/03/01-17:40".toLocalDateTime(),
                "2024/03/01-17:50".toLocalDateTime(),
                "2024/03/01-18:00".toLocalDateTime(),
                "2024/03/01-18:10".toLocalDateTime(),
                "2024/03/01-18:20".toLocalDateTime(),
            ).forEach {
                coffeeChat.isRequestReservationIncludedSchedules(Reservation(start = it, end = it.plusMinutes(30))) shouldBe true
            }

            listOf(
                "2024/03/01-17:20".toLocalDateTime(),
                "2024/03/01-17:30".toLocalDateTime(),
                "2024/03/01-18:30".toLocalDateTime(),
                "2024/03/01-18:40".toLocalDateTime(),
            ).forEach {
                coffeeChat.isRequestReservationIncludedSchedules(Reservation(start = it, end = it.plusMinutes(30))) shouldBe false
            }
        }
    }
})
