package com.wafflestudio.ggzz.domain.letter.model

import com.wafflestudio.ggzz.domain.user.model.User
import com.wafflestudio.ggzz.global.common.model.BaseTimeTraceEntity
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "likes")
class Like(
    @ManyToOne(fetch = FetchType.LAZY)
    val user: User,
    @ManyToOne(fetch = FetchType.LAZY)
    val letter: Letter,
): BaseTimeTraceEntity()