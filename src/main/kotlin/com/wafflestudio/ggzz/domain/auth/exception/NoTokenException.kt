package com.wafflestudio.ggzz.domain.auth.exception

import com.wafflestudio.ggzz.global.common.exception.CustomException
import com.wafflestudio.ggzz.global.common.exception.ErrorType.Unauthorized.NO_TOKEN

class NoTokenException: CustomException.UnauthorizedException(NO_TOKEN, "Token doesn't exist.")