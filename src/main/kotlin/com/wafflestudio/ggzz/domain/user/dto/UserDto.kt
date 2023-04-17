package com.wafflestudio.ggzz.domain.user.dto

import com.wafflestudio.ggzz.domain.user.model.User
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

class UserDto {
    data class SignUpRequest(
        @Schema(title = "로그인 아이디", required = true)
        @field: NotBlank
        val username: String?,
        @Schema(title = "편지에 보여질 닉네임", required = true)
        @field: NotBlank
        val nickname: String?,
        @Schema(title = "로그인 비밀번호", required = true)
        @field: NotBlank
        val password: String?,
    )

    data class LoginRequest(
        @Schema(title = "로그인 아이디", required = true)
        @field: NotBlank
        val username: String?,
        @Schema(title = "로그인 비밀번호", required = true)
        @field: NotBlank
        val password: String?
    )

    data class UserResponse(
        @Schema(title = "사용자 firebaseId", required = true)
        val firebaseId: String?,
        @Schema(title = "로그인 아이디")
        val username: String,
        @Schema(title = "편지에 보여질 닉네임")
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
