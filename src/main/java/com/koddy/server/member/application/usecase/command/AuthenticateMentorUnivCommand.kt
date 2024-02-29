package com.koddy.server.member.application.usecase.command

data class AttemptWithMailCommand(
    val mentorId: Long,
    val schoolMail: String,
)

data class ConfirmMailAuthCodeCommand(
    val mentorId: Long,
    val schoolMail: String,
    val authCode: String,
)

data class AttemptWithProofDataCommand(
    val mentorId: Long,
    val proofDataUploadUrl: String,
)
