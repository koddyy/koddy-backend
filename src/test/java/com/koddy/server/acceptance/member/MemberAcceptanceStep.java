package com.koddy.server.acceptance.member;

import com.koddy.server.common.fixture.MenteeFixture;
import com.koddy.server.common.fixture.MentorFixture;
import com.koddy.server.member.domain.model.Language;
import com.koddy.server.member.presentation.request.AuthenticationConfirmWithMailRequest;
import com.koddy.server.member.presentation.request.AuthenticationWithMailRequest;
import com.koddy.server.member.presentation.request.AuthenticationWithProofDataRequest;
import com.koddy.server.member.presentation.request.CompleteMenteeProfileRequest;
import com.koddy.server.member.presentation.request.CompleteMentorProfileRequest;
import com.koddy.server.member.presentation.request.End;
import com.koddy.server.member.presentation.request.MentorScheduleRequest;
import com.koddy.server.member.presentation.request.SignUpMenteeRequest;
import com.koddy.server.member.presentation.request.SignUpMentorRequest;
import com.koddy.server.member.presentation.request.Start;
import com.koddy.server.member.presentation.request.UpdateMenteeBasicInfoRequest;
import com.koddy.server.member.presentation.request.UpdateMentorBasicInfoRequest;
import com.koddy.server.member.presentation.request.UpdateMentorScheduleRequest;
import com.koddy.server.member.presentation.request.model.LanguageRequestModel;
import com.koddy.server.member.presentation.request.model.MentoringPeriodRequestModel;
import io.restassured.response.ValidatableResponse;
import org.springframework.web.util.UriComponentsBuilder;

import static com.koddy.server.acceptance.CommonRequestFixture.deleteRequestWithAccessToken;
import static com.koddy.server.acceptance.CommonRequestFixture.getRequest;
import static com.koddy.server.acceptance.CommonRequestFixture.getRequestWithAccessToken;
import static com.koddy.server.acceptance.CommonRequestFixture.patchRequestWithAccessToken;
import static com.koddy.server.acceptance.CommonRequestFixture.postRequest;
import static com.koddy.server.acceptance.CommonRequestFixture.postRequestWithAccessToken;

public class MemberAcceptanceStep {
    public static ValidatableResponse 멘토_회원가입_후_로그인을_진행한다(final SignUpMentorRequest request) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/mentors")
                .build()
                .toUri()
                .getPath();

