package com.wafflestudio.nostalgia.domain.user.service

import com.wafflestudio.nostalgia.global.error.InvalidTokenException
import com.wafflestudio.nostalgia.global.error.TokenExpiredException
import io.jsonwebtoken.*
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

    fun generateAccessTokenByUsername(username: String): String {
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
        return Jwts.builder()
            .setClaims(claims)
            .setIssuer(issuer)
            .setExpiration(expiryDate)
            .signWith(signingKey, SignatureAlgorithm.HS256)
            .compact()

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

    fun getUsernameFromToken(authToken: String, type: Type): String {
        return try {
            parse(authToken).body["username"] as String
        } catch (e: ExpiredJwtException) {
            throw TokenExpiredException(type.toString().lowercase())
        } catch (e: Exception) {
            throw InvalidTokenException(type.toString().lowercase())
        }
    }

    private fun parse(authToken: String): Jws<Claims> {
        val prefixRemoved = authToken.replace(tokenPrefix, "").trim { it <= ' ' }
        return Jwts
            .parserBuilder()
            .setSigningKey(signingKey)
            .build()
            .parseClaimsJws(prefixRemoved)
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

enum class Type{
    ACCESS, REFRESH
}