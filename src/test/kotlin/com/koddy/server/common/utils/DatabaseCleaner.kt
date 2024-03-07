package com.koddy.server.common.utils

import jakarta.persistence.EntityManager
import jakarta.persistence.PersistenceContext
import jakarta.persistence.Table
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class DatabaseCleaner(
    @PersistenceContext
    private val entityManager: EntityManager,
) {
    private val tableNames: List<String> =
        entityManager.metamodel
            .entities
            .map { it.javaType }
            .map { it.getAnnotation(Table::class.java) }
            .map { it.name }

    @Transactional
    fun cleanUpDatabase() {
        entityManager.clear()
        entityManager.flush()
        entityManager.createNativeQuery("SET foreign_key_checks = 0").executeUpdate()

        for (tableName in tableNames) {
            entityManager.createNativeQuery("TRUNCATE TABLE $tableName").executeUpdate()
        }

        entityManager.createNativeQuery("SET foreign_key_checks = 1").executeUpdate()
    }
}
