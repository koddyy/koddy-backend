package com.koddy.server.acceptance.member

import com.koddy.server.acceptance.RequestHelper
import com.koddy.server.common.fixture.MenteeFixtureStore
import com.koddy.server.common.fixture.MentorFixtureStore
import com.koddy.server.member.domain.model.Language
import com.koddy.server.member.presentation.request.AuthenticationConfirmWithMailRequest
import com.koddy.server.member.presentation.request.AuthenticationWithMailRequest
import com.koddy.server.member.presentation.request.AuthenticationWithProofDataRequest
import com.koddy.server.member.presentation.request.CompleteMenteeProfileRequest
import com.koddy.server.member.presentation.request.CompleteMentorProfileRequest
import com.koddy.server.member.presentation.request.MentorScheduleRequest
import com.koddy.server.member.presentation.request.SignUpMenteeRequest
import com.koddy.server.member.presentation.request.SignUpMentorRequest
import com.koddy.server.member.presentation.request.UpdateMenteeBasicInfoRequest
import com.koddy.server.member.presentation.request.UpdateMentorBasicInfoRequest
import com.koddy.server.member.presentation.request.UpdateMentorScheduleRequest
import com.koddy.server.member.presentation.request.model.LanguageRequestModel
import com.koddy.server.member.presentation.request.model.MentoringPeriodRequestModel
import io.restassured.response.ValidatableResponse

object MemberAcceptanceStep {
    fun 멘토_회원가입_후_로그인을_진행한다(request: SignUpMentorRequest): ValidatableResponse {
        return RequestHelper.postRequest(
            uri = "/api/mentors",
            body = request,
        )
    }

    fun 멘토_회원가입_후_로그인을_진행한다(fixture: MentorFixtureStore.MentorFixture): ValidatableResponse {
        return RequestHelper.postRequest(
            uri = "/api/mentors",
            body = SignUpMentorRequest(
                provider = fixture.platform.provider.value,
                socialId = fixture.platform.socialId!!,
                email = fixture.platform.email!!.value,
                name = fixture.name,
                languages = LanguageRequestModel(
                    main = fixture.languages
                        .first { it.type == Language.Type.MAIN }
                        .category
                        .code,
                    sub = fixture.languages
                        .filter { it.type == Language.Type.SUB }
                        .map { it.category.code },
                ),
                school = fixture.universityProfile.school,
                major = fixture.universityProfile.major,
                enteredIn = fixture.universityProfile.enteredIn,
            ),
        )
    }

    fun 멘티_회원가입_후_로그인을_진행한다(request: SignUpMenteeRequest): ValidatableResponse {
        return RequestHelper.postRequest(
            uri = "/api/mentees",
            body = request,
        )
    }

    fun 멘티_회원가입_후_로그인을_진행한다(fixture: MenteeFixtureStore.MenteeFixture): ValidatableResponse {
        return RequestHelper.postRequest(
            uri = "/api/mentees",
            body = SignUpMenteeRequest(
                provider = fixture.platform.provider.value,
                socialId = fixture.platform.socialId!!,
                email = fixture.platform.email!!.value,
                name = fixture.name,
                nationality = fixture.nationality.code,
                languages = LanguageRequestModel(
                    main = fixture.languages
                        .first { it.type == Language.Type.MAIN }
                        .category
                        .code,
                    sub = fixture.languages
                        .filter { it.type == Language.Type.SUB }
                        .map { it.category.code },
                ),
                interestSchool = fixture.interest.school,
                interestMajor = fixture.interest.major,
            ),
        )
    }

    fun 서비스를_탈퇴한다(accessToken: String): ValidatableResponse {
        return RequestHelper.deleteRequestWithAccessToken(
            uri = "/api/members",
            accessToken = accessToken,
        )
    }

    fun 멘토_프로필을_완성시킨다(
        fixture: MentorFixtureStore.MentorFixture,
        accessToken: String,
    ): ValidatableResponse {
        return RequestHelper.patchRequestWithAccessToken(
            uri = "/api/mentors/me/complete",
            body = CompleteMentorProfileRequest(
                introduction = fixture.introduction,
                profileImageUrl = fixture.profileImageUrl,
                period = MentoringPeriodRequestModel(
                    startDate = fixture.mentoringPeriod.startDate,
                    endDate = fixture.mentoringPeriod.endDate,
                ),
                schedules = fixture.timelines
                    .map {
                        MentorScheduleRequest(
                            dayOfWeek = it.dayOfWeek.kor,
                            start = MentorScheduleRequest.Start(
                                hour = it.startTime.hour,
                                minute = it.startTime.minute,
                            ),
                            end = MentorScheduleRequest.End(
                                hour = it.endTime.hour,
                                minute = it.endTime.minute,
                            ),
                        )
                    },
            ),
            accessToken = accessToken,
        )
    }

