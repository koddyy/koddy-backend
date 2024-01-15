package com.koddy.server.coffeechat.presentation;

import com.koddy.server.auth.exception.AuthExceptionCode;
import com.koddy.server.coffeechat.application.usecase.ApproveCoffeeChatUseCase;
import com.koddy.server.coffeechat.presentation.dto.request.ApproveMenteeApplyRequest;
import com.koddy.server.coffeechat.presentation.dto.request.ApproveMentorSuggestRequest;
import com.koddy.server.common.ControllerTest;
import com.koddy.server.common.fixture.StrategyFixture;
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

@DisplayName("CoffeeChat -> ApproveCoffeeChatApiController 테스트")
class ApproveCoffeeChatApiControllerTest extends ControllerTest {
    @Autowired
    private ApproveCoffeeChatUseCase approveCoffeeChatUseCase;

    private static final Long COFFEE_CHAT_ID = 1L;
    private final Mentor mentor = MENTOR_1.toDomain().apply(1L);
    private final Mentee mentee = MENTEE_1.toDomain().apply(2L);

    @Nested
    @DisplayName("멘토의 커피챗 제안 수락 API [POST /api/coffeechats/approve/suggest/{coffeeChatId}]")
    class SuggestByMentor {
        private static final String BASE_URL = "/api/coffeechats/approve/suggest/{coffeeChatId}";
        private final ApproveMentorSuggestRequest request = new ApproveMentorSuggestRequest(
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
                    failureDocsWithAccessToken("CoffeeChatApi/Approve/SuggestByMentor/Failure", createHttpSpecSnippets(
                            pathParameters(
                                    path("coffeeChatId", "커피챗 ID(PK)", true)
                            ),
                            requestFields(
                                    body("start", "멘토링 시작 시간", "KST", true),
                                    body("end", "멘토링 종료 시간", "KST", true)
                            )
                    ))
            );
        }

        @Test
        @DisplayName("멘티가 멘토의 커피챗 제안을 수락한다")
        void success() {
            // given
            applyToken(true, mentee.getId(), mentee.getRole());
            doNothing()
                    .when(approveCoffeeChatUseCase)
                    .suggestByMentor(any());

            // when - then
            successfulExecute(
                    patchRequestWithAccessToken(new UrlWithVariables(BASE_URL, COFFEE_CHAT_ID), request),
                    status().isNoContent(),
                    successDocsWithAccessToken("CoffeeChatApi/Approve/SuggestByMentor/Success", createHttpSpecSnippets(
                            pathParameters(
                                    path("coffeeChatId", "커피챗 ID(PK)", true)
                            ),
                            requestFields(
                                    body("start", "멘토링 시작 시간", "KST", true),
                                    body("end", "멘토링 종료 시간", "KST", true)
                            )
                    ))
            );
        }
    }

    @Nested
    @DisplayName("멘티의 커피챗 신청 수락 API [POST /api/coffeechats/approve/apply/{coffeeChatId}]")
    class ApplyByMentee {
        private static final String BASE_URL = "/api/coffeechats/approve/apply/{coffeeChatId}";
        private final ApproveMenteeApplyRequest request = new ApproveMenteeApplyRequest(
                StrategyFixture.KAKAO_ID.getType().getEng(),
                StrategyFixture.KAKAO_ID.getValue()
        );

        @Test
        @DisplayName("멘토가 아니면 권한이 없다")
        void throwExceptionByInvalidPermission() {
            // given
            applyToken(true, mentee.getId(), mentee.getRole());

            // when - then
            failedExecute(
                    patchRequestWithAccessToken(new UrlWithVariables(BASE_URL, COFFEE_CHAT_ID), request),
                    status().isForbidden(),
                    ExceptionSpec.of(AuthExceptionCode.INVALID_PERMISSION),
                    failureDocsWithAccessToken("CoffeeChatApi/Approve/ApplyByMentee/Failure", createHttpSpecSnippets(
                            pathParameters(
                                    path("coffeeChatId", "커피챗 ID(PK)", true)
                            ),
                            requestFields(
                                    body("chatType", "멘토링 진행 방식", "zoom, google, kakao, link, wechat", true),
                                    body("chatValue", "멘토링 진행 방식에 대한 값", "미팅 URL, 메신저 ID, ..", true)
                            )
                    ))
            );
        }

        @Test
        @DisplayName("멘토가 멘티의 커피챗 신청을 수락한다")
        void success() {
            // given
            applyToken(true, mentor.getId(), mentor.getRole());
            doNothing()
                    .when(approveCoffeeChatUseCase)
                    .applyByMentee(any());

            // when - then
            successfulExecute(
                    patchRequestWithAccessToken(new UrlWithVariables(BASE_URL, COFFEE_CHAT_ID), request),
                    status().isNoContent(),
                    successDocsWithAccessToken("CoffeeChatApi/Approve/ApplyByMentee/Success", createHttpSpecSnippets(
                            pathParameters(
                                    path("coffeeChatId", "커피챗 ID(PK)", true)
                            ),
                            requestFields(
                                    body("chatType", "멘토링 진행 방식", "zoom, google, kakao, link, wechat", true),
                                    body("chatValue", "멘토링 진행 방식에 대한 값", "미팅 URL, 메신저 ID, ..", true)
                            )
                    ))
            );
        }
    }
}
