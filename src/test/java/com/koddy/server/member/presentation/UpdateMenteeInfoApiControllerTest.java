package com.koddy.server.member.presentation;

import com.koddy.server.auth.exception.AuthException;
import com.koddy.server.common.ControllerTest;
import com.koddy.server.member.application.usecase.UpdateMenteeInfoUseCase;
import com.koddy.server.member.domain.model.mentee.Mentee;
import com.koddy.server.member.domain.model.mentor.Mentor;
import com.koddy.server.member.presentation.dto.request.UpdateMenteeBasicInfoRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.RequestBuilder;

import static com.koddy.server.auth.exception.AuthExceptionCode.INVALID_PERMISSION;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_1;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.SnippetFactory.body;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.createHttpSpecSnippets;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.failureDocsWithAccessToken;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.successDocsWithAccessToken;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UpdateMenteeInfoApiController.class)
@DisplayName("Member -> UpdateMenteeInfoApiController 테스트")
class UpdateMenteeInfoApiControllerTest extends ControllerTest {
    @MockBean
    private UpdateMenteeInfoUseCase updateMenteeInfoUseCase;

    private final Mentor mentor = MENTOR_1.toDomain().apply(1L);
    private final Mentee mentee = MENTEE_1.toDomain().apply(2L);

    @Nested
    @DisplayName("멘티 기본정보 수정 API [PATCH /api/mentees/me/basic-info] - Required AccessToken")
    class UpdateBasicInfo {
        private static final String BASE_URL = "/api/mentees/me/basic-info";
        private final UpdateMenteeBasicInfoRequest request = new UpdateMenteeBasicInfoRequest(
                MENTEE_1.getName(),
                MENTEE_1.getNationality(),
                MENTEE_1.getProfileImageUrl(),
                MENTEE_1.getIntroduction(),
                MENTEE_1.getLanguages(),
                MENTEE_1.getInterest().getSchool(),
                MENTEE_1.getInterest().getMajor()
        );

        @Test
        @DisplayName("멘티가 아니면 권한이 없다")
        void throwExceptionByInvalidPermission() throws Exception {
            // given
            mockingToken(true, mentor.getId(), mentor.getRoleTypes());
            doThrow(new AuthException(INVALID_PERMISSION))
                    .when(updateMenteeInfoUseCase)
                    .updateBasicInfo(any());

            // when
            final RequestBuilder requestBuilder = patchWithAccessToken(BASE_URL, request);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isForbidden())
                    .andExpectAll(getResultMatchersViaExceptionCode(INVALID_PERMISSION))
                    .andDo(failureDocsWithAccessToken("MemberApi/Update/Mentee/BasicInfo/Failure", createHttpSpecSnippets(
                            requestFields(
                                    body("name", "이름", true),
                                    body("nationality", "국적", "KOREA USA JAPAN CHINA VIETNAM OTHERS", true),
                                    body("profileImageUrl", "프로필 이미지 URL", true),
                                    body("introduction", "멘티 자기소개", true),
                                    body("languages", "사용 가능한 언어", "[국가코드 기반] KR EN CH JP VN", true),
                                    body("interestSchool", "관심있는 학교", true),
                                    body("interestMajor", "관심있는 전공", true)
                            )
                    )));
        }

        @Test
        @DisplayName("멘티 기본정보를 수정한다")
        void success() throws Exception {
            // given
            mockingToken(true, mentee.getId(), mentee.getRoleTypes());
            doNothing()
                    .when(updateMenteeInfoUseCase)
                    .updateBasicInfo(any());

            // when
            final RequestBuilder requestBuilder = patchWithAccessToken(BASE_URL, request);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andDo(successDocsWithAccessToken("MemberApi/Update/Mentee/BasicInfo/Success", createHttpSpecSnippets(
                            requestFields(
                                    body("name", "이름", true),
                                    body("nationality", "국적", "KOREA USA JAPAN CHINA VIETNAM OTHERS", true),
                                    body("profileImageUrl", "프로필 이미지 URL", true),
                                    body("introduction", "멘티 자기소개", true),
                                    body("languages", "사용 가능한 언어", "[국가코드 기반] KR EN CH JP VN", true),
                                    body("interestSchool", "관심있는 학교", true),
                                    body("interestMajor", "관심있는 전공", true)
                            )
                    )));
        }
    }
}
