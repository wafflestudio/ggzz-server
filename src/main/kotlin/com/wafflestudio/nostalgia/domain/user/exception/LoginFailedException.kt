package com.wafflestudio.nostalgia.domain.user.exception

import com.wafflestudio.nostalgia.global.common.exception.CustomException.UnauthorizedException
import com.wafflestudio.nostalgia.global.common.exception.ErrorType.Unauthorized.LOGIN_FAIL

class LoginFailedException: UnauthorizedException(LOGIN_FAIL, "Check your username or password.")