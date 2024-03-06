package com.koddy.server.common

import com.fasterxml.jackson.databind.ObjectMapper
import com.koddy.server.auth.domain.model.AuthToken
import com.koddy.server.auth.domain.service.TokenProvider
import com.koddy.server.auth.exception.AuthException
import com.koddy.server.auth.exception.AuthExceptionCode
import com.koddy.server.auth.utils.TokenResponseWriter
import com.koddy.server.common.config.TestAopConfig
import com.koddy.server.common.config.TestWebBeanConfig
import com.koddy.server.common.docs.DocumentField
import com.koddy.server.common.docs.DocumentFieldType
import com.koddy.server.common.docs.ENUM
import com.koddy.server.common.docs.STRING
import com.koddy.server.common.docs.SnippetBuilder
import com.koddy.server.common.utils.TokenUtils.MENTEE_ACCESS_TOKEN
import com.koddy.server.common.utils.TokenUtils.MENTOR_ACCESS_TOKEN
import com.koddy.server.common.utils.TokenUtils.REFRESH_TOKEN
import com.koddy.server.global.base.BusinessExceptionCode
import com.koddy.server.global.exception.ExceptionResponse
import com.koddy.server.member.domain.model.Role
import com.ninjasquad.springmockk.MockkBean
import io.mockk.every
import io.mockk.justRun
import jakarta.servlet.http.Cookie
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.restdocs.RestDocumentationContextProvider
import org.springframework.restdocs.RestDocumentationExtension
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.operation.preprocess.Preprocessors
import org.springframework.restdocs.snippet.Snippet
import org.springframework.test.web.servlet.MockHttpServletRequestDsl
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.MockMvcResultHandlersDsl
import org.springframework.test.web.servlet.ResultActionsDsl
import org.springframework.test.web.servlet.ResultHandler
import org.springframework.test.web.servlet.delete
import org.springframework.test.web.servlet.get
import org.springframework.test.web.servlet.multipart
import org.springframework.test.web.servlet.patch
import org.springframework.test.web.servlet.post
import org.springframework.test.web.servlet.put
import org.springframework.test.web.servlet.result.ContentResultMatchersDsl
import org.springframework.test.web.servlet.result.MockMvcResultHandlers
import org.springframework.test.web.servlet.setup.DefaultMockMvcBuilder
import org.springframework.test.web.servlet.setup.MockMvcBuilders
import org.springframework.web.context.WebApplicationContext
import org.springframework.web.filter.CharacterEncodingFilter

@Tag("ApiDocs")
@Import(
    TestAopConfig::class,
    TestWebBeanConfig::class,
)
@ExtendWith(RestDocumentationExtension::class)
@AutoConfigureRestDocs
abstract class ApiDocsTestKt {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @Autowired
    private lateinit var objectMapper: ObjectMapper

    @MockkBean
    private lateinit var tokenProvider: TokenProvider

    @Autowired
    protected lateinit var tokenResponseWriter: TokenResponseWriter

    @BeforeEach
    fun setUpMockMvc(
        context: WebApplicationContext,
        provider: RestDocumentationContextProvider,
    ) {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(context)
            .apply<DefaultMockMvcBuilder>(MockMvcRestDocumentation.documentationConfiguration(provider))
            .addFilter<DefaultMockMvcBuilder>(CharacterEncodingFilter("UTF-8", true))
            .alwaysDo<DefaultMockMvcBuilder>(MockMvcResultHandlers.print())
            .alwaysDo<DefaultMockMvcBuilder>(MockMvcResultHandlers.log())
            .build()
    }

    protected fun getRequest(
        url: String,
        pathParams: Array<Any> = emptyArray(),
        buildRequest: MockHttpServletRequestDsl.() -> Unit,
    ): ResultActionsDsl {
        return mockMvc.get(url, pathParams) {
            buildRequest()
        }
    }

    protected fun postRequest(
        url: String,
        pathParams: Array<Any> = emptyArray(),
        buildRequest: MockHttpServletRequestDsl.() -> Unit,
    ): ResultActionsDsl {
        return mockMvc.post(url, pathParams) {
            buildRequest()
        }
    }

    protected fun multipartRequest(
        url: String,
        pathParams: Array<Any> = emptyArray(),
        buildRequest: MockHttpServletRequestDsl.() -> Unit,
    ): ResultActionsDsl {
        return mockMvc.multipart(url, pathParams) {
            buildRequest()
        }
    }

    protected fun patchRequest(
        url: String,
        pathParams: Array<Any> = emptyArray(),
        buildRequest: MockHttpServletRequestDsl.() -> Unit,
    ): ResultActionsDsl {
        return mockMvc.patch(url, pathParams) {
            buildRequest()
        }
    }

    protected fun putRequest(
        url: String,
        pathParams: Array<Any> = emptyArray(),
        buildRequest: MockHttpServletRequestDsl.() -> Unit,
    ): ResultActionsDsl {
        return mockMvc.put(url, pathParams) {
            buildRequest()
        }
    }

    protected fun deleteRequest(
        url: String,
        pathParams: Array<Any> = emptyArray(),
        buildRequest: MockHttpServletRequestDsl.() -> Unit,
    ): ResultActionsDsl {
        return mockMvc.delete(url, pathParams) {
            buildRequest()
        }
    }

