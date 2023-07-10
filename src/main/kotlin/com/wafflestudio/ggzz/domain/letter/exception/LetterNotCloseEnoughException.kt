package com.wafflestudio.ggzz.domain.letter.exception

import com.wafflestudio.ggzz.global.common.exception.CustomException.BadRequestException
import com.wafflestudio.ggzz.global.common.exception.ErrorType.BadRequest.LETTER_NOT_CLOSE_ENOUGH

class LetterNotCloseEnoughException(private val viewRange: Int, private val distance: Double) :
    BadRequestException(LETTER_NOT_CLOSE_ENOUGH, "Get Closer. ${viewRange - distance}m to go.")
