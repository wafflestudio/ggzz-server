package com.wafflestudio.nostalgia.domain.letter.exception

import com.wafflestudio.nostalgia.global.common.exception.CustomException.NotFoundException
import com.wafflestudio.nostalgia.global.common.exception.ErrorType.NotFound.LETTER_NOT_FOUND

class LetterNotFoundException(private val id: Long): NotFoundException(LETTER_NOT_FOUND, "Letter with id '$id' does not exists.")