        return postRequest(uri, request);
    }

    public static ValidatableResponse 멘토_회원가입_후_로그인을_진행한다(final MentorFixture fixture) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/mentors")
                .build()
                .toUri()
                .getPath();

        final SignUpMentorRequest request = new SignUpMentorRequest(
                fixture.getPlatform().getProvider().getValue(),
                fixture.getPlatform().getSocialId(),
                fixture.getPlatform().getEmail().getValue(),
                fixture.getName(),
                new LanguageRequestModel(
                        fixture.getLanguages()
                                .stream()
                                .filter(it -> it.getType() == Language.Type.MAIN)
                                .toList()
                                .get(0)
                                .getCategory()
                                .getCode(),
                        fixture.getLanguages()
                                .stream()
                                .filter(it -> it.getType() == Language.Type.SUB)
                                .map(it -> it.getCategory().getCode())
                                .toList()
                ),
                fixture.getUniversityProfile().getSchool(),
                fixture.getUniversityProfile().getMajor(),
                fixture.getUniversityProfile().getEnteredIn()
        );

        return postRequest(uri, request);
    }

    public static ValidatableResponse 멘티_회원가입_후_로그인을_진행한다(final SignUpMenteeRequest request) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/mentees")
                .build()
                .toUri()
                .getPath();

        return postRequest(uri, request);
    }

    public static ValidatableResponse 멘티_회원가입_후_로그인을_진행한다(final MenteeFixture fixture) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/mentees")
                .build()
                .toUri()
                .getPath();

        final SignUpMenteeRequest request = new SignUpMenteeRequest(
                fixture.getPlatform().getProvider().getValue(),
                fixture.getPlatform().getSocialId(),
                fixture.getPlatform().getEmail().getValue(),
                fixture.getName(),
                fixture.getNationality().code,
                new LanguageRequestModel(
                        fixture.getLanguages()
                                .stream()
                                .filter(it -> it.getType() == Language.Type.MAIN)
                                .toList()
                                .get(0)
                                .getCategory()
                                .getCode(),
                        fixture.getLanguages()
                                .stream()
                                .filter(it -> it.getType() == Language.Type.SUB)
                                .map(it -> it.getCategory().getCode())
                                .toList()
                ),
                fixture.getInterest().getSchool(),
                fixture.getInterest().getMajor()
        );

        return postRequest(uri, request);
    }

    public static ValidatableResponse 서비스를_탈퇴한다(final String accessToken) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/members")
                .build()
                .toUri()
                .getPath();

        return deleteRequestWithAccessToken(uri, accessToken);
    }

    public static ValidatableResponse 멘토_프로필을_완성시킨다(final MentorFixture fixture, final String accessToken) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/mentors/me/complete")
                .build()
                .toUri()
                .getPath();

        final CompleteMentorProfileRequest request = new CompleteMentorProfileRequest(
                fixture.getIntroduction(),
                fixture.getProfileImageUrl(),
                new MentoringPeriodRequestModel(
                        fixture.getMentoringPeriod().getStartDate(),
                        fixture.getMentoringPeriod().getEndDate()
                ),
                fixture.getTimelines()
                        .stream()
                        .map(it -> new MentorScheduleRequest(
                                it.getDayOfWeek().getKor(),
                                new Start(
                                        it.getStartTime().getHour(),
                                        it.getStartTime().getMinute()
                                ),
                                new End(
                                        it.getEndTime().getHour(),
                                        it.getEndTime().getMinute()
                                )
                        ))
                        .toList()
        );

        return patchRequestWithAccessToken(uri, request, accessToken);
    }

    public static ValidatableResponse 멘티_프로필을_완성시킨다(final MenteeFixture fixture, final String accessToken) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/mentees/me/complete")
                .build()
                .toUri()
                .getPath();

        final CompleteMenteeProfileRequest request = new CompleteMenteeProfileRequest(
                fixture.getIntroduction(),
                fixture.getProfileImageUrl()
        );

        return patchRequestWithAccessToken(uri, request, accessToken);
    }

    public static ValidatableResponse 멘토_기본_정보를_수정한다(
            final MentorFixture fixture,
            final LanguageRequestModel languageRequestModel,
            final String accessToken
    ) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/mentors/me/basic-info")
                .build()
                .toUri()
                .getPath();

        final UpdateMentorBasicInfoRequest request = new UpdateMentorBasicInfoRequest(
                fixture.getName(),
                fixture.getProfileImageUrl(),
                fixture.getIntroduction(),
                languageRequestModel,
                fixture.getUniversityProfile().getSchool(),
                fixture.getUniversityProfile().getMajor(),
                fixture.getUniversityProfile().getEnteredIn()
        );

        return patchRequestWithAccessToken(uri, request, accessToken);
    }

    public static ValidatableResponse 멘토_스케줄_정보를_수정한다(final MentorFixture fixture, final String accessToken) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/mentors/me/schedules")
                .build()
                .toUri()
                .getPath();

        final UpdateMentorScheduleRequest request = new UpdateMentorScheduleRequest(
                new MentoringPeriodRequestModel(
                        fixture.getMentoringPeriod().getStartDate(),
                        fixture.getMentoringPeriod().getEndDate()
                ),
                fixture.getTimelines()
                        .stream()
                        .map(it -> new MentorScheduleRequest(
                                it.getDayOfWeek().getKor(),
                                new Start(
                                        it.getStartTime().getHour(),
                                        it.getStartTime().getMinute()
                                ),
                                new End(
                                        it.getEndTime().getHour(),
                                        it.getEndTime().getMinute()
                                )
                        ))
                        .toList()
        );

        return patchRequestWithAccessToken(uri, request, accessToken);
    }

    public static ValidatableResponse 멘티_기본_정보를_수정한다(
            final MenteeFixture fixture,
            final LanguageRequestModel languageRequestModel,
            final String accessToken
    ) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/mentees/me/basic-info")
                .build()
                .toUri()
                .getPath();

        final UpdateMenteeBasicInfoRequest request = new UpdateMenteeBasicInfoRequest(
                fixture.getName(),
                fixture.getNationality().code,
                fixture.getProfileImageUrl(),
                fixture.getIntroduction(),
                languageRequestModel,
                fixture.getInterest().getSchool(),
                fixture.getInterest().getMajor()
        );

        return patchRequestWithAccessToken(uri, request, accessToken);
    }

    public static ValidatableResponse 멘토_마이페이지_프로필을_조회한다(final String accessToken) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/mentors/me")
                .build()
                .toUri()
                .getPath();

        return getRequestWithAccessToken(uri, accessToken);
    }

    public static ValidatableResponse 멘티_마이페이지_프로필을_조회한다(final String accessToken) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/mentees/me")
                .build()
                .toUri()
                .getPath();

        return getRequestWithAccessToken(uri, accessToken);
    }

    public static ValidatableResponse 멘토_기본_프로필을_조회한다(final long mentorId) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/mentors/{mentorId}")
                .build(mentorId)
                .getPath();

        return getRequest(uri);
    }

    public static ValidatableResponse 멘티_기본_프로필을_조회한다(final long menteeId) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/mentees/{menteeId}")
                .build(menteeId)
                .getPath();

        return getRequest(uri);
    }

    public static ValidatableResponse 멘토가_메일을_통해서_학교_인증을_시도한다(final String schoolMail, final String accessToken) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/mentors/me/univ/mail")
                .build()
                .toUri()
                .getPath();

        final AuthenticationWithMailRequest request = new AuthenticationWithMailRequest(schoolMail);

        return postRequestWithAccessToken(uri, request, accessToken);
    }

    public static ValidatableResponse 멘토가_학교_메일로_발송된_인증번호를_제출한다(
            final String schoolMail,
            final String authCode,
            final String accessToken
    ) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/mentors/me/univ/mail/confirm")
                .build()
                .toUri()
                .getPath();

        final AuthenticationConfirmWithMailRequest request = new AuthenticationConfirmWithMailRequest(schoolMail, authCode);

        return postRequestWithAccessToken(uri, request, accessToken);
    }

    public static ValidatableResponse 멘토가_증명자료를_통해서_학교_인증을_시도한다(final String accessToken) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/mentors/me/univ/proof-data")
                .build()
                .toUri()
                .getPath();

        final AuthenticationWithProofDataRequest request = new AuthenticationWithProofDataRequest("https://proof-data-upload-url");

        return postRequestWithAccessToken(uri, request, accessToken);
    }

    public static ValidatableResponse 멘토의_예약된_스케줄_정보를_조회한다(
            final long mentorId,
            final int year,
            final int month,
            final String accessToken
    ) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/mentors/{mentorId}/reserved-schedule?year={year}&month={month}")
                .build(mentorId, year, month)
                .getPath();

        return getRequestWithAccessToken(uri, accessToken);
    }

    public static ValidatableResponse 커피챗_신청한_멘티를_조회한다(final String accessToken) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/mentees/applied-coffeechats")
                .build()
                .toUri()
                .getPath();

        return getRequestWithAccessToken(uri, accessToken);
    }

    public static ValidatableResponse 멘티들을_둘러본다(final String url) {
        return getRequest(url);
    }

    public static ValidatableResponse 커피챗_제안한_멘토를_조회한다(final String accessToken) {
        final String uri = UriComponentsBuilder
                .fromPath("/api/mentors/suggested-coffeechats")
                .build()
                .toUri()
                .getPath();

        return getRequestWithAccessToken(uri, accessToken);
    }

    public static ValidatableResponse 멘토들을_둘러본다(final String url) {
        return getRequest(url);
    }
}
