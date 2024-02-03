package com.koddy.server.member.application.usecase;

import com.koddy.server.global.annotation.KoddyReadOnlyTransactional;
import com.koddy.server.global.annotation.UseCase;
import com.koddy.server.member.application.usecase.query.response.MenteeBasicProfile;
import com.koddy.server.member.application.usecase.query.response.MentorBasicProfile;
import com.koddy.server.member.domain.model.mentee.Mentee;
import com.koddy.server.member.domain.model.mentor.Mentor;
import com.koddy.server.member.domain.repository.MenteeRepository;
import com.koddy.server.member.domain.repository.MentorRepository;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class GetMemberBasicProfileUseCase {
    private final MentorRepository mentorRepository;
    private final MenteeRepository menteeRepository;

    @KoddyReadOnlyTransactional
    public MentorBasicProfile getMentorProfile(final long mentorId) {
        final Mentor mentor = mentorRepository.getProfile(mentorId);
        return MentorBasicProfile.from(mentor);
    }

    @KoddyReadOnlyTransactional
    public MenteeBasicProfile getMenteeProfile(final long menteeId) {
        final Mentee mentee = menteeRepository.getProfile(menteeId);
        return MenteeBasicProfile.from(mentee);
    }
}
