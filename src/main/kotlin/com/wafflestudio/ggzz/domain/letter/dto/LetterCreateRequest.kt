package com.wafflestudio.ggzz.domain.letter.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import org.hibernate.validator.constraints.Range
import org.springframework.web.multipart.MultipartFile

data class LetterCreateRequest(
    @field:[NotNull Range(min = -180, max = 180)]
    val longitude: Double?,
    @field:[NotNull Range(min = -90, max = 90)]
    val latitude: Double?,
    @field:[NotBlank]
    val text: String?,
    val image: MultipartFile?,
    val voice: MultipartFile?,
    @field:[Range(min = 0, max = 24)]
    val viewableTime: Int? = 0,
    @field:[Range(min = 0, max = 200)]
    val viewRange: Int? = 0
)