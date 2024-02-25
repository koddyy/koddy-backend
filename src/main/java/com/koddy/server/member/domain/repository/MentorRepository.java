package com.koddy.server.member.domain.repository;

import com.koddy.server.member.domain.model.mentor.Mentor;
import com.koddy.server.member.exception.MemberException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

import static com.koddy.server.member.exception.MemberExceptionCode.MENTOR_NOT_FOUND;

public interface MentorRepository extends JpaRepository<Mentor, Long> {
    default Mentor getById(final Long id) {
        return findById(id)
                .orElseThrow(() -> new MemberException(MENTOR_NOT_FOUND));
    }

    @Query(
            value = """
                    SELECT *
                    FROM mentor m1
                    INNER JOIN member m2 ON m1.id = m2.id
                    WHERE m1.id = :id
                    """,
            nativeQuery = true
    )
    Optional<Mentor> findByIdWithNative(@Param("id") final Long id);

    default Mentor getByIdWithNative(final Long id) {
        return findByIdWithNative(id)
                .orElseThrow(() -> new MemberException(MENTOR_NOT_FOUND));
    }

    @Query("""
            SELECT m
            FROM Mentor m
            LEFT JOIN FETCH m.schedules
            WHERE m.id = :id
            """)
    Optional<Mentor> findByIdWithSchedules(@Param("id") final Long id);

    default Mentor getByIdWithSchedules(final Long id) {
        return findByIdWithSchedules(id)
                .orElseThrow(() -> new MemberException(MENTOR_NOT_FOUND));
    }

    @Query("""
            SELECT m
            FROM Mentor m
            JOIN FETCH m.availableLanguages
            WHERE m.id = :id
            """)
    Optional<Mentor> findProfile(@Param("id") final Long id);

    default Mentor getProfile(final Long id) {
        return findProfile(id)
                .orElseThrow(() -> new MemberException(MENTOR_NOT_FOUND));
    }
}
