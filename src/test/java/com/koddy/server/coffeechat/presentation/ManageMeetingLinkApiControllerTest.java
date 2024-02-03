package com.koddy.server.coffeechat.presentation;

import com.koddy.server.auth.exception.AuthExceptionCode;
import com.koddy.server.coffeechat.application.usecase.ManageMeetingLinkUseCase;
import com.koddy.server.coffeechat.infrastructure.link.zoom.spec.ZoomMeetingLinkResponse;
import com.koddy.server.coffeechat.presentation.dto.request.CreateMeetingLinkRequest;
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
import static com.koddy.server.common.utils.OAuthUtils.AUTHORIZATION_CODE;
import static com.koddy.server.common.utils.OAuthUtils.REDIRECT_URI;
import static com.koddy.server.common.utils.OAuthUtils.STATE;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.SnippetFactory.body;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.SnippetFactory.path;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.createHttpSpecSnippets;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.failureDocsWithAccessToken;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.successDocsWithAccessToken;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("CoffeeChat -> ManageMeetingLinkApiController 테스트")
class ManageMeetingLinkApiControllerTest extends ControllerTest {
    @Autowired
    private ManageMeetingLinkUseCase manageMeetingLinkUseCase;

    private final Mentor mentor = MENTOR_1.toDomain().apply(1L);
    private final Mentee mentee = MENTEE_1.toDomain().apply(2L);

    @Nested
    @DisplayName("커피챗 링크 생성 API [POST /api/oauth/{provider}/meetings]")
    class CreateMeetingLink {
        private static final String BASE_URL = "/api/oauth/{provider}/meetings";
        private final CreateMeetingLinkRequest request = new CreateMeetingLinkRequest(
                AUTHORIZATION_CODE,
                REDIRECT_URI,
                STATE,
                "xxxyyy와 멘토링 시간",
                LocalDateTime.of(2024, 1, 5, 20, 0).toString(),
                LocalDateTime.of(2024, 1, 5, 21, 0).toString()
        );

        @Test
        @DisplayName("멘토가 아니면 권한이 없다")
        void throwExceptionByInvalidPermission() {
            // given
            applyToken(true, mentee);

            // when - then
            failedExecute(
                    postRequestWithAccessToken(new UrlWithVariables(BASE_URL, "zoom"), request),
                    status().isForbidden(),
                    ExceptionSpec.of(AuthExceptionCode.INVALID_PERMISSION),
                    failureDocsWithAccessToken("CoffeeChatApi/MeetingLink/Create/Failure", createHttpSpecSnippets(
                            pathParameters(
                                    path("provider", "OAuth & Link Provider", "zoom", true)
                            ),
                            requestFields(
                                    body("authorizationCode", "Authorization Code", "QueryParam -> code", true),
                                    body("redirectUri", "Redirect Uri", "Authorization Code 요청 URI와 동일 값", true),
                                    body("state", "State 값", "QueryParam -> state", true),
                                    body("topic", "회의 제목", true),
                                    body("start", "회의 시작 시간", "[KST] yyyy-MM-ddTHH:mm:ss" + ENTER + "-> 시간 = 00:00:00 ~ 23:59:59", true),
                                    body("end", "회의 종료 시간", "[KST] yyyy-MM-ddTHH:mm:ss" + ENTER + "-> 시간 = 00:00:00 ~ 23:59:59", true)
                            )
                    ))
            );
        }

        @Test
        @DisplayName("커피챗 링크를 생성한다")
        void success() {
            // given
            applyToken(true, mentor);
            given(manageMeetingLinkUseCase.create(any())).willReturn(new ZoomMeetingLinkResponse(
                    "88141392261",
                    "sjiwon4491@gmail.com",
                    "xxxyyy와 멘토링 시간",
                    "https://us05web.zoom.us/j/88141392261?pwd=...",
                    60
            ));

            // when - then
            successfulExecute(
                    postRequestWithAccessToken(new UrlWithVariables(BASE_URL, "zoom"), request),
                    status().isOk(),
                    successDocsWithAccessToken("CoffeeChatApi/MeetingLink/Create/Success", createHttpSpecSnippets(
                            pathParameters(
                                    path("provider", "OAuth & Link Provider", "zoom", true)
                            ),
                            requestFields(
                                    body("authorizationCode", "Authorization Code", "QueryParam -> code", true),
                                    body("redirectUri", "Redirect Uri", "Authorization Code 요청 URI와 동일 값", true),
                                    body("state", "State 값", "QueryParam -> state", true),
                                    body("topic", "회의 제목", true),
                                    body("start", "회의 시작 시간", "[KST] yyyy-MM-ddTHH:mm:ss" + ENTER + "-> 시간 = 00:00:00 ~ 23:59:59", true),
                                    body("end", "회의 종료 시간", "[KST] yyyy-MM-ddTHH:mm:ss" + ENTER + "-> 시간 = 00:00:00 ~ 23:59:59", true)
                            ),
                            responseFields(
                                    body("id", "미팅 ID"),
                                    body("hostEmail", "호스트 이메일"),
                                    body("topic", "회의 제목"),
                                    body("joinUrl", "회의 참여 URL"),
                                    body("duration", "회의 시간 (Minute 기준)")
                            )
                    ))
            );
        }
    }

    @Nested
    @DisplayName("커피챗 링크 삭제 API [DELETE /api/oauth/{provider}/meetings/{meetingId}]")
    class DeleteMeetingLink {
        private static final String BASE_URL = "/api/oauth/{provider}/meetings/{meetingId}";

        @Test
        @DisplayName("멘토가 아니면 권한이 없다")
        void throwExceptionByInvalidPermission() {
            // given
            applyToken(true, mentee);

            // when - then
            failedExecute(
                    deleteRequestWithAccessToken(new UrlWithVariables(BASE_URL, "zoom", "88141392261")),
                    status().isForbidden(),
                    ExceptionSpec.of(AuthExceptionCode.INVALID_PERMISSION),
                    failureDocsWithAccessToken("CoffeeChatApi/MeetingLink/Delete/Failure", createHttpSpecSnippets(
                            pathParameters(
                                    path("provider", "OAuth & Link Provider", "zoom", true),
                                    path("meetingId", "미팅 ID", true)
                            )
                    ))
            );
        }

        @Test
        @DisplayName("생성한 커피챗 링크를 삭제한다")
        void success() {
            // given
            applyToken(true, mentor);
            doNothing()
                    .when(manageMeetingLinkUseCase)
                    .delete(any());

            // when - then
            successfulExecute(
                    deleteRequestWithAccessToken(new UrlWithVariables(BASE_URL, "zoom", "88141392261")),
                    status().isNoContent(),
                    successDocsWithAccessToken("CoffeeChatApi/MeetingLink/Delete/Success", createHttpSpecSnippets(
                            pathParameters(
                                    path("provider", "OAuth & Link Provider", "zoom", true),
                                    path("meetingId", "미팅 ID", "미팅 생성 시 응답 받은 ID", true)
                            )
                    ))
            );
        }
    }
}
