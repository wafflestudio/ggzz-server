package com.wafflestudio.nostalgia.domain.user.service

import com.wafflestudio.nostalgia.domain.user.dto.UserDto.*
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import io.jsonwebtoken.security.Keys
import org.springframework.http.ResponseCookie
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

@Service
@Transactional(readOnly = true)
@ConfigurationPropertiesScan
class AuthTokenService (
    private val authProperties: AuthProperties,
){
    private val tokenPrefix = "Bearer "
    private val signingKey = Keys.hmacShaKeyFor(authProperties.jwtSecret.toByteArray())

    fun generateAccessTokenByUsername(username: String): AuthToken {
        val claims: Claims = Jwts.claims()
        claims["username"] = username
        val issuer = authProperties.issuer
        val expiryDate: Date = Date.from(
            LocalDateTime
                .now()
                .plusSeconds(authProperties.jwtExpiration)
                .atZone(ZoneId.systemDefault())
                .toInstant()
        )
        val resultToken = Jwts.builder()
            .setClaims(claims)
            .setIssuer(issuer)
            .setExpiration(expiryDate)
            .signWith(signingKey, SignatureAlgorithm.HS256)
            .compact()

        return AuthToken(resultToken)
    }

    fun generateRefreshTokenByUsername(username: String): String {
        val claims: Claims = Jwts.claims()
        claims["username"] = username
        val issuer = authProperties.issuer
        val expiryDate: Date = Date.from(
            LocalDateTime
                .now()
                .plusSeconds(authProperties.refreshExpiration)
                .atZone(ZoneId.systemDefault())
                .toInstant()
        )
        return Jwts.builder()
            .setClaims(claims)
            .setIssuer(issuer)
            .setExpiration(expiryDate)
            .signWith(signingKey, SignatureAlgorithm.HS256)
            .compact()
    }

    fun generateResponseCookie(token: String): ResponseCookie {
        return ResponseCookie.from("refreshToken", token)
            .httpOnly(true)
            .secure(true)
            .sameSite("None")
            .path("/")
            .maxAge(3600)
            .build()
    }


}