package com.wafflestudio.ggzz.domain.letter.model

import com.wafflestudio.ggzz.domain.letter.dto.LetterCreateRequest
import com.wafflestudio.ggzz.domain.user.model.User
import com.wafflestudio.ggzz.global.common.model.BaseTimeTraceEntity
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.ManyToOne
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@Entity
class Letter(
    @ManyToOne(fetch = FetchType.LAZY)
    val user: User,
    val longitude: Double,
    val latitude: Double,
    var text: String?,
    var image: String?,
    var voice: String?,
    var numberOfLikes: Int = 0,
    val viewableTime: Int = 0,
    val viewRange: Int = 0
) : BaseTimeTraceEntity() {
    constructor(user: User, request: LetterCreateRequest) : this(
        user = user,
        longitude = request.longitude!!,
        latitude = request.latitude!!,
        text = request.text,
        image = null,
        voice = null,
        viewableTime = request.viewableTime!!,
        viewRange = request.viewRange!!
    ) {
        user.letters.add(this)
    }

    fun isViewable(): Boolean {
        return this.viewableTime == 0 || ChronoUnit.HOURS.between(
            this.createdAt,
            LocalDateTime.now()
        ) <= this.viewableTime
    }
}
