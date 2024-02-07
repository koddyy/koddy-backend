package com.koddy.server.coffeechat.domain.repository.query;

import com.koddy.server.coffeechat.domain.model.CoffeeChat;

import java.util.List;

public interface MentorReservedScheduleQueryRepository {
    List<CoffeeChat> fetchReservedCoffeeChat(final long mentorId, final int year, final int month);
}
