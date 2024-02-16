package com.koddy.server.global.utils;

import com.koddy.server.auth.domain.model.Authenticated;
import com.koddy.server.common.UnitTest;
import com.koddy.server.global.exception.GlobalException;
import com.koddy.server.member.domain.model.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static com.koddy.server.global.exception.GlobalExceptionCode.NOT_PROVIDED_UNIV_DOMAIN;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DisplayName("Member/Mentor -> 도메인 [UniversityInfo] 테스트")
class UniversityInfoTest extends UnitTest {
    private final Member<?> member = MENTOR_1.toDomain().apply(1L);
    private final Authenticated authenticated = new Authenticated(member.getId(), member.getAuthority());

    @Test
    @DisplayName("파악할 수 없는 대학교 도메인은 검증할 수 없다")
    void throwExceptionByNotProvidedUnivDomain() {
        assertAll(
                () -> assertThatThrownBy(() -> UniversityInfo.validateDomain(authenticated, "sjiwon@kyonggi.edu"))
                        .isInstanceOf(GlobalException.class)
                        .hasMessage(NOT_PROVIDED_UNIV_DOMAIN.getMessage()),
                () -> assertThatThrownBy(() -> UniversityInfo.validateDomain(authenticated, "sjiwon@kgu.edu"))
                        .isInstanceOf(GlobalException.class)
                        .hasMessage(NOT_PROVIDED_UNIV_DOMAIN.getMessage()),
                () -> assertThatThrownBy(() -> UniversityInfo.validateDomain(authenticated, "sjiwon@kaya.edu"))
                        .isInstanceOf(GlobalException.class)
                        .hasMessage(NOT_PROVIDED_UNIV_DOMAIN.getMessage()),
                () -> assertThatThrownBy(() -> UniversityInfo.validateDomain(authenticated, "gachon@snu.edu"))
                        .isInstanceOf(GlobalException.class)
                        .hasMessage(NOT_PROVIDED_UNIV_DOMAIN.getMessage())
        );
    }

    @Test
    @DisplayName("대학교 도메인을 검증한다")
    void success() {
        assertAll(
                () -> assertDoesNotThrow(() -> UniversityInfo.validateDomain(authenticated, "sjiwon@kyonggi.ac.kr")),
                () -> assertDoesNotThrow(() -> UniversityInfo.validateDomain(authenticated, "sjiwon@kgu.ac.kr")),
                () -> assertDoesNotThrow(() -> UniversityInfo.validateDomain(authenticated, "sjiwon@kaya.ac.kr")),
                () -> assertDoesNotThrow(() -> UniversityInfo.validateDomain(authenticated, "gachon@snu.ac.kr"))
        );
    }
}
