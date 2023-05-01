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
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.context.request.RequestAttributes
import org.springframework.web.context.request.RequestContextHolder

@Service
@Transactional(readOnly = true)
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
) {
    @Transactional
    fun signup(request: SignUpRequest) {
        if (userRepository.existsByUsername(request.username!!)) throw DuplicateUsernameException(request.username)

        val user = User(request, passwordEncoder.encode(request.password))

        userRepository.save(user)

        val context = SecurityContextHolder.getContext()
        context.authentication = UserToken(UserPrincipal(user))
        RequestContextHolder.currentRequestAttributes()
            .setAttribute("SPRING_SECURITY_CONTEXT", context, RequestAttributes.SCOPE_SESSION)
    }

    fun login(request: LoginRequest) {
        val user = userRepository.findByUsername(request.username!!) ?: throw LoginFailedException()

        if (!passwordEncoder.matches(request.password, user.password)) throw LoginFailedException()

        val context = SecurityContextHolder.getContext()
        context.authentication = UserToken(UserPrincipal(user))
        RequestContextHolder.currentRequestAttributes()
            .setAttribute("SPRING_SECURITY_CONTEXT", context, RequestAttributes.SCOPE_SESSION)
    }
}