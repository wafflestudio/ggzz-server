package com.wafflestudio.ggzz.domain.auth.model

import org.springframework.security.core.annotation.AuthenticationPrincipal

@Target(AnnotationTarget.VALUE_PARAMETER)
@Retention(AnnotationRetention.RUNTIME)
@AuthenticationPrincipal(expression="id")
annotation class CurrentUserId(val required: Boolean = true)
