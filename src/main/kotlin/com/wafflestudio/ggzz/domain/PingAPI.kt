package com.wafflestudio.ggzz.domain

import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping

@Controller
class PingAPI {

    private val logger = LoggerFactory.getLogger(this::class.java)

    @GetMapping("/ping")
    fun ping(): ResponseEntity<String> {
        logger.info("GET /ping")
        return ResponseEntity.ok("pong")
    }
}