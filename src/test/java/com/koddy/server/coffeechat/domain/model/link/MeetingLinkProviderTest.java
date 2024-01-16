package com.koddy.server.coffeechat.domain.model.link;

import com.koddy.server.coffeechat.exception.CoffeeChatException;
import com.koddy.server.common.UnitTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.koddy.server.coffeechat.domain.model.link.MeetingLinkProvider.GOOGLE;
import static com.koddy.server.coffeechat.domain.model.link.MeetingLinkProvider.ZOOM;
import static com.koddy.server.coffeechat.exception.CoffeeChatExceptionCode.INVALID_MEETING_LINK_PROVIDER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("CoffeeChat/Link -> MeetingLinkProvider 테스트")
class MeetingLinkProviderTest extends UnitTest {
    @Test
    @DisplayName("제공하지 않는 Provider에 대해서 MeetingLinkProvider를 가져오려고 하면 예외가 발생한다")
    void throwExceptionByInvalidMeetingLinkProvider() {
        assertThatThrownBy(() -> MeetingLinkProvider.from("anonymous"))
                .isInstanceOf(CoffeeChatException.class)
                .hasMessage(INVALID_MEETING_LINK_PROVIDER.getMessage());
    }

    @Test
    @DisplayName("주어진 Provider에 따른 MeetingLinkProvider를 가져온다")
    void getSpecificMeetingLinkProvider() {
        assertAll(
                () -> assertThat(MeetingLinkProvider.from("zoom")).isEqualTo(ZOOM),
                () -> assertThat(MeetingLinkProvider.from("google")).isEqualTo(GOOGLE)
        );
    }
}
