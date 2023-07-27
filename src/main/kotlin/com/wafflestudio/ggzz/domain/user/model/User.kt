package com.wafflestudio.ggzz.domain.user.model

import com.wafflestudio.ggzz.domain.letter.model.Letter
import com.wafflestudio.ggzz.global.common.model.BaseTimeTraceEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.OneToMany

@Entity
class User(
    @Column(unique = true)
    var ggzzId: String?,

    @Column(unique = true)
    var firebaseId: String?,

    @Column(unique = true)
    var username: String,

    var password: String?,

    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY)
    val letters: MutableList<Letter> = mutableListOf(),

    ) : BaseTimeTraceEntity()
