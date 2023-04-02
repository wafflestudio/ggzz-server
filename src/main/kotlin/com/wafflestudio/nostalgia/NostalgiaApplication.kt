package com.wafflestudio.nostalgia

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class NostalgiaApplication

fun main(args: Array<String>) {
    runApplication<NostalgiaApplication>(*args)
}
