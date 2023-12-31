package com.koddy.server.member.presentation;

import com.koddy.server.common.ControllerTest;
import com.koddy.server.member.application.usecase.DuplicateCheckUseCase;
import com.koddy.server.member.application.usecase.SimpleSignUpUseCase;
import com.koddy.server.member.presentation.dto.request.EmailDuplicateCheckRequest;
import com.koddy.server.member.presentation.dto.request.SimpleSignUpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.RequestBuilder;

import static com.koddy.server.common.utils.RestDocsSpecificationUtils.SnippetFactory.body;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.createHttpSpecSnippets;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.failureDocs;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.successDocs;
import static com.koddy.server.global.exception.GlobalExceptionCode.VALIDATION_ERROR;
import static com.koddy.server.member.domain.model.MemberType.MENTOR;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SimpleSignUpApiController.class)
@DisplayName("Member -> SimpleSignUpApiController 테스트")
class SimpleSignUpApiControllerTest extends ControllerTest {
    @MockBean
    private DuplicateCheckUseCase duplicateCheckUseCase;

    @MockBean
    private SimpleSignUpUseCase simpleSignUpUseCase;

    @Nested
    @DisplayName("이메일 중복 체크 API [POST /api/members/duplicate/email]")
    class EmailDuplicateCheck {
        private static final String BASE_URL = "/api/members/duplicate/email";

        @Test
        @DisplayName("이메일 중복 체크를 진행한다")
        void success() throws Exception {
            // given
            final EmailDuplicateCheckRequest request = new EmailDuplicateCheckRequest("sjiwon4491@gmail.com");
            given(duplicateCheckUseCase.isEmailUsable(anyString())).willReturn(true);

            // when
            final RequestBuilder requestBuilder = post(BASE_URL, request);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(successDocs("MemberApi/EmailDuplicateCheck", createHttpSpecSnippets(
                            requestFields(
                                    body("value", "중복 체크를 진행할 이메일", true)
                            ),
                            responseFields(
                                    body("result", "사용 가능 여부")
                            )
                    )));
        }
    }

    @Nested
    @DisplayName("간편 회원가입 API [POST /api/members]")
    class SimpleSignUp {
        private static final String BASE_URL = "/api/members";

        @Test
        @DisplayName("이메일 중복 확인을 진행하지 않았으면 회원가입이 불가능하다")
        void throwExceptionByEmailNotChecked() throws Exception {
            // given
            final SimpleSignUpRequest request = new SimpleSignUpRequest("sjiwon4491@gmail.com", false, "Helloworld123!@#", MENTOR);

            // when
            final RequestBuilder requestBuilder = post(BASE_URL, request);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isBadRequest())
                    .andExpectAll(getResultMatchersViaExceptionCode(VALIDATION_ERROR, "이메일 중복 확인을 진행해야 합니다."))
                    .andDo(failureDocs("MemberApi/SignUp/Failure", createHttpSpecSnippets(
                            requestFields(
                                    body("email", "이메일", true),
                                    body("checked", "이메일 중복 체크 결과", true),
                                    body("password", "비밀번호", true),
                                    body("type", "사용자 타입", "MENTOR / MENTEE", true)
                            )
                    )));
        }

        @Test
        @DisplayName("간편 회원가입을 진행한다")
        void success() throws Exception {
            // given
            final SimpleSignUpRequest request = new SimpleSignUpRequest("sjiwon4491@gmail.com", true, "Helloworld123!@#", MENTOR);
            given(simpleSignUpUseCase.invoke(any())).willReturn(1L);

            // when
            final RequestBuilder requestBuilder = post(BASE_URL, request);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(successDocs("MemberApi/SignUp/Success", createHttpSpecSnippets(
                            requestFields(
                                    body("email", "이메일", true),
                                    body("checked", "이메일 중복 체크 결과", true),
                                    body("password", "비밀번호", true),
                                    body("type", "사용자 타입", "MENTOR / MENTEE", true)
                            ),
                            responseFields(
                                    body("id", "사용자 ID(PK)")
                            )
                    )));
        }
    }
}
