package com.wafflestudio.ggzz.domain.user.service

import com.wafflestudio.ggzz.domain.user.exception.UserNameNotFoundException
import com.wafflestudio.ggzz.domain.user.model.UserPrincipal
import com.wafflestudio.ggzz.domain.user.repository.UserRepository
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Service

@Service
class UserDetailService(
    private val userRepository: UserRepository
    ): UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        val user = userRepository.findByUsername(username) ?: throw UserNameNotFoundException(username)
        return UserPrincipal(user)
    }
}