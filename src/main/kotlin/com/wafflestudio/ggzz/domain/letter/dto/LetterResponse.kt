package com.wafflestudio.ggzz.domain.letter.dto

import com.wafflestudio.ggzz.domain.letter.model.Letter
import com.wafflestudio.ggzz.global.common.dto.TimeTraceDto
import java.time.LocalDateTime

data class LetterResponse(
    override val id: Long,
    override val createdAt: LocalDateTime,
    val createdBy: String,
    val longitude: Double,
    val latitude: Double
) : TimeTraceDto.Response(id, createdAt) {
    constructor(letter: Letter) : this(
        id = letter.id,
        createdAt = letter.createdAt,
        createdBy = letter.user.username,
        longitude = letter.longitude,
        latitude = letter.latitude
    )
}