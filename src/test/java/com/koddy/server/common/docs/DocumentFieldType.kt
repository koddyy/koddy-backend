package com.koddy.server.common.docs

import org.springframework.restdocs.payload.JsonFieldType
import kotlin.reflect.KClass

sealed class DocumentFieldType(
    val type: JsonFieldType,
)

data object ARRAY : DocumentFieldType(JsonFieldType.ARRAY)

data object BOOLEAN : DocumentFieldType(JsonFieldType.BOOLEAN)

data object OBJECT : DocumentFieldType(JsonFieldType.OBJECT)

data object NUMBER : DocumentFieldType(JsonFieldType.NUMBER)

data object NULL : DocumentFieldType(JsonFieldType.NULL)

data object STRING : DocumentFieldType(JsonFieldType.STRING)

data object ANY : DocumentFieldType(JsonFieldType.VARIES)

data class ENUM<T : Enum<T>>(val enums: List<T>) : DocumentFieldType(JsonFieldType.STRING) {
    constructor(clazz: KClass<T>) : this(clazz.java.enumConstants.asList())
}
