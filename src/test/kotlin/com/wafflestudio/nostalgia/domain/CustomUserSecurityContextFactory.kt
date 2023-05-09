package com.wafflestudio.nostalgia.domain

import com.wafflestudio.nostalgia.domain.user.model.User
import com.wafflestudio.nostalgia.domain.user.model.UserPrincipal
import com.wafflestudio.nostalgia.domain.user.model.UserToken
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.test.context.support.WithSecurityContextFactory

class CustomUserSecurityContextFactory : WithSecurityContextFactory<WithCustomUser> {
    override fun createSecurityContext(customUserAnnotation: WithCustomUser): SecurityContext {
        val user = User(customUserAnnotation.username, customUserAnnotation.nickname, customUserAnnotation.password)
        val userPrincipal = UserPrincipal(user)
        val userToken = UserToken(userPrincipal)

        val context = SecurityContextHolder.createEmptyContext()
        context.authentication = userToken
        return context
    }
}
