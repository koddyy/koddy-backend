package com.koddy.server.common.fixture;

import com.koddy.server.coffeechat.domain.model.CoffeeChat;
import com.koddy.server.coffeechat.domain.model.Reservation;
import com.koddy.server.coffeechat.domain.model.Strategy;
import com.koddy.server.member.domain.model.mentee.Mentee;
import com.koddy.server.member.domain.model.mentor.Mentor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;

import static com.koddy.server.common.fixture.StrategyFixture.KAKAO_ID;

@Getter
@RequiredArgsConstructor
public enum CoffeeChatFixture {
    월요일_1주차_20_00_시작(mentoring(1, 20, 0), mentoring(1, 20, 30), KAKAO_ID.toDomain()),
    월요일_1주차_20_30_시작(mentoring(1, 20, 30), mentoring(1, 21, 0), KAKAO_ID.toDomain()),
    월요일_1주차_21_00_시작(mentoring(1, 21, 0), mentoring(1, 21, 30), KAKAO_ID.toDomain()),
    월요일_1주차_21_30_시작(mentoring(1, 21, 30), mentoring(1, 22, 0), KAKAO_ID.toDomain()),
    월요일_2주차_20_00_시작(mentoring(8, 20, 0), mentoring(8, 20, 30), KAKAO_ID.toDomain()),
    월요일_2주차_20_30_시작(mentoring(8, 20, 30), mentoring(8, 21, 0), KAKAO_ID.toDomain()),
    월요일_2주차_21_00_시작(mentoring(8, 21, 0), mentoring(8, 21, 30), KAKAO_ID.toDomain()),
    월요일_2주차_21_30_시작(mentoring(8, 21, 30), mentoring(8, 22, 0), KAKAO_ID.toDomain()),
    월요일_3주차_20_00_시작(mentoring(15, 20, 0), mentoring(15, 20, 30), KAKAO_ID.toDomain()),
    월요일_3주차_20_30_시작(mentoring(15, 20, 30), mentoring(15, 21, 0), KAKAO_ID.toDomain()),
    월요일_3주차_21_00_시작(mentoring(15, 21, 0), mentoring(15, 21, 30), KAKAO_ID.toDomain()),
    월요일_3주차_21_30_시작(mentoring(15, 21, 30), mentoring(15, 22, 0), KAKAO_ID.toDomain()),
    월요일_4주차_20_00_시작(mentoring(22, 20, 0), mentoring(22, 20, 30), KAKAO_ID.toDomain()),
    월요일_4주차_20_30_시작(mentoring(22, 20, 30), mentoring(22, 21, 0), KAKAO_ID.toDomain()),
    월요일_4주차_21_00_시작(mentoring(22, 21, 0), mentoring(22, 21, 30), KAKAO_ID.toDomain()),
    월요일_4주차_21_30_시작(mentoring(22, 21, 30), mentoring(22, 22, 0), KAKAO_ID.toDomain()),

    화요일_1주차_20_00_시작(mentoring(2, 20, 0), mentoring(2, 20, 30), KAKAO_ID.toDomain()),
    화요일_1주차_20_30_시작(mentoring(2, 20, 30), mentoring(2, 21, 0), KAKAO_ID.toDomain()),
    화요일_1주차_21_00_시작(mentoring(2, 21, 0), mentoring(2, 21, 30), KAKAO_ID.toDomain()),
    화요일_1주차_21_30_시작(mentoring(2, 21, 30), mentoring(2, 22, 0), KAKAO_ID.toDomain()),
    화요일_2주차_20_00_시작(mentoring(9, 20, 0), mentoring(9, 20, 30), KAKAO_ID.toDomain()),
    화요일_2주차_20_30_시작(mentoring(9, 20, 30), mentoring(9, 21, 0), KAKAO_ID.toDomain()),
    화요일_2주차_21_00_시작(mentoring(9, 21, 0), mentoring(9, 21, 30), KAKAO_ID.toDomain()),
    화요일_2주차_21_30_시작(mentoring(9, 21, 30), mentoring(9, 22, 0), KAKAO_ID.toDomain()),
    화요일_3주차_20_00_시작(mentoring(16, 20, 0), mentoring(16, 20, 30), KAKAO_ID.toDomain()),
    화요일_3주차_20_30_시작(mentoring(16, 20, 30), mentoring(16, 21, 0), KAKAO_ID.toDomain()),
    화요일_3주차_21_00_시작(mentoring(16, 21, 0), mentoring(16, 21, 30), KAKAO_ID.toDomain()),
    화요일_3주차_21_30_시작(mentoring(16, 21, 30), mentoring(16, 22, 0), KAKAO_ID.toDomain()),
    화요일_4주차_20_00_시작(mentoring(23, 20, 0), mentoring(23, 20, 30), KAKAO_ID.toDomain()),
    화요일_4주차_20_30_시작(mentoring(23, 20, 30), mentoring(23, 21, 0), KAKAO_ID.toDomain()),
    화요일_4주차_21_00_시작(mentoring(23, 21, 0), mentoring(23, 21, 30), KAKAO_ID.toDomain()),
    화요일_4주차_21_30_시작(mentoring(23, 21, 30), mentoring(23, 22, 0), KAKAO_ID.toDomain()),

