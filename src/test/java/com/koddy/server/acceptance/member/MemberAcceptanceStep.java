package com.koddy.server.acceptance.member;

import com.koddy.server.common.fixture.MenteeFixture;
import com.koddy.server.common.fixture.MentorFixture;
import com.koddy.server.member.presentation.dto.request.LanguageRequest;
import com.koddy.server.member.presentation.dto.request.SignUpMenteeRequest;
import com.koddy.server.member.presentation.dto.request.SignUpMentorRequest;
import io.restassured.response.ValidatableResponse;
import org.springframework.web.util.UriComponentsBuilder;

import static com.koddy.server.acceptance.CommonRequestFixture.deleteRequest;
import static com.koddy.server.acceptance.CommonRequestFixture.postRequest;

public class MemberAcceptanceStep {
    public static ValidatableResponse 멘토_회원가입_후_로그인을_진행한다(final MentorFixture fixture) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/mentors")
                .build()
                .toUri()
                .getPath();

        final SignUpMentorRequest request = new SignUpMentorRequest(
                fixture.getEmail().getValue(),
                fixture.getName(),
                fixture.getProfileImageUrl(),
                fixture.getLanguages()
                        .stream()
                        .map(it -> new LanguageRequest(
                                it.getCategory().getCode(),
                                it.getType().getValue()
                        ))
                        .toList(),
                fixture.getUniversityProfile().getSchool(),
                fixture.getUniversityProfile().getMajor(),
                fixture.getUniversityProfile().getEnteredIn()
        );

        return postRequest(request, uri);
    }

    public static ValidatableResponse 멘티_회원가입_후_로그인을_진행한다(final MenteeFixture fixture) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/mentees")
                .build()
                .toUri()
                .getPath();

        final SignUpMenteeRequest request = new SignUpMenteeRequest(
                fixture.getEmail().getValue(),
                fixture.getName(),
                fixture.getProfileImageUrl(),
                fixture.getNationality().getKor(),
                fixture.getLanguages()
                        .stream()
                        .map(it -> new LanguageRequest(
                                it.getCategory().getCode(),
                                it.getType().getValue()
                        ))
                        .toList(),
                fixture.getInterest().getSchool(),
                fixture.getInterest().getMajor()
        );

        return postRequest(request, uri);
    }

    public static ValidatableResponse 서비스를_탈퇴한다(final String accessToken) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/members")
                .build()
                .toUri()
                .getPath();

        return deleteRequest(accessToken, uri);
    }
}
