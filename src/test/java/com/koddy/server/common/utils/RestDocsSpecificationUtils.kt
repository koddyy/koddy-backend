package com.koddy.server.common.utils

import com.koddy.server.auth.domain.model.AuthToken
import org.springframework.restdocs.cookies.CookieDescriptor
import org.springframework.restdocs.cookies.CookieDocumentation
import org.springframework.restdocs.headers.HeaderDescriptor
import org.springframework.restdocs.headers.HeaderDocumentation
import org.springframework.restdocs.mockmvc.MockMvcRestDocumentation
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler
import org.springframework.restdocs.operation.preprocess.OperationRequestPreprocessor
import org.springframework.restdocs.operation.preprocess.OperationResponsePreprocessor
import org.springframework.restdocs.operation.preprocess.Preprocessors
import org.springframework.restdocs.payload.FieldDescriptor
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.request.ParameterDescriptor
import org.springframework.restdocs.request.RequestDocumentation
import org.springframework.restdocs.request.RequestPartDescriptor
import org.springframework.restdocs.snippet.Attributes
import org.springframework.restdocs.snippet.Snippet

object RestDocsSpecificationUtils {
    @JvmStatic
    fun successDocs(
        identifier: String,
        vararg snippets: Snippet,
    ): RestDocumentationResultHandler {
        return MockMvcRestDocumentation.document(
            identifier,
            documentRequest,
            documentResponse,
            *snippets,
        )
    }

    @JvmStatic
    fun successDocsWithAccessToken(
        identifier: String,
        vararg snippets: Snippet,
    ): RestDocumentationResultHandler {
        return MockMvcRestDocumentation.document(
            identifier,
            documentRequest,
            documentResponse,
            *arrayOf(headerWithAccessToken, *snippets),
        )
    }

    @JvmStatic
    fun successDocsWithRefreshToken(
        identifier: String,
        vararg snippets: Snippet,
    ): RestDocumentationResultHandler {
        return MockMvcRestDocumentation.document(
            identifier,
            documentRequest,
            documentResponse,
            *arrayOf(cookieWithRefreshToken, *snippets),
        )
    }

    @JvmStatic
    fun failureDocs(
        identifier: String,
        vararg snippets: Snippet,
    ): RestDocumentationResultHandler {
        return MockMvcRestDocumentation.document(
            identifier,
            documentRequest,
            documentResponse,
            *arrayOf(exceptionResponseFields, *snippets),
        )
    }

    @JvmStatic
    fun failureDocsWithAccessToken(
        identifier: String,
        vararg snippets: Snippet,
    ): RestDocumentationResultHandler {
        return MockMvcRestDocumentation.document(
            identifier,
            documentRequest,
            documentResponse,
            *arrayOf(headerWithAccessToken, exceptionResponseFields, *snippets),
        )
    }

    @JvmStatic
    fun failureDocsWithRefreshToken(
        identifier: String,
        vararg snippets: Snippet,
    ): RestDocumentationResultHandler {
        return MockMvcRestDocumentation.document(
            identifier,
            documentRequest,
            documentResponse,
            *arrayOf(cookieWithRefreshToken, exceptionResponseFields, *snippets),
        )
    }

    @JvmStatic
    fun createHttpSpecSnippets(vararg snippets: Snippet): Array<Snippet> {
        return snippets.toList().toTypedArray()
    }

    object SnippetFactory {
        /**
         * HeaderDocumentation.requestHeaders()
         * <br></br>
         * HeaderDocumentation.responseHeaders()
         */
        @JvmStatic
        fun header(
            name: String?,
            description: String?,
        ): HeaderDescriptor {
            return HeaderDocumentation
                .headerWithName(name)
                .description(description)
        }

        @JvmStatic
        fun header(
            name: String?,
            description: String?,
            mustRequired: Boolean,
        ): HeaderDescriptor {
            val result: HeaderDescriptor =
                HeaderDocumentation
                    .headerWithName(name)
                    .description(description)
            return if (mustRequired) result else result.optional()
        }

