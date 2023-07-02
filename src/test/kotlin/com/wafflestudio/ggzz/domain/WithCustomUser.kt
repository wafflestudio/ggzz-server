package com.wafflestudio.ggzz.domain

import org.springframework.security.test.context.support.WithSecurityContext

@Retention(AnnotationRetention.RUNTIME)
@WithSecurityContext(factory = CustomUserSecurityContextFactory::class)
annotation class WithCustomUser(
    val firebaseId: String = "firebaseId",
    val username: String = "username",
    val nickname: String = "nickname",
    val password: String = "password"
)
