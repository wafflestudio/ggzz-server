package com.wafflestudio.ggzz.domain.letter.dto

import com.wafflestudio.ggzz.domain.letter.model.Letter
import com.wafflestudio.ggzz.global.common.dto.TimeTraceDto
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.hibernate.validator.constraints.Range
import java.time.LocalDateTime

class LetterDto {
    data class CreateRequest(
        @field:NotBlank
        val title: String?,
        @field:NotBlank
        val summary: String?,
        @field:[NotNull Range(min = -180, max = 180)]
        val longitude: Double?,
        @field:[NotNull Range(min = -90, max = 90)]
        val latitude: Double?,
        val text: String?,
        val viewableTime: Int? = 0,
        val viewRange: Int? = 0
    )

    data class Response(
        override val id: Long,
        override val createdAt: LocalDateTime,
        val createdBy: String,
        val title: String,
        val summary: String,
        val longitude: Double,
        val latitude: Double
    ) : TimeTraceDto.Response(id, createdAt) {
        constructor(letter: Letter) : this(
            id = letter.id,
            createdAt = letter.createdAt,
            createdBy = letter.user.nickname!!,
            title = letter.title,
            summary = letter.summary,
            longitude = letter.longitude,
            latitude = letter.latitude
        )
    }

    data class DetailResponse(
        override val id: Long,
        override val createdAt: LocalDateTime,
        val createdBy: String,
        val title: String,
        val summary: String,
        val longitude: Double,
        val latitude: Double,
        val text: String?,
        val image: String?,
        val voice: String?
    ) : TimeTraceDto.Response(id, createdAt) {
        constructor(letter: Letter) : this(
            id = letter.id,
            createdAt = letter.createdAt,
            createdBy = letter.user.nickname!!,
            title = letter.title,
            summary = letter.summary,
            longitude = letter.longitude,
            latitude = letter.latitude,
            text = letter.text,
            image = letter.image,
            voice = letter.voice,
        )
    }
}
