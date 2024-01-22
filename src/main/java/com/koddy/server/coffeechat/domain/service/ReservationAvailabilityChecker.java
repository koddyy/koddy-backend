package com.koddy.server.coffeechat.domain.service;

import com.koddy.server.coffeechat.domain.model.CoffeeChat;
import com.koddy.server.coffeechat.domain.model.Reservation;
import com.koddy.server.coffeechat.domain.repository.CoffeeChatRepository;
import com.koddy.server.member.domain.model.mentor.Mentor;
import com.koddy.server.member.exception.MemberException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.util.List;

import static com.koddy.server.member.exception.MemberExceptionCode.CANNOT_RESERVATION;

@Component
@RequiredArgsConstructor
public class ReservationAvailabilityChecker {
    private final CoffeeChatRepository coffeeChatRepository;

    public void check(final Mentor mentor, final Reservation start, final Reservation end) {
        mentor.validateReservationData(start, end);
        validateAlreadyReservedTime(mentor, start, end);
    }

    private void validateAlreadyReservedTime(final Mentor mentor, final Reservation start, final Reservation end) {
        final List<CoffeeChat> reservedCoffeeChat = coffeeChatRepository.getReservedCoffeeChat(mentor.getId(), start.getYear(), start.getMonth());
        if (alreadyReserved(reservedCoffeeChat, start, end)) {
            throw new MemberException(CANNOT_RESERVATION);
        }
    }

    private boolean alreadyReserved(
            final List<CoffeeChat> reservedCoffeeChat,
            final Reservation start,
            final Reservation end
    ) {
        final List<CoffeeChat> filteringWithStart = reservedCoffeeChat.stream()
                .filter(it -> it.isReservationIncluded(start))
                .toList();
        final List<CoffeeChat> filteringWithEnd = reservedCoffeeChat.stream()
                .filter(it -> it.isReservationIncluded(end))
                .toList();
        return !filteringWithStart.isEmpty() || !filteringWithEnd.isEmpty();
    }
}
