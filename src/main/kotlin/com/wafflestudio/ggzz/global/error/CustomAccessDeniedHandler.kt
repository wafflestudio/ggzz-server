package com.wafflestudio.ggzz.global.error

import com.google.gson.FieldNamingPolicy
import com.google.gson.GsonBuilder
import com.wafflestudio.ggzz.global.common.dto.ErrorResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.slf4j.LoggerFactory
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component

@Component
class CustomAccessDeniedHandler: AccessDeniedHandler {

    private val logger = LoggerFactory.getLogger(this::class.java)


    override fun handle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        accessDeniedException: AccessDeniedException?
    ) {

        logger.info("{} {} cookies={} exception={}", request.method, request.requestURI, request.cookies, accessDeniedException?.message)
        logger.info("auth = {}", SecurityContextHolder.getContext().authentication)

        val gson = GsonBuilder().setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES).create()
        response.contentType = "application/json"
        response.characterEncoding = "utf-8"
        response.writer.write(gson.toJson(ErrorResponse(WrongAPIException())))
        response.status = HttpServletResponse.SC_FORBIDDEN
    }
}