package com.wafflestudio.ggzz.domain.user.service

import com.wafflestudio.ggzz.domain.user.dto.UserDto
import com.wafflestudio.ggzz.domain.user.exception.BadRequestException
import com.wafflestudio.ggzz.domain.user.exception.DuplicateUsernameException
import com.wafflestudio.ggzz.domain.user.exception.UserNotFoundException
import com.wafflestudio.ggzz.domain.user.model.User
import com.wafflestudio.ggzz.domain.user.repository.UserRepository
import com.wafflestudio.ggzz.global.config.FirebaseConfig
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

interface UserService {
    fun updateOrCreate(request: UserDto.SignUpRequest): User
    fun update(firebaseToken: String): User
}

@Service
internal class UserServiceImpl(
    private val userRepository: UserRepository,
    private val firebaseConfig: FirebaseConfig,
    private val passwordEncoder: PasswordEncoder
) : UserService {
    override fun updateOrCreate(request: UserDto.SignUpRequest): User {
        val username = request.username ?: throw BadRequestException()
        val nickname = request.nickname ?: throw BadRequestException()
        val password = request.password ?: throw BadRequestException()

        val authentication = SecurityContextHolder.getContext().authentication
        if (authentication != null && authentication.isAuthenticated) {
            var user = authentication.principal as User
            user = getUser(user.firebaseId)

            if (user.username != username && userRepository.existsByUsername(username)) {
                throw DuplicateUsernameException(username)
            }
            user.username = username
            user.nickname = nickname
            user.password = passwordEncoder.encode(password)
            return userRepository.save(user)
        } else {
            throw UserNotFoundException()
        }
    }

    override fun update(firebaseToken: String): User {
        TODO("Not yet implemented")
    }

    private fun getUser(firebaseId: String): User {
        val firebaseId = runCatching { firebaseConfig.verifyId(firebaseId) }
            .getOrElse { throw UserNotFoundException() }

        return userRepository.findByFirebaseId(firebaseId)
    }
}