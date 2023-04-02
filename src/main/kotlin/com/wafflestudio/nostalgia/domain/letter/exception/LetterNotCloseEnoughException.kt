package com.wafflestudio.nostalgia.domain.letter.exception

import com.wafflestudio.nostalgia.global.common.exception.CustomException.BadRequestException
import com.wafflestudio.nostalgia.global.common.exception.ErrorType.BadRequest.LETTER_NOT_CLOSE_ENOUGH

class LetterNotCloseEnoughException: BadRequestException(LETTER_NOT_CLOSE_ENOUGH, "You should be closer to the letter.")