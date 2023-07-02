package com.wafflestudio.ggzz.domain.user.exception

import com.wafflestudio.ggzz.global.common.exception.CustomException
import com.wafflestudio.ggzz.global.common.exception.ErrorType.NotFound.USER_NOT_FOUND

class UserNotFoundException(id: Long): CustomException.NotFoundException(USER_NOT_FOUND, "User with id '$id' does not exists.")