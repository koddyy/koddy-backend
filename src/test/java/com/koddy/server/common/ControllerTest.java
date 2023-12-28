package com.koddy.server.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.koddy.server.auth.exception.AuthException;
import com.koddy.server.auth.utils.TokenProvider;
import com.koddy.server.common.config.TestWebBeanConfiguration;
import com.koddy.server.global.base.KoddyExceptionCode;
import com.koddy.server.global.exception.alert.SlackAlertManager;
import com.koddy.server.member.domain.model.RoleType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.restdocs.RestDocumentationContextProvider;
import org.springframework.restdocs.RestDocumentationExtension;
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation;
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

import static com.koddy.server.auth.exception.AuthExceptionCode.INVALID_TOKEN;
import static com.koddy.server.common.utils.TokenUtils.applyAccessToken;
import static com.koddy.server.common.utils.TokenUtils.applyRefreshToken;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.log;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@Tag("Controller")
@WebMvcTest
@ExtendWith(RestDocumentationExtension.class)
@Import(TestWebBeanConfiguration.class)
@AutoConfigureRestDocs
public abstract class ControllerTest {
    // common & external
    @Autowired
    protected MockMvc mockMvc;

    // common & internal
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private TokenProvider tokenProvider;

    @MockBean
    private SlackAlertManager slackAlertManager;