    fun 멘토_프로필을_완성시킨다(
        fixture: MentorFixtureStore.MentorFixture,
        period: MentoringPeriodRequestModel,
        accessToken: String,
    ): ValidatableResponse {
        return RequestHelper.patchRequestWithAccessToken(
            uri = "/api/mentors/me/complete",
            body = CompleteMentorProfileRequest(
                introduction = fixture.introduction,
                profileImageUrl = fixture.profileImageUrl,
                period = period,
                schedules = fixture.timelines
                    .map {
                        MentorScheduleRequest(
                            dayOfWeek = it.dayOfWeek.kor,
                            start = MentorScheduleRequest.Start(
                                hour = it.startTime.hour,
                                minute = it.startTime.minute,
                            ),
                            end = MentorScheduleRequest.End(
                                hour = it.endTime.hour,
                                minute = it.endTime.minute,
                            ),
                        )
                    },
            ),
            accessToken = accessToken,
        )
    }

    fun 멘토_프로필을_완성시킨다(
        fixture: MentorFixtureStore.MentorFixture,
        schedules: List<MentorScheduleRequest>,
        accessToken: String,
    ): ValidatableResponse {
        return RequestHelper.patchRequestWithAccessToken(
            uri = "/api/mentors/me/complete",
            body = CompleteMentorProfileRequest(
                introduction = fixture.introduction,
                profileImageUrl = fixture.profileImageUrl,
                period = MentoringPeriodRequestModel(
                    startDate = fixture.mentoringPeriod.startDate,
                    endDate = fixture.mentoringPeriod.endDate,
                ),
                schedules = schedules,
            ),
            accessToken = accessToken,
        )
    }

    fun 멘토_프로필을_완성시킨다(
        fixture: MentorFixtureStore.MentorFixture,
        period: MentoringPeriodRequestModel,
        schedules: List<MentorScheduleRequest>,
        accessToken: String,
    ): ValidatableResponse {
        return RequestHelper.patchRequestWithAccessToken(
            uri = "/api/mentors/me/complete",
            body = CompleteMentorProfileRequest(
                introduction = fixture.introduction,
                profileImageUrl = fixture.profileImageUrl,
                period = period,
                schedules = schedules,
            ),
            accessToken = accessToken,
        )
    }

    fun 멘티_프로필을_완성시킨다(
        fixture: MenteeFixtureStore.MenteeFixture,
        accessToken: String,
    ): ValidatableResponse {
        return RequestHelper.patchRequestWithAccessToken(
            uri = "/api/mentees/me/complete",
            body = CompleteMenteeProfileRequest(
                introduction = fixture.introduction,
                profileImageUrl = fixture.profileImageUrl,
            ),
            accessToken = accessToken,
        )
    }

    fun 멘토_기본_정보를_수정한다(
        fixture: MentorFixtureStore.MentorFixture,
        languageRequestModel: LanguageRequestModel,
        accessToken: String,
    ): ValidatableResponse {
        return RequestHelper.patchRequestWithAccessToken(
            uri = "/api/mentors/me/basic-info",
            body = UpdateMentorBasicInfoRequest(
                name = fixture.name,
                profileImageUrl = fixture.profileImageUrl,
                introduction = fixture.introduction,
                languages = languageRequestModel,
                school = fixture.universityProfile.school,
                major = fixture.universityProfile.major,
                enteredIn = fixture.universityProfile.enteredIn,
            ),
            accessToken = accessToken,
        )
    }

    fun 멘토_스케줄_정보를_수정한다(
        fixture: MentorFixtureStore.MentorFixture,
        accessToken: String,
    ): ValidatableResponse {
        return RequestHelper.patchRequestWithAccessToken(
            uri = "/api/mentors/me/schedules",
            body = UpdateMentorScheduleRequest(
                period = MentoringPeriodRequestModel(
                    startDate = fixture.mentoringPeriod.startDate,
                    endDate = fixture.mentoringPeriod.endDate,
                ),
                schedules = fixture.timelines
                    .map {
                        MentorScheduleRequest(
                            dayOfWeek = it.dayOfWeek.kor,
                            start = MentorScheduleRequest.Start(
                                hour = it.startTime.hour,
                                minute = it.startTime.minute,
                            ),
                            end = MentorScheduleRequest.End(
                                hour = it.endTime.hour,
                                minute = it.endTime.minute,
                            ),
                        )
                    },
            ),
            accessToken = accessToken,
        )
    }

