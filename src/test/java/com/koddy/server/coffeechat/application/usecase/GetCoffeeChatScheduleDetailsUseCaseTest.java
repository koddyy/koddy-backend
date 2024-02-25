package com.koddy.server.coffeechat.application.usecase;

import com.koddy.server.auth.domain.model.Authenticated;
import com.koddy.server.coffeechat.application.usecase.query.GetCoffeeChatScheduleDetails;
import com.koddy.server.coffeechat.application.usecase.query.response.MenteeCoffeeChatScheduleDetails;
import com.koddy.server.coffeechat.application.usecase.query.response.MentorCoffeeChatScheduleDetails;
import com.koddy.server.coffeechat.domain.model.CoffeeChat;
import com.koddy.server.coffeechat.domain.model.response.CoffeeChatScheduleDetails;
import com.koddy.server.coffeechat.domain.repository.CoffeeChatRepository;
import com.koddy.server.common.UnitTest;
import com.koddy.server.common.fixture.CoffeeChatFixture.MenteeFlow;
import com.koddy.server.common.fixture.CoffeeChatFixture.MentorFlow;
import com.koddy.server.common.mock.fake.FakeEncryptor;
import com.koddy.server.global.utils.encrypt.Encryptor;
import com.koddy.server.member.domain.model.mentee.Mentee;
import com.koddy.server.member.domain.model.mentor.Mentor;
import com.koddy.server.member.domain.repository.AvailableLanguageRepository;
import com.koddy.server.member.domain.repository.MenteeRepository;
import com.koddy.server.member.domain.repository.MentorRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTEE_PENDING;
import static com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_APPROVE;
import static com.koddy.server.common.fixture.CoffeeChatFixture.월요일_1주차_20_00_시작;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_1;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static com.koddy.server.member.domain.model.Language.Category.EN;
import static com.koddy.server.member.domain.model.Language.Category.KR;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

@DisplayName("CoffeeChat -> GetCoffeeChatScheduleDetailsUseCase 테스트")
class GetCoffeeChatScheduleDetailsUseCaseTest extends UnitTest {
    private final CoffeeChatRepository coffeeChatRepository = mock(CoffeeChatRepository.class);
    private final MentorRepository mentorRepository = mock(MentorRepository.class);
    private final MenteeRepository menteeRepository = mock(MenteeRepository.class);
    private final AvailableLanguageRepository availableLanguageRepository = mock(AvailableLanguageRepository.class);
    private final Encryptor encryptor = new FakeEncryptor();
    private final GetCoffeeChatScheduleDetailsUseCase sut = new GetCoffeeChatScheduleDetailsUseCase(
            coffeeChatRepository,
            mentorRepository,
            menteeRepository,
            availableLanguageRepository,
            encryptor
    );

    private final Mentor mentor = MENTOR_1.toDomain().apply(1L);
    private final Mentee mentee = MENTEE_1.toDomain().apply(2L);

