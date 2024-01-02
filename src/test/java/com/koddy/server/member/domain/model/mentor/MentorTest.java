package com.koddy.server.member.domain.model.mentor;

import com.koddy.server.common.ParallelTest;
import com.koddy.server.common.fixture.ScheduleFixture;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_2;
import static com.koddy.server.member.domain.model.Nationality.KOREA;
import static com.koddy.server.member.domain.model.RoleType.MENTOR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Member/Mentor -> 도메인 [Mentor] 테스트")
class MentorTest extends ParallelTest {
    @Nested
    @DisplayName("Mentor 생성")
    class Construct {
        @Test
        @DisplayName("Mentor를 생성한다")
        void success() {
            final Mentor mentorA = MENTOR_1.toDomain();

            assertAll(
                    () -> assertThat(mentorA.getEmail().getValue()).isEqualTo(MENTOR_1.getEmail().getValue()),
                    () -> assertThat(mentorA.getName()).isEqualTo(MENTOR_1.getName()),
                    () -> assertThat(mentorA.getNationality()).isEqualTo(KOREA),
                    () -> assertThat(mentorA.getProfileImageUrl()).isEqualTo(MENTOR_1.getProfileImageUrl()),
                    () -> assertThat(mentorA.getIntroduction()).isEqualTo(MENTOR_1.getIntroduction()),
                    () -> assertThat(mentorA.getLanguages()).containsExactlyInAnyOrderElementsOf(MENTOR_1.getLanguages()),
                    () -> assertThat(mentorA.getRoleTypes()).containsExactlyInAnyOrder(MENTOR),
                    () -> assertThat(mentorA.getUniversityProfile().getSchool()).isEqualTo(MENTOR_1.getUniversityProfile().getSchool()),
                    () -> assertThat(mentorA.getUniversityProfile().getMajor()).isEqualTo(MENTOR_1.getUniversityProfile().getMajor()),
                    () -> assertThat(mentorA.getUniversityProfile().getEnteredIn()).isEqualTo(MENTOR_1.getUniversityProfile().getEnteredIn()),
                    () -> assertThat(mentorA.getChatTimes())
                            .map(ChatTime::getSchedule)
                            .containsExactlyInAnyOrderElementsOf(MENTOR_1.getSchedules())
            );
        }
    }

    @Test
    @DisplayName("Mentor 프로필이 완성되었는지 확인한다 (자기소개, 스케줄)")
    void isProfileComplete() {
        final Mentor mentorA = MENTOR_1.toDomain();
        assertThat(mentorA.isProfileComplete()).isTrue();

        final Mentor mentorB = new Mentor(
                MENTOR_2.getEmail(),
                MENTOR_2.getName(),
                MENTOR_2.getProfileImageUrl(),
                null,
                MENTOR_2.getLanguages(),
                MENTOR_2.getUniversityProfile(),
                ScheduleFixture.allDays()
        );
        assertThat(mentorB.isProfileComplete()).isFalse();

        final Mentor mentorC = new Mentor(
                MENTOR_2.getEmail(),
                MENTOR_2.getName(),
                MENTOR_2.getProfileImageUrl(),
                MENTOR_2.getIntroduction(),
                MENTOR_2.getLanguages(),
                MENTOR_2.getUniversityProfile(),
                List.of()
        );
        assertThat(mentorC.isProfileComplete()).isFalse();
    }

    @Nested
    @DisplayName("Mentor 기본 정보 수정")
    class UpdateBasicInfo {
        @Test
        @DisplayName("Mentor 기본 정보를 수정한다")
        void success() {
            // given
            final Mentor mentor = MENTOR_1.toDomain().apply(1L);

            // when
            mentor.updateBasicInfo(
                    MENTOR_2.getName(),
                    MENTOR_2.getProfileImageUrl(),
                    MENTOR_2.getIntroduction(),
                    MENTOR_2.getLanguages(),
                    MENTOR_2.getUniversityProfile().getSchool(),
                    MENTOR_2.getUniversityProfile().getMajor(),
                    MENTOR_2.getUniversityProfile().getEnteredIn()
            );

            // then
            assertAll(
                    () -> assertThat(mentor.getName()).isEqualTo(MENTOR_2.getName()),
                    () -> assertThat(mentor.getProfileImageUrl()).isEqualTo(MENTOR_2.getProfileImageUrl()),
                    () -> assertThat(mentor.getIntroduction()).isEqualTo(MENTOR_2.getIntroduction()),
                    () -> assertThat(mentor.getLanguages()).containsExactlyInAnyOrderElementsOf(MENTOR_2.getLanguages()),
                    () -> assertThat(mentor.getRoleTypes()).containsExactlyInAnyOrder(MENTOR),
                    () -> assertThat(mentor.getUniversityProfile().getSchool()).isEqualTo(MENTOR_2.getUniversityProfile().getSchool()),
                    () -> assertThat(mentor.getUniversityProfile().getMajor()).isEqualTo(MENTOR_2.getUniversityProfile().getMajor()),
                    () -> assertThat(mentor.getUniversityProfile().getEnteredIn()).isEqualTo(MENTOR_2.getUniversityProfile().getEnteredIn()),
                    () -> assertThat(mentor.getChatTimes())
                            .map(ChatTime::getSchedule)
                            .containsExactlyInAnyOrderElementsOf(MENTOR_1.getSchedules())
            );
        }
    }

    @Nested
    @DisplayName("Mentor 스케줄 수정")
    class UpdateSchedule {
        @Test
        @DisplayName("Mentor 스케줄을 수정한다")
        void success() {
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
}
