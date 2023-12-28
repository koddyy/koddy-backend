package com.koddy.server.member.domain.model.mentor;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static com.koddy.server.member.domain.model.Nationality.ANONYMOUS;
import static com.koddy.server.member.domain.model.RoleType.MENTOR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Member/Mentor -> 도메인 [Mentor] 테스트")
class MentorTest {
    private static final String EMPTY = "EMPTY";

    @Test
    @DisplayName("Mentor를 생성한다")
    void construct() {
        /* 초기 Mentor */
        final Mentor mentor = new Mentor(MENTOR_1.getEmail(), MENTOR_1.getPassword());
        assertAll(
                () -> assertThat(mentor.getEmail().getValue()).isEqualTo(MENTOR_1.getEmail().getValue()),
                () -> assertThat(mentor.getPassword()).isNotNull(),
                () -> assertThat(mentor.getName()).isEqualTo(EMPTY),
                () -> assertThat(mentor.getNationality()).isEqualTo(ANONYMOUS),
                () -> assertThat(mentor.getProfileImageUrl()).isEqualTo(EMPTY),
                () -> assertThat(mentor.getAvailableLanguages()).isEmpty(),
                () -> assertThat(mentor.getRoleTypes()).containsExactlyInAnyOrder(MENTOR),
                () -> assertThat(mentor.getUniversityProfile().getSchool()).isEqualTo(EMPTY),
                () -> assertThat(mentor.getUniversityProfile().getMajor()).isEqualTo(EMPTY),
                () -> assertThat(mentor.getUniversityProfile().getGrade()).isEqualTo(0),
                () -> assertThat(mentor.getMeetingUrl()).isEqualTo(EMPTY),
                () -> assertThat(mentor.getIntroduction()).isEqualTo(EMPTY),
                () -> assertThat(mentor.getChatTimes()).isEmpty()
        );

        /* complete Mentor */
        mentor.complete(
                MENTOR_1.getName(),
                MENTOR_1.getNationality(),
                MENTOR_1.getProfileImageUrl(),
                MENTOR_1.getLanguages(),
                MENTOR_1.getUniversityProfile(),
                MENTOR_1.getMeetingUrl(),
                MENTOR_1.getIntroduction(),
                MENTOR_1.getSchedules()
        );
        assertAll(
                () -> assertThat(mentor.getEmail().getValue()).isEqualTo(MENTOR_1.getEmail().getValue()),
                () -> assertThat(mentor.getPassword()).isNotNull(),
                () -> assertThat(mentor.getName()).isEqualTo(MENTOR_1.getName()),
                () -> assertThat(mentor.getNationality()).isEqualTo(MENTOR_1.getNationality()),
                () -> assertThat(mentor.getProfileImageUrl()).isEqualTo(MENTOR_1.getProfileImageUrl()),
                () -> assertThat(mentor.getLanguages()).containsExactlyInAnyOrderElementsOf(MENTOR_1.getLanguages()),
                () -> assertThat(mentor.getRoleTypes()).containsExactlyInAnyOrder(MENTOR),
                () -> assertThat(mentor.getUniversityProfile().getSchool()).isEqualTo(MENTOR_1.getUniversityProfile().getSchool()),
                () -> assertThat(mentor.getUniversityProfile().getMajor()).isEqualTo(MENTOR_1.getUniversityProfile().getMajor()),
                () -> assertThat(mentor.getUniversityProfile().getGrade()).isEqualTo(MENTOR_1.getUniversityProfile().getGrade()),
                () -> assertThat(mentor.getMeetingUrl()).isEqualTo(MENTOR_1.getMeetingUrl()),
                () -> assertThat(mentor.getIntroduction()).isEqualTo(MENTOR_1.getIntroduction()),
                () -> assertThat(mentor.getChatTimes()).hasSize(MENTOR_1.getSchedules().size())
        );
    }
}
