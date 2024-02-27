package com.koddy.server.common

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.koddy.server.auth.domain.model.AuthToken
import com.koddy.server.auth.domain.service.TokenProvider
import com.koddy.server.auth.exception.AuthException
import com.koddy.server.auth.exception.AuthExceptionCode
import com.koddy.server.common.config.MockAllUseCaseBeanFactoryPostProcessor
import com.koddy.server.common.config.ResetMockTestExecutionListener
import com.koddy.server.common.config.TestAopConfig
import com.koddy.server.common.config.TestWebBeanConfig
import com.koddy.server.common.docs.DocumentField
import com.koddy.server.common.docs.DocumentFieldType
import com.koddy.server.common.docs.ENUM
import com.koddy.server.common.utils.TokenUtils.applyAccessToken
import com.koddy.server.common.utils.TokenUtils.applyRefreshToken
import com.koddy.server.global.base.BusinessExceptionCode
import com.koddy.server.member.domain.model.Member
import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.FeatureSpec
import io.mockk.every
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.ArgumentMatchers.anyString
import org.mockito.BDDMockito.doThrow
import org.mockito.MockitoAnnotations
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.mock.web.MockMultipartFile
import org.springframework.restdocs.ManualRestDocumentation
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.cookies.CookieDocumentation
import org.springframework.restdocs.cookies.RequestCookiesSnippet
import org.springframework.restdocs.cookies.ResponseCookiesSnippet
import org.springframework.restdocs.headers.HeaderDocumentation
import org.springframework.restdocs.headers.RequestHeadersSnippet
import org.springframework.restdocs.headers.ResponseHeadersSnippet
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.delete
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.patch
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post
import org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.payload.RequestFieldsSnippet
import org.springframework.restdocs.payload.ResponseFieldsSnippet
import org.springframework.restdocs.request.PathParametersSnippet
import org.springframework.restdocs.request.QueryParametersSnippet
import org.springframework.restdocs.request.RequestDocumentation
import org.springframework.restdocs.request.RequestPartsSnippet
import org.springframework.restdocs.snippet.Snippet
import org.springframework.test.context.TestExecutionListeners
import org.springframework.test.context.junit.jupiter.SpringExtension
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.ResultActions
import org.springframework.test.web.servlet.ResultMatcher
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.test.web.servlet.setup.StandaloneMockMvcBuilder
import org.springframework.web.filter.CharacterEncodingFilter

@Tag("ApiDocs")
@ExtendWith(
    SpringExtension::class,
    RestDocumentationExtension::class,
)
@Import(
    TestAopConfig::class,
    TestWebBeanConfig::class,
    MockAllUseCaseBeanFactoryPostProcessor::class,
)
@TestExecutionListeners(
    value = [ResetMockTestExecutionListener::class],
    mergeMode = TestExecutionListeners.MergeMode.MERGE_WITH_DEFAULTS,
)
@AutoConfigureRestDocs
@WebMvcTest
abstract class ApiDocsTestKt : FeatureSpec() {
    protected abstract val controller: Any

    private val restDocumentation = ManualRestDocumentation()
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var tokenProvider: TokenProvider

    init {
        beforeSpec {
            setUpMockMvc()
        }
        beforeEach {
            MockitoAnnotations.openMocks(this)
            restDocumentation.beforeTest(javaClass, it.name.testName)
        }
        afterEach {
            restDocumentation.afterTest()
        }
    }

    private fun setUpMockMvc() {
        this.mockMvc =
            MockMvcBuilders.standaloneSetup(controller)
                .apply<StandaloneMockMvcBuilder>(MockMvcRestDocumentation.documentationConfiguration(restDocumentation))
                .alwaysDo<StandaloneMockMvcBuilder>(MockMvcResultHandlers.print())
                .alwaysDo<StandaloneMockMvcBuilder>(MockMvcResultHandlers.log())
                .addFilter<StandaloneMockMvcBuilder>(CharacterEncodingFilter("UTF-8", true))
                .build()
    }

