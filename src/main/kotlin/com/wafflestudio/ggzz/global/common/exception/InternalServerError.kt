package com.wafflestudio.ggzz.global.common.exception

import com.wafflestudio.ggzz.global.common.exception.CustomException.ServerErrorException
import com.wafflestudio.ggzz.global.common.exception.ErrorType.ServerError.INTERNAL_SERVER_ERROR

class InternalServerError(message: String): ServerErrorException(INTERNAL_SERVER_ERROR, "Internal Server Error: $message")