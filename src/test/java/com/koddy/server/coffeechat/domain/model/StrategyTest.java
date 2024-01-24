package com.koddy.server.coffeechat.domain.model;

import com.koddy.server.common.UnitTest;
import com.koddy.server.global.utils.encrypt.Encryptor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.koddy.server.coffeechat.domain.model.Strategy.Type.GOOGLE_MEET_LINK;
import static com.koddy.server.coffeechat.domain.model.Strategy.Type.KAKAO_ID;
import static com.koddy.server.coffeechat.domain.model.Strategy.Type.LINK_ID;
import static com.koddy.server.coffeechat.domain.model.Strategy.Type.WECHAT_ID;
import static com.koddy.server.coffeechat.domain.model.Strategy.Type.ZOOM_LINK;
import static com.koddy.server.common.utils.EncryptorFactory.getEncryptor;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("CoffeeChat -> 도메인 [StrategyTest] 테스트")
class StrategyTest extends UnitTest {
    private final Encryptor encryptor = getEncryptor();

    @Test
    @DisplayName("미팅 방식을 결정한다")
    void construct() {
        // given
        final String link = "https://google.com";
        final String messangerId = "sjiwon";

        // when
        final Strategy strategyA = Strategy.of(ZOOM_LINK, link, encryptor);
        final Strategy strategyB = Strategy.of(GOOGLE_MEET_LINK, link, encryptor);
        final Strategy strategyC = Strategy.of(KAKAO_ID, messangerId, encryptor);
        final Strategy strategyD = Strategy.of(LINK_ID, messangerId, encryptor);
        final Strategy strategyE = Strategy.of(WECHAT_ID, messangerId, encryptor);

        // then
        assertAll(
                () -> assertThat(strategyA.getType()).isEqualTo(ZOOM_LINK),
                () -> assertThat(encryptor.symmetricDecrypt(strategyA.getValue())).isEqualTo(link),
                () -> assertThat(strategyB.getType()).isEqualTo(GOOGLE_MEET_LINK),
                () -> assertThat(encryptor.symmetricDecrypt(strategyB.getValue())).isEqualTo(link),
                () -> assertThat(strategyC.getType()).isEqualTo(KAKAO_ID),
                () -> assertThat(encryptor.symmetricDecrypt(strategyC.getValue())).isEqualTo(messangerId),
                () -> assertThat(strategyD.getType()).isEqualTo(LINK_ID),
                () -> assertThat(encryptor.symmetricDecrypt(strategyD.getValue())).isEqualTo(messangerId),
                () -> assertThat(strategyE.getType()).isEqualTo(WECHAT_ID),
                () -> assertThat(encryptor.symmetricDecrypt(strategyE.getValue())).isEqualTo(messangerId)
        );
    }
}
