package com.koddy.server

import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class HealthCheckApi {
    @GetMapping("/api/health")
    fun hello(): ResponseEntity<Void> = ResponseEntity.ok().build()
}
