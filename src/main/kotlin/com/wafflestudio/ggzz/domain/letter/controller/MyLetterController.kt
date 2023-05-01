package com.wafflestudio.ggzz.domain.letter.controller

import com.wafflestudio.ggzz.domain.letter.dto.LetterDto.Response
import com.wafflestudio.ggzz.domain.letter.service.LetterService
import com.wafflestudio.ggzz.domain.user.model.CurrentUser
import com.wafflestudio.ggzz.global.common.dto.ListResponse
import io.swagger.v3.oas.annotations.Operation
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Validated
@RestController
@RequestMapping("/api/v1/me/letters")
class MyLetterController(
    private val letterService: LetterService
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @Operation(summary = "내 편지 리스트 가져오기")
    @GetMapping
    fun getMyLetters(@CurrentUser userId: Long): ResponseEntity<ListResponse<Response>> {
        logger.info("GET /api/v1/me/letters, user=$userId")
        return ResponseEntity.ok(letterService.getMyLetters(userId))
    }
}