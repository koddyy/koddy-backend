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
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.CANCEL;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.COMPLETE;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.NO_SHOW;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.PENDING;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.REJECT;
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

    private Mentor mentor;
    private Mentee mentee;

    @BeforeEach
    void setUp() {
        mentor = memberRepository.save(MENTOR_1.toDomain());
        mentee = memberRepository.save(MENTEE_1.toDomain());
    }

    @Test
    @DisplayName("상태에 따른 커피챗을 조회한다")
    void findByIdAndStatus() {
        // given
        final String applyReason = "Hello";
        final Reservation start = new Reservation(LocalDateTime.of(2024, 2, 1, 9, 0));
        final Reservation end = new Reservation(LocalDateTime.of(2024, 2, 1, 10, 0));
        final CoffeeChat coffeeChatA = sut.save(CoffeeChat.applyCoffeeChat(mentee, mentor, applyReason, start, end));
        final CoffeeChat coffeeChatB = sut.save(CoffeeChat.suggestCoffeeChat(mentor, mentee, applyReason));

        assertAll(
                () -> assertThat(sut.findByIdAndStatus(coffeeChatA.getId(), APPLY)).isPresent(),
                () -> assertThat(sut.findByIdAndStatus(coffeeChatA.getId(), PENDING)).isEmpty(),
                () -> assertThat(sut.findByIdAndStatus(coffeeChatA.getId(), APPROVE)).isEmpty(),
                () -> assertThat(sut.findByIdAndStatus(coffeeChatA.getId(), REJECT)).isEmpty(),
                () -> assertThat(sut.findByIdAndStatus(coffeeChatA.getId(), COMPLETE)).isEmpty(),
                () -> assertThat(sut.findByIdAndStatus(coffeeChatA.getId(), CANCEL)).isEmpty(),
                () -> assertThat(sut.findByIdAndStatus(coffeeChatA.getId(), NO_SHOW)).isEmpty(),

                () -> assertThat(sut.findByIdAndStatus(coffeeChatB.getId(), APPLY)).isPresent(),
                () -> assertThat(sut.findByIdAndStatus(coffeeChatB.getId(), PENDING)).isEmpty(),
                () -> assertThat(sut.findByIdAndStatus(coffeeChatB.getId(), APPROVE)).isEmpty(),
                () -> assertThat(sut.findByIdAndStatus(coffeeChatB.getId(), REJECT)).isEmpty(),
                () -> assertThat(sut.findByIdAndStatus(coffeeChatB.getId(), COMPLETE)).isEmpty(),
                () -> assertThat(sut.findByIdAndStatus(coffeeChatB.getId(), CANCEL)).isEmpty(),
                () -> assertThat(sut.findByIdAndStatus(coffeeChatB.getId(), NO_SHOW)).isEmpty()
        );

        /* 신청/제안에 대한 수락 */
        coffeeChatA.approveMenteeApply(StrategyFixture.ZOOM_LINK.toDomain()); // APPLY -> APPROVE
        coffeeChatB.approveMentorSuggest(start, end); // APPLY -> PENDING

        assertAll(
                () -> assertThat(sut.findByIdAndStatus(coffeeChatA.getId(), APPLY)).isEmpty(),
                () -> assertThat(sut.findByIdAndStatus(coffeeChatA.getId(), APPROVE)).isPresent(),
                () -> assertThat(sut.findByIdAndStatus(coffeeChatA.getId(), REJECT)).isEmpty(),
                () -> assertThat(sut.findByIdAndStatus(coffeeChatA.getId(), COMPLETE)).isEmpty(),
                () -> assertThat(sut.findByIdAndStatus(coffeeChatA.getId(), CANCEL)).isEmpty(),
                () -> assertThat(sut.findByIdAndStatus(coffeeChatA.getId(), NO_SHOW)).isEmpty(),

                () -> assertThat(sut.findByIdAndStatus(coffeeChatB.getId(), APPLY)).isEmpty(),
                () -> assertThat(sut.findByIdAndStatus(coffeeChatB.getId(), PENDING)).isPresent(),
                () -> assertThat(sut.findByIdAndStatus(coffeeChatB.getId(), APPROVE)).isEmpty(),
                () -> assertThat(sut.findByIdAndStatus(coffeeChatB.getId(), REJECT)).isEmpty(),
                () -> assertThat(sut.findByIdAndStatus(coffeeChatB.getId(), COMPLETE)).isEmpty(),
                () -> assertThat(sut.findByIdAndStatus(coffeeChatB.getId(), CANCEL)).isEmpty(),
                () -> assertThat(sut.findByIdAndStatus(coffeeChatB.getId(), NO_SHOW)).isEmpty()
        );
    }
}
