package com.wafflestudio.ggzz.global.common.utils

class JwtUtils {
    companion object {
        fun isBearerToken(token: String): Boolean {
            return token.startsWith("Bearer ")
        }

        fun removeBearerPrefix(token: String): String {
            return token.removePrefix("Bearer ")
        }
    }
}