package com.wafflestudio.ggzz.domain.user.exception

import com.wafflestudio.ggzz.global.common.exception.CustomException.ConflictException
import com.wafflestudio.ggzz.global.common.exception.ErrorType.Conflict.USERNAME_CONFLICT

class DuplicateUsernameException(username: String): ConflictException(USERNAME_CONFLICT, "Username '$username' exists.")
