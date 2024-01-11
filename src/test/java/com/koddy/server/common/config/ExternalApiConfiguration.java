package com.koddy.server.common.config;

import com.koddy.server.auth.application.adapter.OAuthConnector;
import com.koddy.server.auth.application.adapter.OAuthUriGenerator;
import com.koddy.server.auth.domain.model.code.AuthCodeGenerator;
import com.koddy.server.coffeechat.application.adapter.MeetingLinkManager;
import com.koddy.server.common.mock.stub.StubEmailSender;
import com.koddy.server.common.mock.stub.StubFileManager;
import com.koddy.server.common.mock.stub.StubMeetingLinkManager;
import com.koddy.server.common.mock.stub.StubOAuthConnector;
import com.koddy.server.common.mock.stub.StubOAuthUriGenerator;
import com.koddy.server.file.application.adapter.FileManager;
import com.koddy.server.mail.application.adapter.EmailSender;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;

@TestConfiguration
public class ExternalApiConfiguration {
    @Bean
    public OAuthUriGenerator oAuthUri() {
        return new StubOAuthUriGenerator();
    }

    @Bean
    public OAuthConnector oAuthConnector() {
        return new StubOAuthConnector();
    }

    @Bean
    @Primary
    public FileManager fileManager() {
        return new StubFileManager();
    }

    @Bean
    @Primary
    public EmailSender emailSender() {
        return new StubEmailSender();
    }

    @Bean
    @Primary
    public MeetingLinkManager meetingLinkManager() {
        return new StubMeetingLinkManager();
    }

    @Bean
    @Primary
    public AuthCodeGenerator authCodeGenerator() {
        return () -> "123456";
    }
}
