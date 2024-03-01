package com.koddy.server.notification.presentation;

import com.koddy.server.common.ApiDocsTest;
import com.koddy.server.member.domain.model.mentor.Mentor;
import com.koddy.server.notification.application.usecase.ReadNotificationUseCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.SnippetFactory.path;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.createHttpSpecSnippets;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.successDocsWithAccessToken;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Notification -> ReadNotificationApi 테스트")
class ReadNotificationApiTest extends ApiDocsTest {
    @Autowired
    private ReadNotificationUseCase readNotificationUseCase;

    private final Mentor mentor = MENTOR_1.toDomain().apply(1L);

    @Nested
    @DisplayName("알림 단건 읽음 처리 API [PATCH /api/notifications/{notificationId}/read]")
    class ReadSingle {
        private static final String BASE_URL = "/api/notifications/{notificationId}/read";

        @Test
        @DisplayName("알림 단건 읽음 처리를 진행한다")
        void success() {
            // given
            applyToken(true, mentor);
            doNothing()
                    .when(readNotificationUseCase)
                    .readSingle(any());

            // when - then
            successfulExecute(
                    patchRequestWithAccessToken(new UrlWithVariables(BASE_URL, 1L)),
                    status().isNoContent(),
                    successDocsWithAccessToken("NotificationApi/ReadProcessing/Single", createHttpSpecSnippets(
                            pathParameters(
                                    path("notificationId", "알림 ID(PK)", true)
                            )
                    ))
            );
        }
    }

    @Nested
    @DisplayName("알림 전체 읽음 처리 API [PATCH /api/notifications/me/read-all]")
    class ReadAll {
        private static final String BASE_URL = "/api/notifications/me/read-all";

        @Test
        @DisplayName("알림 전체 읽음 처리를 진행한다")
        void success() {
            // given
            applyToken(true, mentor);
            doNothing()
                    .when(readNotificationUseCase)
                    .readAll(mentor.getId());

            // when - then
            successfulExecute(
                    patchRequestWithAccessToken(BASE_URL),
                    status().isNoContent(),
                    successDocsWithAccessToken("NotificationApi/ReadProcessing/All")
            );
        }
    }
}
