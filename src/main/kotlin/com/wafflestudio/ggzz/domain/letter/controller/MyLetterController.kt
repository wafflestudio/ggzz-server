package com.wafflestudio.ggzz.domain.letter.controller

import com.wafflestudio.ggzz.domain.auth.model.CurrentUserId
import com.wafflestudio.ggzz.domain.letter.dto.LetterResponse
import com.wafflestudio.ggzz.domain.letter.service.MyLetterService
import com.wafflestudio.ggzz.global.common.dto.ListResponse
import org.slf4j.LoggerFactory
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Validated
@RestController
@RequestMapping("/api/v1/me/letters")
class MyLetterController(
    private val myLetterService: MyLetterService
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @GetMapping
    fun getMyLetters(@CurrentUserId userId: Long): ListResponse<LetterResponse> {
        logger.info("[{}] GET /api/v1/me/letters", userId)
        return myLetterService.getMyLetters(userId)
    }
}