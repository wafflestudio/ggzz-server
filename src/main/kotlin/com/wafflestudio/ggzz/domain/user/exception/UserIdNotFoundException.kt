package com.wafflestudio.ggzz.domain.user.exception
import com.wafflestudio.ggzz.domain.auth.model.Provider
import com.wafflestudio.ggzz.global.common.exception.CustomException.NotFoundException
import com.wafflestudio.ggzz.global.common.exception.ErrorType.NotFound.USER_NOT_FOUND

class UserIdNotFoundException(type: Provider, id: String): NotFoundException(USER_NOT_FOUND, "User with $type ID '$id' does not exist.")
