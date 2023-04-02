package com.wafflestudio.nostalgia.domain.user.model

import org.springframework.security.core.annotation.AuthenticationPrincipal

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@AuthenticationPrincipal(expression="id")
annotation class CurrentUser(val required: Boolean = true)
