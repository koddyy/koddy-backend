package com.koddy.server.member.application.usecase;

import com.koddy.server.global.annotation.KoddyReadOnlyTransactional;
import com.koddy.server.global.annotation.UseCase;
import com.koddy.server.global.query.PageCreator;
import com.koddy.server.global.query.PageResponse;
import com.koddy.server.global.query.SliceResponse;
import com.koddy.server.member.application.usecase.query.GetMentorsByCondition;
import com.koddy.server.member.application.usecase.query.GetSuggestedMentors;
import com.koddy.server.member.application.usecase.query.response.MentorSimpleSearchProfile;
import com.koddy.server.member.domain.model.mentor.Mentor;
import com.koddy.server.member.domain.repository.query.MenteeMainSearchRepository;
import com.koddy.server.member.domain.repository.query.spec.SearchMentorCondition;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

@UseCase
@RequiredArgsConstructor
public class MenteeMainSearchUseCase {
    private final MenteeMainSearchRepository menteeMainSearchRepository;

    @KoddyReadOnlyTransactional
    public PageResponse<List<MentorSimpleSearchProfile>> getSuggestedMentors(final GetSuggestedMentors query) {
        final Page<Mentor> result = menteeMainSearchRepository.fetchSuggestedMentors(query.menteeId(), query.limit());
        return new PageResponse<>(
                result.stream()
                        .map(MentorSimpleSearchProfile::of)
                        .toList(),
                result.getTotalElements(),
                result.hasNext()
        );
    }

    @KoddyReadOnlyTransactional
    public SliceResponse<List<MentorSimpleSearchProfile>> getMentorsByCondition(final GetMentorsByCondition query) {
        final SearchMentorCondition condition = query.toCondition();
        final Pageable pageable = PageCreator.create(query.page());
        final Slice<Mentor> result = menteeMainSearchRepository.fetchMentorsByCondition(condition, pageable);

        return new SliceResponse<>(
                result.getContent()
                        .stream()
                        .map(MentorSimpleSearchProfile::of)
                        .toList(),
                result.hasNext()
        );
    }
}
