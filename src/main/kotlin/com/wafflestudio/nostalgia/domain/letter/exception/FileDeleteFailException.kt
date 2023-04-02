package com.wafflestudio.nostalgia.domain.letter.exception

import com.wafflestudio.nostalgia.global.common.exception.CustomException
import com.wafflestudio.nostalgia.global.common.exception.ErrorType

class FileDeleteFailException: CustomException.ServerErrorException(ErrorType.ServerError.FILE_DELETE_FAIL,
    "File delete failed. Try again.")