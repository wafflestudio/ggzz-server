package com.wafflestudio.ggzz.global.config.filter

import com.wafflestudio.ggzz.domain.user.model.User
import com.wafflestudio.ggzz.domain.user.repository.UserRepository
import com.wafflestudio.ggzz.global.config.FirebaseConfig
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class FirebaseTokenFilter(
    private val firebaseConfig: FirebaseConfig,
    private val userRepository: UserRepository
) : OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain
    ) {
        val token = extractTokenFromRequest(request)

        if (token != null) {
            val authentication = createAuthentication(token)
            SecurityContextHolder.getContext().authentication = authentication
        }

        filterChain.doFilter(request, response)
    }

    private fun extractTokenFromRequest(request: HttpServletRequest): String? {
        val authorizationHeader = request.getHeader("Authorization")
        return if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            authorizationHeader.substring(7)
        } else null
    }

    private fun createAuthentication(token: String): Authentication {
        val firebaseId = firebaseConfig.getIdByToken(token)
        val authenticatedUser: User = if (userRepository.existsByFirebaseId(firebaseId)) {
            userRepository.findByFirebaseId(firebaseId)
        } else {
            User(firebaseId)
        }

        return UsernamePasswordAuthenticationToken(authenticatedUser, null, authenticatedUser.getAuthorities())
    }
}
