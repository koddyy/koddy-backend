package com.koddy.server.common.config;

import com.koddy.server.auth.application.adapter.OAuthLoginProcessor;
import com.koddy.server.coffeechat.application.adapter.MeetingLinkManager;
import com.koddy.server.common.mock.stub.StubEmailSender;
import com.koddy.server.common.mock.stub.StubMeetingLinkManager;
import com.koddy.server.common.mock.stub.StubOAuthLoginProcessor;
import com.koddy.server.mail.application.adapter.EmailSender;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class ExternalApiConfig {
    @Bean
    public OAuthLoginProcessor oAuthLoginProcessor() {
        return new StubOAuthLoginProcessor();
    }

    @Bean
    public MeetingLinkManager meetingLinkManager() {
        return new StubMeetingLinkManager();
    }

    @Bean
    @Primary
    public EmailSender emailSender() {
        return new StubEmailSender();
    }
}
