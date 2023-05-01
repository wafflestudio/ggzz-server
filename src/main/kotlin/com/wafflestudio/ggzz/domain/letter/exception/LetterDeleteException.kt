package com.wafflestudio.ggzz.domain.letter.exception

import com.wafflestudio.ggzz.global.common.exception.CustomException
import com.wafflestudio.ggzz.global.common.exception.ErrorType.Forbidden.LETTER_DELETE_FORBIDDEN

class LetterDeleteException: CustomException.ForbiddenException(LETTER_DELETE_FORBIDDEN,
    "You are not the owner of this letter.")