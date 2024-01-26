package com.koddy.server.member.presentation;

import com.koddy.server.auth.exception.AuthExceptionCode;
import com.koddy.server.common.ControllerTest;
import com.koddy.server.global.PageResponse;
import com.koddy.server.member.application.usecase.MenteeMainSearchUseCase;
import com.koddy.server.member.application.usecase.query.response.MentorSimpleSearchProfile;
import com.koddy.server.member.domain.model.mentee.Mentee;
import com.koddy.server.member.domain.model.mentor.Mentor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Map;

import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_1;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.SnippetFactory.body;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.SnippetFactory.query;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.createHttpSpecSnippets;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.failureDocsWithAccessToken;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.successDocs;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.successDocsWithAccessToken;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Member -> MenteeMainSearchApiController 테스트")
class MenteeMainSearchApiControllerTest extends ControllerTest {
    @Autowired
    private MenteeMainSearchUseCase menteeMainSearchUseCase;

    private final Mentor mentor = MENTOR_1.toDomain().apply(1L);
    private final Mentee mentee = MENTEE_1.toDomain().apply(2L);

    @Nested
    @DisplayName("커피챗 제안한 멘토 조회 API [GET /api/mentors/suggested-coffeechats]")
    class GetSuggestedMentors {
        private static final String BASE_URL = "/api/mentors/suggested-coffeechats";

        @Test
        @DisplayName("멘티가 아니면 권한이 없다")
        void throwExceptionByInvalidPermission() {
            // given
            applyToken(true, mentor.getId(), mentor.getRole());

            // when - then
            failedExecute(
                    getRequestWithAccessToken(BASE_URL),
                    status().isForbidden(),
                    ExceptionSpec.of(AuthExceptionCode.INVALID_PERMISSION),
                    failureDocsWithAccessToken("MemberApi/Mentee/MainSearch/SuggestedMentors/Failure")
            );
        }

        @Test
        @DisplayName("멘티 자신에게 커피챗을 제안한 멘토들을 최신순 기준으로 조회한다 [Limit 3]")
        void success() {
            // given
            applyToken(true, mentee.getId(), mentee.getRole());
            given(menteeMainSearchUseCase.getSuggestedMentors(mentee.getId())).willReturn(List.of(
                    new MentorSimpleSearchProfile(
                            3,
                            "멘토3",
                            "https://mentor3-url",
                            "서울대학교",
                            "컴퓨터공학부",
                            19
                    ),
                    new MentorSimpleSearchProfile(
                            2,
                            "멘토2",
                            "https://mentor2-url",
                            "연세대학교",
                            "컴퓨터공학부",
                            17
                    ),
                    new MentorSimpleSearchProfile(
                            1,
                            "멘토1",
                            "https://mentor1-url",
                            "고려대학교",
                            "컴퓨터공학부",
                            18
                    )
            ));

            // when - then
            successfulExecute(
                    getRequestWithAccessToken(BASE_URL),
                    status().isOk(),
                    successDocsWithAccessToken("MemberApi/Mentee/MainSearch/SuggestedMentors/Success", createHttpSpecSnippets(
                            responseFields(
                                    body("result[].id", "멘토 ID(PK)"),
                                    body("result[].name", "이름"),
                                    body("result[].profileImageUrl", "프로필 이미지 URL"),
                                    body("result[].school", "학교"),
                                    body("result[].major", "전공"),
                                    body("result[].enteredIn", "학번")
                            )
                    ))
            );
        }
    }

    @Nested
    @DisplayName("멘토 둘러보기 API [GET /api/mentors]")
    class GetMentorsByCondition {
        private static final String BASE_URL = "/api/mentors";

        @Test
        @DisplayName("멘토들을 조건에 따라 둘러본다")
        void success() {
            // given
            given(menteeMainSearchUseCase.getMentorsByCondition(any())).willReturn(new PageResponse<>(
                    List.of(
                            new MentorSimpleSearchProfile(
                                    3,
                                    "멘토3",
                                    "https://mentor3-url",
                                    "서울대학교",
                                    "컴퓨터공학부",
                                    19
                            ),
                            new MentorSimpleSearchProfile(
                                    2,
                                    "멘토2",
                                    "https://mentor2-url",
                                    "연세대학교",
                                    "컴퓨터공학부",
                                    17
                            ),
                            new MentorSimpleSearchProfile(
                                    1,
                                    "멘토1",
                                    "https://mentor1-url",
                                    "고려대학교",
                                    "컴퓨터공학부",
                                    18
                            )
                    ),
                    false
            ));

            // when - then
            final MultiValueMap<String, String> languages = new LinkedMultiValueMap<>();
            languages.add("languages", "EN");
            languages.add("languages", "CN");

            successfulExecute(
                    getRequest(
                            BASE_URL,
                            Map.of("page", "1"),
                            List.of(languages)
                    ),
                    status().isOk(),
                    successDocs("MemberApi/Mentee/MainSearch/Mentors", createHttpSpecSnippets(
                            queryParameters(
                                    query("languages", "선택한 언어", false),
                                    query("page", "페이지", "1부터 시작", true)
                            ),
                            responseFields(
                                    body("result[].id", "멘토 ID(PK)"),
                                    body("result[].name", "이름"),
                                    body("result[].profileImageUrl", "프로필 이미지 URL"),
                                    body("result[].school", "학교"),
                                    body("result[].major", "전공"),
                                    body("result[].enteredIn", "학번"),
                                    body("hasNext", "다음 스크롤 존재 여부")
                            )
                    ))
            );
        }
    }
}