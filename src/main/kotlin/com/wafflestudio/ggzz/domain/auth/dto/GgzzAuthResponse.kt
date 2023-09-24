package com.wafflestudio.ggzz.domain.auth.dto

import com.wafflestudio.ggzz.domain.user.dto.UserBasicInfoResponse

data class GgzzAuthResponse(
    val accessToken: String,
    val user: UserBasicInfoResponse?
)