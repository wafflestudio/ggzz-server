package com.wafflestudio.ggzz.domain.letter.exception

import com.wafflestudio.ggzz.global.common.exception.CustomException
import com.wafflestudio.ggzz.global.common.exception.ErrorType

class FileDeleteFailException: CustomException.ServerErrorException(ErrorType.ServerError.FILE_DELETE_FAIL,
    "File delete failed. Try again.")