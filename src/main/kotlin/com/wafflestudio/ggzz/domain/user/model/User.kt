package com.wafflestudio.ggzz.domain.user.model

import UserRole
import com.wafflestudio.ggzz.domain.letter.model.Letter
import com.wafflestudio.ggzz.domain.user.dto.UserDto.SignUpRequest
import com.wafflestudio.ggzz.global.common.model.BaseTimeTraceEntity
import jakarta.persistence.*
import org.springframework.security.core.GrantedAuthority

@Entity
data class User(
    @Column(unique = true)
    val firebaseId: String,
    var username: String?,
    var nickname: String?,
    var password: String?,
    @ElementCollection(targetClass = UserRole::class)
    @Enumerated(EnumType.STRING)
    val roles: Set<UserRole> = setOf(UserRole.USER), // 서버 단에서 수동으로 USER -> ADMIN 변경
    @OneToMany(mappedBy = "user", orphanRemoval = true, cascade = [CascadeType.ALL])
    val letters: MutableList<Letter>? = mutableListOf(),
) : BaseTimeTraceEntity() {
    constructor(firebaseId: String, request: SignUpRequest?, encodedPassword: String?) : this(
        firebaseId = firebaseId,
        username = request?.username,
        nickname = request?.nickname,
        password = encodedPassword,
    )

    // firebaseId만 받는 생성자 추가
    constructor(firebaseId: String) : this(
        firebaseId = firebaseId,
        username = null,
        nickname = null,
        password = null
    )

    fun getAuthorities(): Set<GrantedAuthority> = roles
}