    protected fun getRequest(
        url: String,
        buildRequest: MockHttpServletRequestBuilder.() -> Unit,
    ): ResultActions = mockMvc.perform(get(url).apply(buildRequest))

    protected fun getRequest(
        url: String,
        pathParams: Array<Any> = emptyArray(),
        buildRequest: MockHttpServletRequestBuilder.() -> Unit,
    ): ResultActions = mockMvc.perform(get(url, *pathParams).apply(buildRequest))

    protected fun postRequest(
        url: String,
        pathParams: Array<Any> = emptyArray(),
        buildRequest: MockHttpServletRequestBuilder.() -> Unit,
    ): ResultActions = mockMvc.perform(post(url, *pathParams).apply(buildRequest))

    protected fun postRequest(
        url: String,
        request: Any,
        pathParams: Array<Any> = emptyArray(),
        buildRequest: MockHttpServletRequestBuilder.() -> Unit,
    ): ResultActions =
        mockMvc.perform(
            post(url, *pathParams).apply {
                contentType(APPLICATION_JSON)
                content(objectMapper.writeValueAsString(request))
                buildRequest()
            },
        )

    protected fun multipartRequest(
        url: String,
        files: List<MockMultipartFile>,
        buildRequest: MockHttpServletRequestBuilder.() -> Unit,
    ): ResultActions =
        mockMvc.perform(
            RestDocumentationRequestBuilders.multipart(url)
                .apply {
                    files.forEach { this.file(it) }
                    buildRequest()
                },
        )

    protected fun patchRequest(
        url: String,
        request: Any,
        pathParams: Array<Any> = emptyArray(),
        buildRequest: MockHttpServletRequestBuilder.() -> Unit,
    ): ResultActions =
        mockMvc.perform(
            patch(url, *pathParams)
                .apply {
                    contentType(APPLICATION_JSON)
                    content(objectMapper.writeValueAsString(request))
                    buildRequest()
                },
        )

    protected fun putRequest(
        url: String,
        request: Any,
        pathParams: Array<Any> = emptyArray(),
        buildRequest: MockHttpServletRequestBuilder.() -> Unit,
    ): ResultActions =
        mockMvc.perform(
            put(url, *pathParams)
                .apply {
                    contentType(APPLICATION_JSON)
                    content(objectMapper.writeValueAsString(request))
                    buildRequest()
                },
        )

    protected fun deleteRequest(
        url: String,
        pathParams: Array<Any> = emptyArray(),
        buildRequest: MockHttpServletRequestBuilder.() -> Unit,
    ): ResultActions = mockMvc.perform(delete(url, *pathParams).apply(buildRequest))

    protected fun applyToken(
        isValid: Boolean,
        member: Member<*>,
    ) {
        if (isValid.not()) {
            doThrow(AuthException(AuthExceptionCode.INVALID_TOKEN))
                .`when`(tokenProvider)
                .validateToken(anyString())
        }

        every { tokenProvider.getId(anyString()) } returns member.id
        every { tokenProvider.getAuthority(anyString()) } returns member.authority
    }

    protected fun MockHttpServletRequestBuilder.authorizationHeader(member: Member<*>) {
        applyToken(true, member)
        this.header(AuthToken.ACCESS_TOKEN_HEADER, applyAccessToken())
    }

    protected fun MockHttpServletRequestBuilder.cookieHeader(member: Member<*>) {
        applyToken(true, member)
        this.cookie(applyRefreshToken())
    }

    protected fun ResultActions.isStatus(status: Int): ResultActions = andExpect(status().`is`(status))

    protected fun ResultActions.exceptionOccurred(occurredException: OccurredException): ResultActions =
        andExpectAll(*createExceptionResultMatchers(occurredException))

    protected data class DocumentInfo(
        val identifier: String,
    )

