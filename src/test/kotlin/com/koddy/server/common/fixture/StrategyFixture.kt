package com.koddy.server.common.fixture

import com.koddy.server.coffeechat.domain.model.Strategy
import com.koddy.server.common.mock.fake.FakeEncryptor

enum class StrategyFixture(
    val type: Strategy.Type,
    val value: String,
) {
    ZOOM_LINK(Strategy.Type.ZOOM_LINK, "https://zoom-url/sjiwon"),
    GOOGLE_MEET_LINK(Strategy.Type.GOOGLE_MEET_LINK, "https://google-meet-url/sjiwon"),
    KAKAO_ID(Strategy.Type.KAKAO_ID, "sjiwon-kakao"),
    LINK_ID(Strategy.Type.LINK_ID, "sjiwon-line"),
    WECHAT_ID(Strategy.Type.WECHAT_ID, "sjiwon-wechat"),
    ;

    fun toDomain(): Strategy {
        return Strategy.of(
            type = type,
            value = value,
            encryptor = FakeEncryptor(),
        )
    }
}
