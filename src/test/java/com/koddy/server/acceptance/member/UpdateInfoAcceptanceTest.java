package com.koddy.server.acceptance.member;

import com.koddy.server.common.AcceptanceTest;
import com.koddy.server.common.containers.callback.DatabaseCleanerEachCallbackExtension;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static com.koddy.server.acceptance.member.MemberAcceptanceStep.멘토_기본_정보를_수정한다;
import static com.koddy.server.acceptance.member.MemberAcceptanceStep.멘토_스케줄_정보를_수정한다;
import static com.koddy.server.acceptance.member.MemberAcceptanceStep.멘티_기본_정보를_수정한다;
import static com.koddy.server.auth.exception.AuthExceptionCode.INVALID_PERMISSION;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_1;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_2;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_2;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.NO_CONTENT;

@ExtendWith(DatabaseCleanerEachCallbackExtension.class)
@DisplayName("[Acceptance Test] 사용자 정보 수정")
public class UpdateInfoAcceptanceTest extends AcceptanceTest {
    @Nested
    @DisplayName("멘토 정보 수정 API")
    class UpdateMentorInfo {
        @Nested
        @DisplayName("기본 정보 수정")
        class BasicInfo {
            @Test
            @DisplayName("멘티는 접근 권한이 없다")
            void throwExceptionByInvalidPermission() {
                final String accessToken = MENTEE_1.회원가입_로그인_후_AccessToken을_추출한다();
                멘토_기본_정보를_수정한다(MENTOR_2, accessToken)
                        .statusCode(FORBIDDEN.value())
                        .body("errorCode", is(INVALID_PERMISSION.getErrorCode()))
                        .body("message", is(INVALID_PERMISSION.getMessage()));
            }

            @Test
            @DisplayName("멘토 기본 정보를 수정한다")
            void success() {
                final String accessToken = MENTOR_1.회원가입_로그인_후_AccessToken을_추출한다();
                멘토_기본_정보를_수정한다(MENTOR_2, accessToken)
                        .statusCode(NO_CONTENT.value());
            }
        }

        @Nested
        @DisplayName("스케줄 정보 수정")
        class Schedule {
            @Test
            @DisplayName("멘티는 접근 권한이 없다")
            void throwExceptionByInvalidPermission() {
                final String accessToken = MENTEE_1.회원가입_로그인_후_AccessToken을_추출한다();
                멘토_스케줄_정보를_수정한다(MENTOR_2, accessToken)
                        .statusCode(FORBIDDEN.value())
                        .body("errorCode", is(INVALID_PERMISSION.getErrorCode()))
                        .body("message", is(INVALID_PERMISSION.getMessage()));
            }

            @Test
            @DisplayName("멘토 스케줄 정보를 수정한다")
            void success() {
                final String accessToken = MENTOR_1.회원가입_로그인_후_AccessToken을_추출한다();
                멘토_스케줄_정보를_수정한다(MENTOR_2, accessToken)
                        .statusCode(NO_CONTENT.value());
            }
        }
    }

    @Nested
    @DisplayName("멘티 정보 수정 API")
    class UpdateMenteeInfo {
        @Nested
        @DisplayName("기본 정보 수정")
        class BasicInfo {
            @Test
            @DisplayName("멘토는 접근 권한이 없다")
            void throwExceptionByInvalidPermission() {
                final String accessToken = MENTOR_1.회원가입_로그인_후_AccessToken을_추출한다();
                멘티_기본_정보를_수정한다(MENTEE_2, accessToken)
                        .statusCode(FORBIDDEN.value())
                        .body("errorCode", is(INVALID_PERMISSION.getErrorCode()))
                        .body("message", is(INVALID_PERMISSION.getMessage()));
            }

            @Test
            @DisplayName("멘티 기본 정보를 수정한다")
            void success() {
                final String accessToken = MENTEE_1.회원가입_로그인_후_AccessToken을_추출한다();
                멘티_기본_정보를_수정한다(MENTEE_2, accessToken)
                        .statusCode(NO_CONTENT.value());
            }
        }
    }
}
