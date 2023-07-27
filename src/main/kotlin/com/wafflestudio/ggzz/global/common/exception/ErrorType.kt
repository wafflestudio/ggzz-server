package com.wafflestudio.ggzz.global.common.exception

enum class ErrorType {
    ;

    interface ErrorTypeInterface {
        fun getCode(): Int
    }

    enum class BadRequest(private val code: Int) : ErrorTypeInterface {
        INVALID_FIELD(0),
        CONSTRAINT_VIOLATION(1),
        UNSUPPORTED_FILE_TYPE(2),
        FILE_TOO_LARGE(3),

        INVALID_TOKEN(100),
        INVALID_PROVIDER(101),

        LETTER_NOT_CLOSE_ENOUGH(200),
        LETTER_VIEWABLE_TIME_EXPIRED(201),

        ;

        override fun getCode(): Int = code
    }

    enum class Unauthorized(private val code: Int) : ErrorTypeInterface {
        NO_TOKEN(1000),
        LOGIN_FAIL(1001),
        ;

        override fun getCode(): Int = code
    }

    enum class Forbidden(private val code: Int) : ErrorTypeInterface {
        WRONG_API(3000),

        LETTER_DELETE_FORBIDDEN(3100),
        ;

        override fun getCode(): Int = code
    }

    enum class NotFound(private val code: Int) : ErrorTypeInterface {
        LETTER_NOT_FOUND(4000),

        USER_NOT_FOUND(4100),
        ;

        override fun getCode(): Int = code
    }

    enum class Conflict(private val code: Int) : ErrorTypeInterface {
        DUPLICATE_ID(9000),
        DUPLICATE_USERNAME(9001),
        DUPLICATE_FIREBASE_ID(9002),

        LIKE_ALREADY_EXISTS(9100),
        ;

        override fun getCode(): Int = code
    }

    enum class ServerError(private val code: Int) : ErrorTypeInterface {
        INTERNAL_SERVER_ERROR(10000),
        FILE_UPLOAD_FAIL(10001),
        FILE_DELETE_FAIL(10002),
        ;

        override fun getCode(): Int = code
    }

}
