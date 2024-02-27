package com.koddy.server.member.application.usecase;

import com.koddy.server.common.UnitTest;
import com.koddy.server.common.fixture.MentoringPeriodFixture;
import com.koddy.server.member.application.usecase.query.response.MenteePublicProfile;
import com.koddy.server.member.application.usecase.query.response.MentorPublicProfile;
import com.koddy.server.member.domain.model.Language;
import com.koddy.server.member.domain.model.mentee.Mentee;
import com.koddy.server.member.domain.model.mentor.Mentor;
import com.koddy.server.member.domain.model.mentor.MentoringPeriod;
import com.koddy.server.member.domain.model.mentor.Timeline;
import com.koddy.server.member.domain.repository.MenteeRepository;
import com.koddy.server.member.domain.repository.MentorRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.koddy.server.common.fixture.LanguageFixture.EN_SUB;
import static com.koddy.server.common.fixture.LanguageFixture.JP_SUB;
import static com.koddy.server.common.fixture.LanguageFixture.KR_MAIN;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_1;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static com.koddy.server.common.fixture.TimelineFixture.MON_09_22;
import static com.koddy.server.common.fixture.TimelineFixture.THU_09_22;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@DisplayName("Member -> GetMemberPublicProfileUseCase 테스트")
class GetMemberPublicProfileUseCaseTest extends UnitTest {
    private final MentorRepository mentorRepository = mock(MentorRepository.class);
    private final MenteeRepository menteeRepository = mock(MenteeRepository.class);
    private final GetMemberPublicProfileUseCase sut = new GetMemberPublicProfileUseCase(mentorRepository, menteeRepository);

    @Nested
    @DisplayName("멘토 기본(Public) 프로필 조회")
    class GetMentorPublicProfile {
        @Test
        @DisplayName("멘토 기본(Public) 프로필을 조회한다 - [미완성 :: 프로필 이미지 URL, 자기소개, 멘토링 기간, 스케줄]")
        void uncomplete() {
            // given
            final List<Language> languages = List.of(KR_MAIN.toDomain());
            final Mentor mentor = new Mentor(
                    MENTOR_1.getPlatform(),
                    MENTOR_1.getName(),
                    languages,
                    MENTOR_1.getUniversityProfile()
            ).apply(1L);
            given(mentorRepository.getProfile(mentor.getId())).willReturn(mentor);

            // when
            final MentorPublicProfile mentorProfile = sut.getMentorProfile(mentor.getId());

            // then
            assertAll(
                    // Required
                    () -> assertThat(mentorProfile.id()).isEqualTo(mentor.getId()),
                    () -> assertThat(mentorProfile.name()).isEqualTo(mentor.getName()),
                    () -> assertThat(mentorProfile.languages().main()).isEqualTo(KR_MAIN.getCategory().getCode()),
                    () -> assertThat(mentorProfile.languages().sub()).isEmpty(),
                    () -> assertThat(mentorProfile.school()).isEqualTo(mentor.getUniversityProfile().getSchool()),
                    () -> assertThat(mentorProfile.major()).isEqualTo(mentor.getUniversityProfile().getMajor()),
                    () -> assertThat(mentorProfile.enteredIn()).isEqualTo(mentor.getUniversityProfile().getEnteredIn()),
                    () -> assertThat(mentorProfile.authenticated()).isFalse(),

                    // Optional
                    () -> assertThat(mentorProfile.introduction()).isNull(),
                    () -> assertThat(mentorProfile.profileImageUrl()).isNull()
            );
        }

        @Test
        @DisplayName("멘토 기본(Public) 프로필을 조회한다 - [완성]")
        void complete() {
            // given
            final List<Language> languages = List.of(KR_MAIN.toDomain());
            final MentoringPeriod period = MentoringPeriodFixture.FROM_03_01_TO_05_01.toDomain();
            final List<Timeline> timelines = List.of(MON_09_22.toDomain(), THU_09_22.toDomain());

            final Mentor mentor = MENTOR_1.toDomainWithLanguagesAndMentoringInfo(languages, period, timelines).apply(1L);
            given(mentorRepository.getProfile(mentor.getId())).willReturn(mentor);

            // when
            final MentorPublicProfile mentorProfile = sut.getMentorProfile(mentor.getId());

            // then
            assertAll(
                    // Required
                    () -> assertThat(mentorProfile.id()).isEqualTo(mentor.getId()),
                    () -> assertThat(mentorProfile.name()).isEqualTo(mentor.getName()),
                    () -> assertThat(mentorProfile.languages().main()).isEqualTo(KR_MAIN.getCategory().getCode()),
                    () -> assertThat(mentorProfile.languages().sub()).isEmpty(),
                    () -> assertThat(mentorProfile.school()).isEqualTo(mentor.getUniversityProfile().getSchool()),
                    () -> assertThat(mentorProfile.major()).isEqualTo(mentor.getUniversityProfile().getMajor()),
                    () -> assertThat(mentorProfile.enteredIn()).isEqualTo(mentor.getUniversityProfile().getEnteredIn()),
                    () -> assertThat(mentorProfile.authenticated()).isFalse(),

                    // Optional
                    () -> assertThat(mentorProfile.introduction()).isEqualTo(mentor.getIntroduction()),
                    () -> assertThat(mentorProfile.profileImageUrl()).isEqualTo(mentor.getProfileImageUrl())
            );
        }
    }

