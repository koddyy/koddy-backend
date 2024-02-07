package com.koddy.server.global.utils;

import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus;
import com.koddy.server.common.UnitTest;
import com.koddy.server.member.domain.model.Language;
import com.koddy.server.member.domain.model.Nationality;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTEE_APPLY;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTEE_APPLY_COFFEE_CHAT_COMPLETE;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTEE_CANCEL;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTEE_PENDING;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTEE_REJECT;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_APPROVE;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_CANCEL;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_FINALLY_APPROVE;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_FINALLY_REJECT;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_REJECT;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_SUGGEST;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_SUGGEST_COFFEE_CHAT_COMPLETE;
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

    @Test
    @DisplayName("N개의 MenteeFlow 커피챗 상태 데이터를 List<CoffeeChatStatus> 도메인으로 변환한다")
    void convertToMenteeFlowCoffeeChatStatus() {
        // given
        final String value = "APPLY,CANCEL,REJECT,APPROVE,COMPLETE";

        // when
        final List<CoffeeChatStatus> result = FilteringConverter.convertToMenteeFlowCoffeeChatStatus(value);

        // then
        assertThat(result).containsExactlyInAnyOrder(
                MENTEE_APPLY,
                MENTEE_CANCEL,
                MENTOR_REJECT,
                MENTOR_APPROVE,
                MENTEE_APPLY_COFFEE_CHAT_COMPLETE
        );
    }

    @Test
    @DisplayName("N개의 MentorFlow 커피챗 상태 데이터를 List<CoffeeChatStatus> 도메인으로 변환한다")
    void convertToMentorFlowCoffeeChatStatus() {
        // given
        final String value = "SUGGEST,CANCEL,REJECT,PENDING,APPROVE,COMPLETE";

        // when
        final List<CoffeeChatStatus> result = FilteringConverter.convertToMentorFlowCoffeeChatStatus(value);

        // then
        assertThat(result).containsExactlyInAnyOrder(
                MENTOR_SUGGEST,
                MENTOR_CANCEL,
                MENTEE_REJECT,
                MENTEE_PENDING,
                MENTOR_FINALLY_REJECT,
                MENTOR_FINALLY_APPROVE,
                MENTOR_SUGGEST_COFFEE_CHAT_COMPLETE
        );
    }
}
