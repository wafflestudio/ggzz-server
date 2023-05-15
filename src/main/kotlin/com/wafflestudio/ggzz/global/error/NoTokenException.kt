package com.wafflestudio.ggzz.global.error

import com.wafflestudio.ggzz.global.common.exception.CustomException.ForbiddenException
import com.wafflestudio.ggzz.global.common.exception.ErrorType.Forbidden.NO_TOKEN

class NoTokenException(type: String): ForbiddenException(NO_TOKEN, "The $type token doesn't exist.")