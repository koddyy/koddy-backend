package com.koddy.server.coffeechat.presentation;

import com.koddy.server.coffeechat.application.usecase.CreateMeetingLinkUseCase;
import com.koddy.server.coffeechat.infrastructure.link.zoom.spec.ZoomMeetingLinkResponse;
import com.koddy.server.coffeechat.presentation.dto.request.CreateMeetingLinkRequest;
import com.koddy.server.common.ControllerTest;
import com.koddy.server.member.domain.model.mentee.Mentee;
import com.koddy.server.member.domain.model.mentor.Mentor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.RequestBuilder;

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
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CreateMeetingLinkApiController.class)
@DisplayName("CoffeeChat -> CreateMeetingLinkApiController 테스트")
class CreateMeetingLinkApiControllerTest extends ControllerTest {
    @MockBean
    private CreateMeetingLinkUseCase createMeetingLinkUseCase;

    private final Mentor mentor = MENTOR_1.toDomain().apply(1L);
    private final Mentee mentee = MENTEE_1.toDomain().apply(2L);

    @Nested
    @DisplayName("커피챗 링크 자동 생성 API [POST /api/oauth/{provider}/meetings]")
    class CreateMeetingLink {
        private static final String BASE_URL = "/api/oauth/{provider}/meetings";
        private final CreateMeetingLinkRequest request = new CreateMeetingLinkRequest(
                AUTHORIZATION_CODE,
                REDIRECT_URI,
                STATE,
                "xxxyyy와 멘토링 시간",
                LocalDateTime.of(2024, 1, 5, 20, 0),
                LocalDateTime.of(2024, 1, 5, 21, 0)
        );

        @Test
        @DisplayName("멘토가 아니면 권한이 없다")
        void throwExceptionByInvalidPermission() throws Exception {
            // given
            mockingToken(true, mentee.getId(), mentee.getRoleTypes());

            // when
            final RequestBuilder requestBuilder = postWithAccessToken(new PathWithVariables(BASE_URL, "zoom"), request);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isForbidden())
                    .andDo(failureDocsWithAccessToken("CoffeeChatApi/MeetingLink/Create/Failure", createHttpSpecSnippets(
                            pathParameters(
                                    path("provider", "OAuth & Link Provider", "zoom", true)
                            ),
                            requestFields(
                                    body("authorizationCode", "Authorization Code", "Authorization Code Redirect 응답 시 QueryParam으로 넘어오는 Code 값", true),
                                    body("redirectUri", "Redirect Uri", "Authorization Code 요청 시 redirectUri와 반드시 동일한 값", true),
                                    body("state", "State 값", "Authorization Code Redirect 응답 시 QueryParam으로 넘어오는 State 값", true),
                                    body("topic", "회의 제목", true),
                                    body("start", "회의 시작 시간", "KST", true),
                                    body("end", "회의 종료 시간", "KST", true)
                            )
                    )));
        }

        @Test
        @DisplayName("줌 회의 링크를 자동 생성한다")
        void success() throws Exception {
            // given
            mockingToken(true, mentor.getId(), mentor.getRoleTypes());
            given(createMeetingLinkUseCase.invoke(any())).willReturn(new ZoomMeetingLinkResponse(
                    "88141392261",
                    "sjiwon4491@gmail.com",
                    "xxxyyy와 멘토링 시간",
                    "https://us05web.zoom.us/j/88141392261?pwd=...",
                    60
            ));

            // when
            final RequestBuilder requestBuilder = postWithAccessToken(new PathWithVariables(BASE_URL, "zoom"), request);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(successDocsWithAccessToken("CoffeeChatApi/MeetingLink/Create/Success", createHttpSpecSnippets(
                            pathParameters(
                                    path("provider", "OAuth & Link Provider", "zoom", true)
                            ),
                            requestFields(
                                    body("authorizationCode", "Authorization Code", "Authorization Code Redirect 응답 시 QueryParam으로 넘어오는 Code 값", true),
                                    body("redirectUri", "Redirect Uri", "Authorization Code 요청 시 redirectUri와 반드시 동일한 값", true),
                                    body("state", "State 값", "Authorization Code Redirect 응답 시 QueryParam으로 넘어오는 State 값", true),
                                    body("topic", "회의 제목", true),
                                    body("start", "회의 시작 시간", "KST", true),
                                    body("end", "회의 종료 시간", "KST", true)
                            ),
                            responseFields(
                                    body("id", "미팅 ID"),
                                    body("hostEmail", "호스트 이메일"),
                                    body("topic", "회의 제목"),
                                    body("joinUrl", "회의 참여 URL"),
                                    body("duration", "회의 시간 (Minute 기준)")
                            )
                    )));
        }
    }
}
