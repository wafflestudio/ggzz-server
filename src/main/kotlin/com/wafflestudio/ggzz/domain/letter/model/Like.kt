package com.wafflestudio.ggzz.domain.letter.model

import com.wafflestudio.ggzz.domain.user.model.User
import com.wafflestudio.ggzz.global.common.model.BaseTimeTraceEntity
import jakarta.persistence.Entity
import jakarta.persistence.OneToOne
import jakarta.persistence.Table

@Entity
@Table(name = "likes")
class Like(
    @OneToOne
    val user: User,
    @OneToOne
    val letter: Letter,
): BaseTimeTraceEntity()