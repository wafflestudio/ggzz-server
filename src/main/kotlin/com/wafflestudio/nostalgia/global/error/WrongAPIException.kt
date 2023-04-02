package com.wafflestudio.nostalgia.global.error

import com.wafflestudio.nostalgia.global.common.exception.CustomException
import com.wafflestudio.nostalgia.global.common.exception.ErrorType.Forbidden.WRONG_API

class WrongAPIException: CustomException.ForbiddenException(WRONG_API, "You requested for wrong API url.")