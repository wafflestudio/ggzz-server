package com.wafflestudio.ggzz.restdocs

import com.wafflestudio.ggzz.restdocs.RestDocsAttributeKeys.Companion.KEY_DEFAULT_VALUE
import com.wafflestudio.ggzz.restdocs.RestDocsAttributeKeys.Companion.KEY_FORMAT
import com.wafflestudio.ggzz.restdocs.RestDocsAttributeKeys.Companion.KEY_PARAM_TYPE
import com.wafflestudio.ggzz.restdocs.RestDocsAttributeKeys.Companion.KEY_PART_TYPE
import com.wafflestudio.ggzz.restdocs.RestDocsAttributeKeys.Companion.KEY_SAMPLE
import org.springframework.restdocs.payload.FieldDescriptor
import org.springframework.restdocs.payload.JsonFieldType
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.request.ParameterDescriptor
import org.springframework.restdocs.request.RequestDocumentation
import org.springframework.restdocs.request.RequestPartDescriptor
import kotlin.reflect.KClass

/**
 * @see <a href="https://toss.tech/article/kotlin-dsl-restdocs">Kotlin으로 DSL 만들기: 반복적이고 지루한 REST Docs 벗어나기</a>
 */

// ---------------------------
// Custom Type
// ---------------------------

sealed class DocsFieldType(
    val type: JsonFieldType
)

object ARRAY: DocsFieldType(JsonFieldType.ARRAY)
object BOOLEAN: DocsFieldType(JsonFieldType.BOOLEAN)
object OBJECT: DocsFieldType(JsonFieldType.OBJECT)
object NUMBER: DocsFieldType(JsonFieldType.NUMBER)
object NULL: DocsFieldType(JsonFieldType.NULL)
object STRING: DocsFieldType(JsonFieldType.STRING)
object ANY: DocsFieldType(JsonFieldType.VARIES)
object DATE: DocsFieldType(JsonFieldType.STRING)
object DATETIME: DocsFieldType(JsonFieldType.STRING)

sealed class DocsParamType(
    val type: String
)

object FILE: DocsParamType("file")
object TEXT: DocsParamType("string")
object DOUBLE: DocsParamType("double")
object LONG: DocsParamType("long")
object INT: DocsParamType("integer")

// ---------------------------
// Request Body
// ---------------------------

open class Field(
    val descriptor: FieldDescriptor,
) {
    val isIgnored: Boolean = descriptor.isIgnored
    val isOptional: Boolean = descriptor.isOptional

    protected open var default: String
        get() = descriptor.attributes.getOrDefault(KEY_DEFAULT_VALUE, "") as String
        set(value) {
            descriptor.attributes(ApiDocumentUtils.defaultValue(value))
        }

    protected open var format: String
        get() = descriptor.attributes.getOrDefault(KEY_FORMAT, "") as String
        set(value) {
            descriptor.attributes(ApiDocumentUtils.customFormat(value))
        }

    protected open var sample: String
        get() = descriptor.attributes.getOrDefault(KEY_SAMPLE, "") as String
        set(value) {
            descriptor.attributes(ApiDocumentUtils.customSample(value))
        }

    open infix fun means(value: String): Field {
        descriptor.description(value)
        return this
    }

    open infix fun attributes(block: Field.() -> Unit): Field {
        block()
        return this
    }

    open infix fun withDefaultValue(value: String): Field {
        this.default = value
        return this
    }

    open infix fun formattedAs(value: String): Field {
        this.format = value
        return this
    }

    open infix fun example(value: String): Field {
        this.sample = value
        return this
    }

    open infix fun isOptional(value: Boolean): Field {
        if (value) descriptor.optional()
        return this
    }

    open infix fun isIgnored(value: Boolean): Field {
        if (value) descriptor.ignored()
        return this
    }
}

data class ENUM<T : Enum<T>>(val enums: Collection<T>) : DocsFieldType(JsonFieldType.STRING) {
    constructor(clazz: KClass<T>) : this(clazz.java.enumConstants.asList())
}

infix fun String.type(docsFieldType: DocsFieldType): Field {
    val field = createField(this, docsFieldType.type)
    when (docsFieldType) {
        is DATE -> field formattedAs ApiDocumentUtils.DATE_FORMAT
        is DATETIME -> field formattedAs ApiDocumentUtils.DATETIME_FORMAT
        else -> {}
    }
    return field
}

infix fun <T : Enum<T>> String.type(enumFieldType: ENUM<T>): Field {
    val field = createField(this, JsonFieldType.STRING)
    field formattedAs ApiDocumentUtils.enumFormat(enumFieldType.enums)
    return field
}

