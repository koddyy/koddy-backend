package com.koddy.server.common.mock.stub

import com.koddy.server.auth.application.adapter.OAuthLoginProcessor
import com.koddy.server.auth.domain.model.oauth.OAuthProvider
import com.koddy.server.auth.domain.model.oauth.OAuthUserResponse
import com.koddy.server.common.fixture.MenteeFixtureStore.menteeFixture
import com.koddy.server.common.fixture.MentorFixtureStore.mentorFixture
import com.koddy.server.common.utils.OAuthDummy.parseAuthorizationCode

open class StubOAuthLoginProcessor : OAuthLoginProcessor {
    override fun login(
        provider: OAuthProvider,
        code: String,
        redirectUri: String,
        state: String,
    ): OAuthUserResponse {
        val id: Long = parseAuthorizationCode(code)
        return when {
            code.contains("Mentor") -> mentorFixture(id = id).toGoogleUserResponse()
            else -> menteeFixture(id = id).toGoogleUserResponse()
        }
    }
}
