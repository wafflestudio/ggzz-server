package com.wafflestudio.nostalgia.domain.user.model

import com.wafflestudio.nostalgia.domain.letter.model.Letter
import com.wafflestudio.nostalgia.domain.user.dto.UserDto.SignUpRequest
import com.wafflestudio.nostalgia.global.common.model.BaseTimeTraceEntity
import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.OneToMany

@Entity
class User(
    @Column(unique = true)
    val username: String,
    val nickname: String,
    val password: String,

    @OneToMany(mappedBy = "user", orphanRemoval = true, cascade = [CascadeType.ALL])
    val letters: MutableList<Letter> = mutableListOf(),

    ): BaseTimeTraceEntity() {
    constructor(request: SignUpRequest, encodedPassword: String): this(
        username = request.username!!,
        nickname = request.nickname!!,
        password = encodedPassword
    )
}