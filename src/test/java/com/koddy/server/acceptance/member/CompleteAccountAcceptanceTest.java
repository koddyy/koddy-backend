package com.koddy.server.acceptance.member;

import com.koddy.server.common.AcceptanceTest;
import com.koddy.server.common.containers.callback.DatabaseCleanerEachCallbackExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static com.koddy.server.acceptance.member.MemberAcceptanceStep.멘토_프로필을_완성시킨다;
import static com.koddy.server.acceptance.member.MemberAcceptanceStep.멘티_프로필을_완성시킨다;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_1;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@ExtendWith(DatabaseCleanerEachCallbackExtension.class)
@DisplayName("[Acceptance Test] 사용자 프로필 완성")
public class CompleteAccountAcceptanceTest extends AcceptanceTest {
    @Nested
    @DisplayName("멘토 프로필 완성 API")
    class CompleteMentor {
        @Test
        @DisplayName("멘토의 프로필을 완성한다 (자기소개, 스케줄)")
        void success() {
            final String accessToken = MENTOR_1.회원가입과_로그인을_진행한다().token().accessToken();
            멘토_프로필을_완성시킨다(MENTOR_1, accessToken)
                    .statusCode(NO_CONTENT.value());
        }
    }

    @Nested
    @DisplayName("멘티 프로필 완성 API")
    class CompleteMentee {
        @Test
        @DisplayName("멘티의 프로필을 완성한다 (자기소개)")
        void success() {
            final String accessToken = MENTEE_1.회원가입과_로그인을_진행한다().token().accessToken();
            멘티_프로필을_완성시킨다(MENTEE_1, accessToken)
                    .statusCode(NO_CONTENT.value());
        }
    }
}
