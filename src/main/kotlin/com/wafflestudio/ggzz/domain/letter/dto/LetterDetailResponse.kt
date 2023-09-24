package com.wafflestudio.ggzz.domain.letter.dto

import com.wafflestudio.ggzz.domain.letter.model.Letter
import com.wafflestudio.ggzz.global.common.dto.TimeTraceDto
import java.time.LocalDateTime

data class LetterDetailResponse(
    override val id: Long,
    override val createdAt: LocalDateTime,
    val createdBy: String,
    val longitude: Double,
    val latitude: Double,
    val text: String?,
    val image: String?,
    val voice: String?
) : TimeTraceDto.Response(id, createdAt) {
    constructor(letter: Letter) : this(
        id = letter.id,
        createdAt = letter.createdAt,
        createdBy = letter.user.username,
        longitude = letter.longitude,
        latitude = letter.latitude,
        text = letter.text,
        image = letter.image,
        voice = letter.voice,
    )
}