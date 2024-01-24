package com.koddy.server.member.domain.repository.query;

import com.koddy.server.member.domain.model.mentee.Mentee;
import com.koddy.server.member.domain.repository.query.spec.SearchMenteeCondition;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface MentorMainSearchRepository {
    List<Mentee> fetchAppliedMentees(final long mentorId, final int limit);

    Slice<Mentee> fetchMenteesByCondition(final SearchMenteeCondition condition, final Pageable pageable);
}
