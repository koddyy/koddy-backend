package com.koddy.server.global.utils;

import com.koddy.server.common.UnitTest;
import com.koddy.server.global.exception.GlobalException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.koddy.server.global.exception.GlobalExceptionCode.NOT_PROVIDED_UNIV_DOMAIN;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@DisplayName("Member/Mentor -> 도메인 [UniversityInfo] 테스트")
class UniversityInfoTest extends UnitTest {
    @Test
    @DisplayName("파악할 수 없는 대학교 도메인은 검증할 수 없다")
    void throwExceptionByNotProvidedUnivDomain() {
        assertAll(
                () -> assertThatThrownBy(() -> UniversityInfo.validateDomain("sjiwon@kyonggi.edu"))
                        .isInstanceOf(GlobalException.class)
                        .hasMessage(NOT_PROVIDED_UNIV_DOMAIN.getMessage()),
                () -> assertThatThrownBy(() -> UniversityInfo.validateDomain("sjiwon@kgu.edu"))
                        .isInstanceOf(GlobalException.class)
                        .hasMessage(NOT_PROVIDED_UNIV_DOMAIN.getMessage()),
                () -> assertThatThrownBy(() -> UniversityInfo.validateDomain("sjiwon@kaya.edu"))
                        .isInstanceOf(GlobalException.class)
                        .hasMessage(NOT_PROVIDED_UNIV_DOMAIN.getMessage()),
                () -> assertThatThrownBy(() -> UniversityInfo.validateDomain("gachon@snu.edu"))
                        .isInstanceOf(GlobalException.class)
                        .hasMessage(NOT_PROVIDED_UNIV_DOMAIN.getMessage())
        );
    }

    @Test
    @DisplayName("대학교 도메인을 검증한다")
    void success() {
        assertAll(
                () -> assertDoesNotThrow(() -> UniversityInfo.validateDomain("sjiwon@kyonggi.ac.kr")),
                () -> assertDoesNotThrow(() -> UniversityInfo.validateDomain("sjiwon@kgu.ac.kr")),
                () -> assertDoesNotThrow(() -> UniversityInfo.validateDomain("sjiwon@kaya.ac.kr")),
                () -> assertDoesNotThrow(() -> UniversityInfo.validateDomain("gachon@snu.ac.kr"))
        );
    }
}
