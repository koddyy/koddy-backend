package com.koddy.server.common.fixture;

import com.koddy.server.auth.domain.model.oauth.OAuthTokenResponse;
import com.koddy.server.auth.domain.model.oauth.OAuthUserResponse;
import com.koddy.server.auth.infrastructure.oauth.google.response.GoogleTokenResponse;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.NoSuchElementException;

import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_1;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_2;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_3;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_4;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_5;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_2;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_3;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_4;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_5;
import static com.koddy.server.common.utils.TokenUtils.BEARER_TOKEN;
import static com.koddy.server.common.utils.TokenUtils.EXPIRES_IN;
import static com.koddy.server.common.utils.TokenUtils.ID_TOKEN;
import static com.koddy.server.common.utils.TokenUtils.REFRESH_TOKEN;

@Getter
@RequiredArgsConstructor
public enum OAuthFixture {
    GOOGLE_MENTOR_1(
            MENTOR_1.getEmail().getValue(),
            "JIWON",
            new GoogleTokenResponse(
                    BEARER_TOKEN,
                    ID_TOKEN,
                    "JIWON_TOKEN",
                    REFRESH_TOKEN,
                    EXPIRES_IN
            ),
            MENTOR_1.toGoogleUserResponse()
    ),
    GOOGLE_MENTOR_2(
            MENTOR_2.getEmail().getValue(),
            "JIWON",
            new GoogleTokenResponse(
                    BEARER_TOKEN,
                    ID_TOKEN,
                    "JIWON_TOKEN",
                    REFRESH_TOKEN,
                    EXPIRES_IN
            ),
            MENTOR_2.toGoogleUserResponse()
    ),
    GOOGLE_MENTOR_3(
            MENTOR_3.getEmail().getValue(),
            "JIWON",
            new GoogleTokenResponse(
                    BEARER_TOKEN,
                    ID_TOKEN,
                    "JIWON_TOKEN",
                    REFRESH_TOKEN,
                    EXPIRES_IN
            ),
            MENTOR_3.toGoogleUserResponse()
    ),
    GOOGLE_MENTOR_4(
            MENTOR_4.getEmail().getValue(),
            "JIWON",
            new GoogleTokenResponse(
                    BEARER_TOKEN,
                    ID_TOKEN,
                    "JIWON_TOKEN",
                    REFRESH_TOKEN,
                    EXPIRES_IN
            ),
            MENTOR_4.toGoogleUserResponse()
    ),
    GOOGLE_MENTOR_5(
            MENTOR_5.getEmail().getValue(),
            "JIWON",
            new GoogleTokenResponse(
                    BEARER_TOKEN,
                    ID_TOKEN,
                    "JIWON_TOKEN",
                    REFRESH_TOKEN,
                    EXPIRES_IN
            ),
            MENTOR_5.toGoogleUserResponse()
    ),
    GOOGLE_MENTEE_1(
            MENTEE_1.getEmail().getValue(),
            "JIWON",
            new GoogleTokenResponse(
                    BEARER_TOKEN,
                    ID_TOKEN,
                    "JIWON_TOKEN",
                    REFRESH_TOKEN,
                    EXPIRES_IN
            ),
            MENTEE_1.toGoogleUserResponse()
    ),
    GOOGLE_MENTEE_2(
            MENTEE_2.getEmail().getValue(),
            "JIWON",
            new GoogleTokenResponse(
                    BEARER_TOKEN,
                    ID_TOKEN,
                    "JIWON_TOKEN",
                    REFRESH_TOKEN,
                    EXPIRES_IN
            ),
            MENTEE_2.toGoogleUserResponse()
    ),
    GOOGLE_MENTEE_3(
            MENTEE_3.getEmail().getValue(),
            "JIWON",
            new GoogleTokenResponse(
                    BEARER_TOKEN,
                    ID_TOKEN,
                    "JIWON_TOKEN",
                    REFRESH_TOKEN,
                    EXPIRES_IN
            ),
            MENTEE_3.toGoogleUserResponse()
    ),
    GOOGLE_MENTEE_4(
            MENTEE_4.getEmail().getValue(),
            "JIWON",
            new GoogleTokenResponse(
                    BEARER_TOKEN,
                    ID_TOKEN,
                    "JIWON_TOKEN",
                    REFRESH_TOKEN,
                    EXPIRES_IN
            ),
            MENTEE_4.toGoogleUserResponse()
    ),
    GOOGLE_MENTEE_5(
            MENTEE_5.getEmail().getValue(),
            "JIWON",
            new GoogleTokenResponse(
                    BEARER_TOKEN,
                    ID_TOKEN,
                    "JIWON_TOKEN",
                    REFRESH_TOKEN,
                    EXPIRES_IN
            ),
            MENTEE_5.toGoogleUserResponse()
    ),
    ;

    private final String identifier;
    private final String authorizationCode;
    private final OAuthTokenResponse oAuthTokenResponse;
    private final OAuthUserResponse oAuthUserResponse;

    public static String getAuthorizationCodeByIdentifier(final String identifier) {
        return Arrays.stream(values())
                .filter(value -> value.identifier.equals(identifier))
                .findFirst()
                .orElseThrow(NoSuchElementException::new)
                .authorizationCode;
    }

    public static OAuthTokenResponse parseOAuthTokenByCode(final String authorizationCode) {
        return Arrays.stream(values())
                .filter(value -> value.authorizationCode.equals(authorizationCode))
                .findFirst()
                .orElseThrow(NoSuchElementException::new)
                .oAuthTokenResponse;
    }

    public static OAuthUserResponse parseOAuthUserByAccessToken(final String accessToken) {
        return Arrays.stream(values())
                .filter(value -> value.oAuthTokenResponse.accessToken().equals(accessToken))
                .findFirst()
                .orElseThrow(NoSuchElementException::new)
                .oAuthUserResponse;
    }
}
