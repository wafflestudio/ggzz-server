package com.wafflestudio.ggzz.domain.letter.exception

import com.wafflestudio.ggzz.global.common.exception.CustomException.NotFoundException
import com.wafflestudio.ggzz.global.common.exception.ErrorType.NotFound.LETTER_NOT_FOUND

class LetterNotFoundException(private val id: Long): NotFoundException(LETTER_NOT_FOUND, "Letter with id '$id' does not exists.")