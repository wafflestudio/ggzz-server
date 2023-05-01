package com.wafflestudio.ggzz.domain.user.exception

import com.wafflestudio.ggzz.global.common.exception.CustomException.UnauthorizedException
import com.wafflestudio.ggzz.global.common.exception.ErrorType.Unauthorized.LOGIN_FAIL

class LoginFailedException: UnauthorizedException(LOGIN_FAIL, "Check your username or password.")