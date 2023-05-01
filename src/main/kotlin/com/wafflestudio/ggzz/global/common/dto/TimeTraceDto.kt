package com.wafflestudio.ggzz.global.common.dto

import java.time.LocalDateTime

class TimeTraceDto {
    open class Response(
        open val id: Long,
        open val createdAt: LocalDateTime,
    )
}