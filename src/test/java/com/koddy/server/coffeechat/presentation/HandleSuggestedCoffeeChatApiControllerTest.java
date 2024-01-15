package com.koddy.server.coffeechat.presentation;

import com.koddy.server.auth.exception.AuthExceptionCode;
import com.koddy.server.coffeechat.application.usecase.HandleSuggestedCoffeeChatUseCase;
import com.koddy.server.coffeechat.presentation.dto.request.PendingSuggestedCoffeeChatRequest;
import com.koddy.server.coffeechat.presentation.dto.request.RejectSuggestedCoffeeChatRequest;
import com.koddy.server.common.ControllerTest;
import com.koddy.server.member.domain.model.mentee.Mentee;
import com.koddy.server.member.domain.model.mentor.Mentor;
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
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("CoffeeChat -> HandleSuggestedCoffeeChatApiController 테스트")
class HandleSuggestedCoffeeChatApiControllerTest extends ControllerTest {
    @Autowired
    private HandleSuggestedCoffeeChatUseCase handleSuggestedCoffeeChatUseCase;

    private static final Long COFFEE_CHAT_ID = 1L;
    private final Mentor mentor = MENTOR_1.toDomain().apply(1L);
    private final Mentee mentee = MENTEE_1.toDomain().apply(2L);

    @Nested
    @DisplayName("멘토가 제안한 커피챗 거절 API [PATCH /api/coffeechats/suggested/reject/{coffeeChatId}]")
    class Reject {
        private static final String BASE_URL = "/api/coffeechats/suggested/reject/{coffeeChatId}";
        private final RejectSuggestedCoffeeChatRequest request = new RejectSuggestedCoffeeChatRequest("거절..");

        @Test
        @DisplayName("멘티가 아니면 권한이 없다")
        void throwExceptionByInvalidPermission() {
            // given
            applyToken(true, mentor.getId(), mentor.getRole());

            // when - then
            failedExecute(
                    patchRequestWithAccessToken(new UrlWithVariables(BASE_URL, COFFEE_CHAT_ID), request),
                    status().isForbidden(),
                    ExceptionSpec.of(AuthExceptionCode.INVALID_PERMISSION),
                    failureDocsWithAccessToken("CoffeeChatApi/LifeCycle/SuggestedByMentor/Reject/Failure", createHttpSpecSnippets(
                            pathParameters(
                                    path("coffeeChatId", "커피챗 ID(PK)", true)
                            ),
                            requestFields(
                                    body("rejectReason", "거절 사유", true)
                            )
                    ))
            );
        }

        @Test
        @DisplayName("멘토가 제안한 커피챗을 거절한다")
        void success() {
            // given
            applyToken(true, mentee.getId(), mentee.getRole());
            doNothing()
                    .when(handleSuggestedCoffeeChatUseCase)
                    .reject(any());

            // when - then
            successfulExecute(
                    patchRequestWithAccessToken(new UrlWithVariables(BASE_URL, COFFEE_CHAT_ID), request),
                    status().isNoContent(),
                    successDocsWithAccessToken("CoffeeChatApi/LifeCycle/SuggestedByMentor/Reject/Success", createHttpSpecSnippets(
                            pathParameters(
                                    path("coffeeChatId", "커피챗 ID(PK)", true)
                            ),
                            requestFields(
                                    body("rejectReason", "거절 사유", true)
                            )
                    ))
            );
        }
    }

    @Nested
    @DisplayName("멘토가 제안한 커피챗 1차 수락 API [PATCH /api/coffeechats/suggested/pending/{coffeeChatId}]")
    class Pending {
        private static final String BASE_URL = "/api/coffeechats/suggested/pending/{coffeeChatId}";
        private final PendingSuggestedCoffeeChatRequest request = new PendingSuggestedCoffeeChatRequest(
                LocalDateTime.of(2024, 2, 1, 18, 0),
                LocalDateTime.of(2024, 2, 1, 19, 0)
        );

        @Test
        @DisplayName("멘티가 아니면 권한이 없다")
        void throwExceptionByInvalidPermission() {
            // given
            applyToken(true, mentor.getId(), mentor.getRole());

            // when - then
            failedExecute(
                    patchRequestWithAccessToken(new UrlWithVariables(BASE_URL, COFFEE_CHAT_ID), request),
                    status().isForbidden(),
                    ExceptionSpec.of(AuthExceptionCode.INVALID_PERMISSION),
                    failureDocsWithAccessToken("CoffeeChatApi/LifeCycle/SuggestedByMentor/Pending/Failure", createHttpSpecSnippets(
                            pathParameters(
                                    path("coffeeChatId", "커피챗 ID(PK)", true)
                            ),
                            requestFields(
                                    body("start", "멘토링 시작 날짜", "KST", true),
                                    body("end", "멘토링 종료 날짜", "KST", true)
                            )
                    ))
            );
        }

        @Test
        @DisplayName("멘토가 제안한 커피챗을 1차 수락한다")
        void success() {
            // given
            applyToken(true, mentee.getId(), mentee.getRole());
            doNothing()
                    .when(handleSuggestedCoffeeChatUseCase)
                    .pending(any());

            // when - then
            successfulExecute(
                    patchRequestWithAccessToken(new UrlWithVariables(BASE_URL, COFFEE_CHAT_ID), request),
                    status().isNoContent(),
                    successDocsWithAccessToken("CoffeeChatApi/LifeCycle/SuggestedByMentor/Pending/Success", createHttpSpecSnippets(
                            pathParameters(
                                    path("coffeeChatId", "커피챗 ID(PK)", true)
                            ),
                            requestFields(
                                    body("start", "멘토링 시작 날짜", "KST", true),
                                    body("end", "멘토링 종료 날짜", "KST", true)
                            )
                    ))
            );
        }
    }
}
