package com.wafflestudio.ggzz.domain.user.service

import com.wafflestudio.ggzz.global.error.InvalidTokenException
import com.wafflestudio.ggzz.global.error.TokenExpiredException
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jws
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

    fun generateTokenByUsername(username: String, type: Type): String {
        val claims: Claims = Jwts.claims()
        claims["username"] = username
        val issuer = authProperties.issuer
        val expiryDate: Date = Date.from(
            LocalDateTime
                .now()
                .plusSeconds(if (type == Type.ACCESS) authProperties.jwtExpiration else authProperties.refreshExpiration)
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
            .maxAge(authProperties.jwtExpiration)
            .build()
    }
}

enum class Type{
    ACCESS, REFRESH
}