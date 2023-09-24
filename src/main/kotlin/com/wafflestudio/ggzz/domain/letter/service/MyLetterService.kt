package com.wafflestudio.ggzz.domain.letter.service

import com.wafflestudio.ggzz.domain.letter.dto.LetterResponse
import com.wafflestudio.ggzz.domain.letter.repository.LetterRepository
import com.wafflestudio.ggzz.global.common.dto.ListResponse
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class MyLetterService(
    private val letterRepository: LetterRepository
) {
    fun getMyLetters(userId: Long): ListResponse<LetterResponse> {
        return ListResponse(letterRepository.findLettersByUserId(userId).map { LetterResponse(it) })
    }
}