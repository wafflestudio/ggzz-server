package com.wafflestudio.ggzz.domain.user.controller

import com.wafflestudio.ggzz.domain.user.dto.UserDto
import com.wafflestudio.ggzz.domain.user.service.UserService
import com.wafflestudio.ggzz.domain.user.model.CurrentUser
import io.swagger.v3.oas.annotations.Operation
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController

@RestController
class UserController(
    private val userService: UserService,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @Operation(summary = "회원가입")
    @PostMapping("/signup")
    fun signup(@RequestBody @Valid request: UserDto.SignUpRequest): ResponseEntity<UserDto.UserResponse> {
        logger.info("POST /signup")
        val user = userService.updateOrCreate(request)
        val userResponse = UserDto.UserResponse.fromEntity(user)
        return ResponseEntity.ok(userResponse)
    }

    @Operation(summary = "로그인")
    @PostMapping("/login")
    fun login(@RequestBody @Valid request: LoginRequest): ResponseEntity<AuthToken> {
        logger.info("POST /login")
        return userService.login(request)
    }

    @Operation(summary = "로그인 확인 용도")
    @GetMapping("/login")
    fun isLoggedIn(): ResponseEntity<Any> {
        logger.info("GET /login")
        return ResponseEntity.ok().build()
    }

    @Operation(summary = "로그아웃: refreshToken 쿠키 삭제 용도")
    @PostMapping("/logout")
    fun logout(@CurrentUser userId: Long): ResponseEntity<Any> {
        logger.info("POST /logout")
        return userService.logout(userId)
    }
}