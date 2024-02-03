package com.koddy.server.member.presentation;

import com.koddy.server.auth.exception.AuthExceptionCode;
import com.koddy.server.common.ControllerTest;
import com.koddy.server.member.application.usecase.UpdateMenteeInfoUseCase;
import com.koddy.server.member.domain.model.Language;
import com.koddy.server.member.domain.model.mentee.Mentee;
import com.koddy.server.member.domain.model.mentor.Mentor;
import com.koddy.server.member.presentation.dto.request.LanguageRequest;
import com.koddy.server.member.presentation.dto.request.UpdateMenteeBasicInfoRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_1;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.SnippetFactory.body;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.createHttpSpecSnippets;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.failureDocsWithAccessToken;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.successDocsWithAccessToken;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Member -> UpdateMenteeInfoApiController 테스트")
class UpdateMenteeInfoApiControllerTest extends ControllerTest {
    @Autowired
    private UpdateMenteeInfoUseCase updateMenteeInfoUseCase;

    private final Mentor mentor = MENTOR_1.toDomain().apply(1L);
    private final Mentee mentee = MENTEE_1.toDomain().apply(2L);

    @Nested
    @DisplayName("멘티 기본정보 수정 API [PATCH /api/mentees/me/basic-info]")
    class UpdateBasicInfo {
        private static final String BASE_URL = "/api/mentees/me/basic-info";
        private final UpdateMenteeBasicInfoRequest request = new UpdateMenteeBasicInfoRequest(
                MENTEE_1.getName(),
                MENTEE_1.getNationality().getCode(),
                MENTEE_1.getProfileImageUrl(),
                MENTEE_1.getIntroduction(),
                new LanguageRequest(
                        Language.Category.KR.getCode(),
                        List.of(
                                Language.Category.EN.getCode(),
                                Language.Category.JP.getCode(),
                                Language.Category.CN.getCode()
                        )
                ),
                MENTEE_1.getInterest().getSchool(),
                MENTEE_1.getInterest().getMajor()
        );

        @Test
        @DisplayName("멘티가 아니면 권한이 없다")
        void throwExceptionByInvalidPermission() {
            // given
            applyToken(true, mentor);

            // when - then
            failedExecute(
                    patchRequestWithAccessToken(BASE_URL, request),
                    status().isForbidden(),
                    ExceptionSpec.of(AuthExceptionCode.INVALID_PERMISSION),
                    failureDocsWithAccessToken("MemberApi/Update/Mentee/BasicInfo/Failure", createHttpSpecSnippets(
                            requestFields(
                                    body("name", "이름", true),
                                    body("nationality", "국적", "KR EN CN JP VN ETC", true),
                                    body("profileImageUrl", "프로필 이미지 URL", true),
                                    body("introduction", "멘티 자기소개", false),
                                    body("languages", "사용 가능한 언어", "KR EN CN JP VN", true),
                                    body("languages.main", "메인 언어", "1개", true),
                                    body("languages.sub[]", "서브 언어", "0..N개", false),
                                    body("interestSchool", "관심있는 학교", true),
                                    body("interestMajor", "관심있는 전공", true)
                            )
                    ))
            );
        }

        @Test
        @DisplayName("멘티 기본정보를 수정한다")
        void success() {
            // given
            applyToken(true, mentee);
            doNothing()
                    .when(updateMenteeInfoUseCase)
                    .updateBasicInfo(any());

            // when - then
            successfulExecute(
                    patchRequestWithAccessToken(BASE_URL, request),
                    status().isNoContent(),
                    successDocsWithAccessToken("MemberApi/Update/Mentee/BasicInfo/Success", createHttpSpecSnippets(
                            requestFields(
                                    body("name", "이름", true),
                                    body("nationality", "국적", "KR EN CN JP VN ETC", true),
                                    body("profileImageUrl", "프로필 이미지 URL", true),
                                    body("introduction", "멘티 자기소개", false),
                                    body("languages", "사용 가능한 언어", "KR EN CN JP VN", true),
                                    body("languages.main", "메인 언어", "1개", true),
                                    body("languages.sub[]", "서브 언어", "0..N개", false),
                                    body("interestSchool", "관심있는 학교", true),
                                    body("interestMajor", "관심있는 전공", true)
                            )
                    ))
            );
        }
    }
}
