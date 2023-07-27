package com.wafflestudio.ggzz.domain.auth.exception

import com.wafflestudio.ggzz.global.common.exception.CustomException.ConflictException
import com.wafflestudio.ggzz.global.common.exception.ErrorType.Conflict.DUPLICATE_ID

class DuplicateGgzzIdException(id: String): ConflictException(DUPLICATE_ID, "ID '$id' exists.")