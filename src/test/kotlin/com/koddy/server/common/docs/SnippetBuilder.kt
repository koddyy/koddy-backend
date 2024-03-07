package com.koddy.server.common.docs

import org.springframework.restdocs.cookies.CookieDocumentation
import org.springframework.restdocs.headers.HeaderDocumentation
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.request.RequestDocumentation
import org.springframework.restdocs.snippet.Snippet

class SnippetBuilder {
    private val snippets: MutableList<Snippet> = mutableListOf()

    fun requestHeaders(vararg fields: DocumentField) {
        snippets.add(
            HeaderDocumentation.requestHeaders(
                fields.map(DocumentField::toHeaderDescriptor).toList(),
            ),
        )
    }

    fun responseHeaders(vararg fields: DocumentField) {
        snippets.add(
            HeaderDocumentation.responseHeaders(
                fields.map(DocumentField::toHeaderDescriptor).toList(),
            ),
        )
    }

    fun requestCookies(vararg fields: DocumentField) {
        snippets.add(
            CookieDocumentation.requestCookies(
                fields.map(DocumentField::toCookieDescriptor).toList(),
            ),
        )
    }

    fun responseCookies(vararg fields: DocumentField) {
        snippets.add(
            CookieDocumentation.responseCookies(
                fields.map(DocumentField::toCookieDescriptor).toList(),
            ),
        )
    }

    fun pathParameters(vararg fields: DocumentField) {
        snippets.add(
            RequestDocumentation.pathParameters(
                fields.map(DocumentField::toParameterDescriptor).toList(),
            ),
        )
    }

    fun queryParameters(vararg fields: DocumentField) {
        snippets.add(
            RequestDocumentation.queryParameters(
                fields.map(DocumentField::toParameterDescriptor).toList(),
            ),
        )
    }

    fun fileForms(vararg fields: DocumentField) {
        snippets.add(
            RequestDocumentation.requestParts(
                fields.map(DocumentField::toFileDescriptor).toList(),
            ),
        )
    }

    fun requestFields(vararg fields: DocumentField) {
        snippets.add(
            PayloadDocumentation.requestFields(
                fields.map(DocumentField::toFieldDescriptor).toList(),
            ),
        )
    }

    fun responseFields(vararg fields: DocumentField) {
        snippets.add(
            PayloadDocumentation.responseFields(
                fields.map(DocumentField::toFieldDescriptor).toList(),
            ),
        )
    }

    fun build(): Array<out Snippet> = snippets.toTypedArray()
}
