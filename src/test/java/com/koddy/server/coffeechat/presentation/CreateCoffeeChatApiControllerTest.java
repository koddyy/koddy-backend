package com.koddy.server.coffeechat.presentation;

import com.koddy.server.auth.exception.AuthExceptionCode;
import com.koddy.server.coffeechat.application.usecase.CreateCoffeeChatUseCase;
import com.koddy.server.coffeechat.presentation.dto.request.MenteeApplyCoffeeChatRequest;
import com.koddy.server.coffeechat.presentation.dto.request.MentorSuggestCoffeeChatRequest;
import com.koddy.server.common.ControllerTest;
import com.koddy.server.member.domain.model.mentee.Mentee;
import com.koddy.server.member.domain.model.mentor.Mentor;
import com.koddy.server.member.exception.MemberException;
import com.koddy.server.member.exception.MemberExceptionCode;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_1;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.SnippetFactory.body;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.SnippetFactory.path;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.createHttpSpecSnippets;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.failureDocsWithAccessToken;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.successDocsWithAccessToken;
import static com.koddy.server.member.exception.MemberExceptionCode.CANNOT_RESERVATION;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("CoffeeChat -> CreateCoffeeChatApiController 테스트")
class CreateCoffeeChatApiControllerTest extends ControllerTest {
    @Autowired
    private CreateCoffeeChatUseCase createCoffeeChatUseCase;

    private final Mentor mentor = MENTOR_1.toDomain().apply(1L);
    private final Mentee mentee = MENTEE_1.toDomain().apply(2L);
    private final String applyReason = "신청 이유...";

    @Nested
    @DisplayName("멘토 -> 멘티 커피챗 제안 API [POST /api/coffeechats/suggest/{menteeId}]")
    class SuggestCoffeeChat {
        private static final String BASE_URL = "/api/coffeechats/suggest/{menteeId}";
        private final MentorSuggestCoffeeChatRequest request = new MentorSuggestCoffeeChatRequest(applyReason);

        @Test
        @DisplayName("멘토가 아니면 권한이 없다")
        void throwExceptionByInvalidPermission() {
            // given
            applyToken(true, mentee.getId(), mentee.getRole());

            // when - then
            failedExecute(
                    postRequestWithAccessToken(new UrlWithVariables(BASE_URL, mentee.getId()), request),
                    status().isForbidden(),
                    ExceptionSpec.of(AuthExceptionCode.INVALID_PERMISSION),
                    failureDocsWithAccessToken("CoffeeChatApi/LifeCycle/Create/MentorSuggest/Failure", createHttpSpecSnippets(
                            pathParameters(
                                    path("menteeId", "멘티 ID(PK)", true)
                            ),
                            requestFields(
                                    body("applyReason", "커피챗 제안 이유", true)
                            )
                    ))
            );
        }

        @Test
        @DisplayName("멘토가 멘티에게 커피챗을 제안한다")
        void success() {
            // given
            applyToken(true, mentor.getId(), mentor.getRole());
            given(createCoffeeChatUseCase.suggestCoffeeChat(any())).willReturn(1L);

            // when - then
            successfulExecute(
                    postRequestWithAccessToken(new UrlWithVariables(BASE_URL, mentee.getId()), request),
                    status().isOk(),
                    successDocsWithAccessToken("CoffeeChatApi/LifeCycle/Create/MentorSuggest/Success", createHttpSpecSnippets(
                            pathParameters(
                                    path("menteeId", "멘티 ID(PK)", true)
                            ),
                            requestFields(
                                    body("applyReason", "커피챗 제안 이유", true)
                            ),
                            responseFields(
                                    body("coffeeChatId", "커피챗 ID(PK)")
                            )
                    ))
            );
        }
    }

    @Nested
    @DisplayName("멘티 -> 멘토 커피챗 신청 API [POST /api/coffeechats/apply/{mentorId}]")
    class ApplyCoffeeChat {
        private static final String BASE_URL = "/api/coffeechats/apply/{mentorId}";
        private final MenteeApplyCoffeeChatRequest request = new MenteeApplyCoffeeChatRequest(
                applyReason,
                LocalDateTime.of(2024, 2, 1, 18, 0).toString(),
                LocalDateTime.of(2024, 2, 1, 19, 30).toString()
        );

