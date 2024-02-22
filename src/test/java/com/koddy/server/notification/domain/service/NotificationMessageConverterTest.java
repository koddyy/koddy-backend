package com.koddy.server.notification.domain.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.koddy.server.common.UnitTest;
import com.koddy.server.notification.domain.model.NotificationMessage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("Notification -> NotificationMessageConverter 테스트")
class NotificationMessageConverterTest extends UnitTest {
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final NotificationMessageConverter sut = new NotificationMessageConverter(objectMapper);

    @Test
    @DisplayName("NotificationMessage 데이터를 String 메시지로 변환한다")
    void toFlatMessage() {
        // given
        final NotificationMessage messageA = NotificationMessage.from("사용자A");
        final NotificationMessage messageB = NotificationMessage.of("사용자B", "거절..");

        // when
        final String convertA = sut.toFlatMessage(messageA);
        final String convertB = sut.toFlatMessage(messageB);

        // then
        assertAll(
                () -> assertThat(convertA).isEqualTo("{\"from\":\"사용자A\",\"rejectReason\":null}"),
                () -> assertThat(convertB).isEqualTo("{\"from\":\"사용자B\",\"rejectReason\":\"거절..\"}")
        );
    }

    @Test
    @DisplayName("String 메시지를 NotificationMessage 데이터로 변환한다")
    void toNotificationMessage() {
        // given
        final String messageA = "{\"from\":\"사용자A\",\"rejectReason\":null}";
        final String messageB = "{\"from\":\"사용자B\",\"rejectReason\":\"거절..\"}";

        // when
        final NotificationMessage convertA = sut.toNotificationMessage(messageA);
        final NotificationMessage convertB = sut.toNotificationMessage(messageB);

        // then
        assertAll(
                () -> assertThat(convertA.from()).isEqualTo("사용자A"),
                () -> assertThat(convertA.rejectReason()).isNull(),
                () -> assertThat(convertB.from()).isEqualTo("사용자B"),
                () -> assertThat(convertB.rejectReason()).isEqualTo("거절..")
        );
    }
}
