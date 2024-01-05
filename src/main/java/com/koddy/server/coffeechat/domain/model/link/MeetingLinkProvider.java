package com.koddy.server.coffeechat.domain.model.link;

import com.koddy.server.coffeechat.exception.CoffeeChatException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

import static com.koddy.server.coffeechat.exception.CoffeeChatExceptionCode.INVALID_MEETING_LINK_PROVIDER;

@Getter
@RequiredArgsConstructor
public enum MeetingLinkProvider {
    ZOOM("zoom"),
    GOOGLE("google"),
    ;

    private final String provider;

    public static MeetingLinkProvider from(final String provider) {
        return Arrays.stream(values())
                .filter(meetingLinkProvider -> meetingLinkProvider.provider.equals(provider))
                .findFirst()
                .orElseThrow(() -> new CoffeeChatException(INVALID_MEETING_LINK_PROVIDER));
    }
}
