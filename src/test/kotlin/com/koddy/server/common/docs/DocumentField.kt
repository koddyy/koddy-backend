package com.koddy.server.common.docs

import org.springframework.restdocs.cookies.CookieDescriptor
import org.springframework.restdocs.cookies.CookieDocumentation
import org.springframework.restdocs.headers.HeaderDescriptor
import org.springframework.restdocs.headers.HeaderDocumentation
import org.springframework.restdocs.payload.FieldDescriptor
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.request.ParameterDescriptor
import org.springframework.restdocs.request.RequestDocumentation
import org.springframework.restdocs.request.RequestPartDescriptor
import org.springframework.restdocs.snippet.Attributes

class DocumentField(
    private val name: String,
    private var fieldType: JsonFieldType? = null,
    private val enumValues: List<Any>? = null,
) {
    private lateinit var description: String
    private var constraint: String? = null
    private var isOptional = false

    infix fun means(description: String): DocumentField {
        this.description = description
        return this
    }

    infix fun constraint(constraint: String): DocumentField {
        this.constraint = constraint
        return this
    }

    infix fun isOptional(value: Boolean): DocumentField {
        this.isOptional = value
        return this
    }

    fun toHeaderDescriptor(): HeaderDescriptor {
        return HeaderDocumentation.headerWithName(name).also {
            it.description(description)
            if (constraint != null) it.attributes(applyConstraint(constraint!!))
            if (isOptional) it.optional()
        }
    }

    fun toCookieDescriptor(): CookieDescriptor {
        return CookieDocumentation.cookieWithName(name).also {
            it.description(description)
            if (constraint != null) it.attributes(applyConstraint(constraint!!))
            if (isOptional) it.optional()
        }
    }

    fun toParameterDescriptor(): ParameterDescriptor {
        return RequestDocumentation.parameterWithName(name).also {
            it.description(description)
            if (constraint != null) it.attributes(applyConstraint(constraint!!))
            if (isOptional) it.optional()
        }
    }

    fun toFileDescriptor(): RequestPartDescriptor {
        return RequestDocumentation.partWithName(name).also {
            it.description(description)
            if (enumValues != null) {
                it.description("$description -> Enum = $enumValues")
            }
            if (constraint != null) it.attributes(applyConstraint(constraint!!))
            if (isOptional) it.optional()
        }
    }

    fun toFieldDescriptor(): FieldDescriptor {
        return PayloadDocumentation.fieldWithPath(name).also {
            it.type(fieldType)
            it.description(description)
            if (constraint != null) it.attributes(applyConstraint(constraint!!))
            if (isOptional) it.optional()
        }
    }

    private fun applyConstraint(value: String): Attributes.Attribute = Attributes.Attribute("constraints", value)
}
