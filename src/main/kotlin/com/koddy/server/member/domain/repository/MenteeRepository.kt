package com.koddy.server.member.domain.repository

import com.koddy.server.member.domain.model.mentee.Mentee
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface MenteeRepository : JpaRepository<Mentee, Long> {
    // @Query
    @Query(
        value = """
                SELECT *
                FROM mentee m1
                INNER JOIN member m2 ON m1.id = m2.id
                WHERE m1.id = :id
                """,
        nativeQuery = true,
    )
    fun findByIdWithNative(@Param("id") id: Long): Mentee?

    @Query(
        """
        SELECT m
        FROM Mentee m
        JOIN FETCH m.availableLanguages
        WHERE m.id = :id
        """,
    )
    fun findByIdWithLanguages(@Param("id") id: Long): Mentee?
}
