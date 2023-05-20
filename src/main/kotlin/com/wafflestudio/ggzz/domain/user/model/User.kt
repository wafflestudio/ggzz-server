package com.wafflestudio.ggzz.domain.user.model

import com.wafflestudio.ggzz.domain.letter.model.Letter
import com.wafflestudio.ggzz.domain.user.dto.UserDto.SignUpRequest
import com.wafflestudio.ggzz.global.common.model.BaseTimeTraceEntity
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

    ) : BaseTimeTraceEntity() {
    constructor(request: SignUpRequest, encodedPassword: String) : this(
        username = request.username!!,
        nickname = request.nickname!!,
        password = encodedPassword
    )
}
