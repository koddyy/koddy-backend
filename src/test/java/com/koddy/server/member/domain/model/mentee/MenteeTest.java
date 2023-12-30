package com.koddy.server.member.domain.model.mentee;

import com.koddy.server.common.ParallelTest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_1;
import static com.koddy.server.member.domain.model.Member.EMPTY;
import static com.koddy.server.member.domain.model.Nationality.ANONYMOUS;
import static com.koddy.server.member.domain.model.RoleType.MENTEE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Member/Mentee -> 도메인 [Mentee] 테스트")
class MenteeTest extends ParallelTest {
    @Test
    @DisplayName("Mentee를 생성한다")
    void construct() {
        /* 초기 Mentee */
        final Mentee mentee = new Mentee(MENTEE_1.getEmail(), MENTEE_1.getPassword());
        assertAll(
                () -> assertThat(mentee.getEmail().getValue()).isEqualTo(MENTEE_1.getEmail().getValue()),
                () -> assertThat(mentee.getPassword()).isNotNull(),
                () -> assertThat(mentee.getName()).isEqualTo(EMPTY),
                () -> assertThat(mentee.getNationality()).isEqualTo(ANONYMOUS),
                () -> assertThat(mentee.getProfileImageUrl()).isEqualTo(EMPTY),
                () -> assertThat(mentee.getAvailableLanguages()).isEmpty(),
                () -> assertThat(mentee.getRoleTypes()).containsExactlyInAnyOrder(MENTEE),
                () -> assertThat(mentee.getInterest().getSchool()).isEqualTo(EMPTY),
                () -> assertThat(mentee.getInterest().getMajor()).isEqualTo(EMPTY)
        );

        /* complete Mentee */
        mentee.complete(
                MENTEE_1.getName(),
                MENTEE_1.getNationality(),
                MENTEE_1.getProfileImageUrl(),
                MENTEE_1.getLanguages(),
                MENTEE_1.getInterest()
        );
        assertAll(
                () -> assertThat(mentee.getEmail().getValue()).isEqualTo(MENTEE_1.getEmail().getValue()),
                () -> assertThat(mentee.getPassword()).isNotNull(),
                () -> assertThat(mentee.getName()).isEqualTo(MENTEE_1.getName()),
                () -> assertThat(mentee.getNationality()).isEqualTo(MENTEE_1.getNationality()),
                () -> assertThat(mentee.getProfileImageUrl()).isEqualTo(MENTEE_1.getProfileImageUrl()),
                () -> assertThat(mentee.getLanguages()).containsExactlyInAnyOrderElementsOf(MENTEE_1.getLanguages()),
                () -> assertThat(mentee.getRoleTypes()).containsExactlyInAnyOrder(MENTEE),
                () -> assertThat(mentee.getInterest().getSchool()).isEqualTo(MENTEE_1.getInterest().getSchool()),
                () -> assertThat(mentee.getInterest().getMajor()).isEqualTo(MENTEE_1.getInterest().getMajor())
        );
    }
}
