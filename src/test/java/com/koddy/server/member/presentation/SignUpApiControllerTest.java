package com.koddy.server.member.presentation;

import com.koddy.server.common.ControllerTest;
import com.koddy.server.member.application.usecase.SimpleSignUpUseCase;
import com.koddy.server.member.presentation.dto.request.SignUpRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.RequestBuilder;

import static com.koddy.server.common.utils.RestDocsSpecificationUtils.SnippetFactory.body;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.createResponseSnippets;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.failureDocs;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.successDocs;
import static com.koddy.server.global.exception.GlobalExceptionCode.VALIDATION_ERROR;
import static com.koddy.server.member.domain.model.MemberType.MENTOR;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(SignUpApiController.class)
@DisplayName("Member -> SignUpApiController 테스트")
class SignUpApiControllerTest extends ControllerTest {
    @MockBean
    private SimpleSignUpUseCase simpleSignUpUseCase;

    @Nested
    @DisplayName("간편 회원가입 API [POST /api/members]")
    class SimpleSignUp {
        private static final String BASE_URL = "/api/members";

        @Test
        @DisplayName("회원가입 정보가 누락되면 진행할 수 없다")
        void throwExceptionByInsufficientInfo() throws Exception {
            // given
            final SignUpRequest request1 = new SignUpRequest("", true, "Helloworld123!@#", MENTOR);
            final SignUpRequest request2 = new SignUpRequest("sjiwon4491@gmail.com", null, "Helloworld123!@#", MENTOR);
            final SignUpRequest request3 = new SignUpRequest("sjiwon4491@gmail.com", true, "", MENTOR);
            final SignUpRequest request4 = new SignUpRequest("sjiwon4491@gmail.com", true, "Helloworld123!@#", null);

            // when
            final RequestBuilder requestBuilder1 = post(BASE_URL, request1);
            final RequestBuilder requestBuilder2 = post(BASE_URL, request2);
            final RequestBuilder requestBuilder3 = post(BASE_URL, request3);
            final RequestBuilder requestBuilder4 = post(BASE_URL, request4);

            // then
            mockMvc.perform(requestBuilder1)
                    .andExpect(status().isBadRequest())
                    .andExpectAll(getResultMatchersViaExceptionCode(VALIDATION_ERROR, "이메일은 필수입니다."))
                    .andDo(failureDocs("MemberApi/SignUp/Failure/Case1"));
            mockMvc.perform(requestBuilder2)
                    .andExpect(status().isBadRequest())
                    .andExpectAll(getResultMatchersViaExceptionCode(VALIDATION_ERROR, "이메일 중복 확인 결과는 필수입니다."))
                    .andDo(failureDocs("MemberApi/SignUp/Failure/Case2"));
            mockMvc.perform(requestBuilder3)
                    .andExpect(status().isBadRequest())
                    .andExpectAll(getResultMatchersViaExceptionCode(VALIDATION_ERROR, "패스워드는 필수입니다."))
                    .andDo(failureDocs("MemberApi/SignUp/Failure/Case3"));
            mockMvc.perform(requestBuilder4)
                    .andExpect(status().isBadRequest())
                    .andExpectAll(getResultMatchersViaExceptionCode(VALIDATION_ERROR, "회원 타입은 필수입니다."))
                    .andDo(failureDocs("MemberApi/SignUp/Failure/Case4"));
        }

        @Test
        @DisplayName("이메일 중복 확인을 진행하지 않았으면 회원가입이 불가능하다")
        void throwExceptionByEmailNotChecked() throws Exception {
            // given
            final SignUpRequest request = new SignUpRequest("sjiwon4491@gmail.com", false, "Helloworld123!@#", MENTOR);

            // when
            final RequestBuilder requestBuilder = post(BASE_URL, request);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isBadRequest())
                    .andExpectAll(getResultMatchersViaExceptionCode(VALIDATION_ERROR, "이메일 중복 확인을 진행해야 합니다."))
                    .andDo(failureDocs("MemberApi/SignUp/Failure/Case5"));
        }

        @Test
        @DisplayName("간편 회원가입을 진행한다")
        void success() throws Exception {
            // given
            final SignUpRequest request = new SignUpRequest("sjiwon4491@gmail.com", true, "Helloworld123!@#", MENTOR);
            given(simpleSignUpUseCase.invoke(any())).willReturn(1L);

            // when
            final RequestBuilder requestBuilder = post(BASE_URL, request);

            // then
            mockMvc.perform(requestBuilder)
                    .andExpect(status().isOk())
                    .andDo(successDocs("MemberApi/SignUp/Success", createResponseSnippets(
                            requestFields(
                                    body("email", "이메일", true),
                                    body("checked", "이메일", true),
                                    body("password", "이메일", true),
                                    body("type", "이메일", true)
                            ),
                            responseFields(
                                    body("id", "사용자 ID(PK)")
                            )
                    )));
        }
    }
}
