package com.koddy.server.common.utils

import com.koddy.server.auth.domain.model.AuthToken.REFRESH_TOKEN_HEADER
import com.koddy.server.auth.domain.model.AuthToken.TOKEN_TYPE
import jakarta.servlet.http.Cookie

object TokenUtils {
    const val ID_TOKEN: String = "ID-TOKEN"
    const val ACCESS_TOKEN: String = "ACCESS-TOKEN"
    const val REFRESH_TOKEN: String = "REFRESH-TOKEN"
    const val EXPIRES_IN: Long = 3000

    @JvmStatic
    fun applyAccessToken(): String = "$TOKEN_TYPE $ACCESS_TOKEN"

    @JvmStatic
    fun applyRefreshToken(): Cookie = Cookie(REFRESH_TOKEN_HEADER, REFRESH_TOKEN)
}
