package com.wafflestudio.ggzz.domain.auth.handler

import com.google.gson.Gson
import com.wafflestudio.ggzz.domain.auth.exception.WrongAPIException
import com.wafflestudio.ggzz.global.common.dto.ErrorResponse
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler

class CustomAccessDeniedHandler: AccessDeniedHandler {
    override fun handle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        accessDeniedException: AccessDeniedException?
    ) {
        response.contentType = "application/json"
        response.characterEncoding = "utf-8"
        response.writer.write(Gson().toJson(ErrorResponse(WrongAPIException())))
        response.status = HttpServletResponse.SC_FORBIDDEN
    }
}