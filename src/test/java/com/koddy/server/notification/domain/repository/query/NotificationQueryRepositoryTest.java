package com.koddy.server.notification.domain.repository.query;

import com.koddy.server.coffeechat.domain.model.CoffeeChat;
import com.koddy.server.coffeechat.domain.repository.CoffeeChatRepository;
import com.koddy.server.common.RepositoryTest;
import com.koddy.server.common.fixture.CoffeeChatFixture.MenteeFlow;
import com.koddy.server.common.fixture.CoffeeChatFixture.MentorFlow;
import com.koddy.server.global.query.PageCreator;
import com.koddy.server.member.domain.model.mentee.Mentee;
import com.koddy.server.member.domain.model.mentor.Mentor;
import com.koddy.server.member.domain.repository.MemberRepository;
import com.koddy.server.notification.domain.model.Notification;
import com.koddy.server.notification.domain.repository.NotificationRepository;
import com.koddy.server.notification.domain.repository.query.response.NotificationDetails;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;

import static com.koddy.server.common.fixture.CoffeeChatFixture.금요일_1주차_20_00_시작;
import static com.koddy.server.common.fixture.CoffeeChatFixture.금요일_2주차_20_00_시작;
import static com.koddy.server.common.fixture.CoffeeChatFixture.수요일_1주차_20_00_시작;
import static com.koddy.server.common.fixture.CoffeeChatFixture.수요일_2주차_20_00_시작;
import static com.koddy.server.common.fixture.CoffeeChatFixture.수요일_3주차_20_00_시작;
import static com.koddy.server.common.fixture.CoffeeChatFixture.수요일_4주차_20_00_시작;
import static com.koddy.server.common.fixture.CoffeeChatFixture.월요일_1주차_20_00_시작;
import static com.koddy.server.common.fixture.CoffeeChatFixture.월요일_2주차_20_00_시작;
import static com.koddy.server.common.fixture.CoffeeChatFixture.월요일_3주차_20_00_시작;
import static com.koddy.server.common.fixture.CoffeeChatFixture.월요일_4주차_20_00_시작;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_1;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static com.koddy.server.notification.domain.model.NotificationType.MENTEE_RECEIVE_MENTEE_FLOW_MENTOR_APPROVE;
import static com.koddy.server.notification.domain.model.NotificationType.MENTEE_RECEIVE_MENTEE_FLOW_MENTOR_REJECT;
import static com.koddy.server.notification.domain.model.NotificationType.MENTEE_RECEIVE_MENTOR_FLOW_MENTOR_FINALLY_APPROVE;
import static com.koddy.server.notification.domain.model.NotificationType.MENTEE_RECEIVE_MENTOR_FLOW_MENTOR_FINALLY_CANCEL;
import static com.koddy.server.notification.domain.model.NotificationType.MENTEE_RECEIVE_MENTOR_FLOW_MENTOR_SUGGEST;
import static com.koddy.server.notification.domain.model.NotificationType.MENTOR_RECEIVE_MENTEE_FLOW_MENTEE_APPLY;
import static com.koddy.server.notification.domain.model.NotificationType.MENTOR_RECEIVE_MENTEE_FLOW_MENTOR_APPROVE;
import static com.koddy.server.notification.domain.model.NotificationType.MENTOR_RECEIVE_MENTOR_FLOW_MENTEE_PENDING;
import static com.koddy.server.notification.domain.model.NotificationType.MENTOR_RECEIVE_MENTOR_FLOW_MENTOR_FINALLY_APPROVE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Import(NotificationQueryRepositoryImpl.class)
@DisplayName("Notification -> NotificationQueryRepository 테스트")
class NotificationQueryRepositoryTest extends RepositoryTest {
    @Autowired
    private NotificationQueryRepositoryImpl sut;

    @Autowired
    private MemberRepository memberRepository;

    @Autowired
    private CoffeeChatRepository coffeeChatRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    private Mentor mentor;
    private Mentee mentee;
    private CoffeeChat[] coffeeChats = new CoffeeChat[10];
    private Notification[] notifications = new Notification[28];

    private final Pageable pageable1 = PageCreator.create(1);
    private final Pageable pageable2 = PageCreator.create(2);

