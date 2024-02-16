package com.koddy.server

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class KoddyApplication

fun main(args: Array<String>) {
    runApplication<KoddyApplication>(*args)
}
