package com.wafflestudio.ggzz.domain

import com.wafflestudio.ggzz.domain.user.model.User
import com.wafflestudio.ggzz.domain.user.model.UserPrincipal
import com.wafflestudio.ggzz.domain.user.model.UserToken
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
