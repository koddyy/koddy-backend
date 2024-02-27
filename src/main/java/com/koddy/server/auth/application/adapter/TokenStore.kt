package com.koddy.server.auth.application.adapter

interface TokenStore {
    fun synchronizeRefreshToken(
        memberId: Long,
        refreshToken: String,
    )

    fun updateRefreshToken(
        memberId: Long,
        newRefreshToken: String,
    )

    fun deleteRefreshToken(memberId: Long)

    fun isMemberRefreshToken(
        memberId: Long,
        refreshToken: String,
    ): Boolean
}
