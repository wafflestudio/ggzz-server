package com.wafflestudio.ggzz.domain.letter.repository

import com.wafflestudio.ggzz.domain.letter.model.Letter
import org.springframework.data.jpa.repository.JpaRepository

interface LetterRepository : JpaRepository<Letter, Long> {
    fun findLetterById(id: Long): Letter?
    fun findLettersByUserId(userId: Long): List<Letter>
    fun findAllByIsViewableTrueAndViewableTimeNot(value: Int): List<Letter>
}
