package com.wafflestudio.ggzz.domain.auth.provider

import com.wafflestudio.ggzz.domain.auth.model.Provider
import com.wafflestudio.ggzz.domain.user.model.User

interface ProviderService {
    fun supports(provider: Provider): Boolean
    fun createNewUser(token: String): User
    fun getUser(token: String): User
}