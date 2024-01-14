package com.koddy.server.auth.domain.model;

import com.koddy.server.common.ParallelTest;
import com.koddy.server.member.domain.model.Member;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static com.koddy.server.common.utils.TokenUtils.REFRESH_TOKEN;
import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Auth -> 도메인 Aggregate [Token] 테스트")
class TokenTest extends ParallelTest {
    private final Member<?> member = MENTOR_1.toDomain().apply(1L);

    @Test
    @DisplayName("Token을 업데이트한다")
    void updateRefreshToken() {
        // given
        final Token token = new Token(member.getId(), REFRESH_TOKEN);

        // when
        token.updateRefreshToken(REFRESH_TOKEN + "_update");

        // then
        assertThat(token.getRefreshToken()).isEqualTo(REFRESH_TOKEN + "_update");
    }
}
