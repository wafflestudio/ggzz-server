package com.wafflestudio.ggzz.domain.auth.handler

import com.google.gson.Gson
import com.wafflestudio.ggzz.global.common.dto.ErrorResponse
import com.wafflestudio.ggzz.global.common.exception.CustomException
import com.wafflestudio.ggzz.global.common.exception.InternalServerError
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint

class CustomAuthenticationEntryPoint: AuthenticationEntryPoint {
    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException?
    ) {
        val customException = request.getAttribute("exception") as CustomException? ?: InternalServerError("")
        response.contentType = "application/json"
        response.characterEncoding = "utf-8"
        response.writer.write(Gson().toJson(ErrorResponse(customException)))
        response.status = HttpServletResponse.SC_UNAUTHORIZED
    }
}