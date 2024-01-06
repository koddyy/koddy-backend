package com.koddy.server.member.domain.model.mentee;

import com.koddy.server.common.ParallelTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_1;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_2;
import static com.koddy.server.member.domain.model.RoleType.MENTEE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Member/Mentee -> 도메인 [Mentee] 테스트")
class MenteeTest extends ParallelTest {
    @Nested
    @DisplayName("Mentee 생성")
    class Construct {
        @Test
        @DisplayName("Mentee를 생성한다")
        void success() {
            final Mentee mentee = MENTEE_1.toDomain();

            assertAll(
                    () -> assertThat(mentee.getEmail().getValue()).isEqualTo(MENTEE_1.getEmail().getValue()),
                    () -> assertThat(mentee.getName()).isEqualTo(MENTEE_1.getName()),
                    () -> assertThat(mentee.getNationality()).isEqualTo(MENTEE_1.getNationality()),
                    () -> assertThat(mentee.getProfileImageUrl()).isEqualTo(MENTEE_1.getProfileImageUrl()),
                    () -> assertThat(mentee.getIntroduction()).isEqualTo(MENTEE_1.getIntroduction()),
                    () -> assertThat(mentee.getLanguages()).containsExactlyInAnyOrderElementsOf(MENTEE_1.getLanguages()),
                    () -> assertThat(mentee.getAuthorities()).containsExactlyInAnyOrder(MENTEE.getAuthority()),
                    () -> assertThat(mentee.getInterest().getSchool()).isEqualTo(MENTEE_1.getInterest().getSchool()),
                    () -> assertThat(mentee.getInterest().getMajor()).isEqualTo(MENTEE_1.getInterest().getMajor())
            );
        }
    }

    @Test
    @DisplayName("Mentee 프로필이 완성되었는지 확인한다 (자기소개)")
    void isProfileComplete() {
        final Mentee menteeA = MENTEE_1.toDomain();
        assertThat(menteeA.isProfileComplete()).isTrue();

        final Mentee menteeB = new Mentee(
                MENTEE_2.getEmail(),
                MENTEE_2.getName(),
                MENTEE_2.getProfileImageUrl(),
                MENTEE_2.getNationality(),
                null,
                MENTEE_2.getLanguages(),
                MENTEE_2.getInterest()
        );
        assertThat(menteeB.isProfileComplete()).isFalse();
    }

    @Nested
    @DisplayName("Mentee 기본 정보 수정")
    class UpdateBasicInfo {
        @Test
        @DisplayName("Mentee 기본 정보를 수정한다")
        void success() {
            // given
            final Mentee mentee = MENTEE_1.toDomain().apply(1L);

            // when
            mentee.updateBasicInfo(
                    MENTEE_2.getName(),
                    MENTEE_2.getNationality(),
                    MENTEE_2.getProfileImageUrl(),
                    MENTEE_2.getIntroduction(),
                    MENTEE_2.getLanguages(),
                    MENTEE_2.getInterest().getSchool(),
                    MENTEE_2.getInterest().getMajor()
            );

            // then
            assertAll(
                    () -> assertThat(mentee.getName()).isEqualTo(MENTEE_2.getName()),
                    () -> assertThat(mentee.getNationality()).isEqualTo(MENTEE_2.getNationality()),
                    () -> assertThat(mentee.getProfileImageUrl()).isEqualTo(MENTEE_2.getProfileImageUrl()),
                    () -> assertThat(mentee.getIntroduction()).isEqualTo(MENTEE_2.getIntroduction()),
                    () -> assertThat(mentee.getLanguages()).containsExactlyInAnyOrderElementsOf(MENTEE_2.getLanguages()),
                    () -> assertThat(mentee.getAuthorities()).containsExactlyInAnyOrder(MENTEE.getAuthority()),
                    () -> assertThat(mentee.getInterest().getSchool()).isEqualTo(MENTEE_2.getInterest().getSchool()),
                    () -> assertThat(mentee.getInterest().getMajor()).isEqualTo(MENTEE_2.getInterest().getMajor())
            );
        }
    }
}
