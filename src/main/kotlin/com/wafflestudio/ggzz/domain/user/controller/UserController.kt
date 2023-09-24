package com.wafflestudio.ggzz.domain.user.controller

import com.wafflestudio.ggzz.domain.auth.model.CurrentUserId
import com.wafflestudio.ggzz.domain.user.dto.UserBasicInfoResponse
import com.wafflestudio.ggzz.domain.user.service.UserService
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/users")
class UserController(
    private val userService: UserService
) {

    private val logger = LoggerFactory.getLogger(UserController::class.java)

    @GetMapping("/me")
    fun getMe(@CurrentUserId id: Long): UserBasicInfoResponse {
        logger.info("[{}] GET /api/v1/users/me", id)
        return userService.getMe(id)
    }

}
