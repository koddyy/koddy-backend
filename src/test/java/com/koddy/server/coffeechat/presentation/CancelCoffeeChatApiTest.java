//package com.koddy.server.coffeechat.presentation;
//
//import com.koddy.server.coffeechat.application.usecase.CancelCoffeeChatUseCase;
//import com.koddy.server.coffeechat.presentation.request.CancelCoffeeChatRequest;
//import com.koddy.server.common.ApiDocsTest;
//import com.koddy.server.member.domain.model.mentor.Mentor;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Nested;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//
//import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
//import static com.koddy.server.common.utils.RestDocsSpecificationUtils.SnippetFactory.path;
//import static com.koddy.server.common.utils.RestDocsSpecificationUtils.createHttpSpecSnippets;
//import static com.koddy.server.common.utils.RestDocsSpecificationUtils.successDocsWithAccessToken;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.Mockito.doNothing;
//import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
//import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
//
//@DisplayName("CoffeeChat -> CancelCoffeeChatApi 테스트")
//class CancelCoffeeChatApiTest extends ApiDocsTest {
//    @Autowired
//    private CancelCoffeeChatUseCase cancelCoffeeChatUseCase;
//
//    private static final Long COFFEE_CHAT_ID = 1L;
//    private final Mentor mentor = MENTOR_1.toDomain().apply(1L);
//
//    @Nested
//    @DisplayName("신청/제안한 커피챗 취소 API [DELETE /api/coffeechats/cancel/{coffeeChatId}]")
//    class Cancel {
//        private static final String BASE_URL = "/api/coffeechats/cancel/{coffeeChatId}";
//        private final CancelCoffeeChatRequest request = new CancelCoffeeChatRequest("취소..");
//
//        @Test
//        @DisplayName("신청/제안한 커피챗을 취소한다")
//        void success() {
//            // given
//            applyToken(true, mentor);
//            doNothing()
//                    .when(cancelCoffeeChatUseCase)
//                    .invoke(any());
//
//            // when - then
//            successfulExecute(
//                    patchRequestWithAccessToken(new UrlWithVariables(BASE_URL, COFFEE_CHAT_ID), request),
//                    status().isNoContent(),
//                    successDocsWithAccessToken("CoffeeChatApi/LifeCycle/Cancel", createHttpSpecSnippets(
//                            pathParameters(
//                                    path("coffeeChatId", "커피챗 ID(PK)", true)
//                            )
//                    ))
//            );
//        }
//    }
//}
