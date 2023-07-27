package com.wafflestudio.ggzz.domain.auth.exception

import com.wafflestudio.ggzz.global.common.exception.CustomException.BadRequestException
import com.wafflestudio.ggzz.global.common.exception.ErrorType.BadRequest.INVALID_TOKEN

class InvalidTokenException: BadRequestException(INVALID_TOKEN, "Invalid token.")