    @Test
    @DisplayName("멘토 내 일정의 커피챗 상세 정보를 조회한다")
    void convertForMentor() {
        // given
        final CoffeeChat coffeeChat = MentorFlow.suggestAndPending(월요일_1주차_20_00_시작, mentor, mentee).apply(1L);

        given(coffeeChatRepository.getById(coffeeChat.getId())).willReturn(coffeeChat);
        given(mentorRepository.getByIdWithNative(coffeeChat.getMentorId())).willReturn(mentor);
        given(menteeRepository.getByIdWithNative(coffeeChat.getMenteeId())).willReturn(mentee);
        given(availableLanguageRepository.findByMemberIdWithNative(mentor.getId())).willReturn(mentor.getAvailableLanguages());
        given(availableLanguageRepository.findByMemberIdWithNative(mentee.getId())).willReturn(mentee.getAvailableLanguages());

        // when
        final CoffeeChatScheduleDetails result = sut.invoke(new GetCoffeeChatScheduleDetails(
                new Authenticated(mentor.getId(), mentor.getAuthority()),
                coffeeChat.getId()
        ));

        // then
        assertAll(
                () -> assertThat(result).isInstanceOf(MentorCoffeeChatScheduleDetails.class),
                () -> {
                    final MentorCoffeeChatScheduleDetails details = (MentorCoffeeChatScheduleDetails) result;
                    assertAll(
                            () -> assertThat(details.mentee().id()).isEqualTo(mentee.getId()),
                            () -> assertThat(details.mentee().name()).isEqualTo(mentee.getName()),
                            () -> assertThat(details.mentee().profileImageUrl()).isEqualTo(mentee.getProfileImageUrl()),
                            () -> assertThat(details.mentee().nationality()).isEqualTo(mentee.getNationality().code),
                            () -> assertThat(details.mentee().introduction()).isEqualTo(mentee.getIntroduction()),
                            () -> assertThat(details.mentee().languages().main()).isEqualTo(EN.getCode()),
                            () -> assertThat(details.mentee().languages().sub()).containsExactlyInAnyOrder(KR.getCode()),
                            () -> assertThat(details.mentee().interestSchool()).isEqualTo(mentee.getInterest().getSchool()),
                            () -> assertThat(details.mentee().interestMajor()).isEqualTo(mentee.getInterest().getMajor()),
                            () -> assertThat(details.mentee().status()).isEqualTo(mentee.getStatus().name()),

                            () -> assertThat(details.coffeeChat().id()).isEqualTo(coffeeChat.getId()),
                            () -> assertThat(details.coffeeChat().status()).isEqualTo(MENTEE_PENDING.name()),
                            () -> assertThat(details.coffeeChat().applyReason()).isNull(),
                            () -> assertThat(details.coffeeChat().suggestReason()).isNotNull(),
                            () -> assertThat(details.coffeeChat().cancelReason()).isNull(),
                            () -> assertThat(details.coffeeChat().rejectReason()).isNull(),
                            () -> assertThat(details.coffeeChat().question()).isNotNull(),
                            () -> assertThat(details.coffeeChat().start()).isEqualTo(월요일_1주차_20_00_시작.getStart()),
                            () -> assertThat(details.coffeeChat().end()).isEqualTo(월요일_1주차_20_00_시작.getEnd()),
                            () -> assertThat(details.coffeeChat().chatType()).isNull(),
                            () -> assertThat(details.coffeeChat().chatValue()).isNull()
                    );
                }
        );
    }

