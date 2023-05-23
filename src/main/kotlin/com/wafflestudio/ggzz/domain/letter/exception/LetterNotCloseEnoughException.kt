package com.wafflestudio.ggzz.domain.letter.exception

import com.wafflestudio.ggzz.global.common.exception.CustomException.BadRequestException
import com.wafflestudio.ggzz.global.common.exception.ErrorType.BadRequest.LETTER_NOT_CLOSE_ENOUGH

class LetterNotCloseEnoughException(private val viewRange: Int) :
    BadRequestException(LETTER_NOT_CLOSE_ENOUGH, "This letter can be viewed within ${viewRange}m.")
