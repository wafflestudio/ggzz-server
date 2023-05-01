package com.wafflestudio.ggzz.global.error

import com.wafflestudio.ggzz.global.common.exception.CustomException
import com.wafflestudio.ggzz.global.common.exception.ErrorType.Unauthorized.NOT_LOGGED_IN

class NotLoggedInException: CustomException.UnauthorizedException(NOT_LOGGED_IN, "You have to first login.")