private fun createField(value: String, type: JsonFieldType): Field {
    val descriptor = PayloadDocumentation.fieldWithPath(value)
        .type(type)
        .attributes(ApiDocumentUtils.emptySample(), ApiDocumentUtils.emptyFormat(), ApiDocumentUtils.emptyDefaultValue())
        .description("")

    return Field(descriptor)
}

// ---------------------------
// Request Part
// ---------------------------

open class Part(
    val descriptor: RequestPartDescriptor,
) {
    val isIgnored: Boolean = descriptor.isIgnored
    val isOptional: Boolean = descriptor.isOptional

    protected open var default: String
        get() = descriptor.attributes.getOrDefault(KEY_DEFAULT_VALUE, "") as String
        set(value) {
            descriptor.attributes(ApiDocumentUtils.defaultValue(value))
        }

    protected open var format: String
        get() = descriptor.attributes.getOrDefault(KEY_FORMAT, "") as String
        set(value) {
            descriptor.attributes(ApiDocumentUtils.customFormat(value))
        }

    protected open var sample: String
        get() = descriptor.attributes.getOrDefault(KEY_SAMPLE, "") as String
        set(value) {
            descriptor.attributes(ApiDocumentUtils.customSample(value))
        }

    protected open var type: String
        get() = descriptor.attributes.getOrDefault(KEY_PART_TYPE, "") as String
        set(value) {
            descriptor.attributes(ApiDocumentUtils.customPartType(value))
        }

    open infix fun typedAs(value: String): Part {
        this.type = value
        return this
    }

    open infix fun means(value: String): Part {
        descriptor.description(value)
        return this
    }

    open infix fun attributes(block: Part.() -> Unit): Part {
        block()
        return this
    }

    open infix fun withDefaultValue(value: String): Part {
        this.default = value
        return this
    }

    open infix fun formattedAs(value: String): Part {
        this.format = value
        return this
    }

    open infix fun example(value: String): Part {
        this.sample = value
        return this
    }

    open infix fun isOptional(value: Boolean): Part {
        if (value) descriptor.optional()
        return this
    }

    open infix fun isIgnored(value: Boolean): Part {
        if (value) descriptor.ignored()
        return this
    }
}

infix fun String.partType(type: DocsParamType): Part {
    val part = createPart(this)
    part typedAs type.type
    return part
}

private fun createPart(value: String): Part {
    val descriptor = RequestDocumentation.partWithName(value)
        .attributes(ApiDocumentUtils.emptySample(), ApiDocumentUtils.emptyFormat(), ApiDocumentUtils.emptyDefaultValue())
        .description("")
    return Part(descriptor)
}

// ---------------------------
// Request Param
// ---------------------------

open class Parameter(
    val descriptor: ParameterDescriptor,
) {
    val isIgnored: Boolean = descriptor.isIgnored
    val isOptional: Boolean = descriptor.isOptional

    protected open var default: String
        get() = descriptor.attributes.getOrDefault(KEY_DEFAULT_VALUE, "") as String
        set(value) {
            descriptor.attributes(ApiDocumentUtils.defaultValue(value))
        }

    protected open var format: String
        get() = descriptor.attributes.getOrDefault(KEY_FORMAT, "") as String
        set(value) {
            descriptor.attributes(ApiDocumentUtils.customFormat(value))
        }

    protected open var sample: String
        get() = descriptor.attributes.getOrDefault(KEY_SAMPLE, "") as String
        set(value) {
            descriptor.attributes(ApiDocumentUtils.customSample(value))
        }

    protected open var type: String
        get() = descriptor.attributes.getOrDefault(KEY_PARAM_TYPE, "") as String
        set(value) {
            descriptor.attributes(ApiDocumentUtils.customParamType(value))
        }

    open infix fun typedAs(value: String): Parameter {
        this.type = value
        return this
    }

    open infix fun means(value: String): Parameter {
        descriptor.description(value)
        return this
    }

    open infix fun attributes(block: Parameter.() -> Unit): Parameter {
        block()
        return this
    }

    open infix fun withDefaultValue(value: String): Parameter {
        this.default = value
        return this
    }

    open infix fun formattedAs(value: String): Parameter {
        this.format = value
        return this
    }

    open infix fun example(value: String): Parameter {
        this.sample = value
        return this
    }

    open infix fun isOptional(value: Boolean): Parameter {
        if (value) descriptor.optional()
        return this
    }

    open infix fun isIgnored(value: Boolean): Parameter {
        if (value) descriptor.ignored()
        return this
    }
}

infix fun String.parameterType(type: DocsParamType): Parameter {
    val param = createParameter(this)
    param typedAs type.type
    return param
}

private fun createParameter(value: String): Parameter {
    val descriptor = RequestDocumentation.parameterWithName(value)
        .attributes(ApiDocumentUtils.emptySample(), ApiDocumentUtils.emptyFormat(), ApiDocumentUtils.emptyDefaultValue())
        .description("")
    return Parameter(descriptor)
}
