package com.wafflestudio.nostalgia.domain.letter.model

import com.wafflestudio.nostalgia.domain.letter.dto.LetterDto.CreateRequest
import com.wafflestudio.nostalgia.domain.user.model.User
import com.wafflestudio.nostalgia.global.common.model.BaseTimeTraceEntity
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.ManyToOne

@Entity
class Letter(
    @ManyToOne(fetch = FetchType.LAZY)
    val user: User,
    val title: String,
    val summary: String,
    val longitude: Double,
    val latitude: Double,
    var text: String?,
    var image: String?,
    var voice: String?,
): BaseTimeTraceEntity() {
    constructor(user: User, request: CreateRequest) : this(
        user = user,
        title = request.title!!,
        summary = request.summary!!,
        longitude = request.longitude!!,
        latitude = request.latitude!!,
        text = request.text,
        image = null,
        voice = null,
    ) {
        user.letters.add(this)
    }
}