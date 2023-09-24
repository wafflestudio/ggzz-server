package com.wafflestudio.ggzz.domain.auth.model

import com.wafflestudio.ggzz.domain.auth.exception.InvalidTokenException
import com.wafflestudio.ggzz.domain.auth.exception.NoTokenException
import com.wafflestudio.ggzz.global.common.utils.JwtUtils
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter

class JwtTokenFilter(
    private val jwtTokenProvider: JwtTokenProvider
): OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        request.getHeader(HttpHeaders.AUTHORIZATION)?.let { token ->
            try {
                val id = jwtTokenProvider.getIdFromToken(JwtUtils.removeBearerPrefix(token))
                val authentication = GgzzToken.of(id)

                SecurityContextHolder.clearContext()
                SecurityContextHolder.getContext().authentication = authentication

            } catch (e: Exception) {
                request.setAttribute("exception", InvalidTokenException())
            }
        } ?: run {
            request.setAttribute("exception", NoTokenException())
        }

        filterChain.doFilter(request, response)
    }
}