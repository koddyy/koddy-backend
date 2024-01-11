package com.koddy.server.common.config;

import com.koddy.server.auth.application.adapter.OAuthConnector;
import com.koddy.server.auth.application.adapter.OAuthUriGenerator;
import com.koddy.server.coffeechat.application.adapter.MeetingLinkManager;
import com.koddy.server.common.mock.stub.StubEmailSender;
import com.koddy.server.common.mock.stub.StubMeetingLinkManager;
import com.koddy.server.common.mock.stub.StubOAuthConnector;
import com.koddy.server.common.mock.stub.StubOAuthUriGenerator;
import com.koddy.server.mail.application.adapter.EmailSender;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class ExternalApiConfig {
    @Bean
    public OAuthUriGenerator oAuthUri() {
        return new StubOAuthUriGenerator();
    }

    @Bean
    public OAuthConnector oAuthConnector() {
        return new StubOAuthConnector();
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
