package com.wafflestudio.ggzz.domain.auth.model

import com.wafflestudio.ggzz.global.common.utils.JwtUtils
import com.wafflestudio.ggzz.global.config.properties.GgzzJwtProperties
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.security.Keys
import java.time.LocalDateTime
import java.time.ZoneId
import java.util.*

class JwtTokenProvider (
    private val ggzzJwtProperties: GgzzJwtProperties,
) {

    private val signingKey = Keys.hmacShaKeyFor(ggzzJwtProperties.secret.toByteArray())
    private val parser = Jwts.parserBuilder().setSigningKey(signingKey).build()

    fun generateToken(id: Long): String {
        val expiryDate: Date = Date.from(
            LocalDateTime
                .now()
                .plusSeconds(ggzzJwtProperties.expiration)
                .atZone(ZoneId.systemDefault())
                .toInstant()
        )

        return Jwts.builder()
            .setSubject(id.toString())
            .setIssuer(ggzzJwtProperties.issuer)
            .setExpiration(expiryDate)
            .signWith(signingKey, SignatureAlgorithm.HS256)
            .compact()
    }

    fun getIdFromToken(token: String): Long = getAllClaims(token).subject.toLong()

    private fun getAllClaims(token: String): Claims {
        val prefixRemoved = JwtUtils.removeBearerPrefix(token)
        return parser
            .parseClaimsJws(prefixRemoved)
            .body
    }

}
