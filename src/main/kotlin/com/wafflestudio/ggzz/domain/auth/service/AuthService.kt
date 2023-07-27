package com.wafflestudio.ggzz.domain.auth.service

import com.wafflestudio.ggzz.domain.auth.dto.GgzzAuthResponse
import com.wafflestudio.ggzz.domain.auth.dto.GgzzLoginRequest
import com.wafflestudio.ggzz.domain.auth.dto.GgzzSignupRequest
import com.wafflestudio.ggzz.domain.auth.dto.ProviderLoginRequest
import com.wafflestudio.ggzz.domain.auth.exception.DuplicateGgzzIdException
import com.wafflestudio.ggzz.domain.auth.exception.DuplicateUsernameException
import com.wafflestudio.ggzz.domain.auth.exception.InvalidProviderException
import com.wafflestudio.ggzz.domain.auth.exception.LoginFailedException
import com.wafflestudio.ggzz.domain.auth.model.JwtTokenProvider
import com.wafflestudio.ggzz.domain.auth.model.Provider.GGZZ
import com.wafflestudio.ggzz.domain.auth.provider.ProviderService
import com.wafflestudio.ggzz.domain.user.dto.UserBasicInfoResponse
import com.wafflestudio.ggzz.domain.user.exception.UserIdNotFoundException
import com.wafflestudio.ggzz.domain.user.exception.UserNotFoundException
import com.wafflestudio.ggzz.domain.user.model.User
import com.wafflestudio.ggzz.domain.user.repository.UserRepository
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class AuthService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtTokenProvider: JwtTokenProvider,
    private val providerServices: List<ProviderService>,
) {

    @Transactional
    fun signup(request: GgzzSignupRequest): GgzzAuthResponse {
        userRepository.findByGgzzId(request.ggzzId!!)?.let { throw DuplicateGgzzIdException(request.ggzzId) }
        userRepository.findByUsername(request.username!!)?.let { throw DuplicateUsernameException(request.username) }

        val encodedPassword = passwordEncoder.encode(request.password)
        val user = userRepository.save(
            User(
                ggzzId = request.ggzzId,
                firebaseId = null,
                username = request.username,
                password = encodedPassword,
            )
        )

        return GgzzAuthResponse(
            jwtTokenProvider.generateToken(user.id),
            UserBasicInfoResponse.fromEntity(user)
        )
    }

    fun login(request: GgzzLoginRequest): GgzzAuthResponse {
        val user = userRepository.findByGgzzId(request.ggzzId!!) ?: throw UserIdNotFoundException(GGZZ, request.ggzzId)

        if (!passwordEncoder.matches(request.password, user.password)) throw LoginFailedException()

        return GgzzAuthResponse(
            jwtTokenProvider.generateToken(user.id),
            UserBasicInfoResponse.fromEntity(user)
        )
    }

    fun refresh(token: String): GgzzAuthResponse {
        val id = jwtTokenProvider.getIdFromToken(token)
        val user = userRepository.findUserById(id) ?: throw UserNotFoundException()

        return GgzzAuthResponse(
            jwtTokenProvider.generateToken(id),
            UserBasicInfoResponse.fromEntity(user)
        )
    }

    @Transactional
    fun signupWithProvider(request: ProviderLoginRequest): GgzzAuthResponse {
        return providerServices.find { it.supports(request.provider!!) }?.let { providerService ->
            val user = providerService.createNewUser(request.accessToken!!)
            val token = jwtTokenProvider.generateToken(user.id)

            GgzzAuthResponse(token, UserBasicInfoResponse.fromEntity(user))

        } ?: throw InvalidProviderException(request.provider!!)
    }

    fun loginWithProvider(request: ProviderLoginRequest): GgzzAuthResponse {
        return providerServices.find { it.supports(request.provider!!) }?.let { providerService ->
            val user = providerService.getUser(request.accessToken!!)
            val token = jwtTokenProvider.generateToken(user.id)

            GgzzAuthResponse(token, UserBasicInfoResponse.fromEntity(user))

        } ?: throw InvalidProviderException(request.provider!!)
    }

}