package com.wafflestudio.ggzz.domain.user.exception
import com.wafflestudio.ggzz.global.common.exception.CustomException.NotFoundException
import com.wafflestudio.ggzz.global.common.exception.ErrorType.NotFound.USER_NOT_FOUND

class UserNotFoundException(id: Long? = null): NotFoundException(USER_NOT_FOUND, "User${id?.let { " with id '$id'" }} does not exist.")
