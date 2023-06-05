package com.wafflestudio.ggzz.domain.user.controller

import com.wafflestudio.ggzz.domain.user.dto.UserDto
import com.wafflestudio.ggzz.domain.user.service.UserService
import io.swagger.v3.oas.annotations.Operation
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseCookie
import org.springframework.http.ResponseEntity
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

    @Operation(summary = "로그아웃: JSESSIONID 쿠키 삭제 용도")
    @PostMapping("/logout")
    fun logout(): ResponseEntity<Any> {
        logger.info("POST /logout")
        val cookie = ResponseCookie.from("JSESSIONID", "")
            .maxAge(0)
            .path("/")
            .secure(true)
            .sameSite("None")
            .httpOnly(true)
            .build().toString()

        return ResponseEntity.ok().header("Set-cookie", cookie).build()
    }
}