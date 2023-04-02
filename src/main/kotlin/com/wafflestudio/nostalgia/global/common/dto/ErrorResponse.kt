package com.wafflestudio.nostalgia.global.common.dto

import com.wafflestudio.nostalgia.global.common.exception.CustomException
import com.wafflestudio.nostalgia.global.common.exception.ErrorType

data class ErrorResponse(
    val errorCode: Int,
    val errorType: ErrorType.ErrorTypeInterface,
    val detail: String
) {
    constructor(customException: CustomException): this(
        errorCode = customException.errorType.getCode(),
        errorType = customException.errorType,
        detail = customException.detail
    )
}