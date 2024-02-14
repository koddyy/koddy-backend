package com.koddy.server.global.utils;

import com.koddy.server.common.UnitTest;
import com.koddy.server.member.domain.model.Language;
import com.koddy.server.member.domain.model.Nationality;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.koddy.server.member.domain.model.Language.Category.CN;
import static com.koddy.server.member.domain.model.Language.Category.EN;
import static com.koddy.server.member.domain.model.Language.Category.JP;
import static com.koddy.server.member.domain.model.Language.Category.KR;
import static com.koddy.server.member.domain.model.Language.Category.VN;
import static com.koddy.server.member.domain.model.Nationality.CHINA;
import static com.koddy.server.member.domain.model.Nationality.ETC;
import static com.koddy.server.member.domain.model.Nationality.JAPAN;
import static com.koddy.server.member.domain.model.Nationality.KOREA;
import static com.koddy.server.member.domain.model.Nationality.USA;
import static com.koddy.server.member.domain.model.Nationality.VIETNAM;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Global -> FilteringConverter 테스트")
class FilteringConverterTest extends UnitTest {
    @Test
    @DisplayName("N개의 국적 데이터를 List<Nationality> 도메인으로 변환한다")
    void convertToNationality() {
        // given
        final String value = "KR,EN,CN,JP,VN,ETC";

        // when
        final List<Nationality> result = FilteringConverter.convertToNationality(value);

        // then
        assertThat(result).containsExactly(KOREA, USA, JAPAN, CHINA, VIETNAM, ETC);
    }

    @Test
    @DisplayName("N개의 언어 데이터를 List<Language.Category> 도메인으로 변환한다")
    void convertToLanguage() {
        // given
        final String value = "KR,EN,CN,JP,VN";

        // when
        final List<Language.Category> result = FilteringConverter.convertToLanguage(value);

        // then
        assertThat(result).containsExactly(KR, EN, CN, JP, VN);
    }
}
