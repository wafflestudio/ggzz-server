package com.wafflestudio.ggzz.global.config

import com.wafflestudio.ggzz.domain.auth.handler.CustomAccessDeniedHandler
import com.wafflestudio.ggzz.domain.auth.handler.CustomAuthenticationEntryPoint
import com.wafflestudio.ggzz.domain.auth.model.JwtTokenFilter
import com.wafflestudio.ggzz.domain.auth.model.JwtTokenProvider
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
class SecurityConfig(
    private val jwtTokenProvider: JwtTokenProvider
) {

    companion object {
        private val CORS_WHITELIST = listOf(
            "https://wackathon-infp-client.vercel.app",
            "http://localhost:3000"
        )
        private val GET_WHITELIST = arrayOf("/ping", "/api/v1/letters/**", "/docs/index.html")
        private val POST_WHITELIST = arrayOf("/api/v1/auth/**")
    }

    @Bean
    fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
        return http
            .httpBasic { it.disable() }
            .formLogin { it.disable() }
            .csrf { it.disable() }
            .logout { it.disable() }
            .requestCache { it.disable() }
            .anonymous { it.disable() }
            .cors { cors ->
                cors.configurationSource(corsConfigurationSource())
            }
            .sessionManagement { sessionManagement ->
                sessionManagement
                    .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .exceptionHandling { exceptionHandling ->
                exceptionHandling
                    .authenticationEntryPoint(customAuthenticationEntryPoint())
                    .accessDeniedHandler(customAccessDeniedHandler())
            }
            .authorizeHttpRequests { authorizeRequests ->
                authorizeRequests
                    .requestMatchers(HttpMethod.GET, *GET_WHITELIST).permitAll()
                    .requestMatchers(HttpMethod.POST, *POST_WHITELIST).permitAll()
                    .anyRequest().authenticated()
            }
            .addFilterAt(JwtTokenFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter::class.java)
            .build()
    }

    @Bean
    fun corsConfigurationSource(): CorsConfigurationSource {
        val config = CorsConfiguration()
        config.allowCredentials = true
        config.allowedOriginPatterns = CORS_WHITELIST
        config.addAllowedHeader("*")
        config.addAllowedMethod("*")
        config.addExposedHeader("Authorization")

        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", config)
        return source
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder()
    }

    @Bean
    fun noopAuthenticationManager(): AuthenticationManager {
        return AuthenticationManager { throw IllegalStateException("Authentication Manager is not used.") }
    }

    @Bean
    fun customAuthenticationEntryPoint(): CustomAuthenticationEntryPoint {
        return CustomAuthenticationEntryPoint()
    }

    @Bean
    fun customAccessDeniedHandler(): CustomAccessDeniedHandler {
        return CustomAccessDeniedHandler()
    }

}

