package com.koddy.server.coffeechat.presentation;

import com.koddy.server.auth.exception.AuthExceptionCode;
import com.koddy.server.coffeechat.application.usecase.GetMentorCoffeeChatScheduleUseCase;
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

import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.CANCEL;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.REJECT;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.SUGGEST;
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

@DisplayName("CoffeeChat -> MentorCoffeeChatScheduleQueryApiController 테스트")
class MentorCoffeeChatScheduleQueryApiControllerTest extends ControllerTest {
    @Autowired
    private GetMentorCoffeeChatScheduleUseCase getMentorCoffeeChatScheduleUseCase;

    private final Mentor mentor = MENTOR_1.toDomain().apply(1L);
    private final Mentee mentee = MENTEE_1.toDomain().apply(2L);

    @Nested
    @DisplayName("멘토가 제안한 커피챗 상태별 조회 API [GET /api/coffeechats/mentors/me/suggested]")
    class GetAppliedCoffeeChats {
        private static final String BASE_URL = "/api/coffeechats/mentors/me/suggested";

        @Test
        @DisplayName("멘토가 아니면 권한이 없다")
        void throwExceptionByInvalidPermission() {
            // given
            applyToken(true, mentee.getId(), mentee.getRole());

            // when - then
            failedExecute(
                    getRequestWithAccessToken(BASE_URL, Map.of(
                            "status", "SUGGEST",
                            "page", "1"
                    )),
                    status().isForbidden(),
                    ExceptionSpec.of(AuthExceptionCode.INVALID_PERMISSION),
                    failureDocsWithAccessToken("CoffeeChatApi/Schedule/Mentor/Suggested/Failure", createHttpSpecSnippets(
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
        @DisplayName("멘토가 제안한 커피챗을 상태별로 조회한다")
        void success() {
            // given
            applyToken(true, mentor.getId(), mentor.getRole());
            given(getMentorCoffeeChatScheduleUseCase.getSuggestedCoffeeChats(any())).willReturn(new SliceResponse<>(
                    List.of(new MentorCoffeeChatScheduleData(
                            1L,
                            SUGGEST.getValue(),
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
                            "status", "SUGGEST",
                            "page", "1"
                    )),
                    status().isOk(),
                    successDocsWithAccessToken("CoffeeChatApi/Schedule/Mentor/Suggested/Success", createHttpSpecSnippets(
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
    @DisplayName("멘토가 신청받은 커피챗 상태별 조회 API [GET /api/coffeechats/mentors/me/applied]")
    class GetSuggestedCoffeeChats {
        private static final String BASE_URL = "/api/coffeechats/mentors/me/applied";

        @Test
        @DisplayName("멘토가 아니면 권한이 없다")
        void throwExceptionByInvalidPermission() {
            // given
            applyToken(true, mentee.getId(), mentee.getRole());

            // when - then
            failedExecute(
                    getRequestWithAccessToken(BASE_URL, Map.of(
                            "status", "CANCEL,REJECT",
                            "page", "1"
                    )),
                    status().isForbidden(),
                    ExceptionSpec.of(AuthExceptionCode.INVALID_PERMISSION),
                    failureDocsWithAccessToken("CoffeeChatApi/Schedule/Mentor/Applied/Failure", createHttpSpecSnippets(
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
        @DisplayName("멘토가 신청받은 커피챗을 상태별로 조회한다")
        void success() {
            // given
            applyToken(true, mentor.getId(), mentor.getRole());
            given(getMentorCoffeeChatScheduleUseCase.getAppliedCoffeeChats(any())).willReturn(new SliceResponse<>(
                    List.of(
                            new MentorCoffeeChatScheduleData(
                                    2L,
                                    CANCEL.getValue(),
                                    mentee.getId(),
                                    mentee.getName(),
                                    mentee.getProfileImageUrl(),
                                    mentee.getInterest().getSchool(),
                                    mentee.getInterest().getMajor()
                            ),
                            new MentorCoffeeChatScheduleData(
                                    1L,
                                    REJECT.getValue(),
                                    mentee.getId(),
                                    mentee.getName(),
                                    mentee.getProfileImageUrl(),
                                    mentee.getInterest().getSchool(),
                                    mentee.getInterest().getMajor()
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
                    successDocsWithAccessToken("CoffeeChatApi/Schedule/Mentor/Applied/Success", createHttpSpecSnippets(
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
}
