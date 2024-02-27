package com.koddy.server.member.application.usecase;

import com.koddy.server.global.annotation.KoddyReadOnlyTransactional;
import com.koddy.server.global.annotation.UseCase;
import com.koddy.server.member.application.usecase.query.response.MenteePublicProfile;
import com.koddy.server.member.application.usecase.query.response.MentorPublicProfile;
import com.koddy.server.member.domain.model.mentee.Mentee;
import com.koddy.server.member.domain.model.mentor.Mentor;
import com.koddy.server.member.domain.repository.MenteeRepository;
import com.koddy.server.member.domain.repository.MentorRepository;

@UseCase
public class GetMemberPublicProfileUseCase {
    private final MentorRepository mentorRepository;
    private final MenteeRepository menteeRepository;

    public GetMemberPublicProfileUseCase(
            final MentorRepository mentorRepository,
            final MenteeRepository menteeRepository
    ) {
        this.mentorRepository = mentorRepository;
        this.menteeRepository = menteeRepository;
    }

    @KoddyReadOnlyTransactional
    public MentorPublicProfile getMentorProfile(final long mentorId) {
        final Mentor mentor = mentorRepository.getProfile(mentorId);
        return MentorPublicProfile.from(mentor);
    }

    @KoddyReadOnlyTransactional
    public MenteePublicProfile getMenteeProfile(final long menteeId) {
        final Mentee mentee = menteeRepository.getProfile(menteeId);
        return MenteePublicProfile.from(mentee);
    }
}