    /**
     * Request Token DSL
     */
    protected fun MockHttpServletRequestDsl.accessToken(token: String) {
        header(AuthToken.ACCESS_TOKEN_HEADER, "${AuthToken.TOKEN_TYPE} $token")
        when (token) {
            MENTOR_ACCESS_TOKEN -> {
                justRun { tokenProvider.validateAccessToken(any()) }
                every { tokenProvider.getId(any()) } returns 1
                every { tokenProvider.getAuthority(any()) } returns Role.MENTOR.authority
            }

            MENTEE_ACCESS_TOKEN -> {
                justRun { tokenProvider.validateAccessToken(any()) }
                every { tokenProvider.getId(any()) } returns 1
                every { tokenProvider.getAuthority(any()) } returns Role.MENTEE.authority
            }

            else -> every { tokenProvider.validateAccessToken(any()) } throws AuthException(AuthExceptionCode.AUTH_REQUIRED)
        }
    }

    protected fun MockHttpServletRequestDsl.refreshToken(token: String) {
        cookie(Cookie(AuthToken.REFRESH_TOKEN_HEADER, token))
        when (token) {
            REFRESH_TOKEN -> justRun { tokenProvider.validateRefreshToken(any()) }
            else -> every { tokenProvider.validateRefreshToken(any()) } throws AuthException(AuthExceptionCode.INVALID_PERMISSION)
        }
    }

    /**
     * Request Body DSL
     */
    protected fun MockHttpServletRequestDsl.bodyContent(value: Any) {
        content = objectMapper.writeValueAsString(value)
        contentType = MediaType.APPLICATION_JSON
    }

    /**
     * Responsd Body DSL
     */
    protected fun ContentResultMatchersDsl.success(value: Any) {
        json(objectMapper.writeValueAsString(value), true)
    }

    protected fun ContentResultMatchersDsl.exception(
        code: BusinessExceptionCode,
        message: String? = null,
    ) {
        json(
            jsonContent = objectMapper.writeValueAsString(
                when (message.isNullOrBlank()) {
                    true -> ExceptionResponse(code)
                    false -> ExceptionResponse(code, message)
                },
            ),
            strict = true,
        )
    }

    // REST Docs Document DSL
    protected fun MockMvcResultHandlersDsl.makeSuccessDocs(
        identifier: String,
        snippetBuilder: SnippetBuilder.() -> Unit,
    ) {
        val builder = SnippetBuilder()
        builder.snippetBuilder()
        handle(makeDocument(identifier, *builder.build()))
    }

    protected fun MockMvcResultHandlersDsl.makeSuccessDocsWithAccessToken(
        identifier: String,
        snippetBuilder: SnippetBuilder.() -> Unit,
    ) {
        val builder = SnippetBuilder()
        builder.requestHeaders(AuthToken.ACCESS_TOKEN_HEADER type STRING means "Access Token")
        builder.snippetBuilder()
        handle(makeDocument(identifier, *builder.build()))
    }

    protected fun MockMvcResultHandlersDsl.makeSuccessDocsWithRefreshToken(
        identifier: String,
        snippetBuilder: SnippetBuilder.() -> Unit,
    ) {
        val builder = SnippetBuilder()
        builder.requestCookies(AuthToken.REFRESH_TOKEN_HEADER type STRING means "Refresh Token")
        builder.snippetBuilder()
        handle(makeDocument(identifier, *builder.build()))
    }

    protected fun MockMvcResultHandlersDsl.makeFailureDocs(
        identifier: String,
        snippetBuilder: SnippetBuilder.() -> Unit,
    ) {
        val builder = SnippetBuilder()
        builder.responseFields(
            "errorCode" type STRING means "커스텀 예외 코드",
            "message" type STRING means "예외 메시지",
        )
        builder.snippetBuilder()
        handle(makeDocument(identifier, *builder.build()))
    }

    protected fun MockMvcResultHandlersDsl.makeFailureDocsWithAccessToken(
        identifier: String,
        snippetBuilder: SnippetBuilder.() -> Unit,
    ) {
        val builder = SnippetBuilder()
        builder.requestHeaders(AuthToken.ACCESS_TOKEN_HEADER type STRING means "Access Token")
        builder.responseFields(
            "errorCode" type STRING means "커스텀 예외 코드",
            "message" type STRING means "예외 메시지",
        )
        builder.snippetBuilder()
        handle(makeDocument(identifier, *builder.build()))
    }

    protected fun MockMvcResultHandlersDsl.makeFailureDocsWithRefreshToken(
        identifier: String,
        snippetBuilder: SnippetBuilder.() -> Unit,
    ) {
        val builder = SnippetBuilder()
        builder.requestCookies(AuthToken.REFRESH_TOKEN_HEADER type STRING means "Refresh Token")
        builder.responseFields(
            "errorCode" type STRING means "커스텀 예외 코드",
            "message" type STRING means "예외 메시지",
        )
        builder.snippetBuilder()
        handle(makeDocument(identifier, *builder.build()))
    }

    private fun makeDocument(
        identifier: String,
        vararg snippets: Snippet,
    ): ResultHandler {
        return MockMvcRestDocumentation.document(
            identifier,
            Preprocessors.preprocessRequest(Preprocessors.prettyPrint()),
            Preprocessors.preprocessResponse(Preprocessors.prettyPrint()),
            *snippets,
        )
    }

    protected infix fun String.type(fieldType: DocumentFieldType): DocumentField = DocumentField(this, fieldType.type)

    protected infix fun <T : Enum<T>> String.type(fieldType: ENUM<T>): DocumentField {
        return DocumentField(this, fieldType.type, fieldType.enums)
    }
}
