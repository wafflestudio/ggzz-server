package com.wafflestudio.nostalgia.domain.letter.exception

import com.wafflestudio.nostalgia.global.common.exception.CustomException.BadRequestException
import com.wafflestudio.nostalgia.global.common.exception.ErrorType.BadRequest.UNSUPPORTED_FILE_TYPE

class UnsupportedFileTypeException(
    private val type: String?,
    private val sourceType: String
): BadRequestException(UNSUPPORTED_FILE_TYPE, "This file type $type is not supported for $sourceType.")