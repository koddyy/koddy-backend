package com.koddy.server.member.application.usecase;

import com.koddy.server.global.PageCreator;
import com.koddy.server.global.PageResponse;
import com.koddy.server.global.annotation.KoddyReadOnlyTransactional;
import com.koddy.server.global.annotation.UseCase;
import com.koddy.server.member.application.usecase.query.GetMenteesByCondition;
import com.koddy.server.member.application.usecase.query.response.MenteeSimpleSearchProfile;
import com.koddy.server.member.domain.model.mentee.Mentee;
import com.koddy.server.member.domain.repository.query.MentorMainSearchRepository;
import com.koddy.server.member.domain.repository.query.spec.SearchMenteeCondition;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

@UseCase
@RequiredArgsConstructor
public class MentorMainSearchUseCase {
    private final MentorMainSearchRepository mentorMainSearchRepository;

    @KoddyReadOnlyTransactional
    public List<MenteeSimpleSearchProfile> getAppliedMentees(final long mentorId, final int limit) {
        final List<Mentee> result = mentorMainSearchRepository.fetchAppliedMentees(mentorId, limit);
        return result.stream()
                .map(MenteeSimpleSearchProfile::of)
                .toList();
    }

    @KoddyReadOnlyTransactional
    public PageResponse<List<MenteeSimpleSearchProfile>> getMenteesByCondition(final GetMenteesByCondition query) {
        final SearchMenteeCondition condition = query.toCondition();
        final Pageable pageable = PageCreator.create(query.page());
        final Slice<Mentee> result = mentorMainSearchRepository.fetchMenteesByCondition(condition, pageable);

        return new PageResponse<>(
                result.getContent()
                        .stream()
                        .map(MenteeSimpleSearchProfile::of)
                        .toList(),
                result.hasNext()
        );
    }
}