    수요일_1주차_20_00_시작(mentoring(3, 20, 0), mentoring(3, 20, 30), KAKAO_ID.toDomain()),
    수요일_1주차_20_30_시작(mentoring(3, 20, 30), mentoring(3, 21, 0), KAKAO_ID.toDomain()),
    수요일_1주차_21_00_시작(mentoring(3, 21, 0), mentoring(3, 21, 30), KAKAO_ID.toDomain()),
    수요일_1주차_21_30_시작(mentoring(3, 21, 30), mentoring(3, 22, 0), KAKAO_ID.toDomain()),
    수요일_2주차_20_00_시작(mentoring(10, 20, 0), mentoring(10, 20, 30), KAKAO_ID.toDomain()),
    수요일_2주차_20_30_시작(mentoring(10, 20, 30), mentoring(10, 21, 0), KAKAO_ID.toDomain()),
    수요일_2주차_21_00_시작(mentoring(10, 21, 0), mentoring(10, 21, 30), KAKAO_ID.toDomain()),
    수요일_2주차_21_30_시작(mentoring(10, 21, 30), mentoring(10, 22, 0), KAKAO_ID.toDomain()),
    수요일_3주차_20_00_시작(mentoring(17, 20, 0), mentoring(17, 20, 30), KAKAO_ID.toDomain()),
    수요일_3주차_20_30_시작(mentoring(17, 20, 30), mentoring(17, 21, 0), KAKAO_ID.toDomain()),
    수요일_3주차_21_00_시작(mentoring(17, 21, 0), mentoring(17, 21, 30), KAKAO_ID.toDomain()),
    수요일_3주차_21_30_시작(mentoring(17, 21, 30), mentoring(17, 22, 0), KAKAO_ID.toDomain()),
    수요일_4주차_20_00_시작(mentoring(24, 20, 0), mentoring(24, 20, 30), KAKAO_ID.toDomain()),
    수요일_4주차_20_30_시작(mentoring(24, 20, 30), mentoring(24, 21, 0), KAKAO_ID.toDomain()),
    수요일_4주차_21_00_시작(mentoring(24, 21, 0), mentoring(24, 21, 30), KAKAO_ID.toDomain()),
    수요일_4주차_21_30_시작(mentoring(24, 21, 30), mentoring(24, 22, 0), KAKAO_ID.toDomain()),

