package com.wafflestudio.nostalgia.global.error

import com.wafflestudio.nostalgia.global.common.exception.CustomException.UnauthorizedException
import com.wafflestudio.nostalgia.global.common.exception.ErrorType.Unauthorized.TOKEN_EXPIRED

class TokenExpiredException(type: String): UnauthorizedException(TOKEN_EXPIRED, "The $type token is expired.")