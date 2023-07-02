package com.wafflestudio.ggzz.domain.user.service

import com.wafflestudio.ggzz.domain.user.dto.UserDto
import com.wafflestudio.ggzz.domain.user.exception.BadRequestException
import com.wafflestudio.ggzz.domain.user.exception.DuplicateUsernameException
import com.wafflestudio.ggzz.domain.user.exception.UserNotFoundException
import com.wafflestudio.ggzz.domain.user.model.User
import com.wafflestudio.ggzz.domain.user.repository.UserRepository
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service

interface UserService {
    fun updateOrCreate(request: UserDto.SignUpRequest): User
}

@Service
internal class UserServiceImpl(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder
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
}
