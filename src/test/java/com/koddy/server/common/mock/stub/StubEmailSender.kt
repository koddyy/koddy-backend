package com.koddy.server.common.mock.stub

import com.koddy.server.global.log.logger
import com.koddy.server.mail.application.adapter.EmailSender
import org.slf4j.Logger

open class StubEmailSender : EmailSender {
    private val log: Logger = logger()

    override fun sendEmailAuthMail(
        targetEmail: String,
        authCode: String,
    ) = log.info("학교 인증 메일 발송 -> Email=[{}], AuthCode=[{}]", targetEmail, authCode)
}
