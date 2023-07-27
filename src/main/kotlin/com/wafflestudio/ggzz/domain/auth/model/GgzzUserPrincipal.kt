package com.wafflestudio.ggzz.domain.auth.model

import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails


class GgzzUserPrincipal(val id: Long) : UserDetails {
    private val authorities = listOf(SimpleGrantedAuthority("USER"))

    override fun getAuthorities() = authorities
    override fun getPassword() = null
    override fun getUsername() = null
    override fun isAccountNonExpired() = true
    override fun isAccountNonLocked() = true
    override fun isCredentialsNonExpired() = true
    override fun isEnabled() = true
}


