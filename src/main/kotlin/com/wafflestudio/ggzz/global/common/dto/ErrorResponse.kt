package com.wafflestudio.ggzz.global.common.dto

import com.wafflestudio.ggzz.global.common.exception.CustomException
import com.wafflestudio.ggzz.global.common.exception.ErrorType

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