        @JvmStatic
        fun header(
            name: String?,
            description: String?,
            constraint: String,
        ): HeaderDescriptor {
            return HeaderDocumentation
                .headerWithName(name)
                .description(description)
                .attributes(constraint(constraint))
        }

        @JvmStatic
        fun header(
            name: String?,
            description: String?,
            constraint: String,
            mustRequired: Boolean,
        ): HeaderDescriptor {
            val result: HeaderDescriptor =
                HeaderDocumentation
                    .headerWithName(name)
                    .description(description)
                    .attributes(constraint(constraint))
            return if (mustRequired) result else result.optional()
        }

        /**
         * CookieDocumentation.requestCookies()
         * <br></br>
         * CookieDocumentation.responseCookies()
         */
        @JvmStatic
        fun cookie(
            name: String?,
            description: String?,
        ): CookieDescriptor {
            return CookieDocumentation
                .cookieWithName(name)
                .description(description)
        }

        @JvmStatic
        fun cookie(
            name: String?,
            description: String?,
            mustRequired: Boolean,
        ): CookieDescriptor {
            val result: CookieDescriptor =
                CookieDocumentation
                    .cookieWithName(name)
                    .description(description)
            return if (mustRequired) result else result.optional()
        }

        @JvmStatic
        fun cookie(
            name: String?,
            description: String?,
            constraint: String,
        ): CookieDescriptor {
            return CookieDocumentation
                .cookieWithName(name)
                .description(description)
                .attributes(constraint(constraint))
        }

        @JvmStatic
        fun cookie(
            name: String?,
            description: String?,
            constraint: String,
            mustRequired: Boolean,
        ): CookieDescriptor {
            val result: CookieDescriptor =
                CookieDocumentation
                    .cookieWithName(name)
                    .description(description)
                    .attributes(constraint(constraint))
            return if (mustRequired) result else result.optional()
        }

        /**
         * RequestDocumentation.pathParameters()
         */
        @JvmStatic
        fun path(
            name: String?,
            description: String?,
        ): ParameterDescriptor {
            return RequestDocumentation
                .parameterWithName(name)
                .description(description)
        }

        @JvmStatic
        fun path(
            name: String?,
            description: String?,
            mustRequired: Boolean,
        ): ParameterDescriptor {
            val result: ParameterDescriptor =
                RequestDocumentation
                    .parameterWithName(name)
                    .description(description)
            return if (mustRequired) result else result.optional()
        }

        @JvmStatic
        fun path(
            name: String?,
            description: String?,
            constraint: String,
        ): ParameterDescriptor {
            return RequestDocumentation
                .parameterWithName(name)
                .description(description)
                .attributes(constraint(constraint))
        }

        @JvmStatic
        fun path(
            name: String?,
            description: String?,
            constraint: String,
            mustRequired: Boolean,
        ): ParameterDescriptor {
            val result: ParameterDescriptor =
                RequestDocumentation
                    .parameterWithName(name)
                    .description(description)
                    .attributes(constraint(constraint))
            return if (mustRequired) result else result.optional()
        }

        /**
         * RequestDocumentation.queryParameters()
         */
        @JvmStatic
        fun query(
            name: String?,
            description: String?,
        ): ParameterDescriptor {
            return RequestDocumentation
                .parameterWithName(name)
                .description(description)
        }

        @JvmStatic
        fun query(
            name: String?,
            description: String?,
            mustRequired: Boolean,
        ): ParameterDescriptor {
            val result: ParameterDescriptor =
                RequestDocumentation
                    .parameterWithName(name)
                    .description(description)
            return if (mustRequired) result else result.optional()
        }

        @JvmStatic
        fun query(
            name: String?,
            description: String?,
            constraint: String,
        ): ParameterDescriptor {
            return RequestDocumentation
                .parameterWithName(name)
                .description(description)
                .attributes(constraint(constraint))
        }

