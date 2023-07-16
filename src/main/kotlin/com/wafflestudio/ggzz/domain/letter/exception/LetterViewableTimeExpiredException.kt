package com.wafflestudio.ggzz.domain.letter.exception

import com.wafflestudio.ggzz.global.common.exception.CustomException.BadRequestException
import com.wafflestudio.ggzz.global.common.exception.ErrorType.BadRequest.LETTER_VIEWABLE_TIME_EXPIRED

class LetterViewableTimeExpiredException :
    BadRequestException(LETTER_VIEWABLE_TIME_EXPIRED, "This letter is no longer viewable.")
