package com.wafflestudio.nostalgia.domain

import org.springframework.restdocs.operation.preprocess.OperationRequestPreprocessor
import org.springframework.restdocs.operation.preprocess.OperationResponsePreprocessor
import org.springframework.restdocs.operation.preprocess.Preprocessors.*

interface ApiDocumentUtils {

    companion object {
        fun getDocumentRequest(): OperationRequestPreprocessor {
            return preprocessRequest(prettyPrint())
        }

        fun getDocumentResponse(): OperationResponsePreprocessor {
            return preprocessResponse(prettyPrint())
        }
    }
}