        @JvmStatic
        fun query(
            name: String?,
            description: String?,
            constraint: String,
            mustRequired: Boolean,
        ): ParameterDescriptor {
            val result: ParameterDescriptor =
                RequestDocumentation
                    .parameterWithName(name)
                    .description(description)
                    .attributes(constraint(constraint))
            return if (mustRequired) result else result.optional()
        }

        /**
         * RequestDocumentation.partWithName()
         */
        @JvmStatic
        fun file(
            name: String?,
            description: String?,
        ): RequestPartDescriptor {
            return RequestDocumentation
                .partWithName(name)
                .description(description)
        }

        @JvmStatic
        fun file(
            name: String?,
            description: String?,
            mustRequired: Boolean,
        ): RequestPartDescriptor {
            val result: RequestPartDescriptor =
                RequestDocumentation
                    .partWithName(name)
                    .description(description)
            return if (mustRequired) result else result.optional()
        }

        @JvmStatic
        fun file(
            name: String?,
            description: String?,
            constraint: String,
        ): RequestPartDescriptor {
            return RequestDocumentation
                .partWithName(name)
                .description(description)
                .attributes(constraint(constraint))
        }

        @JvmStatic
        fun file(
            name: String?,
            description: String?,
            constraint: String,
            mustRequired: Boolean,
        ): RequestPartDescriptor {
            val result: RequestPartDescriptor =
                RequestDocumentation
                    .partWithName(name)
                    .description(description)
                    .attributes(constraint(constraint))
            return if (mustRequired) result else result.optional()
        }

        /**
         * PayloadDocumentation.requestFields()
         * <br></br>
         * PayloadDocumentation.responseFields()
         */
        @JvmStatic
        fun body(
            name: String?,
            description: String?,
        ): FieldDescriptor {
            return PayloadDocumentation
                .fieldWithPath(name)
                .description(description)
        }

        @JvmStatic
        fun body(
            name: String?,
            description: String?,
            mustRequired: Boolean,
        ): FieldDescriptor {
            val result: FieldDescriptor =
                PayloadDocumentation
                    .fieldWithPath(name)
                    .description(description)
            return if (mustRequired) result else result.optional()
        }

        @JvmStatic
        fun body(
            name: String?,
            description: String?,
            constraint: String,
        ): FieldDescriptor {
            return PayloadDocumentation
                .fieldWithPath(name)
                .description(description)
                .attributes(constraint(constraint))
        }

        @JvmStatic
        fun body(
            name: String?,
            description: String?,
            constraint: String,
            mustRequired: Boolean,
        ): FieldDescriptor {
            val result: FieldDescriptor =
                PayloadDocumentation
                    .fieldWithPath(name)
                    .description(description)
                    .attributes(constraint(constraint))
            return if (mustRequired) result else result.optional()
        }

        private fun constraint(value: String): Attributes.Attribute {
            return Attributes.Attribute("constraints", value)
        }
    }

    private val documentRequest: OperationRequestPreprocessor
        get() = Preprocessors.preprocessRequest(Preprocessors.prettyPrint())

    private val documentResponse: OperationResponsePreprocessor
        get() = Preprocessors.preprocessResponse(Preprocessors.prettyPrint())

    private val headerWithAccessToken: Snippet
        get() =
            HeaderDocumentation.requestHeaders(
                SnippetFactory.header(
                    name = AuthToken.ACCESS_TOKEN_HEADER,
                    description = "Access Token",
                    mustRequired = true,
                ),
            )

    private val cookieWithRefreshToken: Snippet
        get() =
            CookieDocumentation.requestCookies(
                SnippetFactory.cookie(
                    name = AuthToken.REFRESH_TOKEN_HEADER,
                    description = "Refresh Token",
                    mustRequired = true,
                ),
            )

    private val exceptionResponseFields: Snippet
        get() =
            PayloadDocumentation.responseFields(
                PayloadDocumentation.fieldWithPath("errorCode").description("커스텀 예외 코드"),
                PayloadDocumentation.fieldWithPath("message").description("예외 메시지"),
            )
}
