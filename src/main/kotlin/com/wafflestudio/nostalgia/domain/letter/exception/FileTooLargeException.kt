package com.wafflestudio.nostalgia.domain.letter.exception

import com.wafflestudio.nostalgia.global.common.exception.CustomException
import com.wafflestudio.nostalgia.global.common.exception.ErrorType.BadRequest.FILE_TOO_LARGE

class FileTooLargeException: CustomException.BadRequestException(FILE_TOO_LARGE,
    "Max file size = 2MB, Total max size = 20MB")