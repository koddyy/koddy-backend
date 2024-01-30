package com.koddy.server.member.presentation;

import com.koddy.server.auth.exception.AuthExceptionCode;
import com.koddy.server.common.ControllerTest;
import com.koddy.server.global.PageResponse;
import com.koddy.server.member.application.usecase.MentorMainSearchUseCase;
import com.koddy.server.member.application.usecase.query.response.CarouselProfileResponse;
import com.koddy.server.member.application.usecase.query.response.MenteeSimpleSearchProfile;
import com.koddy.server.member.domain.model.mentee.Mentee;
import com.koddy.server.member.domain.model.mentor.Mentor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

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

@DisplayName("Member -> MentorMainSearchApiController 테스트")
class MentorMainSearchApiControllerTest extends ControllerTest {
    @Autowired
    private MentorMainSearchUseCase mentorMainSearchUseCase;

    private final Mentor mentor = MENTOR_1.toDomain().apply(1L);
    private final Mentee mentee = MENTEE_1.toDomain().apply(2L);

    @Nested
    @DisplayName("커피챗 신청한 멘티 조회 API [GET /api/mentees/applied-coffeechats]")
    class GetAppliedMentees {
        private static final String BASE_URL = "/api/mentees/applied-coffeechats";

        @Test
        @DisplayName("멘토가 아니면 권한이 없다")
        void throwExceptionByInvalidPermission() {
            // given
            applyToken(true, mentee.getId(), mentee.getRole());

            // when - then
            failedExecute(
                    getRequestWithAccessToken(BASE_URL),
                    status().isForbidden(),
                    ExceptionSpec.of(AuthExceptionCode.INVALID_PERMISSION),
                    failureDocsWithAccessToken("MemberApi/Mentor/MainSearch/AppliedMentees/Failure")
            );
        }

        @Test
        @DisplayName("멘토 자신에게 커피챗을 신청한 멘티들을 최신순 기준으로 조회한다")
        void success() {
            // given
            applyToken(true, mentor.getId(), mentor.getRole());
            given(mentorMainSearchUseCase.getAppliedMentees(any())).willReturn(new CarouselProfileResponse<>(
                    List.of(
                            new MenteeSimpleSearchProfile(
                                    3,
                                    "멘티3",
                                    "https://mentee3-url",
                                    "EN",
                                    "서울대학교",
                                    "컴퓨터공학부"
                            ),
                            new MenteeSimpleSearchProfile(
                                    2,
                                    "멘티2",
                                    "https://mentee2-url",
                                    "JP",
                                    "연세대학교",
                                    "컴퓨터공학부"
                            ),
                            new MenteeSimpleSearchProfile(
                                    1,
                                    "멘티1",
                                    "https://mentee1-url",
                                    "CN",
                                    "고려대학교",
                                    "컴퓨터공학부"
                            )
                    ),
                    5
            ));

            // when - then
            successfulExecute(
                    getRequestWithAccessToken(BASE_URL, Map.of("limit", "3")),
                    status().isOk(),
                    successDocsWithAccessToken("MemberApi/Mentor/MainSearch/AppliedMentees/Success", createHttpSpecSnippets(
                            queryParameters(
                                    query("limit", "데이터 Limit 개수", "안넘어오면 기본 3", false)
                            ),
                            responseFields(
                                    body("result[].id", "멘티 ID(PK)"),
                                    body("result[].name", "이름"),
                                    body("result[].profileImageUrl", "프로필 이미지 URL"),
                                    body("result[].nationality", "국적", "KR EN CN JP VN ETC"),
                                    body("result[].interestSchool", "관심있는 학교"),
                                    body("result[].interestMajor", "관심있는 전공"),
                                    body("totalCount", "전체 개수")
                            )
                    ))
            );
        }
    }

    @Nested
    @DisplayName("멘티 둘러보기 API [GET /api/mentees]")
    class GetMenteesByCondition {
        private static final String BASE_URL = "/api/mentees";

        @Test
        @DisplayName("멘티들을 조건에 따라 둘러본다")
        void success() {
            // given
            given(mentorMainSearchUseCase.getMenteesByCondition(any())).willReturn(new PageResponse<>(
                    List.of(
                            new MenteeSimpleSearchProfile(
                                    3,
                                    "멘티3",
                                    "https://mentee3-url",
                                    "EN",
                                    "서울대학교",
                                    "컴퓨터공학부"
                            ),
                            new MenteeSimpleSearchProfile(
                                    2,
                                    "멘티2",
                                    "https://mentee2-url",
                                    "JP",
                                    "연세대학교",
                                    "컴퓨터공학부"
                            ),
                            new MenteeSimpleSearchProfile(
                                    1,
                                    "멘티1",
                                    "https://mentee1-url",
                                    "CN",
                                    "고려대학교",
                                    "컴퓨터공학부"
                            )
                    ),
                    false
            ));

            // when - then
            successfulExecute(
                    getRequest(
                            BASE_URL,
                            Map.of(
                                    "nationalities", "EN,JP,CN",
                                    "languages", "EN,KR",
                                    "page", "1"
                            )
                    ),
                    status().isOk(),
                    successDocs("MemberApi/Mentor/MainSearch/Mentees", createHttpSpecSnippets(
                            queryParameters(
                                    query("nationalities", "선택한 국적", "- 국가 코드 기반 (KR EN CN JP VN ETC)" + ENTER + "- 콤마(,) 기준 분리", false),
                                    query("languages", "선택한 언어", "- 국가 코드 기반 (KR EN CN JP VN)" + ENTER + "- 콤마(,) 기준 분리", false),
                                    query("page", "페이지", "1부터 시작", true)
                            ),
                            responseFields(
                                    body("result[].id", "멘티 ID(PK)"),
                                    body("result[].name", "이름"),
                                    body("result[].profileImageUrl", "프로필 이미지 URL"),
                                    body("result[].nationality", "국적", "KR EN CN JP VN ETC"),
                                    body("result[].interestSchool", "관심있는 학교"),
                                    body("result[].interestMajor", "관심있는 전공"),
                                    body("hasNext", "다음 스크롤 존재 여부")
                            )
                    ))
            );
        }
    }
}
