package com.koddy.server.auth.application.usecase.command

data class ReissueTokenCommand(
    val refreshToken: String,
)
