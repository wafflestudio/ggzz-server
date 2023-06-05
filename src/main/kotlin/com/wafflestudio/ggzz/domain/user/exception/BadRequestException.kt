package com.wafflestudio.ggzz.domain.user.exception

import com.wafflestudio.ggzz.global.common.exception.CustomException.BadRequestException
import com.wafflestudio.ggzz.global.common.exception.ErrorType.BadRequest.UNSATISFIED_REQUEST

class BadRequestException(): BadRequestException(UNSATISFIED_REQUEST, "The request body has not been enough.")