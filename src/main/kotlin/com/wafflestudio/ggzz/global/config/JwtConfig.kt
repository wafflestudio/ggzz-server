package com.wafflestudio.ggzz.global.config

import com.wafflestudio.ggzz.domain.auth.model.JwtTokenProvider
import com.wafflestudio.ggzz.global.config.properties.GgzzJwtProperties
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationPropertiesScan
class JwtConfig {

    @Bean
    fun jwtTokenProvider(ggzzJwtProperties: GgzzJwtProperties): JwtTokenProvider {
        return JwtTokenProvider(ggzzJwtProperties)
    }

}