package com.koddy.server.mail.infrastructure;

import com.koddy.server.global.exception.GlobalException;
import com.koddy.server.mail.application.adapter.EmailSender;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import static com.koddy.server.global.exception.GlobalExceptionCode.UNEXPECTED_SERVER_ERROR;

@Slf4j
@Component
public class DefaultEmailSender implements EmailSender {
    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;
    private final String serviceEmail;

    public DefaultEmailSender(
            final JavaMailSender mailSender,
            final SpringTemplateEngine templateEngine,
            @Value("${spring.mail.username}") final String serviceEmail
    ) {
        this.mailSender = mailSender;
        this.templateEngine = templateEngine;
        this.serviceEmail = serviceEmail;
    }

    @Override
    public void sendEmailAuthMail(final String targetEmail, final String authCode) {
        final Context context = new Context();
        context.setVariable("authCode", authCode);

        final String mailBody = templateEngine.process(AUTH_TEMPLATE, context);
        sendMail(
                KODDY_AUTH_CODE_TITLE,
                targetEmail,
                mailBody
        );
    }

    private void sendMail(final String subject, final String email, final String mailBody) {
        try {
            final MimeMessage message = mailSender.createMimeMessage();
            final MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setSubject(subject);
            helper.setTo(email);
            helper.setFrom(new InternetAddress(serviceEmail, "Koddy"));
            helper.setText(mailBody, true);

            mailSender.send(message);
        } catch (final Exception e) {
            log.error("Email Send Error...", e);
            throw new GlobalException(UNEXPECTED_SERVER_ERROR);
        }
    }
}
