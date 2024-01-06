package com.koddy.server.coffeechat.application.usecase.command;

import com.koddy.server.coffeechat.domain.model.link.MeetingLinkProvider;

public record DeleteMeetingLinkCommand(
        MeetingLinkProvider linkProvider,
        String meetingId
) {
}
