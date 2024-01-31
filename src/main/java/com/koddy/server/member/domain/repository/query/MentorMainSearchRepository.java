package com.koddy.server.member.domain.repository.query;

import com.koddy.server.member.domain.model.mentee.Mentee;
import com.koddy.server.member.domain.repository.query.spec.SearchMenteeCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface MentorMainSearchRepository {
    Page<Mentee> fetchAppliedMentees(final long mentorId, final int limit);

    Slice<Mentee> fetchMenteesByCondition(final SearchMenteeCondition condition, final Pageable pageable);
}
