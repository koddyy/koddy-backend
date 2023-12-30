package com.koddy.server.auth.presentation;

import com.koddy.server.auth.application.usecase.EmailAuthenticationUseCase;
import com.koddy.server.auth.presentation.dto.request.ConfirmAuthCodeRequest;
import com.koddy.server.auth.presentation.dto.request.SendAuthCodeRequest;
import com.koddy.server.common.ControllerTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.RequestBuilder;

import java.util.UUID;

import static com.koddy.server.common.utils.RestDocsSpecificationUtils.SnippetFactory.body;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.createHttpSpecSnippets;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.successDocs;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EmailAuthenticationApiController.class)
@DisplayName("Auth -> EmailAuthenticationApiController 테스트")
class EmailAuthenticationApiControllerTest extends ControllerTest {
    @MockBean
    private EmailAuthenticationUseCase emailAuthenticationUseCase;

    @Nested
    @DisplayName("인증번호 발송 API [POST /api/auth/email]")
    class SendAuthCode {
        private static final String BASE_URL = "/api/auth/email";

        @Test
        @DisplayName("이메일 인증번호를 발송한다")
        void success() throws Exception {
            // given
            final SendAuthCodeRequest request = new SendAuthCodeRequest("sjiwon4491@gmail.com");
            doNothing()
                    .when(emailAuthenticationUseCase)
                    .sendAuthCode(any());

            // when
            final RequestBuilder requestBuilder = post(BASE_URL, request);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andDo(successDocs("MemberApi/EmailAuth/Send", createHttpSpecSnippets(
                            requestFields(
                                    body("email", "인증 대상 이메일", true)
                            )
                    )));
        }
    }

    @Nested
    @DisplayName("인증번호 확인 API [POST /api/auth/email/confirm]")
    class ConfirmAuthCode {
        private static final String BASE_URL = "/api/auth/email/confirm";

        @Test
        @DisplayName("인증번호를 확인한다")
        void success() throws Exception {
            // given
            final String authCode = UUID.randomUUID().toString().replaceAll("-", "").substring(0, 8);
            final ConfirmAuthCodeRequest request = new ConfirmAuthCodeRequest("sjiwon4491@gmail.com", authCode);
            doNothing()
                    .when(emailAuthenticationUseCase)
                    .confirmAuthCode(any());

            // when
            final RequestBuilder requestBuilder = post(BASE_URL, request);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isNoContent())
                    .andDo(successDocs("MemberApi/EmailAuth/Confirm", createHttpSpecSnippets(
                            requestFields(
                                    body("email", "인증 대상 이메일", true),
                                    body("authCode", "인증번호", true)
                            )
                    )));
        }
    }
}
