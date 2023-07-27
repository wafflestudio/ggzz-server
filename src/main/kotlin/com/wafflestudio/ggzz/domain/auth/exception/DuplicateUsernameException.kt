package com.wafflestudio.ggzz.domain.auth.exception

import com.wafflestudio.ggzz.global.common.exception.CustomException.ConflictException
import com.wafflestudio.ggzz.global.common.exception.ErrorType.Conflict.DUPLICATE_USERNAME

class DuplicateUsernameException(username: String): ConflictException(DUPLICATE_USERNAME, "Username '$username' exists.")
