package com.wafflestudio.ggzz.domain.letter.controller

import com.wafflestudio.ggzz.domain.letter.dto.LetterDto
import com.wafflestudio.ggzz.domain.letter.service.LetterInteractionService
import com.wafflestudio.ggzz.domain.user.model.CurrentUser
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/v1/letters")
class LetterInteractionController(
    private val letterInteractionService: LetterInteractionService
) {

    @PutMapping("/{letter-id}/like")
    fun likeLetter(
        @CurrentUser userId: Long,
        @PathVariable("letter-id") letterId: Long
    ): ResponseEntity<LetterDto.Response> {
        return ResponseEntity.ok(letterInteractionService.likeLetter(userId, letterId))
    }

    @DeleteMapping("/{letter-id}/like")
    fun unlikeLetter(
        @CurrentUser userId: Long,
        @PathVariable("letter-id") letterId: Long
    ): ResponseEntity<LetterDto.Response> {
        return ResponseEntity.ok(letterInteractionService.unlikeLetter(userId, letterId))
    }

}