package com.koddy.server.member.domain.repository.query;

import com.koddy.server.member.domain.model.mentor.Mentor;
import com.koddy.server.member.domain.repository.query.spec.SearchMentorCondition;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

public interface MenteeMainSearchRepository {
    List<Mentor> fetchSuggestedMentors(final long menteeId, final int limit);

    Slice<Mentor> fetchMentorsByCondition(final SearchMentorCondition condition, final Pageable pageable);
}
