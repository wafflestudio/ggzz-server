package com.wafflestudio.ggzz.domain.auth.dto

import com.wafflestudio.ggzz.domain.auth.model.Provider
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class ProviderLoginRequest(
    @field:NotBlank
    val accessToken: String?,
    @field:NotNull
    val provider: Provider?,
)