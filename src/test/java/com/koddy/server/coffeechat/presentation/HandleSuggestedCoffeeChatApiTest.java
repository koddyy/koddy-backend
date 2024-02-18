package com.koddy.server.coffeechat.presentation;

import com.koddy.server.auth.exception.AuthExceptionCode;
import com.koddy.server.coffeechat.application.usecase.HandleMentorSuggestedCoffeeChatUseCase;
import com.koddy.server.coffeechat.presentation.request.PendingSuggestedCoffeeChatRequest;
import com.koddy.server.coffeechat.presentation.request.RejectSuggestedCoffeeChatRequest;
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
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("CoffeeChat -> HandleSuggestedCoffeeChatApi 테스트")
class HandleSuggestedCoffeeChatApiTest extends ControllerTest {
    @Autowired
    private HandleMentorSuggestedCoffeeChatUseCase handleMentorSuggestedCoffeeChatUseCase;

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
            applyToken(true, mentor);

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
            applyToken(true, mentee);
            doNothing()
                    .when(handleMentorSuggestedCoffeeChatUseCase)
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
                "질문..",
                LocalDateTime.of(2024, 2, 1, 18, 0).toString(),
                LocalDateTime.of(2024, 2, 1, 19, 0).toString()
        );

        @Test
        @DisplayName("멘티가 아니면 권한이 없다")
        void throwExceptionByInvalidPermission() {
            // given
            applyToken(true, mentor);

            // when - then
            failedExecute(
                    patchRequestWithAccessToken(new UrlWithVariables(BASE_URL, COFFEE_CHAT_ID), request),
                    status().isForbidden(),
                    ExceptionSpec.of(AuthExceptionCode.INVALID_PERMISSION),
                    failureDocsWithAccessToken("CoffeeChatApi/LifeCycle/SuggestedByMentor/Pending/Failure/Case1", createHttpSpecSnippets(
                            pathParameters(
                                    path("coffeeChatId", "커피챗 ID(PK)", true)
                            ),
                            requestFields(
                                    body("question", "멘토에게 궁금한 점", true),
                                    body("start", "멘토링 시작 날짜", "[KST] yyyy-MM-ddTHH:mm:ss" + ENTER + "-> 시간 = 00:00:00 ~ 23:59:59", true),
                                    body("end", "멘토링 종료 날짜", "[KST] yyyy-MM-ddTHH:mm:ss" + ENTER + "-> 시간 = 00:00:00 ~ 23:59:59", true)
                            )
                    ))
            );
        }

        @Test
        @DisplayName("이미 예약되었거나 멘토링이 가능하지 않은 날짜면 예외가 발생한다")
        void throwExceptionByCannotReservation() {
            // given
            applyToken(true, mentee);
            doThrow(new MemberException(CANNOT_RESERVATION))
                    .when(handleMentorSuggestedCoffeeChatUseCase)
                    .pending(any());

            // when - then
            failedExecute(
                    patchRequestWithAccessToken(new UrlWithVariables(BASE_URL, COFFEE_CHAT_ID), request),
                    status().isConflict(),
                    ExceptionSpec.of(MemberExceptionCode.CANNOT_RESERVATION),
                    failureDocsWithAccessToken("CoffeeChatApi/LifeCycle/SuggestedByMentor/Pending/Failure/Case2", createHttpSpecSnippets(
                            pathParameters(
                                    path("coffeeChatId", "커피챗 ID(PK)", true)
                            ),
                            requestFields(
                                    body("question", "멘토에게 궁금한 점", true),
                                    body("start", "멘토링 시작 날짜", "[KST] yyyy-MM-ddTHH:mm:ss" + ENTER + "-> 시간 = 00:00:00 ~ 23:59:59", true),
                                    body("end", "멘토링 종료 날짜", "[KST] yyyy-MM-ddTHH:mm:ss" + ENTER + "-> 시간 = 00:00:00 ~ 23:59:59", true)
                            )
                    ))
            );
        }

        @Test
        @DisplayName("멘토가 제안한 커피챗을 1차 수락한다")
        void success() {
            // given
            applyToken(true, mentee);
            doNothing()
                    .when(handleMentorSuggestedCoffeeChatUseCase)
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
                                    body("question", "멘토에게 궁금한 점", true),
                                    body("start", "멘토링 시작 날짜", "[KST] yyyy-MM-ddTHH:mm:ss" + ENTER + "-> 시간 = 00:00:00 ~ 23:59:59", true),
                                    body("end", "멘토링 종료 날짜", "[KST] yyyy-MM-ddTHH:mm:ss" + ENTER + "-> 시간 = 00:00:00 ~ 23:59:59", true)
                            )
                    ))
            );
        }
    }
}
