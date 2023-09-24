package com.wafflestudio.ggzz.domain.auth.dto

import jakarta.validation.constraints.NotBlank

data class GgzzLoginRequest(
    @field: NotBlank
    val ggzzId: String?,
    @field: NotBlank
    val password: String?
)