        @Test
        @DisplayName("멘티가 아니면 권한이 없다")
        void throwExceptionByInvalidPermission() {
            // given
            applyToken(true, mentor.getId(), mentor.getRole());

            // when - then
            failedExecute(
                    postRequestWithAccessToken(new UrlWithVariables(BASE_URL, mentor.getId()), request),
                    status().isForbidden(),
                    ExceptionSpec.of(AuthExceptionCode.INVALID_PERMISSION),
                    failureDocsWithAccessToken("CoffeeChatApi/LifeCycle/Create/MenteeApply/Failure/Case1", createHttpSpecSnippets(
                            pathParameters(
                                    path("mentorId", "멘토 ID(PK)", true)
                            ),
                            requestFields(
                                    body("applyReason", "커피챗 신청 이유", true),
                                    body("start", "커피챗 날짜 (시작 시간)", "[KST] yyyy-MM-ddTHH:mm:ss" + ENTER + "-> 시간 = 00:00:00 ~ 23:59:59", true),
                                    body("end", "커피챗 날짜 (종료 시간)", "[KST] yyyy-MM-ddTHH:mm:ss" + ENTER + "-> 시간 = 00:00:00 ~ 23:59:59", true)
                            )
                    ))
            );
        }

        @Test
        @DisplayName("이미 예약되었거나 멘토링이 가능하지 않은 날짜면 예외가 발생한다")
        void throwExceptionByCannotReservation() {
            // given
            applyToken(true, mentee.getId(), mentee.getRole());
            doThrow(new MemberException(CANNOT_RESERVATION))
                    .when(createCoffeeChatUseCase)
                    .applyCoffeeChat(any());

            // when - then
            failedExecute(
                    postRequestWithAccessToken(new UrlWithVariables(BASE_URL, mentor.getId()), request),
                    status().isConflict(),
                    ExceptionSpec.of(MemberExceptionCode.CANNOT_RESERVATION),
                    failureDocsWithAccessToken("CoffeeChatApi/LifeCycle/Create/MenteeApply/Failure/Case2", createHttpSpecSnippets(
                            pathParameters(
                                    path("mentorId", "멘토 ID(PK)", true)
                            ),
                            requestFields(
                                    body("applyReason", "커피챗 신청 이유", true),
                                    body("start", "커피챗 날짜 (시작 시간)", "[KST] yyyy-MM-ddTHH:mm:ss" + ENTER + "-> 시간 = 00:00:00 ~ 23:59:59", true),
                                    body("end", "커피챗 날짜 (종료 시간)", "[KST] yyyy-MM-ddTHH:mm:ss" + ENTER + "-> 시간 = 00:00:00 ~ 23:59:59", true)
                            )
                    ))
            );
        }

        @Test
        @DisplayName("멘티가 멘토에게 커피챗을 신청한다")
        void success() {
            // given
            applyToken(true, mentee.getId(), mentee.getRole());
            given(createCoffeeChatUseCase.applyCoffeeChat(any())).willReturn(1L);

            // when - then
            successfulExecute(
                    postRequestWithAccessToken(new UrlWithVariables(BASE_URL, mentor.getId()), request),
                    status().isOk(),
                    successDocsWithAccessToken("CoffeeChatApi/LifeCycle/Create/MenteeApply/Success", createHttpSpecSnippets(
                            pathParameters(
                                    path("mentorId", "멘토 ID(PK)", true)
                            ),
                            requestFields(
                                    body("applyReason", "커피챗 신청 이유", true),
                                    body("start", "커피챗 날짜 (시작 시간)", "[KST] yyyy-MM-ddTHH:mm:ss" + ENTER + "-> 시간 = 00:00:00 ~ 23:59:59", true),
                                    body("end", "커피챗 날짜 (종료 시간)", "[KST] yyyy-MM-ddTHH:mm:ss" + ENTER + "-> 시간 = 00:00:00 ~ 23:59:59", true)
                            ),
                            responseFields(
                                    body("coffeeChatId", "커피챗 ID(PK)")
                            )
                    ))
            );
        }
    }
}
