package com.wafflestudio.ggzz.domain.user.model

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails

class UserToken(
    private val userPrincipal: UserDetails,
    private val firebaseId: String
): AbstractAuthenticationToken(userPrincipal.authorities) {
    override fun getCredentials() = null
    override fun getPrincipal() = userPrincipal
    override fun isAuthenticated() = true
    fun getFirebaseId(): String = firebaseId
}