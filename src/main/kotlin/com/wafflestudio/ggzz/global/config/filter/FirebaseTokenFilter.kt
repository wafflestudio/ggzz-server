package com.wafflestudio.ggzz.global.config.filter

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseToken
import com.wafflestudio.ggzz.domain.user.model.User
import com.wafflestudio.ggzz.domain.user.repository.UserRepository
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class FirebaseTokenFilter : OncePerRequestFilter() {
    @Autowired
    private lateinit var userRepository: UserRepository

    override fun doFilterInternal(
        request: HttpServletRequest, response: HttpServletResponse, filterChain: FilterChain
    ) {
        val token = extractTokenFromRequest(request)

        if (token != null) {
            val firebaseToken = validateToken(token)
            val authentication = firebaseToken?.let { createAuthentication(it) }
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

    private fun validateToken(token: String): FirebaseToken? {
        try {
            val firebaseAuth = FirebaseAuth.getInstance()
            return firebaseAuth.verifyIdToken(token)
        } catch (e: Exception) {
            println(e.message)
            throw IllegalStateException("The Given FirebaseToken is wrong.")
        }
    }

    private fun createAuthentication(decodedToken: FirebaseToken): Authentication {
        val userId = decodedToken.uid
        val user = User(firebaseId = userId)
        val savedUser = userRepository.save(user)

        return UsernamePasswordAuthenticationToken(savedUser, null, user.getAuthorities())
    }
}