    @Nested
    @DisplayName("멘티 기본(Public) 프로필 조회")
    class GetMenteePublicProfile {
        @Test
        @DisplayName("멘티 기본(Public) 프로필을 조회한다 - [미완성 :: 프로필 이미지 URL, 자기소개]")
        void uncomplete() {
            // given
            final List<Language> languages = List.of(KR_MAIN.toDomain(), EN_SUB.toDomain(), JP_SUB.toDomain());
            final Mentee mentee = new Mentee(
                    MENTEE_1.getPlatform(),
                    MENTEE_1.getName(),
                    MENTEE_1.getNationality(),
                    languages,
                    MENTEE_1.getInterest()
            ).apply(1L);
            given(menteeRepository.getProfile(mentee.getId())).willReturn(mentee);

            // when
            final MenteePublicProfile menteeProfile = sut.getMenteeProfile(mentee.getId());

            // then
            assertAll(
                    // Required
                    () -> assertThat(menteeProfile.id()).isEqualTo(mentee.getId()),
                    () -> assertThat(menteeProfile.name()).isEqualTo(mentee.getName()),
                    () -> assertThat(menteeProfile.nationality()).isEqualTo(mentee.getNationality().code),
                    () -> assertThat(menteeProfile.languages().main()).isEqualTo(KR_MAIN.getCategory().getCode()),
                    () -> assertThat(menteeProfile.languages().sub()).containsExactlyInAnyOrder(
                            EN_SUB.getCategory().getCode(),
                            JP_SUB.getCategory().getCode()
                    ),
                    () -> assertThat(menteeProfile.interestSchool()).isEqualTo(mentee.getInterest().getSchool()),
                    () -> assertThat(menteeProfile.interestMajor()).isEqualTo(mentee.getInterest().getMajor()),

                    // Optional
                    () -> assertThat(menteeProfile.introduction()).isNull(),
                    () -> assertThat(menteeProfile.profileImageUrl()).isNull()
            );
        }

        @Test
        @DisplayName("멘티 기본(Public) 프로필을 조회한다 - [완성]")
        void complete() {
            // given
            final List<Language> languages = List.of(KR_MAIN.toDomain(), EN_SUB.toDomain(), JP_SUB.toDomain());
            final Mentee mentee = MENTEE_1.toDomainWithLanguages(languages).apply(1L);
            given(menteeRepository.getProfile(mentee.getId())).willReturn(mentee);

            // when
            final MenteePublicProfile menteeProfile = sut.getMenteeProfile(mentee.getId());

            // then
            assertAll(
                    // Required
                    () -> assertThat(menteeProfile.id()).isEqualTo(mentee.getId()),
                    () -> assertThat(menteeProfile.name()).isEqualTo(mentee.getName()),
                    () -> assertThat(menteeProfile.nationality()).isEqualTo(mentee.getNationality().code),
                    () -> assertThat(menteeProfile.languages().main()).isEqualTo(KR_MAIN.getCategory().getCode()),
                    () -> assertThat(menteeProfile.languages().sub()).containsExactlyInAnyOrder(
                            EN_SUB.getCategory().getCode(),
                            JP_SUB.getCategory().getCode()
                    ),
                    () -> assertThat(menteeProfile.interestSchool()).isEqualTo(mentee.getInterest().getSchool()),
                    () -> assertThat(menteeProfile.interestMajor()).isEqualTo(mentee.getInterest().getMajor()),

                    // Optional
                    () -> assertThat(menteeProfile.introduction()).isEqualTo(mentee.getIntroduction()),
                    () -> assertThat(menteeProfile.profileImageUrl()).isEqualTo(mentee.getProfileImageUrl())
            );
        }
    }
}