package com.koddy.server.member.domain.repository

import com.koddy.server.member.domain.model.mentor.Mentor
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface MentorRepository : JpaRepository<Mentor, Long> {
    // @Query
    @Query(
        value = """
                SELECT *
                FROM mentor m1
                INNER JOIN member m2 ON m1.id = m2.id
                WHERE m1.id = :id
                """,
        nativeQuery = true,
    )
    fun findByIdWithNative(@Param("id") id: Long): Mentor?

    @Query(
        """
        SELECT m
        FROM Mentor m
        LEFT JOIN FETCH m.schedules
        WHERE m.id = :id
        """,
    )
    fun findByIdWithSchedules(@Param("id") id: Long): Mentor?

    @Query(
        """
        SELECT m
        FROM Mentor m
        JOIN FETCH m.availableLanguages
        WHERE m.id = :id
        """,
    )
    fun findByIdWithLanguages(@Param("id") id: Long): Mentor?
}
