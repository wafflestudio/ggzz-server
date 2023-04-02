package com.wafflestudio.nostalgia.domain.user.exception

import com.wafflestudio.nostalgia.global.common.exception.CustomException.ConflictException
import com.wafflestudio.nostalgia.global.common.exception.ErrorType.Conflict.USERNAME_CONFLICT

class DuplicateUsernameException(username: String): ConflictException(USERNAME_CONFLICT, "Username '$username' exists.")