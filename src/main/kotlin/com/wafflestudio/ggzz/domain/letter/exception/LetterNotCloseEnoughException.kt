package com.wafflestudio.ggzz.domain.letter.exception

import com.wafflestudio.ggzz.global.common.exception.CustomException.BadRequestException
import com.wafflestudio.ggzz.global.common.exception.ErrorType.BadRequest.LETTER_NOT_CLOSE_ENOUGH

class LetterNotCloseEnoughException: BadRequestException(LETTER_NOT_CLOSE_ENOUGH, "You should be closer to the letter.")