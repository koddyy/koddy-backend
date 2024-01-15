package com.koddy.server.coffeechat.domain.repository;

import com.koddy.server.coffeechat.domain.model.CoffeeChat;
import com.koddy.server.coffeechat.domain.model.Reservation;
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
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_1;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static org.assertj.core.api.Assertions.assertThat;
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
}
