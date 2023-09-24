package com.wafflestudio.ggzz.domain.auth.exception

import com.wafflestudio.ggzz.global.common.exception.CustomException.UnauthorizedException
import com.wafflestudio.ggzz.global.common.exception.ErrorType.Unauthorized.LOGIN_FAIL

class LoginFailedException: UnauthorizedException(LOGIN_FAIL, "Login failed")