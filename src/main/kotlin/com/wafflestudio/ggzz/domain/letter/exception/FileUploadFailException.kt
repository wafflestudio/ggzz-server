package com.wafflestudio.ggzz.domain.letter.exception

import com.wafflestudio.ggzz.global.common.exception.CustomException.ServerErrorException
import com.wafflestudio.ggzz.global.common.exception.ErrorType.ServerError.FILE_UPLOAD_FAIL

class FileUploadFailException: ServerErrorException(FILE_UPLOAD_FAIL, "File upload failed. Try again.")