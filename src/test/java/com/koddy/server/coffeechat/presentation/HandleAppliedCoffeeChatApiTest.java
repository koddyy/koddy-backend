package com.koddy.server.coffeechat.presentation;

import com.koddy.server.auth.exception.AuthExceptionCode;
import com.koddy.server.coffeechat.application.usecase.HandleMenteeAppliedCoffeeChatUseCase;
import com.koddy.server.coffeechat.presentation.request.ApproveAppliedCoffeeChatRequest;
import com.koddy.server.coffeechat.presentation.request.RejectAppliedCoffeeChatRequest;
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

@DisplayName("CoffeeChat -> HandleAppliedCoffeeChatApi 테스트")
class HandleAppliedCoffeeChatApiTest extends ControllerTest {
    @Autowired
    private HandleMenteeAppliedCoffeeChatUseCase handleMenteeAppliedCoffeeChatUseCase;

    private static final Long COFFEE_CHAT_ID = 1L;
    private final Mentor mentor = MENTOR_1.toDomain().apply(1L);
    private final Mentee mentee = MENTEE_1.toDomain().apply(2L);

    @Nested
    @DisplayName("멘티가 신청한 커피챗 거절 API [PATCH /api/coffeechats/applied/reject/{coffeeChatId}]")
    class Reject {
        private static final String BASE_URL = "/api/coffeechats/applied/reject/{coffeeChatId}";
        private final RejectAppliedCoffeeChatRequest request = new RejectAppliedCoffeeChatRequest("거절..");

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
                    failureDocsWithAccessToken("CoffeeChatApi/LifeCycle/AppliedByMentee/Reject/Failure", createHttpSpecSnippets(
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
        @DisplayName("멘티가 신청한 커피챗을 거절한다")
        void success() {
            // given
            applyToken(true, mentor);
            doNothing()
                    .when(handleMenteeAppliedCoffeeChatUseCase)
                    .reject(any());

            // when - then
            successfulExecute(
                    patchRequestWithAccessToken(new UrlWithVariables(BASE_URL, COFFEE_CHAT_ID), request),
                    status().isNoContent(),
                    successDocsWithAccessToken("CoffeeChatApi/LifeCycle/AppliedByMentee/Reject/Success", createHttpSpecSnippets(
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
    @DisplayName("멘티가 신청한 커피챗 수락 API [PATCH /api/coffeechats/applied/approve/{coffeeChatId}]")
    class Approve {
        private static final String BASE_URL = "/api/coffeechats/applied/approve/{coffeeChatId}";
        private final ApproveAppliedCoffeeChatRequest request = new ApproveAppliedCoffeeChatRequest(
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
                    failureDocsWithAccessToken("CoffeeChatApi/LifeCycle/AppliedByMentee/Approve/Failure", createHttpSpecSnippets(
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
        @DisplayName("멘티가 신청한 커피챗을 수락한다")
        void success() {
            // given
            applyToken(true, mentor);
            doNothing()
                    .when(handleMenteeAppliedCoffeeChatUseCase)
                    .approve(any());

            // when - then
            successfulExecute(
                    patchRequestWithAccessToken(new UrlWithVariables(BASE_URL, COFFEE_CHAT_ID), request),
                    status().isNoContent(),
                    successDocsWithAccessToken("CoffeeChatApi/LifeCycle/AppliedByMentee/Approve/Success", createHttpSpecSnippets(
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
