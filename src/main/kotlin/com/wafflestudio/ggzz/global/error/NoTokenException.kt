package com.wafflestudio.nostalgia.global.error

import com.wafflestudio.nostalgia.global.common.exception.CustomException.ForbiddenException
import com.wafflestudio.nostalgia.global.common.exception.ErrorType.Forbidden.NO_TOKEN

class NoTokenException(type: String): ForbiddenException(NO_TOKEN, "The $type token doesn't exist.")