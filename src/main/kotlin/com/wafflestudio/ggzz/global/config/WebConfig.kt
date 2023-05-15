package com.wafflestudio.nostalgia.global.config

import com.wafflestudio.nostalgia.domain.user.repository.UserRepository
import com.wafflestudio.nostalgia.domain.user.service.AuthTokenService
import com.wafflestudio.nostalgia.domain.user.service.Type
import com.wafflestudio.nostalgia.global.common.Authenticated
import com.wafflestudio.nostalgia.global.common.UserContext
import com.wafflestudio.nostalgia.global.error.*
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.context.annotation.Configuration
import org.springframework.core.MethodParameter
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.context.request.ServletWebRequest
import org.springframework.web.method.HandlerMethod
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig(
    private val authInterceptor: AuthInterceptor,
) : WebMvcConfigurer {

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(authInterceptor).addPathPatterns("/api/v1/**")
    }
}

@Configuration
class AuthInterceptor(
    private val userRepository: UserRepository,
    private val authTokenService: AuthTokenService,
) : HandlerInterceptor {
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {
        val handlerCasted = (handler as? HandlerMethod) ?: return true
        val needAuthentication = handlerCasted.hasMethodAnnotation(Authenticated::class.java)

        if (needAuthentication) {
            val authToken =
                request.getHeader("Authorization") ?: throw NoTokenException(Type.ACCESS.toString().lowercase())
            val username = authTokenService.getUsernameFromToken(authToken, Type.ACCESS)
            userRepository.findByUsername(username) ?: throw InvalidTokenException(Type.ACCESS.toString().lowercase())
            request.setAttribute("username", username)
        }
        return super.preHandle(request, response, handler)
    }
}