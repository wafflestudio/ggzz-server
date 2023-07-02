package com.wafflestudio.ggzz.domain

import org.springframework.restdocs.operation.preprocess.Preprocessors.*
import java.util.regex.Pattern

interface ApiDocumentUtils {

    companion object {
        fun getDocumentRequest() = preprocessRequest(
            modifyUris()
                .scheme("https")
                .host("ggzz-dev-api.wafflestudio.com")
                .removePort(),
            replacePattern(Pattern.compile("_csrf=.*\$"), ""),
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
    }
}
