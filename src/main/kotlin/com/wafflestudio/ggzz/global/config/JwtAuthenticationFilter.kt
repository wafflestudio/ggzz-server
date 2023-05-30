package com.wafflestudio.ggzz.global.config

import com.wafflestudio.ggzz.global.auth.JwtTokenProvider
import io.jsonwebtoken.io.IOException
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.ServletRequest
import jakarta.servlet.ServletResponse
import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.GenericFilterBean

class JwtAuthenticationFilter(private val jwtTokenProvider: JwtTokenProvider): GenericFilterBean() {
    @Throws(IOException::class, ServletException::class)
    override fun doFilter(request: ServletRequest, response: ServletResponse, chain: FilterChain) {
        // 헤더에서 JWT 를 받아옵니다.
        val authToken: String = jwtTokenProvider.resolveToken((request as HttpServletRequest))
        // 유효한 토큰인지 확인합니다.
        if (jwtTokenProvider.validateToken(authToken)) {
            // 토큰이 유효하면 토큰으로부터 유저 정보를 받아옵니다.
            val authentication = jwtTokenProvider.getAuthentication(authToken)
            // SecurityContext 에 Authentication 객체를 저장합니다.
            SecurityContextHolder.getContext().authentication = authentication
        }
        chain.doFilter(request, response)
    }
}