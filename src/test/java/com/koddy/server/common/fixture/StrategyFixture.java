package com.koddy.server.common.fixture;

import com.koddy.server.coffeechat.domain.model.Strategy;
import com.koddy.server.common.mock.fake.FakeEncryptor;

public enum StrategyFixture {
    ZOOM_LINK(Strategy.Type.ZOOM_LINK, "https://zoom-url/sjiwon"),
    GOOGLE_MEET_LINK(Strategy.Type.GOOGLE_MEET_LINK, "https://google-meet-url/sjiwon"),
    KAKAO_ID(Strategy.Type.KAKAO_ID, "sjiwon-kakao"),
    LINK_ID(Strategy.Type.LINK_ID, "sjiwon-line"),
    WECHAT_ID(Strategy.Type.WECHAT_ID, "sjiwon-wechat"),
    ;

    private final Strategy.Type type;
    private final String value;

    StrategyFixture(
            final Strategy.Type type,
            final String value
    ) {
        this.type = type;
        this.value = value;
    }

    public Strategy toDomain() {
        return Strategy.of(type, value, new FakeEncryptor());
    }

    public Strategy.Type getType() {
        return type;
    }

    public String getValue() {
        return value;
    }
}