    @Test
    @DisplayName("멘티 내 일정의 커피챗 상세 정보를 조회한다")
    void convertForMentee() {
        // given
        final CoffeeChat coffeeChat = MenteeFlow.applyAndApprove(월요일_1주차_20_00_시작, mentee, mentor).apply(1L);

        given(coffeeChatRepository.getById(coffeeChat.getId())).willReturn(coffeeChat);
        given(mentorRepository.getByIdWithNative(coffeeChat.getMentorId())).willReturn(mentor);
        given(menteeRepository.getByIdWithNative(coffeeChat.getMenteeId())).willReturn(mentee);
        given(availableLanguageRepository.findByMemberIdWithNative(mentor.getId())).willReturn(mentor.getAvailableLanguages());
        given(availableLanguageRepository.findByMemberIdWithNative(mentee.getId())).willReturn(mentee.getAvailableLanguages());

        // when
        final CoffeeChatScheduleDetails result = sut.invoke(new GetCoffeeChatScheduleDetails(
                new Authenticated(mentee.getId(), mentee.getAuthority()),
                coffeeChat.getId()
        ));

        // then
        assertAll(
                () -> assertThat(result).isInstanceOf(MenteeCoffeeChatScheduleDetails.class),
                () -> {
                    final MenteeCoffeeChatScheduleDetails details = (MenteeCoffeeChatScheduleDetails) result;
                    assertAll(
                            () -> assertThat(details.mentor().id()).isEqualTo(mentor.getId()),
                            () -> assertThat(details.mentor().name()).isEqualTo(mentor.getName()),
                            () -> assertThat(details.mentor().profileImageUrl()).isEqualTo(mentor.getProfileImageUrl()),
                            () -> assertThat(details.mentor().introduction()).isEqualTo(mentor.getIntroduction()),
                            () -> assertThat(details.mentor().languages().main()).isEqualTo(KR.getCode()),
                            () -> assertThat(details.mentor().languages().sub()).containsExactlyInAnyOrder(EN.getCode()),
                            () -> assertThat(details.mentor().school()).isEqualTo(mentor.getUniversityProfile().getSchool()),
                            () -> assertThat(details.mentor().major()).isEqualTo(mentor.getUniversityProfile().getMajor()),
                            () -> assertThat(details.mentor().enteredIn()).isEqualTo(mentor.getUniversityProfile().getEnteredIn()),
                            () -> assertThat(details.mentor().status()).isEqualTo(mentor.getStatus().name()),

                            () -> assertThat(details.coffeeChat().id()).isEqualTo(coffeeChat.getId()),
                            () -> assertThat(details.coffeeChat().status()).isEqualTo(MENTOR_APPROVE.name()),
                            () -> assertThat(details.coffeeChat().applyReason()).isNotNull(),
                            () -> assertThat(details.coffeeChat().suggestReason()).isNull(),
                            () -> assertThat(details.coffeeChat().cancelReason()).isNull(),
                            () -> assertThat(details.coffeeChat().rejectReason()).isNull(),
                            () -> assertThat(details.coffeeChat().question()).isNotNull(),
                            () -> assertThat(details.coffeeChat().start()).isEqualTo(월요일_1주차_20_00_시작.getStart()),
                            () -> assertThat(details.coffeeChat().end()).isEqualTo(월요일_1주차_20_00_시작.getEnd()),
                            () -> assertThat(details.coffeeChat().chatType()).isEqualTo(월요일_1주차_20_00_시작.getStrategy().getType().getEng()),
                            () -> assertThat(details.coffeeChat().chatValue()).isEqualTo(encryptor.decrypt(월요일_1주차_20_00_시작.getStrategy().getValue()))
                    );
                }
        );
    }

