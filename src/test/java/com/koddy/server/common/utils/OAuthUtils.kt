package com.koddy.server.common.utils

import okhttp3.internal.format

object OAuthUtils {
    const val GOOGLE_PROVIDER: String = "google"
    const val REDIRECT_URI: String = "http://localhost:3000/login/google"
    const val AUTHORIZATION_CODE: String = "oauth-authorization-code"
    const val STATE: String = "oauth-state-value"
    private const val MENTOR_PREFIX: String = "Mentor-%d"
    private const val MENTEE_PREFIX: String = "Mentee-%d"

    fun mentorAuthorizationCode(id: Long): String = format(MENTOR_PREFIX, id)
    fun menteeAuthorizationCode(id: Long): String = format(MENTEE_PREFIX, id)
    fun parseAuthorizationCode(value: String): Long = value.split("-".toRegex())[1].toLong()
}
