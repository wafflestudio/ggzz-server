package com.wafflestudio.nostalgia.global.common.exception

import com.wafflestudio.nostalgia.domain.letter.exception.FileTooLargeException
import com.wafflestudio.nostalgia.global.common.dto.ErrorResponse
import com.wafflestudio.nostalgia.global.common.exception.CustomException.*
import com.wafflestudio.nostalgia.global.common.exception.ErrorType.BadRequest.CONSTRAINT_VIOLATION
import com.wafflestudio.nostalgia.global.common.exception.ErrorType.BadRequest.INVALID_FIELD
import jakarta.validation.ConstraintViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.multipart.MaxUploadSizeExceededException

@RestControllerAdvice
class CustomControllerAdvice {
    @ExceptionHandler(BadRequestException::class)
    fun badRequest(e: BadRequestException) = ResponseEntity(ErrorResponse(e), HttpStatus.BAD_REQUEST)

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun invalidField(e: MethodArgumentNotValidException) = ResponseEntity(ErrorResponse(
        INVALID_FIELD.getCode(),
        INVALID_FIELD,
        e.fieldErrors.joinToString(separator = " ") { it.field + " " + it.defaultMessage + "." }
    ), HttpStatus.BAD_REQUEST)

    @ExceptionHandler(ConstraintViolationException::class)
    fun constraintViolation(e: ConstraintViolationException) = ResponseEntity(ErrorResponse(
        CONSTRAINT_VIOLATION.getCode(),
        CONSTRAINT_VIOLATION,
        e.constraintViolations.joinToString(separator = " ") {
            it.propertyPath.toString().split('.').last() + " " + it.message + ", but " + it.invalidValue + "."
        }
    ), HttpStatus.BAD_REQUEST)

    @ExceptionHandler(MaxUploadSizeExceededException::class)
    fun maxUpload(e: MaxUploadSizeExceededException) = ResponseEntity(ErrorResponse(FileTooLargeException()), HttpStatus.BAD_REQUEST)

    @ExceptionHandler(UnauthorizedException::class)
    fun unauthorized(e: UnauthorizedException) = ResponseEntity(ErrorResponse(e), HttpStatus.UNAUTHORIZED)

    @ExceptionHandler(ForbiddenException::class)
    fun forbidden(e: ForbiddenException) = ResponseEntity(ErrorResponse(e), HttpStatus.FORBIDDEN)

    @ExceptionHandler(NotFoundException::class)
    fun notFound(e: NotFoundException) = ResponseEntity(ErrorResponse(e), HttpStatus.NOT_FOUND)

    @ExceptionHandler(ConflictException::class)
    fun conflict(e: ConflictException) = ResponseEntity(ErrorResponse(e), HttpStatus.CONFLICT)

    @ExceptionHandler(ServerErrorException::class)
    fun serverError(e: ServerErrorException) = ResponseEntity(ErrorResponse(e), HttpStatus.INTERNAL_SERVER_ERROR)
}