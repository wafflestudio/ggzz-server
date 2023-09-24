package com.wafflestudio.ggzz.domain.letter.controller

import com.wafflestudio.ggzz.domain.auth.model.CurrentUserId
import com.wafflestudio.ggzz.domain.letter.dto.LetterResponse
import com.wafflestudio.ggzz.domain.letter.service.LetterInteractionService
import org.slf4j.LoggerFactory
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/api/v1/letters")
class LetterInteractionController(
    private val letterInteractionService: LetterInteractionService
) {

    private val logger = LoggerFactory.getLogger(LetterInteractionController::class.java)

    @PostMapping("/{letter-id}/like")
    fun likeLetter(
        @CurrentUserId userId: Long,
        @PathVariable("letter-id") letterId: Long
    ): LetterResponse {
        logger.info("[{}] POST /api/v1/letters/{}/like", userId, letterId)
        return letterInteractionService.likeLetter(userId, letterId)
    }

    @DeleteMapping("/{letter-id}/like")
    fun unlikeLetter(
        @CurrentUserId userId: Long,
        @PathVariable("letter-id") letterId: Long
    ): LetterResponse {
        logger.info("[{}] DELETE /api/v1/letters/{}/like", userId, letterId)
        return letterInteractionService.unlikeLetter(userId, letterId)
    }

}