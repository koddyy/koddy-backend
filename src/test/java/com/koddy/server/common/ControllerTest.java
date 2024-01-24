package com.koddy.server.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.koddy.server.auth.exception.AuthException;
import com.koddy.server.auth.utils.TokenProvider;
import com.koddy.server.common.config.MockAllUseCaseBeanFactoryPostProcessor;
import com.koddy.server.common.config.ResetMockTestExecutionListener;
import com.koddy.server.common.config.TestAopConfig;
import com.koddy.server.common.config.TestWebBeanConfig;
import com.koddy.server.global.base.KoddyExceptionCode;
import com.koddy.server.global.exception.alert.SlackAlertManager;
import com.koddy.server.member.domain.model.Role;
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
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.util.MultiValueMap;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.filter.CharacterEncodingFilter;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

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
@Import({TestAopConfig.class, TestWebBeanConfig.class, MockAllUseCaseBeanFactoryPostProcessor.class})
@TestExecutionListeners(
        value = ResetMockTestExecutionListener.class,
        mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS
)
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

    public record UrlWithVariables(
            String url,
            Object... variables
    ) {
    }

    /**
     * GET
     */
    protected RequestBuilder getRequest(final String url) {
        return MockMvcRequestBuilders.get(url);
    }

    protected RequestBuilder getRequest(final UrlWithVariables path) {
        return RestDocumentationRequestBuilders.get(path.url, path.variables);
    }

    protected final RequestBuilder getRequest(final String url, final Map<String, String> params) {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(url);

        for (final String key : params.keySet()) {
            requestBuilder = requestBuilder.queryParam(key, params.get(key));
        }

        return requestBuilder;
    }

    protected final RequestBuilder getRequest(final UrlWithVariables path, final Map<String, String> params) {
        MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders.get(path.url, path.variables);

        for (final String key : params.keySet()) {
            requestBuilder = requestBuilder.queryParam(key, params.get(key));
        }

        return requestBuilder;
    }

    protected final RequestBuilder getRequest(
            final String url,
            final Map<String, String> params,
            final List<MultiValueMap<String, String>> multiParams
    ) {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(url);

        for (final String key : params.keySet()) {
            requestBuilder = requestBuilder.queryParam(key, params.get(key));
        }

        for (final MultiValueMap<String, String> multiValue : multiParams) {
            requestBuilder = requestBuilder.queryParams(multiValue);
        }

        return requestBuilder;
    }

    protected final RequestBuilder getRequest(
            final UrlWithVariables path,
            final Map<String, String> params,
            final List<MultiValueMap<String, String>> multiParams
    ) {
        MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders.get(path.url, path.variables);

        for (final String key : params.keySet()) {
            requestBuilder = requestBuilder.queryParam(key, params.get(key));
        }

        for (final MultiValueMap<String, String> multiValue : multiParams) {
            requestBuilder = requestBuilder.queryParams(multiValue);
        }

        return requestBuilder;
    }

    protected RequestBuilder getRequestWithAccessToken(final String url) {
        return MockMvcRequestBuilders
                .get(url)
                .header(AUTHORIZATION, applyAccessToken());
    }

    protected RequestBuilder getRequestWithAccessToken(final UrlWithVariables path) {
        return RestDocumentationRequestBuilders
                .get(path.url, path.variables)
                .header(AUTHORIZATION, applyAccessToken());
    }

    protected final RequestBuilder getRequestWithAccessToken(final String url, final Map<String, String> params) {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(url);

        for (final String key : params.keySet()) {
            requestBuilder = requestBuilder.queryParam(key, params.get(key));
        }

        return requestBuilder.header(AUTHORIZATION, applyAccessToken());
    }

    protected final RequestBuilder getRequestWithAccessToken(final UrlWithVariables path, final Map<String, String> params) {
        MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders.get(path.url, path.variables);

        for (final String key : params.keySet()) {
            requestBuilder = requestBuilder.queryParam(key, params.get(key));
        }

        return requestBuilder.header(AUTHORIZATION, applyAccessToken());
    }

    protected final RequestBuilder getRequestWithAccessToken(
            final String url,
            final Map<String, String> params,
            final List<MultiValueMap<String, String>> multiParams
    ) {
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get(url);

        for (final String key : params.keySet()) {
            requestBuilder = requestBuilder.queryParam(key, params.get(key));
        }

        for (final MultiValueMap<String, String> multiValue : multiParams) {
            requestBuilder = requestBuilder.queryParams(multiValue);
        }

        return requestBuilder.header(AUTHORIZATION, applyAccessToken());
    }

    protected final RequestBuilder getRequestWithAccessToken(
            final UrlWithVariables path,
            final Map<String, String> params,
            final List<MultiValueMap<String, String>> multiParams
    ) {
        MockHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders.get(path.url, path.variables);

        for (final String key : params.keySet()) {
            requestBuilder = requestBuilder.queryParam(key, params.get(key));
        }

        for (final MultiValueMap<String, String> multiValue : multiParams) {
            requestBuilder = requestBuilder.queryParams(multiValue);
        }

        return requestBuilder.header(AUTHORIZATION, applyAccessToken());
    }

    /**
     * POST + application/json
     */
    protected RequestBuilder postRequest(final String url) {
        return MockMvcRequestBuilders
                .post(url)
                .contentType(APPLICATION_JSON);
    }

    protected RequestBuilder postRequest(final UrlWithVariables path) {
        return RestDocumentationRequestBuilders
                .post(path.url, path.variables)
                .contentType(APPLICATION_JSON);
    }

    protected RequestBuilder postRequest(final String url, final Object data) {
        return MockMvcRequestBuilders
                .post(url)
                .contentType(APPLICATION_JSON)
                .content(toBody(data));
    }

    protected RequestBuilder postRequest(final UrlWithVariables path, final Object data) {
        return RestDocumentationRequestBuilders
                .post(path.url, path.variables)
                .contentType(APPLICATION_JSON)
                .content(toBody(data));
    }

    protected RequestBuilder postRequestWithAccessToken(final String url) {
        return MockMvcRequestBuilders
                .post(url)
                .contentType(APPLICATION_JSON)
                .header(AUTHORIZATION, applyAccessToken());
    }

    protected RequestBuilder postRequestWithAccessToken(final UrlWithVariables path) {
        return RestDocumentationRequestBuilders
                .post(path.url, path.variables)
                .contentType(APPLICATION_JSON)
                .header(AUTHORIZATION, applyAccessToken());
    }

    protected RequestBuilder postRequestWithAccessToken(final String url, final Object data) {
        return MockMvcRequestBuilders
                .post(url)
                .contentType(APPLICATION_JSON)
                .header(AUTHORIZATION, applyAccessToken())
                .content(toBody(data));
    }

    protected RequestBuilder postRequestWithAccessToken(final UrlWithVariables path, final Object data) {
        return RestDocumentationRequestBuilders
                .post(path.url, path.variables)
                .contentType(APPLICATION_JSON)
                .header(AUTHORIZATION, applyAccessToken())
                .content(toBody(data));
    }

    protected RequestBuilder postRequestWithRefreshToken(final String url) {
        return MockMvcRequestBuilders
                .post(url)
                .cookie(applyRefreshToken());
    }

    protected RequestBuilder postRequestWithRefreshToken(final UrlWithVariables path) {
        return RestDocumentationRequestBuilders
                .post(path.url, path.variables)
                .cookie(applyRefreshToken());
    }

    /**
     * POST + multipart/form-data
     */
    protected final RequestBuilder multipartRequest(final String url, final List<MultipartFile> files) {
        MockMultipartHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.multipart(url);

        for (final MultipartFile file : files) {
            requestBuilder = requestBuilder.file((MockMultipartFile) file);
        }

        return requestBuilder;
    }

    @SafeVarargs
    protected final RequestBuilder multipartRequest(
            final String url,
            final List<MultipartFile> files,
            final Map<String, String> params,
            final MultiValueMap<String, String>... multiParams
    ) {
        MockMultipartHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.multipart(url);

        for (final MultipartFile file : files) {
            requestBuilder = requestBuilder.file((MockMultipartFile) file);
        }

        for (final String key : params.keySet()) {
            requestBuilder = (MockMultipartHttpServletRequestBuilder) requestBuilder.queryParam(key, params.get(key));
        }

        for (final MultiValueMap<String, String> multiParam : multiParams) {
            requestBuilder = (MockMultipartHttpServletRequestBuilder) requestBuilder.queryParams(multiParam);
        }

        return requestBuilder;
    }

    @SafeVarargs
    protected final RequestBuilder multipartRequest(
            final String url,
            final List<Object> uriVariables,
            final List<MultipartFile> files,
            final Map<String, String> params,
            final MultiValueMap<String, String>... multiParams
    ) {
        MockMultipartHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders.multipart(url, uriVariables);

        for (final MultipartFile file : files) {
            requestBuilder = requestBuilder.file((MockMultipartFile) file);
        }

        for (final String key : params.keySet()) {
            requestBuilder = (MockMultipartHttpServletRequestBuilder) requestBuilder.queryParam(key, params.get(key));
        }

        for (final MultiValueMap<String, String> multiParam : multiParams) {
            requestBuilder = (MockMultipartHttpServletRequestBuilder) requestBuilder.queryParams(multiParam);
        }

        return requestBuilder;
    }

    protected final RequestBuilder multipartRequestWithAccessToken(
            final String url,
            final List<MultipartFile> files
    ) {
        MockMultipartHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.multipart(url);

        for (final MultipartFile file : files) {
            requestBuilder = requestBuilder.file((MockMultipartFile) file);
        }

        return requestBuilder.header(AUTHORIZATION, applyAccessToken());
    }

    @SafeVarargs
    protected final RequestBuilder multipartRequestWithAccessToken(
            final String url,
            final List<MultipartFile> files,
            final Map<String, String> params,
            final MultiValueMap<String, String>... multiParams
    ) {
        MockMultipartHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.multipart(url);

        for (final MultipartFile file : files) {
            requestBuilder = requestBuilder.file((MockMultipartFile) file);
        }

        for (final String key : params.keySet()) {
            requestBuilder = (MockMultipartHttpServletRequestBuilder) requestBuilder.queryParam(key, params.get(key));
        }

        for (final MultiValueMap<String, String> multiParam : multiParams) {
            requestBuilder = (MockMultipartHttpServletRequestBuilder) requestBuilder.queryParams(multiParam);
        }

        return requestBuilder.header(AUTHORIZATION, applyAccessToken());
    }

    @SafeVarargs
    protected final RequestBuilder multipartRequestWithAccessToken(
            final String url,
            final List<Object> uriVariables,
            final List<MultipartFile> files,
            final Map<String, String> params,
            final MultiValueMap<String, String>... multiParams
    ) {
        MockMultipartHttpServletRequestBuilder requestBuilder = RestDocumentationRequestBuilders.multipart(url, uriVariables);

        for (final MultipartFile file : files) {
            requestBuilder = requestBuilder.file((MockMultipartFile) file);
        }

        for (final String key : params.keySet()) {
            requestBuilder = (MockMultipartHttpServletRequestBuilder) requestBuilder.queryParam(key, params.get(key));
        }

        for (final MultiValueMap<String, String> multiParam : multiParams) {
            requestBuilder = (MockMultipartHttpServletRequestBuilder) requestBuilder.queryParams(multiParam);
        }

        return requestBuilder.header(AUTHORIZATION, applyAccessToken());
    }

    /**
     * PATCH
     */
    protected RequestBuilder patchRequest(final String url, final Object data) {
        return MockMvcRequestBuilders
                .patch(url)
                .contentType(APPLICATION_JSON)
                .content(toBody(data));
    }

    protected RequestBuilder patchRequest(final UrlWithVariables path, final Object data) {
        return RestDocumentationRequestBuilders
                .patch(path.url, path.variables)
                .contentType(APPLICATION_JSON)
                .content(toBody(data));
    }

    protected RequestBuilder patchRequestWithAccessToken(final String url, final Object data) {
        return MockMvcRequestBuilders
                .patch(url)
                .contentType(APPLICATION_JSON)
                .header(AUTHORIZATION, applyAccessToken())
                .content(toBody(data));
    }

    protected RequestBuilder patchRequestWithAccessToken(final UrlWithVariables path, final Object data) {
        return RestDocumentationRequestBuilders
                .patch(path.url, path.variables)
                .contentType(APPLICATION_JSON)
                .header(AUTHORIZATION, applyAccessToken())
                .content(toBody(data));
    }

    /**
     * DELETE
     */
    protected RequestBuilder deleteRequestWithAccessToken(final String url) {
        return RestDocumentationRequestBuilders
                .delete(url)
                .header(AUTHORIZATION, applyAccessToken());
    }

    protected RequestBuilder deleteRequestWithAccessToken(final UrlWithVariables path) {
        return RestDocumentationRequestBuilders
                .delete(path.url, path.variables)
                .header(AUTHORIZATION, applyAccessToken());
    }

    private String toBody(final Object data) {
        try {
            return objectMapper.writeValueAsString(data);
        } catch (final JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Execute & Result Documentation
     */
    public record ExceptionSpec(
            KoddyExceptionCode code,
            String message
    ) {
        public static ExceptionSpec of(final KoddyExceptionCode code) {
            return new ExceptionSpec(code, null);
        }

        public static ExceptionSpec of(final KoddyExceptionCode code, final String message) {
            return new ExceptionSpec(code, message);
        }
    }

    protected void failedExecute(
            final RequestBuilder requestBuilder,
            final ResultMatcher status,
            final ExceptionSpec exceptionSpec,
            final ResultHandler resultHandler
    ) {
        try {
            mockMvc.perform(requestBuilder)
                    .andExpect(status)
                    .andExpectAll(createExceptionResultMatchers(exceptionSpec))
                    .andDo(resultHandler);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected void successfulExecute(
            final RequestBuilder requestBuilder,
            final ResultMatcher status,
            final ResultHandler resultHandler
    ) {
        try {
            mockMvc.perform(requestBuilder)
                    .andExpect(status)
                    .andDo(resultHandler);
        } catch (final Exception e) {
            throw new RuntimeException(e);
        }
    }

    private ResultMatcher[] createExceptionResultMatchers(final ExceptionSpec exceptionSpec) {
        if (exceptionSpec.message == null) {
            return new ResultMatcher[]{
                    jsonPath("$.errorCode").exists(),
                    jsonPath("$.errorCode").value(exceptionSpec.code.getErrorCode()),
                    jsonPath("$.message").exists(),
                    jsonPath("$.message").value(exceptionSpec.code.getMessage())
            };
        }
        return new ResultMatcher[]{
                jsonPath("$.errorCode").exists(),
                jsonPath("$.errorCode").value(exceptionSpec.code.getErrorCode()),
                jsonPath("$.message").exists(),
                jsonPath("$.message").value(exceptionSpec.message)
        };
    }

    protected void applyToken(final boolean isValid, final Long payloadId, final Role role) {
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
        given(tokenProvider.getAuthority(anyString())).willReturn(role.getAuthority());
    }
}
