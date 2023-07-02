package com.wafflestudio.ggzz.domain.letter.exception

import com.wafflestudio.ggzz.global.common.exception.CustomException.ConflictException
import com.wafflestudio.ggzz.global.common.exception.ErrorType.Conflict.LIKE_ALREADY_EXISTS

class LikeAlreadyExistsException(userId: Long, letterId: Long): ConflictException(
    LIKE_ALREADY_EXISTS, "User with id '$userId' already liked Letter with id '$letterId'.")