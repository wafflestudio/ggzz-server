package com.wafflestudio.ggzz.domain.user.service

import com.wafflestudio.ggzz.domain.user.dto.UserBasicInfoResponse
import com.wafflestudio.ggzz.domain.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class UserService(
    private val userRepository: UserRepository
) {
    fun getMe(id: Long): UserBasicInfoResponse {
        return userRepository.findUserById(id)!!.let { UserBasicInfoResponse.fromEntity(it) }
    }
}
