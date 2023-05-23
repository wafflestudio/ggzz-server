package com.wafflestudio.ggzz.domain.user.service

import com.wafflestudio.ggzz.domain.user.dto.UserDto.AuthToken
import com.wafflestudio.ggzz.domain.user.dto.UserDto.LoginRequest
import com.wafflestudio.ggzz.domain.user.dto.UserDto.SignUpRequest
import com.wafflestudio.ggzz.domain.user.exception.DuplicateUsernameException
import com.wafflestudio.ggzz.domain.user.exception.LoginFailedException
import com.wafflestudio.ggzz.domain.user.model.User
import com.wafflestudio.ggzz.domain.user.repository.UserRepository
import com.wafflestudio.ggzz.global.error.InvalidTokenException
import com.wafflestudio.ggzz.global.error.NotLoggedInException
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.http.ResponseCookie


@Service
@Transactional(readOnly = true)
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val authTokenService: AuthTokenService,
) {
    @Transactional
    fun signup(request: SignUpRequest) {
        if (userRepository.existsByUsername(request.username!!)) throw DuplicateUsernameException(request.username)

        val user = User(request, passwordEncoder.encode(request.password))

        userRepository.save(user)
    }

    @Transactional
    fun login(request: LoginRequest): ResponseEntity<AuthToken> {
        val user = userRepository.findByUsername(request.username!!) ?: throw LoginFailedException()

        if (!passwordEncoder.matches(request.password, user.password)) throw LoginFailedException()

        val accessToken = authTokenService.generateTokenByUsername(request.username, Type.ACCESS)
        val refreshToken = authTokenService.generateTokenByUsername(request.username, Type.REFRESH)
        user.refreshToken = refreshToken
        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, authTokenService.generateResponseCookie(refreshToken).toString())
            .body(AuthToken(accessToken))
    }

    @Transactional
    fun logout(userId: Long): ResponseEntity<Any> {
        val user = userRepository.findMeById(userId)
        user.refreshToken = null

        val cookie = ResponseCookie.from("refreshToken", "")
            .httpOnly(true)
            .secure(true)
            .sameSite("None")
            .path("/")
            .maxAge(0)
            .build().toString()

        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie).build()
    }

    @Transactional
    fun refresh(refreshToken: String): ResponseEntity<AuthToken> {
        val username = authTokenService.getUsernameFromToken(refreshToken, Type.REFRESH)
        val user = userRepository.findByUsername(username) ?: throw InvalidTokenException("Refresh")
        user.refreshToken ?: throw NotLoggedInException()
        if (refreshToken != user.refreshToken) throw InvalidTokenException("Refresh")

        val accessToken = authTokenService.generateTokenByUsername(username, Type.ACCESS)
        val newRefreshToken = authTokenService.generateTokenByUsername(username, Type.REFRESH)
        user.refreshToken = newRefreshToken
        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, authTokenService.generateResponseCookie(newRefreshToken).toString())
            .body(AuthToken(accessToken))
    }
}