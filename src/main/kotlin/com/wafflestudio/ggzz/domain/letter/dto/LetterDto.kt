package com.wafflestudio.ggzz.domain.letter.dto

import com.wafflestudio.ggzz.domain.letter.model.Letter
import com.wafflestudio.ggzz.global.common.dto.TimeTraceDto
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.hibernate.validator.constraints.Range
import java.time.LocalDateTime

class LetterDto {
    data class CreateRequest(
        @Schema(title = "편지 제목", required = true)
        @field:NotBlank
        val title: String?,
        @Schema(title = "편지 요약", required = true)
        @field:NotBlank
        val summary: String?,
        @Schema(title = "경도", required = true, format = "-180 ~ 180", type = "double")
        @field:[NotNull Range(min = -180, max = 180)]
        val longitude: Double?,
        @Schema(title = "위도", required = true, format = "-90 ~ 90", type = "double")
        @field:[NotNull Range(min = -90, max = 90)]
        val latitude: Double?,
        @Schema(title = "편지 내용(글)", required = false)
        val text: String?,
        @Schema(title = "편지 공개 시간", required = false)
        val viewableTime: Int? = 0,
        @Schema(title = "편지 공개 범위", required = false)
        val viewRange: Int? = 0
    )

    data class Response(
        @Schema(title = "API에 사용되는 편지 ID")
        override val id: Long,
        @Schema(title = "편지 생성일자")
        override val createdAt: LocalDateTime,
        @Schema(title = "편지 생성자 닉네임")
        val createdBy: String,
        @Schema(title = "편지 제목")
        val title: String,
        @Schema(title = "편지 내용")
        val summary: String,
        @Schema(title = "경도")
        val longitude: Double,
        @Schema(title = "위도")
        val latitude: Double
    ) : TimeTraceDto.Response(id, createdAt) {
        constructor(letter: Letter) : this(
            id = letter.id,
            createdAt = letter.createdAt,
            createdBy = letter.user.nickname,
            title = letter.title,
            summary = letter.summary,
            longitude = letter.longitude,
            latitude = letter.latitude
        )
    }

    data class DetailResponse(
        @Schema(title = "API에 사용되는 편지 ID")
        override val id: Long,
        @Schema(title = "편지 생성일자")
        override val createdAt: LocalDateTime,
        @Schema(title = "편지 생성자 닉네임")
        val createdBy: String,
        @Schema(title = "편지 제목")
        val title: String,
        @Schema(title = "편지 내용")
        val summary: String,
        @Schema(title = "경도")
        val longitude: Double,
        @Schema(title = "위도")
        val latitude: Double,
        @Schema(title = "편지 내용(글)")
        val text: String?,
        @Schema(title = "편지 내용(이미지)")
        val image: String?,
        @Schema(title = "편지 내용(음성)")
        val voice: String?
    ) : TimeTraceDto.Response(id, createdAt) {
        constructor(letter: Letter) : this(
            id = letter.id,
            createdAt = letter.createdAt,
            createdBy = letter.user.nickname,
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
