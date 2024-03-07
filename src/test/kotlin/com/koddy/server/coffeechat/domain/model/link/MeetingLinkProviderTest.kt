package com.koddy.server.coffeechat.domain.model.link

import com.koddy.server.coffeechat.exception.CoffeeChatException
import com.koddy.server.coffeechat.exception.CoffeeChatExceptionCode.INVALID_MEETING_LINK_PROVIDER
import com.koddy.server.common.UnitTestKt
import io.kotest.assertions.assertSoftly
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.DescribeSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.throwable.shouldHaveMessage

@UnitTestKt
@DisplayName("CoffeeChat/Link -> MeetingLinkProvider 테스트")
internal class MeetingLinkProviderTest : DescribeSpec({
    describe("MeetingLinkProvider's from") {
        context("제공하지 않는 Provider는") {
            it("INVALID_MEETING_LINK_PROVIDER 예외가 발생한다") {
                shouldThrow<CoffeeChatException> {
                    MeetingLinkProvider.from("anonymous")
                } shouldHaveMessage INVALID_MEETING_LINK_PROVIDER.message
            }
        }

        context("제공하는 Provider는") {
            it("MeetingLinkProvider로 변환해서 응답한다") {
                assertSoftly {
                    MeetingLinkProvider.from("zoom") shouldBe MeetingLinkProvider.ZOOM
                    MeetingLinkProvider.from("google") shouldBe MeetingLinkProvider.GOOGLE
                }
            }
        }
    }
})
