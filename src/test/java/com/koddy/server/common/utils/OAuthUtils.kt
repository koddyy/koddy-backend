package com.koddy.server.common.utils

object OAuthUtils {
    const val GOOGLE_PROVIDER: String = "google"
    const val REDIRECT_URI: String = "http://localhost:3000/login/google"
    const val AUTHORIZATION_CODE: String = "oauth-authorization-code"
    const val STATE: String = "oauth-state-value"
}
