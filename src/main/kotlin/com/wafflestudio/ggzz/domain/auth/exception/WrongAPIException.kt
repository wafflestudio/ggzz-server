package com.wafflestudio.ggzz.domain.auth.exception

import com.wafflestudio.ggzz.global.common.exception.CustomException
import com.wafflestudio.ggzz.global.common.exception.ErrorType.Forbidden.WRONG_API

class WrongAPIException: CustomException.ForbiddenException(WRONG_API, "You requested for wrong API url.")