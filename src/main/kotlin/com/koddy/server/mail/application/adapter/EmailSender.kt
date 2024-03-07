package com.koddy.server.mail.application.adapter

fun interface EmailSender {
    fun sendEmailAuthMail(
        targetEmail: String,
        authCode: String,
    )
}
