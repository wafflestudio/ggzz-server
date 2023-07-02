package com.wafflestudio.ggzz.domain

import com.wafflestudio.ggzz.domain.user.model.User
import com.wafflestudio.ggzz.domain.user.model.UserPrincipal
import com.wafflestudio.ggzz.domain.user.model.UserToken
import org.springframework.security.core.context.SecurityContext
import org.springframework.security.core.context.SecurityContextImpl
import org.springframework.security.test.context.support.WithSecurityContextFactory

class CustomUserSecurityContextFactory : WithSecurityContextFactory<WithCustomUser> {
    override fun createSecurityContext(customUserAnnotation: WithCustomUser): SecurityContext {
        val user = User(
            firebaseId = customUserAnnotation.firebaseId,
            username = customUserAnnotation.username,
            nickname = customUserAnnotation.nickname,
            password = customUserAnnotation.password
        )
        val userPrincipal = UserPrincipal(user)
        val userToken = UserToken(userPrincipal)

        return SecurityContextImpl().apply { authentication = userToken }
    }
}
