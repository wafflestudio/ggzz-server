package com.wafflestudio.nostalgia.domain.user.exception

import com.wafflestudio.nostalgia.global.common.exception.CustomException.NotFoundException
import com.wafflestudio.nostalgia.global.common.exception.ErrorType.NotFound.USER_NOT_FOUND

class UserNotFoundException(private val id: Long): NotFoundException(USER_NOT_FOUND, "User with id '$id' does not exists.")