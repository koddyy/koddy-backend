package com.koddy.server.acceptance.coffeechat;

import com.koddy.server.common.AcceptanceTest;
import com.koddy.server.common.containers.callback.DatabaseCleanerEachCallbackExtension;
import com.koddy.server.common.containers.callback.RedisCleanerEachCallbackExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static com.koddy.server.acceptance.coffeechat.CoffeeChatAcceptanceStep.자동_생성한_커피챗_링크를_삭제한다;
import static com.koddy.server.acceptance.coffeechat.CoffeeChatAcceptanceStep.커피챗_링크를_자동_생성한다;
import static com.koddy.server.auth.exception.AuthExceptionCode.INVALID_PERMISSION;
import static com.koddy.server.coffeechat.exception.CoffeeChatExceptionCode.INVALID_MEETING_LINK_PROVIDER;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_1;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NO_CONTENT;
import static org.springframework.http.HttpStatus.OK;

@ExtendWith({
        DatabaseCleanerEachCallbackExtension.class,
        RedisCleanerEachCallbackExtension.class
})
@DisplayName("[Acceptance Test] 커피챗 링크 생성/삭제")
public class ManageMeetingLinkAcceptanceTest extends AcceptanceTest {
    @Nested
    @DisplayName("커피챗 링크 생성 API")
    class Create {
        @Test
        @DisplayName("멘티는 커피챗 링크 관련한 API에 권한이 없다")
        void throwExceptionByInvalidPermission() {
            final String accessToken = MENTEE_1.회원가입과_로그인을_진행한다().token().accessToken();
            커피챗_링크를_자동_생성한다("zoom", accessToken)
                    .statusCode(FORBIDDEN.value())
                    .body("errorCode", is(INVALID_PERMISSION.getErrorCode()))
                    .body("message", is(INVALID_PERMISSION.getMessage()));
        }

        @Test
        @DisplayName("제공하지 않는 Provider에 대한 커피챗 링크는 생성할 수 없다")
        void throwExceptionByInvalidMeetingLinkProvider() {
            final String accessToken = MENTOR_1.회원가입과_로그인을_진행한다().token().accessToken();
            커피챗_링크를_자동_생성한다("kakao", accessToken)
                    .statusCode(BAD_REQUEST.value())
                    .body("errorCode", is(INVALID_MEETING_LINK_PROVIDER.getErrorCode()))
                    .body("message", is(INVALID_MEETING_LINK_PROVIDER.getMessage()));
        }


        @Test
        @DisplayName("줌을 통해서 커피챗 링크를 생성한다")
        void successWithZoom() {
            final String accessToken = MENTOR_1.회원가입과_로그인을_진행한다().token().accessToken();
            커피챗_링크를_자동_생성한다("zoom", accessToken)
                    .statusCode(OK.value())
                    .body("id", notNullValue(String.class))
                    .body("hostEmail", notNullValue(String.class))
                    .body("topic", notNullValue(String.class))
                    .body("joinUrl", notNullValue(String.class))
                    .body("duration", notNullValue(Long.class));
        }
    }

    @Nested
    @DisplayName("커피챗 링크 삭제 API")
    class Delete {
        @Test
        @DisplayName("자동 생성한 커피챗 링크를 삭제한다")
        void success() {
            final String accessToken = MENTOR_1.회원가입과_로그인을_진행한다().token().accessToken();
            커피챗_링크를_자동_생성한다("zoom", accessToken);
            자동_생성한_커피챗_링크를_삭제한다("zoom", "12345678", accessToken)
                    .statusCode(NO_CONTENT.value());
        }
    }
}