    @BeforeEach
    void setUp() {
        mentor = memberRepository.save(MENTOR_1.toDomain());
        mentee = memberRepository.save(MENTEE_1.toDomain());
        coffeeChats = coffeeChatRepository.saveAll(List.of(
                MentorFlow.suggestAndFinallyCancel(월요일_1주차_20_00_시작, mentor, mentee),
                MenteeFlow.applyAndApprove(월요일_2주차_20_00_시작, mentee, mentor),
                MentorFlow.suggestAndFinallyApprove(월요일_3주차_20_00_시작, mentor, mentee),
                MenteeFlow.applyAndReject(월요일_4주차_20_00_시작, mentee, mentor),
                MentorFlow.suggestAndPending(수요일_1주차_20_00_시작, mentor, mentee),
                MentorFlow.suggestAndFinallyCancel(수요일_2주차_20_00_시작, mentor, mentee),
                MenteeFlow.applyAndApprove(수요일_3주차_20_00_시작, mentee, mentor),
                MentorFlow.suggestAndFinallyApprove(수요일_4주차_20_00_시작, mentor, mentee),
                MenteeFlow.applyAndReject(금요일_1주차_20_00_시작, mentee, mentor),
                MentorFlow.suggestAndPending(금요일_2주차_20_00_시작, mentor, mentee)
        )).toArray(CoffeeChat[]::new);
        notifications = notificationRepository.saveAll(List.of(
                Notification.create(mentee, coffeeChats[0], MENTEE_RECEIVE_MENTOR_FLOW_MENTOR_SUGGEST),
                Notification.create(mentor, coffeeChats[0], MENTOR_RECEIVE_MENTOR_FLOW_MENTEE_PENDING),
                Notification.create(mentee, coffeeChats[0], MENTEE_RECEIVE_MENTOR_FLOW_MENTOR_FINALLY_CANCEL),
                Notification.create(mentor, coffeeChats[1], MENTOR_RECEIVE_MENTEE_FLOW_MENTEE_APPLY),
                Notification.create(mentee, coffeeChats[1], MENTEE_RECEIVE_MENTEE_FLOW_MENTOR_APPROVE),
                Notification.create(mentor, coffeeChats[1], MENTOR_RECEIVE_MENTEE_FLOW_MENTOR_APPROVE),
                Notification.create(mentee, coffeeChats[2], MENTEE_RECEIVE_MENTOR_FLOW_MENTOR_SUGGEST),
                Notification.create(mentor, coffeeChats[2], MENTOR_RECEIVE_MENTOR_FLOW_MENTEE_PENDING),
                Notification.create(mentee, coffeeChats[2], MENTEE_RECEIVE_MENTOR_FLOW_MENTOR_FINALLY_APPROVE),
                Notification.create(mentor, coffeeChats[2], MENTOR_RECEIVE_MENTOR_FLOW_MENTOR_FINALLY_APPROVE),
                Notification.create(mentor, coffeeChats[3], MENTOR_RECEIVE_MENTEE_FLOW_MENTEE_APPLY),
                Notification.create(mentee, coffeeChats[3], MENTEE_RECEIVE_MENTEE_FLOW_MENTOR_REJECT),
                Notification.create(mentee, coffeeChats[4], MENTEE_RECEIVE_MENTOR_FLOW_MENTOR_SUGGEST),
                Notification.create(mentor, coffeeChats[4], MENTOR_RECEIVE_MENTOR_FLOW_MENTEE_PENDING),
                Notification.create(mentee, coffeeChats[5], MENTEE_RECEIVE_MENTOR_FLOW_MENTOR_SUGGEST),
                Notification.create(mentor, coffeeChats[5], MENTOR_RECEIVE_MENTOR_FLOW_MENTEE_PENDING),
                Notification.create(mentee, coffeeChats[5], MENTEE_RECEIVE_MENTOR_FLOW_MENTOR_FINALLY_CANCEL),
                Notification.create(mentor, coffeeChats[6], MENTOR_RECEIVE_MENTEE_FLOW_MENTEE_APPLY),
                Notification.create(mentee, coffeeChats[6], MENTEE_RECEIVE_MENTEE_FLOW_MENTOR_APPROVE),
                Notification.create(mentor, coffeeChats[6], MENTOR_RECEIVE_MENTEE_FLOW_MENTOR_APPROVE),
                Notification.create(mentee, coffeeChats[7], MENTEE_RECEIVE_MENTOR_FLOW_MENTOR_SUGGEST),
                Notification.create(mentor, coffeeChats[7], MENTOR_RECEIVE_MENTOR_FLOW_MENTEE_PENDING),
                Notification.create(mentee, coffeeChats[7], MENTEE_RECEIVE_MENTOR_FLOW_MENTOR_FINALLY_APPROVE),
                Notification.create(mentor, coffeeChats[7], MENTOR_RECEIVE_MENTOR_FLOW_MENTOR_FINALLY_APPROVE),
                Notification.create(mentor, coffeeChats[8], MENTOR_RECEIVE_MENTEE_FLOW_MENTEE_APPLY),
                Notification.create(mentee, coffeeChats[8], MENTEE_RECEIVE_MENTEE_FLOW_MENTOR_REJECT),
                Notification.create(mentee, coffeeChats[9], MENTEE_RECEIVE_MENTOR_FLOW_MENTOR_SUGGEST),
                Notification.create(mentor, coffeeChats[9], MENTOR_RECEIVE_MENTOR_FLOW_MENTEE_PENDING)
        )).toArray(Notification[]::new);
    }

