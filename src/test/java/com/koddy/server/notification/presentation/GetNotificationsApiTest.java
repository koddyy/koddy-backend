package com.koddy.server.notification.presentation;

import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus;
import com.koddy.server.common.ControllerTest;
import com.koddy.server.global.query.SliceResponse;
import com.koddy.server.member.domain.model.mentor.Mentor;
import com.koddy.server.notification.application.usecase.GetNotificationsUseCase;
import com.koddy.server.notification.application.usecase.query.response.NotificationSummary;
import com.koddy.server.notification.application.usecase.query.response.NotifyCoffeeChat;
import com.koddy.server.notification.application.usecase.query.response.NotifyMember;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.SnippetFactory.body;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.SnippetFactory.query;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.createHttpSpecSnippets;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.successDocsWithAccessToken;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.queryParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("Notification -> GetNotificationsApi 테스트")
class GetNotificationsApiTest extends ControllerTest {
    @Autowired
    private GetNotificationsUseCase getNotificationsUseCase;

    private final Mentor mentor = MENTOR_1.toDomain().apply(1L);

    @Nested
    @DisplayName("알림 조회 API [GET /api/notifications/me]")
    class GetNotifications {
        private static final String BASE_URL = "/api/notifications/me";

        @Test
        @DisplayName("알림을 조회한다")
        void success() {
            // given
            applyToken(true, mentor);
            given(getNotificationsUseCase.invoke(any())).willReturn(new SliceResponse<>(
                    List.of(
                            new NotificationSummary(
                                    1L,
                                    false,
                                    LocalDateTime.now(),
                                    new NotifyMember(
                                            1L,
                                            "이름",
                                            "프로필 이미지 URL"
                                    ),
                                    new NotifyCoffeeChat(
                                            1L,
                                            CoffeeChatStatus.MENTEE_APPLY.name(),
                                            "취소 사유..",
                                            "거절 사유..",
                                            LocalDate.of(2024, 3, 1)
                                    )
                            )
                    ),
                    false
            ));

            // when - then
            successfulExecute(
                    getRequestWithAccessToken(BASE_URL, Map.of("page", "1")),
                    status().isOk(),
                    successDocsWithAccessToken("NotificationApi/GetMyNotifications", createHttpSpecSnippets(
                            queryParameters(
                                    query("page", "페이지", "1부터 시작", true)
                            ),
                            responseFields(
                                    body("result[].id", "알림 ID(PK)"),
                                    body("result[].read", "알림 읽음 여부"),
                                    body("result[].createdAt", "알림 생성 시간"),
                                    body("result[].member.id", "사용자 ID(PK)"),
                                    body("result[].member.name", "사용자 이름"),
                                    body("result[].member.profileImageUrl", "사용자 프로필 이미지 URL", "Nullable"),
                                    body("result[].coffeeChat.id", "커피챗 ID(PK)"),
                                    body("result[].coffeeChat.status", "커피챗 상태"),
                                    body("result[].coffeeChat.cancelReason", "커피챗 취소 사유", "Nullable"),
                                    body("result[].coffeeChat.rejectReason", "커피챗 거절 사유", "Nullable"),
                                    body("result[].coffeeChat.reservedDay", "커피챗 예약 날짜", "Nullable"),
                                    body("hasNext", "다음 스크롤 존재 여부")
                            )
                    ))
            );
        }
    }
}
