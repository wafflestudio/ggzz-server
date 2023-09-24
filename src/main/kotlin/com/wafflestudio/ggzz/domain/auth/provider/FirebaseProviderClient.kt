package com.wafflestudio.ggzz.domain.auth.provider

import com.google.firebase.auth.FirebaseAuth
import com.wafflestudio.ggzz.domain.auth.exception.DuplicateFirebaseIdException
import com.wafflestudio.ggzz.domain.auth.model.Provider
import com.wafflestudio.ggzz.domain.auth.model.Provider.FIREBASE
import com.wafflestudio.ggzz.domain.user.exception.UserIdNotFoundException
import com.wafflestudio.ggzz.domain.user.model.User
import com.wafflestudio.ggzz.domain.user.repository.UserRepository
import com.wafflestudio.ggzz.global.common.utils.JwtUtils.Companion.removeBearerPrefix
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Propagation
import org.springframework.transaction.annotation.Transactional

@Service
class FirebaseProviderService(
    private val firebaseAuth: FirebaseAuth,
    private val userRepository: UserRepository,
): ProviderService {

    override fun supports(provider: Provider): Boolean {
        return provider == FIREBASE
    }

    @Transactional(propagation = Propagation.MANDATORY)
    override fun createNewUser(token: String): User {
        return firebaseAuth.getUser(removeBearerPrefix(token)).let {
            val user = User(
                ggzzId = null,
                firebaseId = it.uid,
                username = it.displayName,
                password = null,
            )

            userRepository.findByFirebaseId(user.firebaseId!!)?.let { throw DuplicateFirebaseIdException(user.firebaseId!!) }

            userRepository.save(user)
        }
    }

    @Transactional(propagation = Propagation.MANDATORY, readOnly = true)
    override fun getUser(token: String): User {
        val firebaseId = firebaseAuth.getUser(removeBearerPrefix(token)).uid
        return userRepository.findByFirebaseId(firebaseId) ?: throw UserIdNotFoundException(FIREBASE, firebaseId)
    }

}