package com.wafflestudio.ggzz.domain.user.exception

import com.wafflestudio.ggzz.global.common.exception.CustomException.UnauthorizedException
import com.wafflestudio.ggzz.global.common.exception.ErrorType.Unauthorized.Invalid_FIREBASE_TOKEN

class InvalidFirebaseException(message: String): UnauthorizedException(Invalid_FIREBASE_TOKEN, "Invalid Firebase Token : $message")