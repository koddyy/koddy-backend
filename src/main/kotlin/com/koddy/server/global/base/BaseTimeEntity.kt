package com.koddy.server.global.base

import jakarta.persistence.Column
import jakarta.persistence.MappedSuperclass
import jakarta.persistence.PreUpdate
import java.time.LocalDateTime

@MappedSuperclass
abstract class BaseTimeEntity(
    @Column(name = "created_at", updatable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "last_modified_at")
    var lastModifiedAt: LocalDateTime = LocalDateTime.now(),
) {
    @PreUpdate
    fun preUpdate() {
        lastModifiedAt = LocalDateTime.now()
    }
}
