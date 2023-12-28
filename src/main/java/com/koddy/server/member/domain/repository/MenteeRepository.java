package com.koddy.server.member.domain.repository;

import com.koddy.server.member.domain.model.mentee.Mentee;
import com.koddy.server.member.exception.MemberException;
import org.springframework.data.jpa.repository.JpaRepository;

import static com.koddy.server.member.exception.MemberExceptionCode.MENTEE_NOT_FOUND;

public interface MenteeRepository extends JpaRepository<Mentee, Long> {
    default Mentee getById(final Long id) {
        return findById(id)
                .orElseThrow(() -> new MemberException(MENTEE_NOT_FOUND));
    }
}
