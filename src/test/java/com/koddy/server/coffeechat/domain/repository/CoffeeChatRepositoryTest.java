package com.koddy.server.coffeechat.domain.repository;

import com.koddy.server.coffeechat.domain.model.CoffeeChat;
import com.koddy.server.coffeechat.domain.model.Reservation;
import com.koddy.server.coffeechat.exception.CoffeeChatException;
import com.koddy.server.common.RepositoryTest;
import com.koddy.server.common.fixture.StrategyFixture;
import com.koddy.server.member.domain.model.mentee.Mentee;
import com.koddy.server.member.domain.model.mentor.Mentor;
import com.koddy.server.member.domain.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;

import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.APPLY;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.APPROVE;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.PENDING;
import static com.koddy.server.coffeechat.exception.CoffeeChatExceptionCode.APPLIED_OR_SUGGESTED_COFFEE_CHAT_NOT_FOUND;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_1;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertAll;

@DisplayName("CoffeeChat -> CoffeeChatRepository 테스트")
class CoffeeChatRepositoryTest extends RepositoryTest {
    @Autowired
    private CoffeeChatRepository sut;

    @Autowired
    private MemberRepository memberRepository;

    private final String applyReason = "신청..";
    private final Reservation start = new Reservation(LocalDateTime.of(2024, 2, 1, 9, 0));
    private final Reservation end = new Reservation(LocalDateTime.of(2024, 2, 1, 10, 0));

    private Mentor mentor;
    private Mentee mentee;

    @BeforeEach
    void setUp() {
        mentor = memberRepository.save(MENTOR_1.toDomain());
        mentee = memberRepository.save(MENTEE_1.toDomain());
    }

    @Test
    @DisplayName("상태에 따른 CoffeeChat을 조회한다")
    void findByIdAndStatus() {
        // given
        final CoffeeChat coffeeChatA = sut.save(CoffeeChat.applyCoffeeChat(mentee, mentor, applyReason, start, end));
        final CoffeeChat coffeeChatB = sut.save(CoffeeChat.suggestCoffeeChat(mentor, mentee, applyReason));

        /* 1차 조회 */
        assertAll(
                () -> assertThat(sut.findByIdAndStatus(coffeeChatA.getId(), APPLY)).isPresent(),
                () -> assertThat(sut.findByIdAndStatus(coffeeChatB.getId(), APPLY)).isPresent()
        );

        /* coffeeChatA 수락 */
        coffeeChatA.approveFromMenteeApply(StrategyFixture.KAKAO_ID.toDomain());
        assertAll(
                () -> assertThat(sut.findByIdAndStatus(coffeeChatA.getId(), APPROVE)).isPresent(),
                () -> assertThat(sut.findByIdAndStatus(coffeeChatB.getId(), APPLY)).isPresent()
        );

        /* coffeeChatB 1차 수락 */
        coffeeChatB.pendingFromMentorSuggest(
                new Reservation(LocalDateTime.of(2024, 2, 1, 18, 0)),
                new Reservation(LocalDateTime.of(2024, 2, 1, 19, 0))
        );
        assertAll(
                () -> assertThat(sut.findByIdAndStatus(coffeeChatA.getId(), APPROVE)).isPresent(),
                () -> assertThat(sut.findByIdAndStatus(coffeeChatB.getId(), PENDING)).isPresent()
        );

        /* coffeeChatB 최종 수락 */
        coffeeChatB.approvePendingCoffeeChat(StrategyFixture.KAKAO_ID.toDomain());
        assertAll(
                () -> assertThat(sut.findByIdAndStatus(coffeeChatA.getId(), APPROVE)).isPresent(),
                () -> assertThat(sut.findByIdAndStatus(coffeeChatB.getId(), APPROVE)).isPresent()
        );
    }

    @Test
    @DisplayName("멘토가 제안 or 멘티가 신청하고 APPLY 상태인 커피챗을 조회한다")
    void getAppliedOrSuggestedCoffeeChat() {
        // given
        final CoffeeChat mentorSuggestedCoffeeChat = sut.save(CoffeeChat.suggestCoffeeChat(mentor, mentee, applyReason));
        final CoffeeChat menteeAppliedCoffeeChat = sut.save(CoffeeChat.applyCoffeeChat(mentee, mentor, applyReason, start, end));

        // when - then
        assertAll(
                // 멘토가 제안한 커피챗
                () -> assertThat(sut.getAppliedOrSuggestedCoffeeChat(mentorSuggestedCoffeeChat.getId(), mentor.getId())).isEqualTo(mentorSuggestedCoffeeChat),
                () -> assertThatThrownBy(() -> sut.getAppliedOrSuggestedCoffeeChat(mentorSuggestedCoffeeChat.getId(), mentee.getId()))
                        .isInstanceOf(CoffeeChatException.class)
                        .hasMessage(APPLIED_OR_SUGGESTED_COFFEE_CHAT_NOT_FOUND.getMessage()),

                // 멘티가 신청한 커피챗
                () -> assertThat(sut.getAppliedOrSuggestedCoffeeChat(menteeAppliedCoffeeChat.getId(), mentee.getId())).isEqualTo(menteeAppliedCoffeeChat),
                () -> assertThatThrownBy(() -> sut.getAppliedOrSuggestedCoffeeChat(menteeAppliedCoffeeChat.getId(), mentor.getId()))
                        .isInstanceOf(CoffeeChatException.class)
                        .hasMessage(APPLIED_OR_SUGGESTED_COFFEE_CHAT_NOT_FOUND.getMessage())
        );
    }
}
