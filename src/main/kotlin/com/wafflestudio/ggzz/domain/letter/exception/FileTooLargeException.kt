package com.wafflestudio.ggzz.domain.letter.exception

import com.wafflestudio.ggzz.global.common.exception.CustomException
import com.wafflestudio.ggzz.global.common.exception.ErrorType.BadRequest.FILE_TOO_LARGE

class FileTooLargeException: CustomException.BadRequestException(FILE_TOO_LARGE,
    "Max file size = 2MB, Total max size = 20MB")