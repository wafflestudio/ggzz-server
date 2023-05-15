package com.wafflestudio.ggzz.global.common.exception

enum class ErrorType {
    ;

    interface ErrorTypeInterface {
        fun getCode(): Int
    }

    enum class BadRequest(private val code: Int): ErrorTypeInterface {
        INVALID_FIELD(0),
        CONSTRAINT_VIOLATION(1),
        LETTER_NOT_CLOSE_ENOUGH(2),
        UNSUPPORTED_FILE_TYPE(3),
        FILE_TOO_LARGE(4),
        UNSATISFIED_REQUEST(5),
        INVALID_TOKEN(6),
        ;
        override fun getCode(): Int = code
    }

    enum class Unauthorized(private val code: Int): ErrorTypeInterface {
        NOT_LOGGED_IN(1000),
        LOGIN_FAIL(1001),
        INVALID_FIREBASE_TOKEN(1002),
        TOKEN_EXPIRED(1003),
        ;
        override fun getCode(): Int = code
    }

    enum class Forbidden(private val code: Int): ErrorTypeInterface {
        WRONG_API(3000),
        LETTER_DELETE_FORBIDDEN(3001),
        NO_TOKEN(3002),
        ;
        override fun getCode(): Int = code
    }

    enum class NotFound(private val code: Int): ErrorTypeInterface {
        LETTER_NOT_FOUND(4000),
        USER_NOT_FOUND(4001),
        ;

        override fun getCode(): Int = code
    }

    enum class Conflict(private val code: Int): ErrorTypeInterface {
        USERNAME_CONFLICT(9000),
        LIKE_ALREADY_EXISTS(9001),
        ;
        override fun getCode(): Int = code
    }

    enum class ServerError(private val code: Int): ErrorTypeInterface {
        INTERNAL_SERVER_ERROR(10000),
        FILE_UPLOAD_FAIL(10001),
        FILE_DELETE_FAIL(10002),
        ;
        override fun getCode(): Int = code
    }

}