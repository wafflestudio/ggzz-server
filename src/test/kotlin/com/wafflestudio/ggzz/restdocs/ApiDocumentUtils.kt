package com.wafflestudio.ggzz.restdocs

import com.wafflestudio.ggzz.restdocs.RestDocsAttributeKeys.Companion.KEY_DEFAULT_VALUE
import com.wafflestudio.ggzz.restdocs.RestDocsAttributeKeys.Companion.KEY_FORMAT
import com.wafflestudio.ggzz.restdocs.RestDocsAttributeKeys.Companion.KEY_PARAM_TYPE
import com.wafflestudio.ggzz.restdocs.RestDocsAttributeKeys.Companion.KEY_PART_TYPE
import com.wafflestudio.ggzz.restdocs.RestDocsAttributeKeys.Companion.KEY_SAMPLE
import org.springframework.restdocs.operation.preprocess.Preprocessors.*
import org.springframework.restdocs.payload.PayloadDocumentation
import org.springframework.restdocs.request.RequestDocumentation
import org.springframework.restdocs.snippet.Attributes.Attribute
import org.springframework.restdocs.snippet.Snippet

interface ApiDocumentUtils {

    companion object {
        fun getDocumentRequest() = preprocessRequest(
            modifyUris()
                .scheme("https")
                .host("ggzz-dev-api.wafflestudio.com")
                .removePort(),
            modifyHeaders()
                .removeMatching("X-CSRF-TOKEN"),
            prettyPrint()
        )!!

        fun getDocumentResponse() = preprocessResponse(
            modifyHeaders()
                .removeMatching("Vary")
                .removeMatching("X-Content-Type-Options")
                .removeMatching("X-XSS-Protection")
                .removeMatching("Cache-Control")
                .removeMatching("Pragma")
                .removeMatching("Expires")
                .removeMatching("X-Frame-Options"),
            prettyPrint()
        )!!

        fun requestPart(vararg part: Part): Snippet {
            return RequestDocumentation.requestParts(part.map { it.descriptor })
        }

        fun requestParameter(vararg parameter: Parameter): Snippet {
            return RequestDocumentation.queryParameters(parameter.map { it.descriptor })
        }

        fun pathParameter(vararg parameter: Parameter): Snippet {
            return RequestDocumentation.pathParameters(parameter.map { it.descriptor })
        }

        fun requestBody(vararg field: Field): Snippet {
            return PayloadDocumentation.requestFields(field.map { it.descriptor })
        }

        fun defaultValue(value: String) = Attribute(KEY_DEFAULT_VALUE, value)
        fun customFormat(value: String) = Attribute(KEY_FORMAT, value)
        fun customSample(value: String) = Attribute(KEY_SAMPLE, value)
        fun customPartType(value: String) = Attribute(KEY_PART_TYPE, value)
        fun customParamType(value: String) = Attribute(KEY_PARAM_TYPE, value)

        fun emptyDefaultValue() = defaultValue("")
        fun emptyFormat() = customFormat("")
        fun emptySample() = customSample("")

        fun enumFormat(enums: Collection<Any>): String {
            return enums.joinToString(separator = "|")
        }

        const val DATE_FORMAT = "yyyy-MM-dd"
        const val DATETIME_FORMAT = "yyyy-MM-dd hh:mm:ss.SSS"

    }
}
