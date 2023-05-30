package com.wafflestudio.ggzz.domain.user.service

import com.wafflestudio.ggzz.domain.user.dto.UserDto.AuthToken
import com.wafflestudio.ggzz.domain.user.dto.UserDto.LoginRequest
import com.wafflestudio.ggzz.domain.user.dto.UserDto.SignUpRequest
import com.wafflestudio.ggzz.domain.user.exception.DuplicateUsernameException
import com.wafflestudio.ggzz.domain.user.exception.LoginFailedException
import com.wafflestudio.ggzz.domain.user.model.User
import com.wafflestudio.ggzz.domain.user.repository.UserRepository
import com.wafflestudio.ggzz.global.auth.JwtTokenProvider
import com.wafflestudio.ggzz.global.auth.Type
import com.wafflestudio.ggzz.global.error.InvalidTokenException
import com.wafflestudio.ggzz.global.error.NotLoggedInException
import jakarta.servlet.http.Cookie
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.http.ResponseCookie


interface UserService {
    fun updateOrCreate(request: SignUpRequest): User
}


@Service
internal class UserServiceImpl(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider,
) : UserService {
    override fun updateOrCreate(request: SignUpRequest): User {
        val username = request.username!!

        val authentication = SecurityContextHolder.getContext().authentication
        if (authentication != null && authentication.isAuthenticated) {
            val user = authentication.principal as User

            if (user.username != username && userRepository.existsByUsername(username)) {
                throw DuplicateUsernameException(username)
            }
            user.username = username
            user.nickname = request.nickname
            user.password = passwordEncoder.encode(request.password)
            return userRepository.save(user)
        } else {
            throw UserNotFoundException()
        }
    }

    fun login(request: LoginRequest): ResponseEntity<AuthToken> {
        val user = userRepository.findByUsername(request.username!!) ?: throw LoginFailedException()

        if (!passwordEncoder.matches(request.password, user.password)) throw LoginFailedException()

        val accessToken = jwtTokenProvider.generateTokenByUsername(request.username, Type.ACCESS)
        val refreshToken = jwtTokenProvider.generateTokenByUsername(request.username, Type.REFRESH)
        user.refreshToken = refreshToken
        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, jwtTokenProvider.generateResponseCookie(refreshToken).toString())
            .body(AuthToken(accessToken))
    }

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

    fun refresh(cookie: Cookie): ResponseEntity<AuthToken> {
        val refreshToken = cookie.value
        val username = jwtTokenProvider.getUsernameFromToken(refreshToken, Type.REFRESH)
        val user = userRepository.findByUsername(username) ?: throw InvalidTokenException("Refresh")
        user.refreshToken ?: throw NotLoggedInException()
        if (refreshToken != user.refreshToken) throw InvalidTokenException("Refresh")

        val accessToken = jwtTokenProvider.generateTokenByUsername(username, Type.ACCESS)
        val newRefreshToken = jwtTokenProvider.generateTokenByUsername(username, Type.REFRESH)
        user.refreshToken = newRefreshToken
        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, jwtTokenProvider.generateResponseCookie(newRefreshToken).toString())
            .body(AuthToken(accessToken))
    }
}
