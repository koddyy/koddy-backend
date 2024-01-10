package com.koddy.server.member.domain.event;

import com.koddy.server.global.base.BaseEventModel;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class MailAuthenticatedEvent extends BaseEventModel {
    private final long mentorId;
    private final String targetEmail;
    private final String authCode;

    public MailAuthenticatedEvent(final long mentorId, final String targetEmail, final String authCode) {
        super(LocalDateTime.now());
        this.mentorId = mentorId;
        this.targetEmail = targetEmail;
        this.authCode = authCode;
    }
}
