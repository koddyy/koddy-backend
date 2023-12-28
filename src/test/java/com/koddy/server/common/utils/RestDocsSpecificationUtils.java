package com.koddy.server.common.utils;

import org.springframework.restdocs.cookies.CookieDescriptor;
import org.springframework.restdocs.headers.HeaderDescriptor;
import org.springframework.restdocs.mockmvc.RestDocumentationResultHandler;
import org.springframework.restdocs.operation.preprocess.OperationRequestPreprocessor;
import org.springframework.restdocs.operation.preprocess.OperationResponsePreprocessor;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.request.ParameterDescriptor;
import org.springframework.restdocs.request.RequestPartDescriptor;
import org.springframework.restdocs.snippet.Attributes;
import org.springframework.restdocs.snippet.Snippet;

import java.util.Arrays;
import java.util.stream.Stream;

import static com.koddy.server.common.utils.RestDocsSpecificationUtils.SnippetFactory.cookie;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.SnippetFactory.header;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.restdocs.cookies.CookieDocumentation.cookieWithName;
import static org.springframework.restdocs.cookies.CookieDocumentation.requestCookies;
import static org.springframework.restdocs.headers.HeaderDocumentation.headerWithName;
import static org.springframework.restdocs.headers.HeaderDocumentation.requestHeaders;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.parameterWithName;
import static org.springframework.restdocs.request.RequestDocumentation.partWithName;

public class RestDocsSpecificationUtils {
    public static RestDocumentationResultHandler successDocs(final String identifier, final Snippet... snippets) {
        return document(
                identifier,
                getDocumentRequest(),
                getDocumentResponse(),
                snippets
        );
    }

    public static RestDocumentationResultHandler successDocsWithAccessToken(final String identifier, final Snippet... snippets) {
        return document(
                identifier,
                getDocumentRequest(),
                getDocumentResponse(),
                Stream.concat(Arrays.stream(new Snippet[]{getHeaderWithAccessToken()}), Arrays.stream(snippets)).toArray(Snippet[]::new)
        );
    }

    public static RestDocumentationResultHandler successDocsWithRefreshToken(final String identifier, final Snippet... snippets) {
        return document(
                identifier,
                getDocumentRequest(),
                getDocumentResponse(),
                Stream.concat(Arrays.stream(new Snippet[]{getCookieWithRefreshToken()}), Arrays.stream(snippets)).toArray(Snippet[]::new)
        );
    }

    public static RestDocumentationResultHandler failureDocs(final String identifier) {
        return document(
                identifier,
                getDocumentRequest(),
                getDocumentResponse(),
                getExceptionResponseFields()
        );
    }

    public static RestDocumentationResultHandler failureDocsWithAccessToken(final String identifier) {
        return document(
                identifier,
                getDocumentRequest(),
                getDocumentResponse(),
                getHeaderWithAccessToken(),
                getExceptionResponseFields()
        );
    }

    public static RestDocumentationResultHandler failureDocsWithRefreshToken(final String identifier) {
        return document(
                identifier,
                getDocumentRequest(),
                getDocumentResponse(),
                getCookieWithRefreshToken(),
                getExceptionResponseFields()
        );
    }

    private static OperationRequestPreprocessor getDocumentRequest() {
        return preprocessRequest(prettyPrint());
    }

    private static OperationResponsePreprocessor getDocumentResponse() {
        return preprocessResponse(prettyPrint());
    }

    private static Snippet getHeaderWithAccessToken() {
        return requestHeaders(
                header(AUTHORIZATION, "Access Token")
        );
    }

    private static Snippet getCookieWithRefreshToken() {
        return requestCookies(
                cookie("refresh_token", "Refresh Token")
        );
    }

    private static Snippet getExceptionResponseFields() {
        return responseFields(
                fieldWithPath("status").description("HTTP 상태 코드"),
                fieldWithPath("errorCode").description("커스텀 예외 코드"),
                fieldWithPath("message").description("예외 메시지")
        );
    }

    public static Snippet[] createResponseSnippets(final Snippet... snippets) {
        return Arrays.stream(snippets).toArray(Snippet[]::new);
    }

    public static class SnippetFactory {
        /**
         * HeaderDocumentation.requestHeaders()
         * <br>
         * HeaderDocumentation.responseHeaders()
         */
        public static HeaderDescriptor header(final String name, final String description) {
            return headerWithName(name).description(description);
        }

        public static HeaderDescriptor header(final String name, final String description, final String constraint) {
            return headerWithName(name).description(description).attributes(constraint(constraint));
        }

        /**
         * CookieDocumentation.requestCookies()
         * <br>
         * CookieDocumentation.responseCookies()
         */
        public static CookieDescriptor cookie(final String name, final String description) {
            return cookieWithName(name).description(description);
        }

        public static CookieDescriptor cookie(final String name, final String description, final String constraint) {
            return cookieWithName(name).description(description).attributes(constraint(constraint));
        }

        /**
         * RequestDocumentation.pathParameters()
         */
        public static ParameterDescriptor path(final String name, final String description) {
            return parameterWithName(name).description(description);
        }

        public static ParameterDescriptor path(final String name, final String description, final String constraint) {
            return parameterWithName(name).description(description).attributes(constraint(constraint));
        }

        /**
         * RequestDocumentation.queryParameters()
         */
        public static ParameterDescriptor query(final String name, final String description) {
            return parameterWithName(name).description(description);
        }

        public static ParameterDescriptor query(final String name, final String description, final String constraint) {
            return parameterWithName(name).description(description).attributes(constraint(constraint));
        }

        /**
         * RequestDocumentation.partWithName()
         */
        public static RequestPartDescriptor file(final String name, final String description) {
            return partWithName(name).description(description);
        }

        public static RequestPartDescriptor file(final String name, final String description, final String constraint) {
            return partWithName(name).description(description).attributes(constraint(constraint));
        }

        /**
         * PayloadDocumentation.requestFields()
         * <br>
         * PayloadDocumentation.responseFields()
         */
        public static FieldDescriptor body(final String name, final String description) {
            return fieldWithPath(name).description(description);
        }

        public static FieldDescriptor body(final String name, final String description, final String constraint) {
            return fieldWithPath(name).description(description).attributes(constraint(constraint));
        }

        private static Attributes.Attribute constraint(final String value) {
            return new Attributes.Attribute("constraints", value);
        }
    }
}