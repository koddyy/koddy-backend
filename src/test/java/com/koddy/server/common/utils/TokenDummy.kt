package com.koddy.server.common.utils

import com.koddy.server.auth.domain.model.AuthToken

object TokenDummy {
    const val ID_TOKEN: String = "ID-TOKEN"

    const val ACCESS_TOKEN: String = "ACCESS-TOKEN"
    const val MENTOR_ACCESS_TOKEN = "MENTOR-ACCESS-TOKEN"
    const val MENTEE_ACCESS_TOKEN = "MENTEE-ACCESS-TOKEN"
    const val INVALID_ACCESS_TOKEN: String = "INVALID-ACCESS-TOKEN"

    const val REFRESH_TOKEN: String = "REFRESH-TOKEN"
    const val INVALID_REFRESH_TOKEN: String = "INVALID-REFRESH-TOKEN"

    const val EXPIRES_IN: Long = 3000

    fun basicAuthToken(): AuthToken = AuthToken(ACCESS_TOKEN, REFRESH_TOKEN)
}
