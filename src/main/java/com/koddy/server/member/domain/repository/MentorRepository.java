package com.koddy.server.member.domain.repository;

import com.koddy.server.global.annotation.KoddyWritableTransactional;
import com.koddy.server.member.domain.model.mentor.Mentor;
import com.koddy.server.member.exception.MemberException;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

import static com.koddy.server.member.exception.MemberExceptionCode.MENTOR_NOT_FOUND;

public interface MentorRepository extends JpaRepository<Mentor, Long> {
    default Mentor getById(final Long id) {
        return findById(id)
                .orElseThrow(() -> new MemberException(MENTOR_NOT_FOUND));
    }

    @Query("""
            SELECT m
            FROM Mentor m
            JOIN FETCH m.schedules
            WHERE m.id = :id
            """)
    Optional<Mentor> findProfile(@Param("id") final long id);

    default Mentor getProfile(final long id) {
        return findProfile(id)
                .orElseThrow(() -> new MemberException(MENTOR_NOT_FOUND));
    }

    @KoddyWritableTransactional
    @Modifying
    @Query("DELETE FROM Mentor m WHERE m.id = :id")
    void deleteMentor(@Param("id") final long id);
}
