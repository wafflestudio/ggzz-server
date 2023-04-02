package com.wafflestudio.nostalgia.global.error

import com.wafflestudio.nostalgia.global.common.exception.CustomException
import com.wafflestudio.nostalgia.global.common.exception.ErrorType.Unauthorized.NOT_LOGGED_IN

class NotLoggedInException: CustomException.UnauthorizedException(NOT_LOGGED_IN, "You have to first login.")