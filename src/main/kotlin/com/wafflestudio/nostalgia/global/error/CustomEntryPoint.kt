package com.wafflestudio.nostalgia.global.error

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.wafflestudio.nostalgia.global.common.dto.ErrorResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component

@Component
class CustomEntryPoint: AuthenticationEntryPoint {

    private val logger = LoggerFactory.getLogger(this::class.java)

    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException?
    ) {
        logger.info("{} {} cookies={} exception={}", request.method, request.requestURI, request.cookies, authException?.message)
        logger.info("auth = {}", SecurityContextHolder.getContext().authentication)

        val gson = GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create()
        response.contentType = "application/json"
        response.characterEncoding = "utf-8"
        response.writer.write(gson.toJson(ErrorResponse(NotLoggedInException())))
        response.status = HttpServletResponse.SC_UNAUTHORIZED
    }
}