    fun 멘티_기본_정보를_수정한다(
        fixture: MenteeFixtureStore.MenteeFixture,
        languageRequestModel: LanguageRequestModel,
        accessToken: String,
    ): ValidatableResponse {
        return RequestHelper.patchRequestWithAccessToken(
            uri = "/api/mentees/me/basic-info",
            body = UpdateMenteeBasicInfoRequest(
                name = fixture.name,
                nationality = fixture.nationality.code,
                profileImageUrl = fixture.profileImageUrl,
                introduction = fixture.introduction,
                languages = languageRequestModel,
                interestSchool = fixture.interest.school,
                interestMajor = fixture.interest.major,
            ),
            accessToken = accessToken,
        )
    }

    fun 멘토_마이페이지_프로필을_조회한다(accessToken: String): ValidatableResponse {
        return RequestHelper.getRequestWithAccessToken(
            uri = "/api/mentors/me",
            accessToken = accessToken,
        )
    }

    fun 멘티_마이페이지_프로필을_조회한다(accessToken: String): ValidatableResponse {
        return RequestHelper.getRequestWithAccessToken(
            uri = "/api/mentees/me",
            accessToken = accessToken,
        )
    }

    fun 멘토_기본_프로필을_조회한다(mentorId: Long): ValidatableResponse {
        return RequestHelper.getRequest(
            uri = "/api/mentors/$mentorId",
        )
    }

    fun 멘티_기본_프로필을_조회한다(menteeId: Long): ValidatableResponse {
        return RequestHelper.getRequest(
            uri = "/api/mentees/$menteeId",
        )
    }

    fun 멘토가_메일을_통해서_학교_인증을_시도한다(
        schoolMail: String,
        accessToken: String,
    ): ValidatableResponse {
        return RequestHelper.postRequestWithAccessToken(
            uri = "/api/mentors/me/univ/mail",
            body = AuthenticationWithMailRequest(schoolMail = schoolMail),
            accessToken = accessToken,
        )
    }

    fun 멘토가_학교_메일로_발송된_인증번호를_제출한다(
        schoolMail: String,
        authCode: String,
        accessToken: String,
    ): ValidatableResponse {
        return RequestHelper.postRequestWithAccessToken(
            uri = "/api/mentors/me/univ/mail/confirm",
            body = AuthenticationConfirmWithMailRequest(
                schoolMail = schoolMail,
                authCode = authCode,
            ),
            accessToken = accessToken,
        )
    }

    fun 멘토가_증명자료를_통해서_학교_인증을_시도한다(accessToken: String): ValidatableResponse {
        return RequestHelper.postRequestWithAccessToken(
            uri = "/api/mentors/me/univ/proof-data",
            body = AuthenticationWithProofDataRequest(proofDataUploadUrl = "https://proof-data-upload-url"),
            accessToken = accessToken,
        )
    }

    fun 멘토의_예약된_스케줄_정보를_조회한다(
        mentorId: Long,
        year: Int,
        month: Int,
        accessToken: String,
    ): ValidatableResponse {
        return RequestHelper.getRequestWithAccessToken(
            uri = "/api/mentors/$mentorId/reserved-schedule?year=$year&month=$month",
            accessToken = accessToken,
        )
    }

    fun 커피챗_신청한_멘티를_조회한다(accessToken: String): ValidatableResponse {
        return RequestHelper.getRequestWithAccessToken(
            uri = "/api/mentees/applied-coffeechats",
            accessToken = accessToken,
        )
    }

    fun 멘티들을_둘러본다(uri: String): ValidatableResponse {
        return RequestHelper.getRequest(
            uri = uri,
        )
    }

    fun 커피챗_제안한_멘토를_조회한다(accessToken: String): ValidatableResponse {
        return RequestHelper.getRequestWithAccessToken(
            uri = "/api/mentors/suggested-coffeechats",
            accessToken = accessToken,
        )
    }

    fun 멘토들을_둘러본다(uri: String): ValidatableResponse {
        return RequestHelper.getRequest(
            uri = uri,
        )
    }
}
