package com.wafflestudio.ggzz.domain.auth.dto

import jakarta.validation.constraints.NotBlank

data class GgzzSignupRequest(
    @field: NotBlank
    val ggzzId: String?,
    @field: NotBlank
    val username: String?,
    @field: NotBlank
    val password: String?,
)