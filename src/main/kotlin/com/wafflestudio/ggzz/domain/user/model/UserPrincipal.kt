package com.wafflestudio.ggzz.domain.user.model

import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class UserPrincipal(private val user: User): UserDetails {
    private val authorities = listOf(SimpleGrantedAuthority("USER"))

    override fun getAuthorities() = authorities
    override fun getPassword() = user.password
    override fun getUsername() = user.username
    override fun isAccountNonExpired() = true
    override fun isAccountNonLocked() = true
    override fun isCredentialsNonExpired() = true
    override fun isEnabled() = true
    val id = user.id
}