    목요일_1주차_20_00_시작(mentoring(4, 20, 0), mentoring(4, 20, 30), KAKAO_ID.toDomain()),
    목요일_1주차_20_30_시작(mentoring(4, 20, 30), mentoring(4, 21, 0), KAKAO_ID.toDomain()),
    목요일_1주차_21_00_시작(mentoring(4, 21, 0), mentoring(4, 21, 30), KAKAO_ID.toDomain()),
    목요일_1주차_21_30_시작(mentoring(4, 21, 30), mentoring(4, 22, 0), KAKAO_ID.toDomain()),
    목요일_2주차_20_00_시작(mentoring(11, 20, 0), mentoring(11, 20, 30), KAKAO_ID.toDomain()),
    목요일_2주차_20_30_시작(mentoring(11, 20, 30), mentoring(11, 21, 0), KAKAO_ID.toDomain()),
    목요일_2주차_21_00_시작(mentoring(11, 21, 0), mentoring(11, 21, 30), KAKAO_ID.toDomain()),
    목요일_2주차_21_30_시작(mentoring(11, 21, 30), mentoring(11, 22, 0), KAKAO_ID.toDomain()),
    목요일_3주차_20_00_시작(mentoring(18, 20, 0), mentoring(18, 20, 30), KAKAO_ID.toDomain()),
    목요일_3주차_20_30_시작(mentoring(18, 20, 30), mentoring(18, 21, 0), KAKAO_ID.toDomain()),
    목요일_3주차_21_00_시작(mentoring(18, 21, 0), mentoring(18, 21, 30), KAKAO_ID.toDomain()),
    목요일_3주차_21_30_시작(mentoring(18, 21, 30), mentoring(18, 22, 0), KAKAO_ID.toDomain()),
    목요일_4주차_20_00_시작(mentoring(25, 20, 0), mentoring(25, 20, 30), KAKAO_ID.toDomain()),
    목요일_4주차_20_30_시작(mentoring(25, 20, 30), mentoring(25, 21, 0), KAKAO_ID.toDomain()),
    목요일_4주차_21_00_시작(mentoring(25, 21, 0), mentoring(25, 21, 30), KAKAO_ID.toDomain()),
    목요일_4주차_21_30_시작(mentoring(25, 21, 30), mentoring(25, 22, 0), KAKAO_ID.toDomain()),

    금요일_1주차_20_00_시작(mentoring(5, 20, 0), mentoring(5, 20, 30), KAKAO_ID.toDomain()),
    금요일_1주차_20_30_시작(mentoring(5, 20, 30), mentoring(5, 21, 0), KAKAO_ID.toDomain()),
    금요일_1주차_21_00_시작(mentoring(5, 21, 0), mentoring(5, 21, 30), KAKAO_ID.toDomain()),
    금요일_1주차_21_30_시작(mentoring(5, 21, 30), mentoring(5, 22, 0), KAKAO_ID.toDomain()),
    금요일_2주차_20_00_시작(mentoring(12, 20, 0), mentoring(12, 20, 30), KAKAO_ID.toDomain()),
    금요일_2주차_20_30_시작(mentoring(12, 20, 30), mentoring(12, 21, 0), KAKAO_ID.toDomain()),
    금요일_2주차_21_00_시작(mentoring(12, 21, 0), mentoring(12, 21, 30), KAKAO_ID.toDomain()),
    금요일_2주차_21_30_시작(mentoring(12, 21, 30), mentoring(12, 22, 0), KAKAO_ID.toDomain()),
    금요일_3주차_20_00_시작(mentoring(19, 20, 0), mentoring(19, 20, 30), KAKAO_ID.toDomain()),
    금요일_3주차_20_30_시작(mentoring(19, 20, 30), mentoring(19, 21, 0), KAKAO_ID.toDomain()),
    금요일_3주차_21_00_시작(mentoring(19, 21, 0), mentoring(19, 21, 30), KAKAO_ID.toDomain()),
    금요일_3주차_21_30_시작(mentoring(19, 21, 30), mentoring(19, 22, 0), KAKAO_ID.toDomain()),
    금요일_4주차_20_00_시작(mentoring(26, 20, 0), mentoring(26, 20, 30), KAKAO_ID.toDomain()),
    금요일_4주차_20_30_시작(mentoring(26, 20, 30), mentoring(26, 21, 0), KAKAO_ID.toDomain()),
    금요일_4주차_21_00_시작(mentoring(26, 21, 0), mentoring(26, 21, 30), KAKAO_ID.toDomain()),
    금요일_4주차_21_30_시작(mentoring(26, 21, 30), mentoring(26, 22, 0), KAKAO_ID.toDomain()),

