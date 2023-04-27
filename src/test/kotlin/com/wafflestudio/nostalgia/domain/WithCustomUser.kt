package com.wafflestudio.nostalgia.domain

import org.springframework.security.test.context.support.WithSecurityContext

@Retention(AnnotationRetention.RUNTIME)
@WithSecurityContext(factory = CustomUserSecurityContextFactory::class)
annotation class WithCustomUser
