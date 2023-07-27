package com.wafflestudio.ggzz.domain.letter.controller

import com.wafflestudio.ggzz.domain.auth.model.CurrentUserId
import com.wafflestudio.ggzz.domain.letter.dto.LetterCreateRequest
import com.wafflestudio.ggzz.domain.letter.dto.LetterDetailResponse
import com.wafflestudio.ggzz.domain.letter.dto.LetterResponse
import com.wafflestudio.ggzz.domain.letter.service.LetterService
import com.wafflestudio.ggzz.global.common.dto.ListResponse
import jakarta.validation.Valid
import jakarta.validation.constraints.NotNull
import org.hibernate.validator.constraints.Range
import org.slf4j.LoggerFactory
import org.springframework.validation.annotation.Validated
import org.springframework.web.bind.annotation.*

@Validated
@RestController
@RequestMapping("/api/v1/letters")
class LetterController(
    private val letterService: LetterService
) {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @PostMapping
    fun postLetter(
        @CurrentUserId userId: Long,
        @ModelAttribute @Valid request: LetterCreateRequest
    ): LetterDetailResponse {
        logger.info("[{}] POST /api/v1/letters {}", userId, request)
        return letterService.postLetter(userId, request)
    }

    @GetMapping
    fun getLetters(
        @CurrentUserId userId: Long?,
        @RequestParam @NotNull @Range(min = -180, max = 180) longitude: Double?,
        @RequestParam @NotNull @Range(min = -90, max = 90) latitude: Double?,
        @RequestParam(required = false, defaultValue = "200") @Range(min = 1, max = 1000) range: Int?,
    ): ListResponse<LetterResponse> {
        logger.info("[{}] GET /api/v1/letters ?longitude=$longitude & latitude=$latitude & range=$range", userId)
        return letterService.getLetters(longitude!! to latitude!!, range!!)
    }

    @GetMapping("/{id}")
    fun getLetter(
        @CurrentUserId userId: Long?,
        @PathVariable id: Long,
        @RequestParam @NotNull @Range(min = -180, max = 180) longitude: Double?,
        @RequestParam @NotNull @Range(min = -90, max = 90) latitude: Double?,
    ): LetterDetailResponse {
        logger.info("[{}] GET /api/v1/letters/{} ?longitude=$longitude & latitude=$latitude", userId, id)
        return letterService.getLetter(id, longitude!! to latitude!!)
    }

    @DeleteMapping("/{id}")
    fun deleteLetter(
        @CurrentUserId userId: Long,
        @PathVariable id: Long
    ) {
        logger.info("[{}] DELETE /api/v1/letters/{}", userId, id)
        letterService.deleteLetter(userId, id)
    }
}