    토요일_1주차_20_00_시작(mentoring(6, 20, 0), mentoring(6, 20, 30), KAKAO_ID.toDomain()),
    토요일_1주차_20_30_시작(mentoring(6, 20, 30), mentoring(6, 21, 0), KAKAO_ID.toDomain()),
    토요일_1주차_21_00_시작(mentoring(6, 21, 0), mentoring(6, 21, 30), KAKAO_ID.toDomain()),
    토요일_1주차_21_30_시작(mentoring(6, 21, 30), mentoring(6, 22, 0), KAKAO_ID.toDomain()),
    토요일_2주차_20_00_시작(mentoring(13, 20, 0), mentoring(13, 20, 30), KAKAO_ID.toDomain()),
    토요일_2주차_20_30_시작(mentoring(13, 20, 30), mentoring(13, 21, 0), KAKAO_ID.toDomain()),
    토요일_2주차_21_00_시작(mentoring(13, 21, 0), mentoring(13, 21, 30), KAKAO_ID.toDomain()),
    토요일_2주차_21_30_시작(mentoring(13, 21, 30), mentoring(13, 22, 0), KAKAO_ID.toDomain()),
    토요일_3주차_20_00_시작(mentoring(20, 20, 0), mentoring(20, 20, 30), KAKAO_ID.toDomain()),
    토요일_3주차_20_30_시작(mentoring(20, 20, 30), mentoring(20, 21, 0), KAKAO_ID.toDomain()),
    토요일_3주차_21_00_시작(mentoring(20, 21, 0), mentoring(20, 21, 30), KAKAO_ID.toDomain()),
    토요일_3주차_21_30_시작(mentoring(20, 21, 30), mentoring(20, 22, 0), KAKAO_ID.toDomain()),
    토요일_4주차_20_00_시작(mentoring(27, 20, 0), mentoring(27, 20, 30), KAKAO_ID.toDomain()),
    토요일_4주차_20_30_시작(mentoring(27, 20, 30), mentoring(27, 21, 0), KAKAO_ID.toDomain()),
    토요일_4주차_21_00_시작(mentoring(27, 21, 0), mentoring(27, 21, 30), KAKAO_ID.toDomain()),
    토요일_4주차_21_30_시작(mentoring(27, 21, 30), mentoring(27, 22, 0), KAKAO_ID.toDomain()),

    일요일_2주차_20_00_시작(mentoring(7, 20, 0), mentoring(7, 20, 30), KAKAO_ID.toDomain()),
    일요일_2주차_20_30_시작(mentoring(7, 20, 30), mentoring(7, 21, 0), KAKAO_ID.toDomain()),
    일요일_2주차_21_00_시작(mentoring(7, 21, 0), mentoring(7, 21, 30), KAKAO_ID.toDomain()),
    일요일_2주차_21_30_시작(mentoring(7, 21, 30), mentoring(7, 22, 0), KAKAO_ID.toDomain()),
    일요일_3주차_20_00_시작(mentoring(14, 20, 0), mentoring(14, 20, 30), KAKAO_ID.toDomain()),
    일요일_3주차_20_30_시작(mentoring(14, 20, 30), mentoring(14, 21, 0), KAKAO_ID.toDomain()),
    일요일_3주차_21_00_시작(mentoring(14, 21, 0), mentoring(14, 21, 30), KAKAO_ID.toDomain()),
    일요일_3주차_21_30_시작(mentoring(14, 21, 30), mentoring(14, 22, 0), KAKAO_ID.toDomain()),
    일요일_4주차_20_00_시작(mentoring(21, 20, 0), mentoring(21, 20, 30), KAKAO_ID.toDomain()),
    일요일_4주차_20_30_시작(mentoring(21, 20, 30), mentoring(21, 21, 0), KAKAO_ID.toDomain()),
    일요일_4주차_21_00_시작(mentoring(21, 21, 0), mentoring(21, 21, 30), KAKAO_ID.toDomain()),
    일요일_4주차_21_30_시작(mentoring(21, 21, 30), mentoring(21, 22, 0), KAKAO_ID.toDomain()),
    ;

    private final LocalDateTime start;
    private final LocalDateTime end;
    private final Strategy strategy;

    private static LocalDateTime mentoring(final int day, final int hour, final int minute) {
        return LocalDateTime.of(2024, 1, day, hour, minute);
    }

    public static class MenteeFlow {
        public static CoffeeChat apply(final CoffeeChatFixture fixture, final Mentee mentee, final Mentor mentor) {
            return CoffeeChat.apply(
                    mentee,
                    mentor,
                    "신청..",
                    new Reservation(fixture.start),
                    new Reservation(fixture.end)
            );
        }

