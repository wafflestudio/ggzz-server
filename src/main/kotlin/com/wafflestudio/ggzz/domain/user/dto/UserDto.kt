package com.wafflestudio.ggzz.domain.user.dto

import com.wafflestudio.ggzz.domain.user.model.User
import jakarta.validation.constraints.NotBlank

class UserDto {
    data class SignUpRequest(
        @field: NotBlank
        val username: String?,
        @field: NotBlank
        val nickname: String?,
        @field: NotBlank
        val password: String?,
    )

    data class LoginRequest(
        @field: NotBlank
        val username: String?,
        @field: NotBlank
        val password: String?
    )

    data class UserResponse(
        val firebaseId: String?,
        val username: String,
        val nickname: String
    ) {
        companion object {
            fun fromEntity(user: User): UserResponse {
                return UserResponse(
                    firebaseId = user.firebaseId,
                    username = user.username!!,
                    nickname = user.nickname!!
                )
            }
        }
    }

    data class AuthToken(
        val accessToken: String,
    )
}
