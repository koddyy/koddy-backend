package com.koddy.server.member.application.usecase;

import com.koddy.server.global.annotation.KoddyReadOnlyTransactional;
import com.koddy.server.global.annotation.UseCase;
import com.koddy.server.global.query.PageCreator;
import com.koddy.server.global.query.PageResponse;
import com.koddy.server.global.query.SliceResponse;
import com.koddy.server.member.application.usecase.query.GetAppliedMentees;
import com.koddy.server.member.application.usecase.query.GetMenteesByCondition;
import com.koddy.server.member.application.usecase.query.response.AppliedCoffeeChatsByMenteeResponse;
import com.koddy.server.member.application.usecase.query.response.MenteeSimpleSearchProfile;
import com.koddy.server.member.domain.model.mentee.Mentee;
import com.koddy.server.member.domain.repository.query.MentorMainSearchRepository;
import com.koddy.server.member.domain.repository.query.response.AppliedCoffeeChatsByMentee;
import com.koddy.server.member.domain.repository.query.spec.SearchMenteeCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

@UseCase
public class MentorMainSearchUseCase {
    private final MentorMainSearchRepository mentorMainSearchRepository;

    public MentorMainSearchUseCase(final MentorMainSearchRepository mentorMainSearchRepository) {
        this.mentorMainSearchRepository = mentorMainSearchRepository;
    }

    @KoddyReadOnlyTransactional
    public PageResponse<List<AppliedCoffeeChatsByMenteeResponse>> getAppliedMentees(final GetAppliedMentees query) {
        final Page<AppliedCoffeeChatsByMentee> result = mentorMainSearchRepository.fetchAppliedMentees(query.mentorId(), query.limit());
        return new PageResponse<>(
                result.getContent()
                        .stream()
                        .map(AppliedCoffeeChatsByMenteeResponse::from)
                        .toList(),
                result.getTotalElements(),
                result.hasNext()
        );
    }

    @KoddyReadOnlyTransactional
    public SliceResponse<List<MenteeSimpleSearchProfile>> getMenteesByCondition(final GetMenteesByCondition query) {
        final SearchMenteeCondition condition = query.toCondition();
        final Pageable pageable = PageCreator.create(query.page());
        final Slice<Mentee> result = mentorMainSearchRepository.fetchMenteesByCondition(condition, pageable);

        return new SliceResponse<>(
                result.getContent()
                        .stream()
                        .map(MenteeSimpleSearchProfile::from)
                        .toList(),
                result.hasNext()
        );
    }
}
