package com.wafflestudio.ggzz.domain.user.service

import com.wafflestudio.ggzz.domain.user.dto.UserDto
import com.wafflestudio.ggzz.domain.user.dto.UserDto.AuthToken
import com.wafflestudio.ggzz.domain.user.exception.LoginFailedException
import com.wafflestudio.ggzz.domain.user.exception.DuplicateUsernameException
import com.wafflestudio.ggzz.domain.user.exception.UserNotFoundException
import com.wafflestudio.ggzz.domain.user.model.User
import com.wafflestudio.ggzz.domain.user.repository.UserRepository
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.http.ResponseCookie


interface UserService {
    fun updateOrCreate(request: UserDto.SignUpRequest): User
}


@Service
internal class UserServiceImpl(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val authTokenService: AuthTokenService,
) : UserService {
    override fun updateOrCreate(request: UserDto.SignUpRequest): User {
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

    fun login(request: UserDto.LoginRequest): ResponseEntity<AuthToken> {
        val user = userRepository.findByUsername(request.username!!) ?: throw LoginFailedException()

        if (!passwordEncoder.matches(request.password, user.password)) throw LoginFailedException()

        val accessToken = authTokenService.generateAccessTokenByUsername(request.username)
        val refreshToken = authTokenService.generateRefreshTokenByUsername(request.username)
        user.refreshToken = refreshToken
        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, authTokenService.generateResponseCookie(refreshToken).toString())
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
}
