package com.koddy.server.mail.infrastructure

import com.koddy.server.global.exception.GlobalException
import com.koddy.server.global.exception.GlobalExceptionCode.UNEXPECTED_SERVER_ERROR
import com.koddy.server.mail.application.adapter.EmailSender
import jakarta.mail.internet.InternetAddress
import jakarta.mail.internet.MimeMessage
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.mail.javamail.JavaMailSender
import org.springframework.mail.javamail.MimeMessageHelper
import org.springframework.stereotype.Component
import org.thymeleaf.context.Context
import org.thymeleaf.spring6.SpringTemplateEngine

@Component
class DefaultEmailSender(
    private val mailSender: JavaMailSender,
    private val templateEngine: SpringTemplateEngine,
    @Value("\${spring.mail.username}") private val serviceEmail: String,
) : EmailSender {
    private val log: Logger = LoggerFactory.getLogger(this.javaClass)

    override fun sendEmailAuthMail(
        targetEmail: String,
        authCode: String,
    ) {
        val context: Context = Context().apply {
            setVariable("authCode", authCode)
        }
        val mailBody: String = templateEngine.process("EmailAuthCodeTemplate", context)
        sendMail(
            subject = "Koddy 학교 인증 메일입니다.",
            email = targetEmail,
            mailBody = mailBody,
        )
    }

    private fun sendMail(
        subject: String,
        email: String,
        mailBody: String,
    ) {
        try {
            val message: MimeMessage = mailSender.createMimeMessage()
            MimeMessageHelper(message, true, "UTF-8").apply {
                setSubject(subject)
                setTo(email)
                setFrom(InternetAddress(serviceEmail, "Koddy"))
                setText(mailBody, true)
            }
            mailSender.send(message)
        } catch (e: Exception) {
            log.error("Email Send Error...", e)
            throw GlobalException(UNEXPECTED_SERVER_ERROR)
        }
    }
}