    @Test
    @DisplayName("커피챗 상세 정보를 조회한다 [동일 커피챗 -> 멘토 입장 & 멘티 입장]")
    void invoke() {
        // given
        final CoffeeChat coffeeChat = MentorFlow.suggestAndPending(월요일_1주차_20_00_시작, mentor, mentee).apply(1L);

        given(coffeeChatRepository.getById(coffeeChat.getId())).willReturn(coffeeChat);
        given(mentorRepository.getByIdWithNative(coffeeChat.getMentorId())).willReturn(mentor);
        given(menteeRepository.getByIdWithNative(coffeeChat.getMenteeId())).willReturn(mentee);
        given(availableLanguageRepository.findByMemberIdWithNative(mentor.getId())).willReturn(mentor.getAvailableLanguages());
        given(availableLanguageRepository.findByMemberIdWithNative(mentee.getId())).willReturn(mentee.getAvailableLanguages());

        /* 멘토 입장 */
        final CoffeeChatScheduleDetails mentorResult = sut.invoke(new GetCoffeeChatScheduleDetails(
                new Authenticated(mentor.getId(), mentor.getAuthority()),
                coffeeChat.getId()
        ));
        assertAll(
                () -> assertThat(mentorResult).isInstanceOf(MentorCoffeeChatScheduleDetails.class),
                () -> {
                    final MentorCoffeeChatScheduleDetails details = (MentorCoffeeChatScheduleDetails) mentorResult;
                    assertAll(
                            () -> assertThat(details.mentee().id()).isEqualTo(mentee.getId()),
                            () -> assertThat(details.mentee().name()).isEqualTo(mentee.getName()),
                            () -> assertThat(details.mentee().profileImageUrl()).isEqualTo(mentee.getProfileImageUrl()),
                            () -> assertThat(details.mentee().nationality()).isEqualTo(mentee.getNationality().code),
                            () -> assertThat(details.mentee().introduction()).isEqualTo(mentee.getIntroduction()),
                            () -> assertThat(details.mentee().languages().main()).isEqualTo(EN.getCode()),
                            () -> assertThat(details.mentee().languages().sub()).containsExactlyInAnyOrder(KR.getCode()),
                            () -> assertThat(details.mentee().interestSchool()).isEqualTo(mentee.getInterest().getSchool()),
                            () -> assertThat(details.mentee().interestMajor()).isEqualTo(mentee.getInterest().getMajor()),
                            () -> assertThat(details.mentee().status()).isEqualTo(mentee.getStatus().name()),

                            () -> assertThat(details.coffeeChat().id()).isEqualTo(coffeeChat.getId()),
                            () -> assertThat(details.coffeeChat().status()).isEqualTo(MENTEE_PENDING.name()),
                            () -> assertThat(details.coffeeChat().applyReason()).isNull(),
                            () -> assertThat(details.coffeeChat().suggestReason()).isNotNull(),
                            () -> assertThat(details.coffeeChat().cancelReason()).isNull(),
                            () -> assertThat(details.coffeeChat().rejectReason()).isNull(),
                            () -> assertThat(details.coffeeChat().question()).isNotNull(),
                            () -> assertThat(details.coffeeChat().start()).isEqualTo(월요일_1주차_20_00_시작.getStart()),
                            () -> assertThat(details.coffeeChat().end()).isEqualTo(월요일_1주차_20_00_시작.getEnd()),
                            () -> assertThat(details.coffeeChat().chatType()).isNull(),
                            () -> assertThat(details.coffeeChat().chatValue()).isNull()
                    );
                }
        );

        /* 멘티 입장 */
        final CoffeeChatScheduleDetails menteeResult = sut.invoke(new GetCoffeeChatScheduleDetails(
                new Authenticated(mentee.getId(), mentee.getAuthority()),
                coffeeChat.getId()
        ));
        assertAll(
                () -> assertThat(menteeResult).isInstanceOf(MenteeCoffeeChatScheduleDetails.class),
                () -> {
                    final MenteeCoffeeChatScheduleDetails details = (MenteeCoffeeChatScheduleDetails) menteeResult;
                    assertAll(
                            () -> assertThat(details.mentor().id()).isEqualTo(mentor.getId()),
                            () -> assertThat(details.mentor().name()).isEqualTo(mentor.getName()),
                            () -> assertThat(details.mentor().profileImageUrl()).isEqualTo(mentor.getProfileImageUrl()),
                            () -> assertThat(details.mentor().introduction()).isEqualTo(mentor.getIntroduction()),
                            () -> assertThat(details.mentor().languages().main()).isEqualTo(KR.getCode()),
                            () -> assertThat(details.mentor().languages().sub()).containsExactlyInAnyOrder(EN.getCode()),
                            () -> assertThat(details.mentor().school()).isEqualTo(mentor.getUniversityProfile().getSchool()),
                            () -> assertThat(details.mentor().major()).isEqualTo(mentor.getUniversityProfile().getMajor()),
                            () -> assertThat(details.mentor().enteredIn()).isEqualTo(mentor.getUniversityProfile().getEnteredIn()),
                            () -> assertThat(details.mentor().status()).isEqualTo(mentor.getStatus().name()),

                            () -> assertThat(details.coffeeChat().id()).isEqualTo(coffeeChat.getId()),
                            () -> assertThat(details.coffeeChat().status()).isEqualTo(MENTEE_PENDING.name()),
                            () -> assertThat(details.coffeeChat().applyReason()).isNull(),
                            () -> assertThat(details.coffeeChat().suggestReason()).isNotNull(),
                            () -> assertThat(details.coffeeChat().cancelReason()).isNull(),
                            () -> assertThat(details.coffeeChat().rejectReason()).isNull(),
                            () -> assertThat(details.coffeeChat().question()).isNotNull(),
                            () -> assertThat(details.coffeeChat().start()).isEqualTo(월요일_1주차_20_00_시작.getStart()),
                            () -> assertThat(details.coffeeChat().end()).isEqualTo(월요일_1주차_20_00_시작.getEnd()),
                            () -> assertThat(details.coffeeChat().chatType()).isNull(),
                            () -> assertThat(details.coffeeChat().chatValue()).isNull()
                    );
                }
        );
    }
}
