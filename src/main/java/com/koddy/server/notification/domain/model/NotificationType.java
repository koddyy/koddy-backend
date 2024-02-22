package com.koddy.server.notification.domain.model;

import com.koddy.server.coffeechat.domain.model.CoffeeChat;
import com.koddy.server.member.domain.model.mentee.Mentee;
import com.koddy.server.member.domain.model.mentor.Mentor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.format.DateTimeFormatter;

@Getter
@RequiredArgsConstructor
public enum NotificationType {
    // 멘토가 받는 알림
    MENTOR_RECEIVE_MENTOR_FLOW_MENTEE_CANCEL("%s님이 커피챗을 취소했습니다. (취소 사유: %s)", Category.CANCEL_REASON),
    MENTOR_RECEIVE_MENTOR_FLOW_MENTEE_REJECT("%s님이 커피챗을 거절했습니다. (거절 사유: %s)", Category.REJECT_REASON),
    MENTOR_RECEIVE_MENTOR_FLOW_MENTEE_PENDING("%s님이 커피챗을 수락했습니다.", Category.SIMPLE),
    MENTOR_RECEIVE_MENTOR_FLOW_MENTOR_FINALLY_APPROVE("%s님과의 커피챗이 %s로 예정되었습니다.", Category.SCHEDULED),

    MENTOR_RECEIVE_MENTEE_FLOW_MENTEE_APPLY("%s님이 커피챗을 신청했습니다.", Category.SIMPLE),
    MENTOR_RECEIVE_MENTEE_FLOW_MENTEE_CANCEL("%s님이 커피챗을 취소했습니다. (취소 사유: %s)", Category.CANCEL_REASON),
    MENTOR_RECEIVE_MENTEE_FLOW_MENTOR_APPROVE("%s님과의 커피챗이 %s로 예정되었습니다.", Category.SCHEDULED),

    // 멘티가 받는 알림
    MENTEE_RECEIVE_MENTOR_FLOW_MENTOR_SUGGEST("%s님이 커피챗을 제안했습니다.", Category.SIMPLE),
    MENTEE_RECEIVE_MENTOR_FLOW_MENTOR_CANCEL("%s님이 커피챗을 취소했습니다. (취소 사유: %s)", Category.CANCEL_REASON),
    MENTEE_RECEIVE_MENTOR_FLOW_MENTOR_FINALLY_APPROVE("%s님과의 커피챗이 %s로 예정되었습니다.", Category.SCHEDULED),

    MENTEE_RECEIVE_MENTEE_FLOW_MENTOR_CANCEL("%s님이 커피챗을 취소했습니다. (취소 사유: %s)", Category.CANCEL_REASON),
    MENTEE_RECEIVE_MENTEE_FLOW_MENTOR_REJECT("%s님이 커피챗을 거절했습니다. (거절 사유: %s)", Category.REJECT_REASON),
    MENTEE_RECEIVE_MENTEE_FLOW_MENTOR_APPROVE("%s님과의 커피챗이 %s로 예정되었습니다.", Category.SCHEDULED),
    ;

    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy년 MM월 dd일");

    private final String form;
    private final Category category;

    public String completeMentorNotification(
            final Mentee mentee,
            final CoffeeChat coffeeChat
    ) {
        return switch (category) {
            case SIMPLE -> complete(mentee.getName());
            case CANCEL_REASON -> complete(mentee.getName(), coffeeChat.getReason().getCancelReason());
            case REJECT_REASON -> complete(mentee.getName(), coffeeChat.getReason().getRejectReason());
            case SCHEDULED -> complete(mentee.getName(), coffeeChat.getReservedDay().format(dateTimeFormatter));
        };
    }

    public String completeMenteeNotification(
            final Mentor mentor,
            final CoffeeChat coffeeChat
    ) {
        return switch (category) {
            case SIMPLE -> complete(mentor.getName());
            case CANCEL_REASON -> complete(mentor.getName(), coffeeChat.getReason().getCancelReason());
            case REJECT_REASON -> complete(mentor.getName(), coffeeChat.getReason().getRejectReason());
            case SCHEDULED -> complete(mentor.getName(), coffeeChat.getReservedDay().format(dateTimeFormatter));
        };
    }

    private String complete(final Object... arguments) {
        return String.format(form, arguments);
    }

    public enum Category {
        SIMPLE,
        CANCEL_REASON,
        REJECT_REASON,
        SCHEDULED,
    }
}
