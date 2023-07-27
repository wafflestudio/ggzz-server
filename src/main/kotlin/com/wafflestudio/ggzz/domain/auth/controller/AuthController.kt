package com.wafflestudio.ggzz.domain.auth.controller

import com.wafflestudio.ggzz.domain.auth.dto.GgzzAuthResponse
import com.wafflestudio.ggzz.domain.auth.dto.GgzzLoginRequest
import com.wafflestudio.ggzz.domain.auth.dto.GgzzSignupRequest
import com.wafflestudio.ggzz.domain.auth.dto.ProviderLoginRequest
import com.wafflestudio.ggzz.domain.auth.exception.InvalidTokenException
import com.wafflestudio.ggzz.domain.auth.service.AuthService
import com.wafflestudio.ggzz.global.common.utils.JwtUtils
import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.http.HttpHeaders
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/auth")
class AuthController(
    private val authService: AuthService
) {

    @PostMapping("/ggzz/signup")
    fun signup(@RequestBody @Valid request: GgzzSignupRequest): GgzzAuthResponse {
        return authService.signup(request)
    }

    @PostMapping("/ggzz/login")
    fun login(@RequestBody @Valid request: GgzzLoginRequest): GgzzAuthResponse {
        return authService.login(request)
    }

    @PostMapping("/ggzz/refresh")
    fun refresh(request: HttpServletRequest): GgzzAuthResponse {
        request.getHeader(HttpHeaders.AUTHORIZATION)?.let {
            if (JwtUtils.isBearerToken(it))
                return authService.refresh(it)

            throw InvalidTokenException()
        } ?: throw InvalidTokenException()
    }

    @PostMapping("/provider/signup")
    fun signupWithProvider(@RequestBody @Valid request: ProviderLoginRequest): GgzzAuthResponse {
        if (JwtUtils.isBearerToken(request.accessToken!!))
            return authService.signupWithProvider(request)

        throw InvalidTokenException()
    }

    @PostMapping("/provider/login")
    fun loginWithProvider(@RequestBody @Valid request: ProviderLoginRequest): GgzzAuthResponse {
        if (JwtUtils.isBearerToken(request.accessToken!!))
            return authService.loginWithProvider(request)

        throw InvalidTokenException()
    }

}