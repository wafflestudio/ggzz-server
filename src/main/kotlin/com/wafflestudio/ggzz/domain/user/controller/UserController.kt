package com.wafflestudio.ggzz.domain.user.controller

import com.wafflestudio.ggzz.domain.user.dto.UserDto
import com.wafflestudio.ggzz.domain.user.dto.UserDto.AuthToken
import com.wafflestudio.ggzz.domain.user.dto.UserDto.LoginRequest
import com.wafflestudio.ggzz.domain.user.dto.UserDto.SignUpRequest
import com.wafflestudio.ggzz.domain.user.service.UserService
import com.wafflestudio.ggzz.domain.user.model.CurrentUser
import jakarta.servlet.http.Cookie
import jakarta.validation.Valid
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.CookieValue

@RestController
class UserController(
    private val userService: UserService,
) {
    private val logger = LoggerFactory.getLogger(this::class.java)

    @PostMapping("/signup")
    fun signup(@RequestBody @Valid request: SignUpRequest): ResponseEntity<UserDto.UserResponse> {
        logger.info("POST /signup")
        val user = userService.updateOrCreate(request)
        val userResponse = UserDto.UserResponse.fromEntity(user)
        return ResponseEntity.ok(userResponse)
    }

    @PostMapping("/login")
    fun login(@RequestBody @Valid request: LoginRequest): ResponseEntity<AuthToken> {
        logger.info("POST /login")
        return userService.login(request)
    }

    @GetMapping("/login")
    fun isLoggedIn(): ResponseEntity<Any> {
        logger.info("GET /login")
        return ResponseEntity.ok().build()
    }

    @PostMapping("/logout")
    fun logout(@CurrentUser userId: Long): ResponseEntity<Any> {
        logger.info("POST /logout")
        return userService.logout(userId)
    }

    @PostMapping("/refresh")
    fun refresh(@CookieValue(value = "refreshToken", required = false) cookie: Cookie): ResponseEntity<AuthToken> {
        logger.info("POST /refresh")
        return userService.refresh(cookie)
    }
}