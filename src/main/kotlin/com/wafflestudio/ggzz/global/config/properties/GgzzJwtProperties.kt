package com.wafflestudio.ggzz.global.config.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "ggzz.jwt")
data class GgzzJwtProperties (
    val issuer: String,
    val secret: String,
    val expiration: Long,
)