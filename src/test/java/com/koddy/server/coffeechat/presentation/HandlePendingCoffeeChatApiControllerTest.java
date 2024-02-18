package com.koddy.server.coffeechat.presentation;

import com.koddy.server.auth.exception.AuthExceptionCode;
import com.koddy.server.coffeechat.application.usecase.HandlePendingCoffeeChatUseCase;
import com.koddy.server.coffeechat.presentation.request.ApprovePendingCoffeeChatRequest;
import com.koddy.server.coffeechat.presentation.request.RejectPendingCoffeeChatRequest;
import com.koddy.server.common.ControllerTest;
import com.koddy.server.common.fixture.StrategyFixture;
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
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.failureDocsWithAccessToken;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.successDocsWithAccessToken;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("CoffeeChat -> HandlePendingCoffeeChatApiController 테스트")
class HandlePendingCoffeeChatApiControllerTest extends ControllerTest {
    @Autowired
    private HandlePendingCoffeeChatUseCase handlePendingCoffeeChatUseCase;

    private static final Long COFFEE_CHAT_ID = 1L;
    private final Mentor mentor = MENTOR_1.toDomain().apply(1L);
    private final Mentee mentee = MENTEE_1.toDomain().apply(2L);

    @Nested
    @DisplayName("Pending 상태인 커피챗에 대한 최종 거절 API [PATCH /api/coffeechats/pending/reject/{coffeeChatId}]")
    class Reject {
        private static final String BASE_URL = "/api/coffeechats/pending/reject/{coffeeChatId}";
        private final RejectPendingCoffeeChatRequest request = new RejectPendingCoffeeChatRequest("거절...");

        @Test
        @DisplayName("멘토가 아니면 권한이 없다")
        void throwExceptionByInvalidPermission() {
            // given
            applyToken(true, mentee);

            // when - then
            failedExecute(
                    patchRequestWithAccessToken(new UrlWithVariables(BASE_URL, COFFEE_CHAT_ID), request),
                    status().isForbidden(),
                    ExceptionSpec.of(AuthExceptionCode.INVALID_PERMISSION),
                    failureDocsWithAccessToken("CoffeeChatApi/LifeCycle/PendingCoffeeChat/Reject/Failure", createHttpSpecSnippets(
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
        @DisplayName("멘토는 Pending 상태인 커피챗에 대해서 최종 거절한다")
        void success() {
            // given
            applyToken(true, mentor);
            doNothing()
                    .when(handlePendingCoffeeChatUseCase)
                    .reject(any());

            // when - then
            successfulExecute(
                    patchRequestWithAccessToken(new UrlWithVariables(BASE_URL, COFFEE_CHAT_ID), request),
                    status().isNoContent(),
                    successDocsWithAccessToken("CoffeeChatApi/LifeCycle/PendingCoffeeChat/Reject/Success", createHttpSpecSnippets(
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
    @DisplayName("Pending 상태인 커피챗에 대한 최종 수락 API [PATCH /api/coffeechats/pending/approve/{coffeeChatId}]")
    class Approve {
        private static final String BASE_URL = "/api/coffeechats/pending/approve/{coffeeChatId}";
        private final ApprovePendingCoffeeChatRequest request = new ApprovePendingCoffeeChatRequest(
                StrategyFixture.KAKAO_ID.getType().getEng(),
                StrategyFixture.KAKAO_ID.getValue()
        );

        @Test
        @DisplayName("멘토가 아니면 권한이 없다")
        void throwExceptionByInvalidPermission() {
            // given
            applyToken(true, mentee);

            // when - then
            failedExecute(
                    patchRequestWithAccessToken(new UrlWithVariables(BASE_URL, COFFEE_CHAT_ID), request),
                    status().isForbidden(),
                    ExceptionSpec.of(AuthExceptionCode.INVALID_PERMISSION),
                    failureDocsWithAccessToken("CoffeeChatApi/LifeCycle/PendingCoffeeChat/Approve/Failure", createHttpSpecSnippets(
                            pathParameters(
                                    path("coffeeChatId", "커피챗 ID(PK)", true)
                            ),
                            requestFields(
                                    body("chatType", "멘토링 진행 방식", "- 링크 = zoom google" + ENTER + "- 메신저 = kakao line wechat", true),
                                    body("chatValue", "멘토링 진행 방식에 대한 값", "미팅 URL or 메신저 ID", true)
                            )
                    ))
            );
        }

        @Test
        @DisplayName("멘토는 Pending 상태인 커피챗에 대해서 최종 수락한다")
        void success() {
            // given
            applyToken(true, mentor);
            doNothing()
                    .when(handlePendingCoffeeChatUseCase)
                    .approve(any());

            // when - then
            successfulExecute(
                    patchRequestWithAccessToken(new UrlWithVariables(BASE_URL, COFFEE_CHAT_ID), request),
                    status().isNoContent(),
                    successDocsWithAccessToken("CoffeeChatApi/LifeCycle/PendingCoffeeChat/Approve/Success", createHttpSpecSnippets(
                            pathParameters(
                                    path("coffeeChatId", "커피챗 ID(PK)", true)
                            ),
                            requestFields(
                                    body("chatType", "멘토링 진행 방식", "- 링크 = zoom google" + ENTER + "- 메신저 = kakao line wechat", true),
                                    body("chatValue", "멘토링 진행 방식에 대한 값", "미팅 URL or 메신저 ID", true)
                            )
                    ))
            );
        }
    }
}
