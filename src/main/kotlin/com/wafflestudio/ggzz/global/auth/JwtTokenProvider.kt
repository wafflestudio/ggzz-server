package com.wafflestudio.ggzz.global.auth

import com.wafflestudio.ggzz.global.error.InvalidTokenException
import com.wafflestudio.ggzz.global.error.NoTokenException
import com.wafflestudio.ggzz.global.error.TokenExpiredException
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import io.jsonwebtoken.security.Keys
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseCookie
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.stereotype.Component
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

@Component
@ConfigurationPropertiesScan
class JwtTokenProvider (
    private val authProperties: AuthProperties,
    private val userDetailsService: UserDetailsService,
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
            getAllClaims(authToken)["username"] as String
        } catch (e: ExpiredJwtException) {
            throw TokenExpiredException(type.toString().lowercase())
        } catch (e: Exception) {
            throw InvalidTokenException(type.toString().lowercase())
        }
    }

    private fun getAllClaims(authToken: String): Claims {
        val prefixRemoved = authToken.replace(tokenPrefix, "").trim { it <= ' ' }
        return Jwts
            .parserBuilder()
            .setSigningKey(signingKey)
            .build()
            .parseClaimsJws(prefixRemoved)
            .body
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

    fun resolveToken(request: HttpServletRequest): String {
        return request.getHeader("Authorization") ?: throw NoTokenException(Type.ACCESS.toString().lowercase())
    }

    fun validateToken(authToken: String): Boolean {
        try {
            return getAllClaims(authToken).expiration.after(Date())
        } catch (e: ExpiredJwtException) {
            throw TokenExpiredException(Type.ACCESS.toString().lowercase())
        } catch (e: Exception) {
            throw InvalidTokenException(Type.ACCESS.toString().lowercase())
        }
    }

    fun getAuthentication(authToken: String): Authentication {
        val username = getUsernameFromToken(authToken, Type.ACCESS)
        val userDetails = userDetailsService.loadUserByUsername(username)

        return UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)
    }
}

enum class Type{
    ACCESS, REFRESH
}