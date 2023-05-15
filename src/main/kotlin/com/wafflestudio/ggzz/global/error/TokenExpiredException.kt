package com.wafflestudio.ggzz.global.error

import com.wafflestudio.ggzz.global.common.exception.CustomException.UnauthorizedException
import com.wafflestudio.ggzz.global.common.exception.ErrorType.Unauthorized.TOKEN_EXPIRED

class TokenExpiredException(type: String): UnauthorizedException(TOKEN_EXPIRED, "The $type token is expired.")