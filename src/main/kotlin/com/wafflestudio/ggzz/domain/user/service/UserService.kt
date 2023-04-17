package com.wafflestudio.ggzz.domain.user.service

import com.wafflestudio.ggzz.domain.user.dto.UserDto.LoginRequest
import com.wafflestudio.ggzz.domain.user.dto.UserDto.SignUpRequest
import com.wafflestudio.ggzz.domain.user.exception.DuplicateUsernameException
import com.wafflestudio.ggzz.domain.user.exception.LoginFailedException
import com.wafflestudio.ggzz.domain.user.model.User
import com.wafflestudio.ggzz.domain.user.model.UserPrincipal
import com.wafflestudio.ggzz.domain.user.model.UserToken
import com.wafflestudio.ggzz.domain.user.repository.UserRepository
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import com.wafflestudio.nostalgia.domain.user.dto.UserDto.*


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

    fun login(request: LoginRequest): ResponseEntity<AuthToken> {
        val user = userRepository.findByUsername(request.username!!) ?: throw LoginFailedException()

        if (!passwordEncoder.matches(request.password, user.password)) throw LoginFailedException()

        val accessToken = authTokenService.generateAccessTokenByUsername(request.username).accessToken
        val refreshToken = authTokenService.generateRefreshTokenByUsername(request.username)
        user.refreshToken = refreshToken
        return ResponseEntity.ok()
            .header(HttpHeaders.SET_COOKIE, authTokenService.generateResponseCookie(refreshToken).toString())
            .body(AuthToken(accessToken))
    }
}