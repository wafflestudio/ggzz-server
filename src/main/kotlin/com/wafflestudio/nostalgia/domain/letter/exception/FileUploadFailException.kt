package com.wafflestudio.nostalgia.domain.letter.exception

import com.wafflestudio.nostalgia.global.common.exception.CustomException.ServerErrorException
import com.wafflestudio.nostalgia.global.common.exception.ErrorType.ServerError.FILE_UPLOAD_FAIL

class FileUploadFailException: ServerErrorException(FILE_UPLOAD_FAIL, "File upload failed. Try again.")