package com.wafflestudio.ggzz.domain.user.repository

import com.wafflestudio.ggzz.domain.user.model.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {
    fun findUserById(id: Long): User?
    fun findByGgzzId(ggzzId: String): User?
    fun findByFirebaseId(firebaseId: String): User?
    fun findByUsername(username: String): User?
}
