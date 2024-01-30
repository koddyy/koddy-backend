package com.koddy.server.member.domain.repository.query;

import com.koddy.server.member.domain.model.mentor.Mentor;
import com.koddy.server.member.domain.repository.query.spec.SearchMentorCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface MenteeMainSearchRepository {
    Page<Mentor> fetchSuggestedMentors(final long menteeId, final int limit);

    Slice<Mentor> fetchMentorsByCondition(final SearchMentorCondition condition, final Pageable pageable);
}