        public static CoffeeChat apply(final LocalDateTime start, final LocalDateTime end, final Mentee mentee, final Mentor mentor) {
            return CoffeeChat.apply(
                    mentee,
                    mentor,
                    "신청..",
                    new Reservation(start),
                    new Reservation(end)
            );
        }

        public static CoffeeChat applyAndCancel(final CoffeeChatFixture fixture, final Mentee mentee, final Mentor mentor) {
            final CoffeeChat coffeeChat = CoffeeChat.apply(
                    mentee,
                    mentor,
                    "신청..",
                    new Reservation(fixture.start),
                    new Reservation(fixture.end)
            );
            coffeeChat.cancel();
            return coffeeChat;
        }

        public static CoffeeChat applyAndCancel(final LocalDateTime start, final LocalDateTime end, final Mentee mentee, final Mentor mentor) {
            final CoffeeChat coffeeChat = CoffeeChat.apply(
                    mentee,
                    mentor,
                    "신청..",
                    new Reservation(start),
                    new Reservation(end)
            );
            coffeeChat.cancel();
            return coffeeChat;
        }

        public static CoffeeChat applyAndApprove(final CoffeeChatFixture fixture, final Mentee mentee, final Mentor mentor) {
            final CoffeeChat coffeeChat = CoffeeChat.apply(
                    mentee,
                    mentor,
                    "신청..",
                    new Reservation(fixture.start),
                    new Reservation(fixture.end)
            );
            coffeeChat.approveFromMenteeApply(fixture.strategy);
            return coffeeChat;
        }

        public static CoffeeChat applyAndApprove(final LocalDateTime start, final LocalDateTime end, final Mentee mentee, final Mentor mentor) {
            final CoffeeChat coffeeChat = CoffeeChat.apply(
                    mentee,
                    mentor,
                    "신청..",
                    new Reservation(start),
                    new Reservation(end)
            );
            coffeeChat.approveFromMenteeApply(KAKAO_ID.toDomain());
            return coffeeChat;
        }

        public static CoffeeChat applyAndReject(final CoffeeChatFixture fixture, final Mentee mentee, final Mentor mentor) {
            final CoffeeChat coffeeChat = CoffeeChat.apply(
                    mentee,
                    mentor,
                    "신청..",
                    new Reservation(fixture.start),
                    new Reservation(fixture.end)
            );
            coffeeChat.rejectFromMenteeApply("거절..");
            return coffeeChat;
        }

        public static CoffeeChat applyAndReject(final LocalDateTime start, final LocalDateTime end, final Mentee mentee, final Mentor mentor) {
            final CoffeeChat coffeeChat = CoffeeChat.apply(
                    mentee,
                    mentor,
                    "신청..",
                    new Reservation(start),
                    new Reservation(end)
            );
            coffeeChat.rejectFromMenteeApply("거절..");
            return coffeeChat;
        }

        public static CoffeeChat applyAndComplete(final CoffeeChatFixture fixture, final Mentee mentee, final Mentor mentor) {
            final CoffeeChat coffeeChat = CoffeeChat.apply(
                    mentee,
                    mentor,
                    "신청..",
                    new Reservation(fixture.start),
                    new Reservation(fixture.end)
            );
            coffeeChat.approveFromMenteeApply(fixture.strategy);
            coffeeChat.complete();
            return coffeeChat;
        }

        public static CoffeeChat applyAndComplete(final LocalDateTime start, final LocalDateTime end, final Mentee mentee, final Mentor mentor) {
            final CoffeeChat coffeeChat = CoffeeChat.apply(
                    mentee,
                    mentor,
                    "신청..",
                    new Reservation(start),
                    new Reservation(end)
            );
            coffeeChat.approveFromMenteeApply(KAKAO_ID.toDomain());
            coffeeChat.complete();
            return coffeeChat;
        }
    }

    public static class MentorFlow {
        public static CoffeeChat suggest(final Mentor mentor, final Mentee mentee) {
            return CoffeeChat.suggest(mentor, mentee, "신청..");
        }

