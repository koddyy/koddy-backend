package com.koddy.server.mail.application.adapter;

public interface EmailSender {
    void sendEmailAuthMail(final String email, final String authCode);
}
