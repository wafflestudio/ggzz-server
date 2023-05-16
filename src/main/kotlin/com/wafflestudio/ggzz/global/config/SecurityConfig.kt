package com.wafflestudio.ggzz.global.config

import com.wafflestudio.ggzz.global.error.CustomAccessDeniedHandler
import com.wafflestudio.ggzz.global.error.CustomEntryPoint
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.factory.PasswordEncoderFactories
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
class SecurityConfig(
    private val customEntryPoint: CustomEntryPoint,
    private val customAccessDeniedHandler: CustomAccessDeniedHandler,
) {

    companion object {
        private val CORS_WHITELIST = listOf(
            "https://wackathon-infp-client.vercel.app",
            "http://localhost:3000"
        )
        private val SWAGGER = arrayOf("/swagger-ui/**", "/swagger-resources/**", "/v3/api-docs/**")
        private val GET_WHITELIST = arrayOf("/ping", "/api/v1/letters/**", "/docs/**")
        private val POST_WHITELIST = arrayOf("/signup", "/login", "/logout")
    }

    @Bean
    fun securityFilterChain(httpSecurity: HttpSecurity): SecurityFilterChain {
        return httpSecurity
            .httpBasic().disable()
            .cors().configurationSource(corsConfigurationSource())
            .and()
            .csrf().disable()
            .logout().disable()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.ALWAYS)
            .and()
            .exceptionHandling()
            .authenticationEntryPoint(customEntryPoint)
            .accessDeniedHandler(customAccessDeniedHandler)
            .and()
            .authorizeHttpRequests()
            .requestMatchers(HttpMethod.GET, *SWAGGER).permitAll()
            .requestMatchers(HttpMethod.GET, *GET_WHITELIST).permitAll()
            .requestMatchers(HttpMethod.POST, *POST_WHITELIST).permitAll()
            .requestMatchers("/api/v1/**").authenticated()
            .requestMatchers(HttpMethod.GET, "/login").authenticated()
            .and()
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
}

