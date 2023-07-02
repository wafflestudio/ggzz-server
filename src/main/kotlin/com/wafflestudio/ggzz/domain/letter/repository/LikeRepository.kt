package com.wafflestudio.ggzz.domain.letter.repository;

import com.wafflestudio.ggzz.domain.letter.model.Like
import org.springframework.data.jpa.repository.JpaRepository

interface LikeRepository : JpaRepository<Like, Long> {
    fun findLikeByUserIdAndLetterId(userId: Long, letterId: Long): Like?
}