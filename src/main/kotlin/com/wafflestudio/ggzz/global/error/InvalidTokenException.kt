package com.wafflestudio.nostalgia.global.error

import com.wafflestudio.nostalgia.global.common.exception.CustomException.BadRequestException
import com.wafflestudio.nostalgia.global.common.exception.ErrorType.BadRequest.INVALID_TOKEN

class InvalidTokenException(type: String): BadRequestException(INVALID_TOKEN, "The $type token is invalid.")