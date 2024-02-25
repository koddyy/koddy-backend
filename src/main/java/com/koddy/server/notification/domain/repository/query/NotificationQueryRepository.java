package com.koddy.server.notification.domain.repository.query;

import com.koddy.server.notification.domain.repository.query.response.NotificationDetails;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

public interface NotificationQueryRepository {
    Slice<NotificationDetails> fetchMentorNotifications(
            final long mentorId,
            final Pageable pageable
    );

    Slice<NotificationDetails> fetchMenteeNotifications(
            final long menteeId,
            final Pageable pageable
    );
}
