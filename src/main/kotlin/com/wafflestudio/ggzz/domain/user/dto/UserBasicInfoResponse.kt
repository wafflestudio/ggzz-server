package com.wafflestudio.ggzz.domain.user.dto

import com.wafflestudio.ggzz.domain.user.model.User

data class UserBasicInfoResponse(
    val ggzzId: String?,
    val firebaseId: String?,
    val username: String,
) {
    companion object {
        fun fromEntity(user: User): UserBasicInfoResponse {
            return UserBasicInfoResponse(
                ggzzId = user.ggzzId,
                firebaseId = user.firebaseId,
                username = user.username,
            )
        }
    }
}
