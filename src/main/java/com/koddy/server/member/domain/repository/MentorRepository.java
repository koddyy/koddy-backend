package com.koddy.server.member.domain.repository;

import com.koddy.server.member.domain.model.mentor.Mentor;
import com.koddy.server.member.exception.MemberException;
import org.springframework.data.jpa.repository.JpaRepository;

import static com.koddy.server.member.exception.MemberExceptionCode.MENTOR_NOT_FOUND;

public interface MentorRepository extends JpaRepository<Mentor, Long> {
    default Mentor getById(final Long id) {
        return findById(id)
                .orElseThrow(() -> new MemberException(MENTOR_NOT_FOUND));
    }
}
