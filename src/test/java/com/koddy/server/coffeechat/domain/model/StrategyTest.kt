package com.koddy.server.coffeechat.domain.model

import com.koddy.server.common.UnitTestKt
import com.koddy.server.common.mock.fake.FakeEncryptor
import io.kotest.assertions.assertSoftly
import io.kotest.core.annotation.DisplayName
import io.kotest.core.spec.style.FeatureSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe

@UnitTestKt
@DisplayName("CoffeeChat -> 도메인 [Strategy] 테스트")
internal class StrategyTest : FeatureSpec({
    val encryptor = FakeEncryptor()

    feature("Strategy's of") {
        scenario("미팅 방식을 결정한다 [방식에 대한 값 암호화 진행]") {
            val link = "https://google.com"
            val messenger = "sjiwon"

            mapOf(
                Strategy.Type.ZOOM_LINK to link,
                Strategy.Type.GOOGLE_MEET_LINK to link,
                Strategy.Type.KAKAO_ID to messenger,
                Strategy.Type.LINK_ID to messenger,
                Strategy.Type.WECHAT_ID to messenger,
            ).forEach { (type, value) ->
                val result = Strategy.of(type, value, encryptor)
                assertSoftly {
                    result.type shouldBe type
                    result.value shouldNotBe value
                    encryptor.decrypt(result.value) shouldBe value
                }
            }
        }
    }
})
