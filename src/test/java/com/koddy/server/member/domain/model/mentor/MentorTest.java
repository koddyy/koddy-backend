package com.koddy.server.member.domain.model.mentor;

import com.koddy.server.common.ParallelTest;
import com.koddy.server.common.fixture.ScheduleFixture;
import com.koddy.server.member.domain.model.Password;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_2;
import static com.koddy.server.common.utils.EncryptorFactory.getEncryptor;
import static com.koddy.server.member.domain.model.Member.EMPTY;
import static com.koddy.server.member.domain.model.Nationality.ANONYMOUS;
import static com.koddy.server.member.domain.model.RoleType.MENTOR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Member/Mentor -> 도메인 [Mentor] 테스트")
class MentorTest extends ParallelTest {
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
                () -> assertThat(mentor.getChatTimes())
                        .map(ChatTime::getSchedule)
                        .containsExactlyInAnyOrderElementsOf(MENTOR_1.getSchedules())
        );
    }

    @Test
    @DisplayName("Mentor 기본 정보를 수정한다")
    void updateBasicInfo() {
        // given
        final Mentor mentor = MENTOR_1.toDomain().apply(1L);

        // when
        mentor.updateBasicInfo(
                MENTOR_2.getName(),
                MENTOR_2.getProfileImageUrl(),
                MENTOR_2.getLanguages(),
                MENTOR_2.getUniversityProfile().getSchool(),
                MENTOR_2.getUniversityProfile().getMajor(),
                MENTOR_2.getUniversityProfile().getGrade(),
                MENTOR_2.getMeetingUrl(),
                MENTOR_2.getIntroduction()
        );

        // then
        assertAll(
                () -> assertThat(mentor.getName()).isEqualTo(MENTOR_2.getName()),
                () -> assertThat(mentor.getProfileImageUrl()).isEqualTo(MENTOR_2.getProfileImageUrl()),
                () -> assertThat(mentor.getLanguages()).containsExactlyInAnyOrderElementsOf(MENTOR_2.getLanguages()),
                () -> assertThat(mentor.getRoleTypes()).containsExactlyInAnyOrder(MENTOR),
                () -> assertThat(mentor.getUniversityProfile().getSchool()).isEqualTo(MENTOR_2.getUniversityProfile().getSchool()),
                () -> assertThat(mentor.getUniversityProfile().getMajor()).isEqualTo(MENTOR_2.getUniversityProfile().getMajor()),
                () -> assertThat(mentor.getUniversityProfile().getGrade()).isEqualTo(MENTOR_2.getUniversityProfile().getGrade()),
                () -> assertThat(mentor.getMeetingUrl()).isEqualTo(MENTOR_2.getMeetingUrl()),
                () -> assertThat(mentor.getIntroduction()).isEqualTo(MENTOR_2.getIntroduction())
        );
    }

    @Test
    @DisplayName("Mentor 비밀번호를 수정한다")
    void updatePassword() {
        // given
        final Mentor mentor = MENTOR_1.toDomain().apply(1L);
        final Password previous = mentor.getPassword();

        // when
        mentor.updatePassword("MentorUpdate123!@#", getEncryptor());

        // then
        assertAll(
                () -> assertThat(getEncryptor().isHashMatch("MentorUpdate123!@#", previous.getValue())).isFalse(),
                () -> assertThat(getEncryptor().isHashMatch("MentorUpdate123!@#", mentor.getPassword().getValue())).isTrue()
        );
    }

    @Test
    @DisplayName("Mentor 스케줄을 수정한다")
    void updateSchedules() {
        // given
        final Mentor mentor = MENTOR_1.toDomain().apply(1L);

        // when
        final List<Schedule> update = ScheduleFixture.allDays();
        mentor.updateSchedules(update);

        // then
        assertAll(
                () -> assertThat(mentor.getChatTimes())
                        .map(ChatTime::getSchedule)
                        .containsExactlyInAnyOrderElementsOf(update)
        );
    }
}