    @Test
    @DisplayName("멘토의 알림 내역을 조회한다")
    void fetchMentorNotifications() {
        /* 페이지 1 */
        final Slice<NotificationDetails> result1 = sut.fetchMentorNotifications(mentor.getId(), pageable1);
        assertAll(
                () -> assertThat(result1.hasNext()).isTrue(),
                () -> assertThat(result1.getContent())
                        .map(NotificationDetails::id)
                        .containsExactly(
                                notifications[27].getId(), notifications[24].getId(), notifications[23].getId(),
                                notifications[21].getId(), notifications[19].getId(), notifications[17].getId(),
                                notifications[15].getId(), notifications[13].getId(), notifications[10].getId(),
                                notifications[9].getId()
                        ),
                () -> assertThat(result1.getContent())
                        .map(NotificationDetails::coffeeChatId)
                        .containsExactly(
                                coffeeChats[9].getId(), coffeeChats[8].getId(), coffeeChats[7].getId(),
                                coffeeChats[7].getId(), coffeeChats[6].getId(), coffeeChats[6].getId(),
                                coffeeChats[5].getId(), coffeeChats[4].getId(), coffeeChats[3].getId(),
                                coffeeChats[2].getId()
                        )
        );

        /* 페이지 2 */
        final Slice<NotificationDetails> result2 = sut.fetchMentorNotifications(mentor.getId(), pageable2);
        assertAll(
                () -> assertThat(result2.hasNext()).isFalse(),
                () -> assertThat(result2.getContent())
                        .map(NotificationDetails::id)
                        .containsExactly(
                                notifications[7].getId(),
                                notifications[5].getId(),
                                notifications[3].getId(),
                                notifications[1].getId()
                        ),
                () -> assertThat(result2.getContent())
                        .map(NotificationDetails::coffeeChatId)
                        .containsExactly(
                                coffeeChats[2].getId(),
                                coffeeChats[1].getId(),
                                coffeeChats[1].getId(),
                                coffeeChats[0].getId()
                        )
        );
    }

    @Test
    @DisplayName("멘티의 알림 내역을 조회한다")
    void fetchMenteeNotifications() {
        /* 페이지 1 */
        final Slice<NotificationDetails> result1 = sut.fetchMenteeNotifications(mentee.getId(), pageable1);
        assertAll(
                () -> assertThat(result1.hasNext()).isTrue(),
                () -> assertThat(result1.getContent())
                        .map(NotificationDetails::id)
                        .containsExactly(
                                notifications[26].getId(), notifications[25].getId(), notifications[22].getId(),
                                notifications[20].getId(), notifications[18].getId(), notifications[16].getId(),
                                notifications[14].getId(), notifications[12].getId(), notifications[11].getId(),
                                notifications[8].getId()
                        ),
                () -> assertThat(result1.getContent())
                        .map(NotificationDetails::coffeeChatId)
                        .containsExactly(
                                coffeeChats[9].getId(), coffeeChats[8].getId(), coffeeChats[7].getId(),
                                coffeeChats[7].getId(), coffeeChats[6].getId(), coffeeChats[5].getId(),
                                coffeeChats[5].getId(), coffeeChats[4].getId(), coffeeChats[3].getId(),
                                coffeeChats[2].getId()
                        )
        );

        /* 페이지 2 */
        final Slice<NotificationDetails> result2 = sut.fetchMenteeNotifications(mentee.getId(), pageable2);
        assertAll(
                () -> assertThat(result2.hasNext()).isFalse(),
                () -> assertThat(result2.getContent())
                        .map(NotificationDetails::id)
                        .containsExactly(
                                notifications[6].getId(),
                                notifications[4].getId(),
                                notifications[2].getId(),
                                notifications[0].getId()
                        ),
                () -> assertThat(result2.getContent())
                        .map(NotificationDetails::coffeeChatId)
                        .containsExactly(
                                coffeeChats[2].getId(),
                                coffeeChats[1].getId(),
                                coffeeChats[0].getId(),
                                coffeeChats[0].getId()
                        )
        );
    }
}