    protected fun ResultActions.makeDocument(
        documentInfo: DocumentInfo,
        vararg snippets: Snippet,
    ): ResultActions =
        andDo(
            document(
                documentInfo.identifier,
                *snippets,
            ),
        )

    protected data class OccurredException(
        val code: BusinessExceptionCode,
        val message: String?,
    ) {
        companion object {
            fun from(code: BusinessExceptionCode): OccurredException = OccurredException(code, null)

            fun of(
                code: BusinessExceptionCode,
                message: String,
            ): OccurredException = OccurredException(code, message)
        }
    }

    protected infix fun String.type(fieldType: DocumentFieldType): DocumentField = DocumentField(this, fieldType.type)

    protected infix fun <T : Enum<T>> String.type(fieldType: ENUM<T>): DocumentField =
        DocumentField(this, fieldType.type, fieldType.enums)

    protected infix fun String.means(description: String): DocumentField =
        DocumentField(this).apply {
            means(description)
        }

    protected fun requestHeaders(vararg fields: DocumentField): RequestHeadersSnippet =
        HeaderDocumentation.requestHeaders(
            fields.map(DocumentField::toHeaderDescriptor).toList(),
        )

    protected fun responseHeaders(vararg fields: DocumentField): ResponseHeadersSnippet =
        HeaderDocumentation.responseHeaders(
            fields.map(DocumentField::toHeaderDescriptor).toList(),
        )

    protected fun requestCookies(vararg fields: DocumentField): RequestCookiesSnippet =
        CookieDocumentation.requestCookies(
            fields.map(DocumentField::toCookieDescriptor).toList(),
        )

    protected fun responseCookies(vararg fields: DocumentField): ResponseCookiesSnippet =
        CookieDocumentation.responseCookies(
            fields.map(DocumentField::toCookieDescriptor).toList(),
        )

    protected fun pathParameters(vararg fields: DocumentField): PathParametersSnippet =
        RequestDocumentation.pathParameters(
            fields.map(DocumentField::toParameterDescriptor).toList(),
        )

    protected fun queryParameters(vararg fields: DocumentField): QueryParametersSnippet =
        RequestDocumentation.queryParameters(
            fields.map(DocumentField::toParameterDescriptor).toList(),
        )

    protected fun fileForms(vararg fields: DocumentField): RequestPartsSnippet =
        RequestDocumentation.requestParts(
            fields.map(DocumentField::toFileDescriptor).toList(),
        )

    protected fun requestFields(vararg fields: DocumentField): RequestFieldsSnippet =
        PayloadDocumentation.requestFields(
            fields.map(DocumentField::toFieldDescriptor).toList(),
        )

    protected fun responseFields(vararg fields: DocumentField): ResponseFieldsSnippet =
        PayloadDocumentation.responseFields(
            fields.map(DocumentField::toFieldDescriptor).toList(),
        )

    private fun createExceptionResultMatchers(occurredException: OccurredException): Array<ResultMatcher> {
        if (occurredException.message == null) {
            return arrayOf(
                jsonPath("$.errorCode").exists(),
                jsonPath("$.errorCode").value(occurredException.code.errorCode),
                jsonPath("$.message").exists(),
                jsonPath("$.message").value(occurredException.code.message),
            )
        }
        return arrayOf(
            jsonPath("$.errorCode").exists(),
            jsonPath("$.errorCode").value(occurredException.code.errorCode),
            jsonPath("$.message").exists(),
            jsonPath("$.message").value(occurredException.message),
        )
    }

    private fun exceptionResponseFields(): Snippet =
        PayloadDocumentation.responseFields(
            PayloadDocumentation.fieldWithPath("errorCode").description("커스텀 예외 코드"),
            PayloadDocumentation.fieldWithPath("message").description("예외 메시지"),
        )

    companion object {
        private val objectMapper = ObjectMapper().apply {
            registerModule(JavaTimeModule())
        }
    }
}
