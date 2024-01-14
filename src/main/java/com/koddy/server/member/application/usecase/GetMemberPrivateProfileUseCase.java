package com.koddy.server.member.application.usecase;

import com.koddy.server.global.annotation.KoddyReadOnlyTransactional;
import com.koddy.server.global.annotation.UseCase;
import com.koddy.server.member.application.usecase.query.response.MenteeProfile;
import com.koddy.server.member.application.usecase.query.response.MentorProfile;
import com.koddy.server.member.domain.model.mentee.Mentee;
import com.koddy.server.member.domain.model.mentor.Mentor;
import com.koddy.server.member.domain.repository.MenteeRepository;
import com.koddy.server.member.domain.repository.MentorRepository;
import lombok.RequiredArgsConstructor;

@UseCase
@RequiredArgsConstructor
public class GetMemberPrivateProfileUseCase {
    private final MentorRepository mentorRepository;
    private final MenteeRepository menteeRepository;

    @KoddyReadOnlyTransactional
    public MentorProfile getMentorProfile(final long mentorId) {
        final Mentor mentor = mentorRepository.getProfile(mentorId);
        return MentorProfile.of(mentor);
    }

    @KoddyReadOnlyTransactional
    public MenteeProfile getMenteeProfile(final long menteeId) {
        final Mentee mentee = menteeRepository.getProfile(menteeId);
        return MenteeProfile.of(mentee);
    }
}
