package com.wafflestudio.ggzz.domain.user.repository

import com.wafflestudio.ggzz.domain.user.model.User
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, Long> {
    fun findByUsername(username: String): User?
    fun findUserById(id: Long): User?
    fun findMeById(id: Long): User
    fun findByFirebaseId(firebaseId: String): User

    fun existsByFirebaseId(firebaseId: String): Boolean
    fun existsByUsername(username: String): Boolean
}
