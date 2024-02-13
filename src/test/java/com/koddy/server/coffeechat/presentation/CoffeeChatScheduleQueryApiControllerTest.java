package com.koddy.server.coffeechat.presentation;

import com.koddy.server.auth.exception.AuthExceptionCode;
import com.koddy.server.coffeechat.application.usecase.GetCoffeeChatScheduleUseCase;
import com.koddy.server.coffeechat.domain.repository.query.response.MenteeCoffeeChatScheduleData;
import com.koddy.server.coffeechat.domain.repository.query.response.MentorCoffeeChatScheduleData;
import com.koddy.server.common.ControllerTest;
import com.koddy.server.global.query.SliceResponse;
import com.koddy.server.member.domain.model.mentee.Mentee;
import com.koddy.server.member.domain.model.mentor.Mentor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Map;

import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTEE_APPLY;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_1;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.SnippetFactory.body;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.SnippetFactory.query;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.createHttpSpecSnippets;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.failureDocsWithAccessToken;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.successDocsWithAccessToken;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("CoffeeChat -> CoffeeChatScheduleQueryApiController 테스트")
public class CoffeeChatScheduleQueryApiControllerTest extends ControllerTest {
    @Autowired
    private GetCoffeeChatScheduleUseCase getCoffeeChatScheduleUseCase;

    private final Mentor mentor = MENTOR_1.toDomain().apply(1L);
    private final Mentee mentee = MENTEE_1.toDomain().apply(2L);

    @Nested
    @DisplayName("멘토의 내 일정 커피챗 상태별 조회 API [GET /api/coffeechats/mentors/me]")
    class GetMentorSchedules {
        private static final String BASE_URL = "/api/coffeechats/mentors/me";

        @Test
        @DisplayName("멘토가 아니면 권한이 없다")
        void throwExceptionByInvalidPermission() {
            // given
            applyToken(true, mentee);

            // when - then
            failedExecute(
                    getRequestWithAccessToken(BASE_URL, Map.of(
                            "status", "waiting",
                            "page", "1"
                    )),
                    status().isForbidden(),
                    ExceptionSpec.of(AuthExceptionCode.INVALID_PERMISSION),
                    failureDocsWithAccessToken("CoffeeChatApi/Schedule/Mentor/Failure", createHttpSpecSnippets(
                            queryParameters(
                                    query(
                                            "status",
                                            "커피챗 상태",
                                            "- 대기 = waiting" + ENTER
                                                    + "- 예정 = scheduled" + ENTER
                                                    + "- 지나감 = passed",
                                            true
                                    ),
                                    query("page", "페이지", "1부터 시작", true)
                            )
                    ))
            );
        }

        @Test
        @DisplayName("멘토의 내 일정 커피챗을 상태별로 조회한다")
        void success() {
            // given
            applyToken(true, mentor);
            given(getCoffeeChatScheduleUseCase.getMentorSchedules(any())).willReturn(new SliceResponse<>(
                    List.of(new MentorCoffeeChatScheduleData(
                            1L,
                            MENTEE_APPLY.name(),
                            mentee.getId(),
                            mentee.getName(),
                            mentee.getProfileImageUrl(),
                            mentee.getInterest().getSchool(),
                            mentee.getInterest().getMajor()
                    )),
                    false
            ));

            // when - then
            successfulExecute(
                    getRequestWithAccessToken(BASE_URL, Map.of(
                            "status", "waiting",
                            "page", "1"
                    )),
                    status().isOk(),
                    successDocsWithAccessToken("CoffeeChatApi/Schedule/Mentor/Success", createHttpSpecSnippets(
                            queryParameters(
                                    query(
                                            "status",
                                            "커피챗 상태",
                                            "- 대기 = waiting" + ENTER
                                                    + "- 예정 = scheduled" + ENTER
                                                    + "- 지나감 = passed",
                                            true
                                    ),
                                    query("page", "페이지", "1부터 시작", true)
                            ),

                            responseFields(
                                    body("result[].id", "커피챗 ID(PK)"),
                                    body("result[].status", "커피챗 상태"),
                                    body("result[].menteeId", "멘티 ID(PK)"),
                                    body("result[].name", "멘티 이름"),
                                    body("result[].profileImageUrl", "멘티 프로필 이미지 URL"),
                                    body("result[].interestSchool", "멘티 관심있는 학교"),
                                    body("result[].interestMajor", "멘티 관심있는 전공"),
                                    body("hasNext", "다음 스크롤 존재 여부")
                            )
                    ))
            );
        }
    }

    @Nested
    @DisplayName("멘티의 내 일정 커피챗 상태별 조회 API [GET /api/coffeechats/mentees/me]")
    class GetMenteeSchedules {
        private static final String BASE_URL = "/api/coffeechats/mentees/me";

        @Test
        @DisplayName("멘토가 아니면 권한이 없다")
        void throwExceptionByInvalidPermission() {
            // given
            applyToken(true, mentor);

            // when - then
            failedExecute(
                    getRequestWithAccessToken(BASE_URL, Map.of(
                            "status", "waiting",
                            "page", "1"
                    )),
                    status().isForbidden(),
                    ExceptionSpec.of(AuthExceptionCode.INVALID_PERMISSION),
                    failureDocsWithAccessToken("CoffeeChatApi/Schedule/Mentee/Failure", createHttpSpecSnippets(
                            queryParameters(
                                    query(
                                            "status",
                                            "커피챗 상태",
                                            "- 대기 = waiting" + ENTER
                                                    + "- 예정 = scheduled" + ENTER
                                                    + "- 지나감 = passed",
                                            true
                                    ),
                                    query("page", "페이지", "1부터 시작", true)
                            )
                    ))
            );
        }

        @Test
        @DisplayName("멘티의 내 일정 커피챗을 상태별로 조회한다")
        void success() {
            // given
            applyToken(true, mentee);
            given(getCoffeeChatScheduleUseCase.getMenteeSchedules(any())).willReturn(new SliceResponse<>(
                    List.of(new MenteeCoffeeChatScheduleData(
                            1L,
                            MENTEE_APPLY.name(),
                            mentor.getId(),
                            mentor.getName(),
                            mentor.getProfileImageUrl(),
                            mentor.getUniversityProfile().getSchool(),
                            mentor.getUniversityProfile().getMajor(),
                            mentor.getUniversityProfile().getEnteredIn()
                    )),
                    false
            ));

            // when - then
            successfulExecute(
                    getRequestWithAccessToken(BASE_URL, Map.of(
                            "status", "waiting",
                            "page", "1"
                    )),
                    status().isOk(),
                    successDocsWithAccessToken("CoffeeChatApi/Schedule/Mentee/Success", createHttpSpecSnippets(
                            queryParameters(
                                    query(
                                            "status",
                                            "커피챗 상태",
                                            "- 대기 = waiting" + ENTER
                                                    + "- 예정 = scheduled" + ENTER
                                                    + "- 지나감 = passed",
                                            true
                                    ),
                                    query("page", "페이지", "1부터 시작", true)
                            ),

                            responseFields(
                                    body("result[].id", "커피챗 ID(PK)"),
                                    body("result[].status", "커피챗 상태"),
                                    body("result[].mentorId", "멘토 ID(PK)"),
                                    body("result[].name", "멘토 이름"),
                                    body("result[].profileImageUrl", "멘토 프로필 이미지 URL"),
                                    body("result[].school", "멘토 학교"),
                                    body("result[].major", "멘토 전공"),
                                    body("result[].enteredIn", "멘토 학번"),
                                    body("hasNext", "다음 스크롤 존재 여부")
                            )
                    ))
            );
        }
    }
}
