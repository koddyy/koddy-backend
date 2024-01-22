package com.koddy.server.member.presentation;

import com.koddy.server.common.ControllerTest;
import com.koddy.server.member.application.usecase.GetMemberBasicProfileUseCase;
import com.koddy.server.member.application.usecase.query.response.MenteeBasicProfile;
import com.koddy.server.member.application.usecase.query.response.MentorBasicProfile;
import com.koddy.server.member.domain.model.mentee.Mentee;
import com.koddy.server.member.domain.model.mentor.Mentor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_1;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.SnippetFactory.body;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.SnippetFactory.path;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.createHttpSpecSnippets;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.successDocs;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Member -> MemberBasicProflieApiController 테스트")
class MemberBasicProflieApiControllerTest extends ControllerTest {
    @Autowired
    private GetMemberBasicProfileUseCase getMemberBasicProfileUseCase;

    private final Mentor mentor = MENTOR_1.toDomain().apply(1L);
    private final Mentee mentee = MENTEE_1.toDomain().apply(2L);

    @Nested
    @DisplayName("멘토 프로필 조회 API [GET /api/mentors/{mentorId}]")
    class GetMentorProfile {
        private static final String BASE_URL = "/api/mentors/{mentorId}";

        @Test
        @DisplayName("멘토 기본 프로필 정보를 조회한다")
        void success() {
            // given
            given(getMemberBasicProfileUseCase.getMentorProfile(mentor.getId())).willReturn(MentorBasicProfile.of(mentor));

            // when - then
            successfulExecute(
                    getRequest(new UrlWithVariables(BASE_URL, mentor.getId())),
                    status().isOk(),
                    successDocs("MemberApi/PublicProfile/Mentor", createHttpSpecSnippets(
                            pathParameters(
                                    path("mentorId", "멘토 ID(PK)", true)
                            ),
                            responseFields(
                                    body("id", "ID(PK)"),
                                    body("name", "이름"),
                                    body("profileImageUrl", "프로필 이미지 URL"),
                                    body("introduction", "자기 소개"),
                                    body("languages", "사용 가능한 언어"),
                                    body("languages.main", "메인 언어 (1개)"),
                                    body("languages.sub[]", "서브 언어 (0..N개)"),
                                    body("school", "학교"),
                                    body("major", "전공"),
                                    body("enteredIn", "학번")
                            )
                    ))
            );
        }
    }

    @Nested
    @DisplayName("멘티 프로필 조회 API [GET /api/mentees/{menteeId}]")
    class GetMenteeProfile {
        private static final String BASE_URL = "/api/mentees/{menteeId}";

        @Test
        @DisplayName("멘티 기본 프로필 정보를 조회한다")
        void success() {
            // given
            given(getMemberBasicProfileUseCase.getMenteeProfile(mentee.getId())).willReturn(MenteeBasicProfile.of(mentee));

            // when - then
            successfulExecute(
                    getRequest(new UrlWithVariables(BASE_URL, mentee.getId())),
                    status().isOk(),
                    successDocs("MemberApi/PublicProfile/Mentee", createHttpSpecSnippets(
                            pathParameters(
                                    path("menteeId", "멘티 ID(PK)", true)
                            ),
                            responseFields(
                                    body("id", "ID(PK)"),
                                    body("name", "이름"),
                                    body("profileImageUrl", "프로필 이미지 URL"),
                                    body("nationality", "국적"),
                                    body("introduction", "자기 소개"),
                                    body("languages", "사용 가능한 언어"),
                                    body("languages.main", "메인 언어 (1개)"),
                                    body("languages.sub[]", "서브 언어 (0..N개)"),
                                    body("interestSchool", "관심있는 학교"),
                                    body("interestMajor", "관심있는 전공")
                            )
                    ))
            );
        }
    }
}
