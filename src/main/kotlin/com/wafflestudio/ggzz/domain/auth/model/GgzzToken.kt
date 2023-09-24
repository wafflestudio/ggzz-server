package com.wafflestudio.ggzz.domain.auth.model

import org.springframework.security.authentication.AbstractAuthenticationToken
import org.springframework.security.core.userdetails.UserDetails

class GgzzToken(
    private val userPrincipal: UserDetails
) : AbstractAuthenticationToken(userPrincipal.authorities) {
    override fun getCredentials() = null
    override fun getPrincipal() = userPrincipal
    override fun isAuthenticated() = true

    companion object {
        fun of(id: Long) = GgzzToken(GgzzUserPrincipal(id))
    }

}