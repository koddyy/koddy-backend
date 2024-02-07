package com.koddy.server.coffeechat.presentation;

import com.koddy.server.auth.exception.AuthExceptionCode;
import com.koddy.server.coffeechat.application.usecase.GetMenteeCoffeeChatScheduleUseCase;
import com.koddy.server.coffeechat.domain.repository.query.response.MenteeCoffeeChatScheduleData;
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
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTEE_REJECT;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_CANCEL;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_FINALLY_REJECT;
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

@DisplayName("CoffeeChat -> MenteeCoffeeChatScheduleQueryApiController 테스트")
class MenteeCoffeeChatScheduleQueryApiControllerTest extends ControllerTest {
    @Autowired
    private GetMenteeCoffeeChatScheduleUseCase getMenteeCoffeeChatScheduleUseCase;

    private final Mentor mentor = MENTOR_1.toDomain().apply(1L);
    private final Mentee mentee = MENTEE_1.toDomain().apply(2L);

    @Nested
    @DisplayName("멘티가 신청한 커피챗 상태별 조회 API [GET /api/coffeechats/mentees/me/applied]")
    class GetAppliedCoffeeChats {
        private static final String BASE_URL = "/api/coffeechats/mentees/me/applied";

        @Test
        @DisplayName("멘티가 아니면 권한이 없다")
        void throwExceptionByInvalidPermission() {
            // given
            applyToken(true, mentor);

            // when - then
            failedExecute(
                    getRequestWithAccessToken(BASE_URL, Map.of(
                            "status", "APPLY",
                            "page", "1"
                    )),
                    status().isForbidden(),
                    ExceptionSpec.of(AuthExceptionCode.INVALID_PERMISSION),
                    failureDocsWithAccessToken("CoffeeChatApi/Schedule/Mentee/Applied/Failure", createHttpSpecSnippets(
                            queryParameters(
                                    query(
                                            "status",
                                            "커피챗 상태",
                                            "- 전체 = 안보내도됨" + ENTER
                                                    + "- 신청 = APPLY" + ENTER
                                                    + "- 예정 = APPROVE" + ENTER
                                                    + "- 완료 = COMPLETE" + ENTER
                                                    + "- 취소 = CANCEL,REJECT" + ENTER,
                                            false
                                    ),
                                    query("page", "페이지", "1부터 시작", true)
                            )
                    ))
            );
        }

        @Test
        @DisplayName("멘티가 신청한 커피챗을 상태별로 조회한다")
        void success() {
            // given
            applyToken(true, mentee);
            given(getMenteeCoffeeChatScheduleUseCase.getAppliedCoffeeChats(any())).willReturn(new SliceResponse<>(
                    List.of(new MenteeCoffeeChatScheduleData(
                            1L,
                            MENTEE_APPLY.getValue(),
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
                            "status", "APPLY",
                            "page", "1"
                    )),
                    status().isOk(),
                    successDocsWithAccessToken("CoffeeChatApi/Schedule/Mentee/Applied/Success", createHttpSpecSnippets(
                            queryParameters(
                                    query(
                                            "status",
                                            "커피챗 상태",
                                            "- 전체 = 안보내도됨" + ENTER
                                                    + "- 신청 = APPLY" + ENTER
                                                    + "- 예정 = APPROVE" + ENTER
                                                    + "- 완료 = COMPLETE" + ENTER
                                                    + "- 취소 = CANCEL,REJECT" + ENTER,
                                            false
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

    @Nested
    @DisplayName("멘티가 제안받은 커피챗 상태별 조회 API [GET /api/coffeechats/mentees/me/suggested]")
    class GetSuggestedCoffeeChats {
        private static final String BASE_URL = "/api/coffeechats/mentees/me/suggested";

        @Test
        @DisplayName("멘티가 아니면 권한이 없다")
        void throwExceptionByInvalidPermission() {
            // given
            applyToken(true, mentor);

            // when - then
            failedExecute(
                    getRequestWithAccessToken(BASE_URL, Map.of(
                            "status", "CANCEL,REJECT",
                            "page", "1"
                    )),
                    status().isForbidden(),
                    ExceptionSpec.of(AuthExceptionCode.INVALID_PERMISSION),
                    failureDocsWithAccessToken("CoffeeChatApi/Schedule/Mentee/Suggested/Failure", createHttpSpecSnippets(
                            queryParameters(
                                    query(
                                            "status",
                                            "커피챗 상태",
                                            "- 전체 = 안보내도됨" + ENTER
                                                    + "- 제안 = SUGGEST" + ENTER
                                                    + "- 수락 = PENDING" + ENTER
                                                    + "- 예정 = APPROVE" + ENTER
                                                    + "- 완료 = COMPLETE" + ENTER
                                                    + "- 취소 = CANCEL,REJECT" + ENTER,
                                            false
                                    ),
                                    query("page", "페이지", "1부터 시작", true)
                            )
                    ))
            );
        }

        @Test
        @DisplayName("멘티가 제안받은 커피챗을 상태별로 조회한다")
        void success() {
            // given
            applyToken(true, mentee);
            given(getMenteeCoffeeChatScheduleUseCase.getSuggestedCoffeeChats(any())).willReturn(new SliceResponse<>(
                    List.of(
                            new MenteeCoffeeChatScheduleData(
                                    3L,
                                    MENTOR_FINALLY_REJECT.getValue(),
                                    mentor.getId(),
                                    mentor.getName(),
                                    mentor.getProfileImageUrl(),
                                    mentor.getUniversityProfile().getSchool(),
                                    mentor.getUniversityProfile().getMajor(),
                                    mentor.getUniversityProfile().getEnteredIn()
                            ),
                            new MenteeCoffeeChatScheduleData(
                                    2L,
                                    MENTEE_REJECT.getValue(),
                                    mentor.getId(),
                                    mentor.getName(),
                                    mentor.getProfileImageUrl(),
                                    mentor.getUniversityProfile().getSchool(),
                                    mentor.getUniversityProfile().getMajor(),
                                    mentor.getUniversityProfile().getEnteredIn()
                            ),
                            new MenteeCoffeeChatScheduleData(
                                    1L,
                                    MENTOR_CANCEL.getValue(),
                                    mentor.getId(),
                                    mentor.getName(),
                                    mentor.getProfileImageUrl(),
                                    mentor.getUniversityProfile().getSchool(),
                                    mentor.getUniversityProfile().getMajor(),
                                    mentor.getUniversityProfile().getEnteredIn()
                            )
                    ),
                    false
            ));

            // when - then
            successfulExecute(
                    getRequestWithAccessToken(BASE_URL, Map.of(
                            "status", "CANCEL,REJECT",
                            "page", "1"
                    )),
                    status().isOk(),
                    successDocsWithAccessToken("CoffeeChatApi/Schedule/Mentee/Suggested/Success", createHttpSpecSnippets(
                            queryParameters(
                                    query(
                                            "status",
                                            "커피챗 상태",
                                            "- 전체 = 안보내도됨" + ENTER
                                                    + "- 제안 = SUGGEST" + ENTER
                                                    + "- 수락 = PENDING" + ENTER
                                                    + "- 예정 = APPROVE" + ENTER
                                                    + "- 완료 = COMPLETE" + ENTER
                                                    + "- 취소 = CANCEL,REJECT" + ENTER,
                                            false
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
