package com.koddy.server.acceptance.member;

import com.koddy.server.common.fixture.MenteeFixture;
import com.koddy.server.common.fixture.MentorFixture;
import com.koddy.server.member.presentation.dto.request.AuthenticationConfirmWithMailRequest;
import com.koddy.server.member.presentation.dto.request.AuthenticationWithMailRequest;
import com.koddy.server.member.presentation.dto.request.AuthenticationWithProofDataRequest;
import com.koddy.server.member.presentation.dto.request.CompleteMenteeProfileRequest;
import com.koddy.server.member.presentation.dto.request.CompleteMentorProfileRequest;
import com.koddy.server.member.presentation.dto.request.LanguageRequest;
import com.koddy.server.member.presentation.dto.request.MentorScheduleRequest;
import com.koddy.server.member.presentation.dto.request.SignUpMenteeRequest;
import com.koddy.server.member.presentation.dto.request.SignUpMentorRequest;
import com.koddy.server.member.presentation.dto.request.UpdateMenteeBasicInfoRequest;
import com.koddy.server.member.presentation.dto.request.UpdateMentorBasicInfoRequest;
import com.koddy.server.member.presentation.dto.request.UpdateMentorScheduleRequest;
import io.restassured.response.ValidatableResponse;
import org.springframework.web.util.UriComponentsBuilder;

import static com.koddy.server.acceptance.CommonRequestFixture.deleteRequest;
import static com.koddy.server.acceptance.CommonRequestFixture.getRequest;
import static com.koddy.server.acceptance.CommonRequestFixture.patchRequest;
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

    public static ValidatableResponse 멘토_프로필을_완성시킨다(final MentorFixture fixture, final String accessToken) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/mentors/me/complete")
                .build()
                .toUri()
                .getPath();

        final CompleteMentorProfileRequest request = new CompleteMentorProfileRequest(
                fixture.getIntroduction(),
                fixture.getTimelines()
                        .stream()
                        .map(it -> new MentorScheduleRequest(
                                it.getStartDate(),
                                it.getEndDate(),
                                it.getDayOfWeek().getKor(),
                                new MentorScheduleRequest.Start(
                                        it.getPeriod().getStartTime().getHour(),
                                        it.getPeriod().getStartTime().getMinute()
                                ),
                                new MentorScheduleRequest.End(
                                        it.getPeriod().getEndTime().getHour(),
                                        it.getPeriod().getEndTime().getMinute()
                                )
                        ))
                        .toList()
        );

        return patchRequest(accessToken, request, uri);
    }

    public static ValidatableResponse 멘티_프로필을_완성시킨다(final MenteeFixture fixture, final String accessToken) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/mentees/me/complete")
                .build()
                .toUri()
                .getPath();

        final CompleteMenteeProfileRequest request = new CompleteMenteeProfileRequest(fixture.getIntroduction());

        return patchRequest(accessToken, request, uri);
    }

    public static ValidatableResponse 멘토_기본_정보를_수정한다(final MentorFixture fixture, final String accessToken) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/mentors/me/basic-info")
                .build()
                .toUri()
                .getPath();

        final UpdateMentorBasicInfoRequest request = new UpdateMentorBasicInfoRequest(
                fixture.getName(),
                fixture.getProfileImageUrl(),
                fixture.getIntroduction(),
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

        return patchRequest(accessToken, request, uri);
    }

    public static ValidatableResponse 멘토_스케줄_정보를_수정한다(final MentorFixture fixture, final String accessToken) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/mentors/me/schedules")
                .build()
                .toUri()
                .getPath();

        final UpdateMentorScheduleRequest request = new UpdateMentorScheduleRequest(
                fixture.getTimelines()
                        .stream()
                        .map(it -> new MentorScheduleRequest(
                                it.getStartDate(),
                                it.getEndDate(),
                                it.getDayOfWeek().getKor(),
                                new MentorScheduleRequest.Start(
                                        it.getPeriod().getStartTime().getHour(),
                                        it.getPeriod().getStartTime().getMinute()
                                ),
                                new MentorScheduleRequest.End(
                                        it.getPeriod().getEndTime().getHour(),
                                        it.getPeriod().getEndTime().getMinute()
                                )
                        ))
                        .toList()
        );

        return patchRequest(accessToken, request, uri);
    }

    public static ValidatableResponse 멘티_기본_정보를_수정한다(final MenteeFixture fixture, final String accessToken) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/mentees/me/basic-info")
                .build()
                .toUri()
                .getPath();

        final UpdateMenteeBasicInfoRequest request = new UpdateMenteeBasicInfoRequest(
                fixture.getName(),
                fixture.getNationality().getKor(),
                fixture.getProfileImageUrl(),
                fixture.getIntroduction(),
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

        return patchRequest(accessToken, request, uri);
    }

    public static ValidatableResponse 멘토_프로필을_조회한다(final String accessToken) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/mentors/me")
                .build()
                .toUri()
                .getPath();

        return getRequest(accessToken, uri);
    }

    public static ValidatableResponse 멘티_프로필을_조회한다(final String accessToken) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/mentees/me")
                .build()
                .toUri()
                .getPath();

        return getRequest(accessToken, uri);
    }

    public static ValidatableResponse 멘토가_메일을_통해서_학교_인증을_시도한다(final String accessToken) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/mentors/me/univ/mail")
                .build()
                .toUri()
                .getPath();

        final AuthenticationWithMailRequest request = new AuthenticationWithMailRequest("sjiwon@kyonggi.ac.kr");

        return postRequest(accessToken, request, uri);
    }

    public static ValidatableResponse 멘토가_학교_메일로_발송된_인증번호를_제출한다(
            final String authCode,
            final String accessToken
    ) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/mentors/me/univ/mail/confirm")
                .build()
                .toUri()
                .getPath();

        final AuthenticationConfirmWithMailRequest request = new AuthenticationConfirmWithMailRequest("sjiwon@kyonggi.ac.kr", authCode);

        return postRequest(accessToken, request, uri);
    }

    public static ValidatableResponse 멘토가_증명자료를_통해서_학교_인증을_시도한다(final String accessToken) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/mentors/me/univ/proof-data")
                .build()
                .toUri()
                .getPath();

        final AuthenticationWithProofDataRequest request = new AuthenticationWithProofDataRequest("https://proof-data-upload-url");

        return postRequest(accessToken, request, uri);
    }
}
