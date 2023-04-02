package com.wafflestudio.nostalgia.global.common.exception

import com.wafflestudio.nostalgia.global.common.exception.ErrorType.*

abstract class CustomException(
    val errorType: ErrorTypeInterface,
    val detail: String
): RuntimeException() {
    abstract class BadRequestException(errorType: BadRequest, detail: String): CustomException(errorType, detail)
    abstract class UnauthorizedException(errorType: Unauthorized, detail: String): CustomException(errorType, detail)
    abstract class ForbiddenException(errorType: Forbidden, detail: String): CustomException(errorType, detail)
    abstract class NotFoundException(errorType: NotFound, detail: String): CustomException(errorType, detail)
    abstract class ConflictException(errorType: Conflict, detail: String): CustomException(errorType, detail)
    abstract class ServerErrorException(errorType: ServerError, detail: String): CustomException(errorType, detail)
}