        public static CoffeeChat suggestAndCancel(final Mentor mentor, final Mentee mentee) {
            final CoffeeChat coffeeChat = CoffeeChat.suggest(mentor, mentee, "신청..");
            coffeeChat.cancel();
            return coffeeChat;
        }

        public static CoffeeChat suggestAndPending(final CoffeeChatFixture fixture, final Mentor mentor, final Mentee mentee) {
            final CoffeeChat coffeeChat = CoffeeChat.suggest(mentor, mentee, "신청..");
            coffeeChat.pendingFromMentorSuggest("질문..", new Reservation(fixture.start), new Reservation(fixture.end));
            return coffeeChat;
        }

        public static CoffeeChat suggestAndPending(final LocalDateTime start, final LocalDateTime end, final Mentor mentor, final Mentee mentee) {
            final CoffeeChat coffeeChat = CoffeeChat.suggest(mentor, mentee, "신청..");
            coffeeChat.pendingFromMentorSuggest("질문..", new Reservation(start), new Reservation(end));
            return coffeeChat;
        }

        public static CoffeeChat suggestAndReject(final Mentor mentor, final Mentee mentee) {
            final CoffeeChat coffeeChat = CoffeeChat.suggest(mentor, mentee, "신청..");
            coffeeChat.rejectFromMentorSuggest("거절..");
            return coffeeChat;
        }

        public static CoffeeChat suggestAndFinallyApprove(final CoffeeChatFixture fixture, final Mentor mentor, final Mentee mentee) {
            final CoffeeChat coffeeChat = CoffeeChat.suggest(mentor, mentee, "신청..");
            coffeeChat.pendingFromMentorSuggest("질문..", new Reservation(fixture.start), new Reservation(fixture.end));
            coffeeChat.approvePendingCoffeeChat(fixture.strategy);
            return coffeeChat;
        }

        public static CoffeeChat suggestAndFinallyApprove(final LocalDateTime start, final LocalDateTime end, final Mentor mentor, final Mentee mentee) {
            final CoffeeChat coffeeChat = CoffeeChat.suggest(mentor, mentee, "신청..");
            coffeeChat.pendingFromMentorSuggest("질문..", new Reservation(start), new Reservation(end));
            coffeeChat.approvePendingCoffeeChat(KAKAO_ID.toDomain());
            return coffeeChat;
        }

        public static CoffeeChat suggestAndFinallyReject(final CoffeeChatFixture fixture, final Mentor mentor, final Mentee mentee) {
            final CoffeeChat coffeeChat = CoffeeChat.suggest(mentor, mentee, "신청..");
            coffeeChat.pendingFromMentorSuggest("질문..", new Reservation(fixture.start), new Reservation(fixture.end));
            coffeeChat.rejectPendingCoffeeChat("거절..");
            return coffeeChat;
        }

        public static CoffeeChat suggestAndFinallyReject(final LocalDateTime start, final LocalDateTime end, final Mentor mentor, final Mentee mentee) {
            final CoffeeChat coffeeChat = CoffeeChat.suggest(mentor, mentee, "신청..");
            coffeeChat.pendingFromMentorSuggest("질문..", new Reservation(start), new Reservation(end));
            coffeeChat.rejectPendingCoffeeChat("거절..");
            return coffeeChat;
        }

        public static CoffeeChat suggestAndComplete(final CoffeeChatFixture fixture, final Mentor mentor, final Mentee mentee) {
            final CoffeeChat coffeeChat = CoffeeChat.suggest(mentor, mentee, "신청..");
            coffeeChat.pendingFromMentorSuggest("질문..", new Reservation(fixture.start), new Reservation(fixture.end));
            coffeeChat.approvePendingCoffeeChat(fixture.strategy);
            coffeeChat.complete();
            return coffeeChat;
        }

        public static CoffeeChat suggestAndComplete(final LocalDateTime start, final LocalDateTime end, final Mentor mentor, final Mentee mentee) {
            final CoffeeChat coffeeChat = CoffeeChat.suggest(mentor, mentee, "신청..");
            coffeeChat.pendingFromMentorSuggest("질문..", new Reservation(start), new Reservation(end));
            coffeeChat.approvePendingCoffeeChat(KAKAO_ID.toDomain());
            coffeeChat.complete();
            return coffeeChat;
        }
    }
}