    @BeforeEach
    void setUp(final WebApplicationContext context, final RestDocumentationContextProvider provider) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
                .apply(MockMvcRestDocumentation.documentationConfiguration(provider))
                .alwaysDo(print())
                .alwaysDo(log())
                .addFilter(new CharacterEncodingFilter("UTF-8", true))
                .build();
    }

    /**
     * GET
     */
    protected RequestBuilder get(final String url) {
        return MockMvcRequestBuilders
                .get(url);
    }

    protected RequestBuilder get(final String url, final List<Object> uriVariables) {
        return RestDocumentationRequestBuilders
                .get(url, uriVariables);
    }

    protected RequestBuilder getWithAccessToken(final String url) {
        return MockMvcRequestBuilders
                .get(url)
                .header(AUTHORIZATION, applyAccessToken());
    }

    protected RequestBuilder getWithAccessToken(final String url, final List<Object> uriVariables) {
        return RestDocumentationRequestBuilders
                .get(url, uriVariables)
                .header(AUTHORIZATION, applyAccessToken());
    }

    /**
     * POST + application/json
     */
    protected RequestBuilder post(final String url) {
        return MockMvcRequestBuilders
                .post(url)
                .contentType(APPLICATION_JSON);
    }

    protected RequestBuilder post(final String url, final List<Object> uriVariables) {
        return RestDocumentationRequestBuilders
                .post(url, uriVariables)
                .contentType(APPLICATION_JSON);
    }

    protected RequestBuilder post(final String url, final Object data) {
        return MockMvcRequestBuilders
                .post(url)
                .contentType(APPLICATION_JSON)
                .content(toBody(data));
    }

    protected RequestBuilder post(final String url, final List<Object> uriVariables, final Object data) {
        return RestDocumentationRequestBuilders
                .post(url, uriVariables)
                .contentType(APPLICATION_JSON)
                .content(toBody(data));
    }

    protected RequestBuilder postWithAccessToken(final String url) {
        return MockMvcRequestBuilders
                .post(url)
                .contentType(APPLICATION_JSON)
                .header(AUTHORIZATION, applyAccessToken());
    }

    protected RequestBuilder postWithAccessToken(final String url, final List<Object> uriVariables) {
        return RestDocumentationRequestBuilders
                .post(url, uriVariables)
                .contentType(APPLICATION_JSON)
                .header(AUTHORIZATION, applyAccessToken());
    }

    protected RequestBuilder postWithAccessToken(final String url, final Object data) {
        return MockMvcRequestBuilders
                .post(url)
                .contentType(APPLICATION_JSON)
                .content(toBody(data));
    }

    protected RequestBuilder postWithAccessToken(final String url, final List<Object> uriVariables, final Object data) {
        return RestDocumentationRequestBuilders
                .post(url, uriVariables)
                .contentType(APPLICATION_JSON)
                .header(AUTHORIZATION, applyAccessToken())
                .content(toBody(data));
    }

    protected RequestBuilder postWithRefreshToken(final String url) {
        return MockMvcRequestBuilders
                .post(url)
                .cookie(applyRefreshToken());
    }

    protected RequestBuilder postWithRefreshToken(final String url, final List<Object> uriVariables) {
        return RestDocumentationRequestBuilders
                .post(url, uriVariables)
                .cookie(applyRefreshToken());
    }

    /**
     * POST + multipart/form-data
     */
    protected RequestBuilder multipart(
            final String url,
            final List<MultipartFile> files,
            final MultiValueMap<String, String> params
    ) {
        MockMultipartHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.multipart(url);

        for (final MultipartFile file : files) {
            requestBuilder = requestBuilder.file((MockMultipartFile) file);
        }

        return requestBuilder.queryParams(params);
    }

    protected RequestBuilder multipart(
            final String url,
            final List<Object> uriVariables,
            final List<MultipartFile> files,
            final MultiValueMap<String, String> params
    ) {
        MockMultipartHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders.multipart(url, uriVariables);

        for (final MultipartFile file : files) {
            requestBuilder = requestBuilder.file((MockMultipartFile) file);
        }

        return requestBuilder.queryParams(params);
    }

    protected RequestBuilder multipartWithAccessToken(
            final String url,
            final List<MultipartFile> files,
            final MultiValueMap<String, String> params
    ) {
        MockMultipartHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.multipart(url);

        for (final MultipartFile file : files) {
            requestBuilder = requestBuilder.file((MockMultipartFile) file);
        }

        return requestBuilder
                .header(AUTHORIZATION, applyAccessToken())
                .queryParams(params);
    }

    protected RequestBuilder multipartWithAccessToken(
            final String url,
            final List<Object> uriVariables,
            final List<MultipartFile> files,
            final MultiValueMap<String, String> params
    ) {
        MockMultipartHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders.multipart(url, uriVariables);

        for (final MultipartFile file : files) {
            requestBuilder = requestBuilder.file((MockMultipartFile) file);
        }

        return requestBuilder
                .header(AUTHORIZATION, applyAccessToken())
                .queryParams(params);
    }

    /**
     * PATCH
     */
    protected RequestBuilder patch(final String url, final Object data) {
        return MockMvcRequestBuilders
                .patch(url)
                .contentType(APPLICATION_JSON)
                .content(toBody(data));
    }

    protected RequestBuilder patch(final String url, final List<Object> uriVariables, final Object data) {
        return RestDocumentationRequestBuilders
                .patch(url, uriVariables)
                .contentType(APPLICATION_JSON)
                .content(toBody(data));
    }

    protected RequestBuilder patchWithAccessToken(final String url, final Object data) {
        return MockMvcRequestBuilders
                .patch(url)
                .contentType(APPLICATION_JSON)
                .content(toBody(data));
    }

    protected RequestBuilder patchWithAccessToken(final String url, final List<Object> uriVariables, final Object data) {
        return RestDocumentationRequestBuilders
                .patch(url, uriVariables)
                .contentType(APPLICATION_JSON)
                .header(AUTHORIZATION, applyAccessToken())
                .content(toBody(data));
    }

    /**
     * DELETE
     */
    protected RequestBuilder deleteWithAccessToken(final String url, final List<Object> uriVariables) {
        return RestDocumentationRequestBuilders
                .delete(url, uriVariables)
                .header(AUTHORIZATION, applyAccessToken());
    }

    private String toBody(final Object data) {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (final JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    protected ResultMatcher[] getResultMatchersViaExceptionCode(final KoddyExceptionCode code) {
        return new ResultMatcher[]{
                jsonPath("$.errorCode").exists(),
                jsonPath("$.errorCode").value(code.getErrorCode()),
                jsonPath("$.message").exists(),
                jsonPath("$.message").value(code.getMessage())
        };
    }

    protected ResultMatcher[] getResultMatchersViaExceptionCode(final KoddyExceptionCode code, final String message) {
        return new ResultMatcher[]{
                jsonPath("$.errorCode").exists(),
                jsonPath("$.errorCode").value(code.getErrorCode()),
                jsonPath("$.message").exists(),
                jsonPath("$.message").value(message)
        };
    }

    protected void mockingToken(final boolean isValid, final Long payloadId, final List<RoleType> roles) {
        if (isValid) {
            doNothing()
                    .when(tokenProvider)
                    .validateToken(anyString());
        } else {
            doThrow(new AuthException(INVALID_TOKEN))
                    .when(tokenProvider)
                    .validateToken(anyString());
        }
        given(tokenProvider.getId(anyString())).willReturn(payloadId);
        given(tokenProvider.getRoles(anyString())).willReturn(roles);
    }

    protected void mockingTokenWithInvalidException() {
        doThrow(new AuthException(INVALID_TOKEN))
                .when(tokenProvider)
                .validateToken(anyString());
    }
}
