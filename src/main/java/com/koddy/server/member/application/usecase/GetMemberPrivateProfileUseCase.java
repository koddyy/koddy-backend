package com.koddy.server.member.application.usecase;

import com.koddy.server.global.annotation.KoddyReadOnlyTransactional;
import com.koddy.server.global.annotation.UseCase;
import com.koddy.server.member.application.usecase.query.response.MenteeProfile;
import com.koddy.server.member.application.usecase.query.response.MentorProfile;
import com.koddy.server.member.domain.repository.MenteeRepository;
import com.koddy.server.member.domain.repository.MentorRepository;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
@KoddyReadOnlyTransactional
public class GetMemberPrivateProfileUseCase {
    private final MentorRepository mentorRepository;
    private final MenteeRepository menteeRepository;

    public MentorProfile getMentorProfile(final Long mentorId) {
        return null;
    }

    public MenteeProfile getMenteeProfile(final Long menteeId) {
        return null;
    }
}
