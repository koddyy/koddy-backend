package com.koddy.server.member.domain.repository.query;

import com.koddy.server.coffeechat.domain.model.CoffeeChat;
import com.koddy.server.coffeechat.domain.model.Reservation;
import com.koddy.server.coffeechat.domain.repository.CoffeeChatRepository;
import com.koddy.server.common.RepositoryTest;
import com.koddy.server.common.fixture.MenteeFixture;
import com.koddy.server.member.domain.model.mentee.Mentee;
import com.koddy.server.member.domain.model.mentor.Mentor;
import com.koddy.server.member.domain.repository.MemberRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Import(MentorMainSearchRepositoryImpl.class)
@DisplayName("Member -> MentorMainSearchRepository [fetchAppliedMentees] 테스트")
class MentorMainSearchRepositoryFetchAppliedMenteesTest extends RepositoryTest {
    @Autowired
    private MentorMainSearchRepositoryImpl sut;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CoffeeChatRepository coffeeChatRepository;

    private Mentor mentor;
    private final Mentee[] mentees = new Mentee[10];

    @BeforeEach
    void setUp() {
        mentor = memberRepository.save(MENTOR_1.toDomain());
        final List<MenteeFixture> fixtures = Arrays.stream(MenteeFixture.values())
                .limit(10)
                .toList();
        Arrays.setAll(mentees, it -> memberRepository.save(fixtures.get(it).toDomain()));
    }

    @Test
    @DisplayName("멘토 자신에게 커피챗을 신청한 멘티를 limit 개수만큼 최근에 신청한 순서대로 조회한다")
    void findAppliedMentees() {
        // given
        final LocalDateTime time = LocalDateTime.of(2024, 2, 1, 15, 0);

        final CoffeeChat coffeeChat0 = apply(mentees[0], mentor, time, time.plusMinutes(30));
        final CoffeeChat coffeeChat1 = apply(mentees[1], mentor, time.plusMinutes(30), time.plusMinutes(60));
        final CoffeeChat coffeeChat2 = apply(mentees[2], mentor, time.plusMinutes(60), time.plusMinutes(90));
        final CoffeeChat coffeeChat3 = apply(mentees[3], mentor, time.plusMinutes(90), time.plusMinutes(120));
        final CoffeeChat coffeeChat4 = apply(mentees[4], mentor, time.plusMinutes(120), time.plusMinutes(150));
        final CoffeeChat coffeeChat5 = apply(mentees[5], mentor, time.plusMinutes(150), time.plusMinutes(180));
        final CoffeeChat coffeeChat6 = apply(mentees[6], mentor, time.plusMinutes(180), time.plusMinutes(210));
        final CoffeeChat coffeeChat7 = apply(mentees[7], mentor, time.plusMinutes(210), time.plusMinutes(240));
        final CoffeeChat coffeeChat8 = apply(mentees[8], mentor, time.plusMinutes(240), time.plusMinutes(270));
        final CoffeeChat coffeeChat9 = apply(mentees[9], mentor, time.plusMinutes(270), time.plusMinutes(300));

        /* limit별 조회 */
        final List<Mentee> result1 = sut.fetchAppliedMentees(mentor.getId(), 3);
        final List<Mentee> result2 = sut.fetchAppliedMentees(mentor.getId(), 5);
        final List<Mentee> result3 = sut.fetchAppliedMentees(mentor.getId(), 7);
        final List<Mentee> result4 = sut.fetchAppliedMentees(mentor.getId(), 10);

        assertAll(
                () -> assertThat(result1).containsExactly(mentees[9], mentees[8], mentees[7]),
                () -> assertThat(result2).containsExactly(mentees[9], mentees[8], mentees[7], mentees[6], mentees[5]),
                () -> assertThat(result3).containsExactly(mentees[9], mentees[8], mentees[7], mentees[6], mentees[5], mentees[4], mentees[3]),
                () -> assertThat(result4).containsExactly(mentees[9], mentees[8], mentees[7], mentees[6], mentees[5], mentees[4], mentees[3], mentees[2], mentees[1], mentees[0])
        );

        /* cancel 후 limit별 조회 */
        coffeeChat3.cancel();
        coffeeChat5.cancel();
        coffeeChat7.cancel();
        coffeeChat9.cancel();

        final List<Mentee> result5 = sut.fetchAppliedMentees(mentor.getId(), 3);
        final List<Mentee> result6 = sut.fetchAppliedMentees(mentor.getId(), 5);
        final List<Mentee> result7 = sut.fetchAppliedMentees(mentor.getId(), 7);
        final List<Mentee> result8 = sut.fetchAppliedMentees(mentor.getId(), 10);

        assertAll(
                () -> assertThat(result5).containsExactly(mentees[8], mentees[6], mentees[4]),
                () -> assertThat(result6).containsExactly(mentees[8], mentees[6], mentees[4], mentees[2], mentees[1]),
                () -> assertThat(result7).containsExactly(mentees[8], mentees[6], mentees[4], mentees[2], mentees[1], mentees[0]),
                () -> assertThat(result8).containsExactly(mentees[8], mentees[6], mentees[4], mentees[2], mentees[1], mentees[0])
        );
    }

    private CoffeeChat apply(final Mentee mentee, final Mentor mentor, final LocalDateTime start, final LocalDateTime end) {
        return coffeeChatRepository.save(CoffeeChat.applyCoffeeChat(
                mentee,
                mentor,
                "신청..",
                new Reservation(start),
                new Reservation(end)
        ));
    }
}
