package com.koddy.server.coffeechat.presentation;

import com.koddy.server.coffeechat.application.usecase.GetCoffeeChatScheduleDetailsUseCase;
import com.koddy.server.coffeechat.application.usecase.query.response.MenteeCoffeeChatScheduleDetails;
import com.koddy.server.coffeechat.application.usecase.query.response.MentorCoffeeChatScheduleDetails;
import com.koddy.server.coffeechat.domain.model.CoffeeChat;
import com.koddy.server.coffeechat.domain.model.response.CoffeeChatDetails;
import com.koddy.server.coffeechat.domain.model.response.MenteeDetails;
import com.koddy.server.coffeechat.domain.model.response.MentorDetails;
import com.koddy.server.common.ControllerTest;
import com.koddy.server.common.fixture.CoffeeChatFixture;
import com.koddy.server.member.domain.model.mentee.Mentee;
import com.koddy.server.member.domain.model.mentor.Mentor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import static com.koddy.server.common.fixture.CoffeeChatFixture.월요일_1주차_20_00_시작;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_1;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static com.koddy.server.common.utils.EncryptorFactory.getEncryptor;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.SnippetFactory.body;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.SnippetFactory.path;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.createHttpSpecSnippets;
import static com.koddy.server.common.utils.RestDocsSpecificationUtils.successDocsWithAccessToken;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.request.RequestDocumentation.pathParameters;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayName("CoffeeChat -> CoffeeChatScheduleDetailsQueryApiController 테스트")
class CoffeeChatScheduleDetailsQueryApiControllerTest extends ControllerTest {
    @Autowired
    private GetCoffeeChatScheduleDetailsUseCase getCoffeeChatScheduleDetailsUseCase;

    private final Mentor mentor = MENTOR_1.toDomain().apply(1L);
    private final Mentee mentee = MENTEE_1.toDomain().apply(2L);

    @Nested
    @DisplayName("내 일정 커피챗 상세 조회 API [GET /api/coffeechats/{coffeeChatId}]")
    class GetCoffeeChatScheduleDetails {
        private static final String BASE_URL = "/api/coffeechats/{coffeeChatId}";

        @Test
        @DisplayName("내 일정 커피챗 상세 조회를 진행한다 (멘토 입장)")
        void mentor() {
            // given
            final CoffeeChat coffeeChat = CoffeeChatFixture.MentorFlow.suggestAndPending(월요일_1주차_20_00_시작, mentor, mentee).apply(1L);

            applyToken(true, mentor);
            given(getCoffeeChatScheduleDetailsUseCase.invoke(any())).willReturn(new MentorCoffeeChatScheduleDetails(
                    MenteeDetails.from(mentee),
                    CoffeeChatDetails.of(coffeeChat, getEncryptor())
            ));

            // when - then
            successfulExecute(
                    getRequestWithAccessToken(new UrlWithVariables(BASE_URL, coffeeChat.getId())),
                    status().isOk(),
                    successDocsWithAccessToken("CoffeeChatApi/ScheduleDetails/Mentor", createHttpSpecSnippets(
                            pathParameters(
                                    path("coffeeChatId", "커피챗 ID(PK)", true)
                            ),
                            responseFields(
                                    body("mentee.id", "멘티 ID(PK)"),
                                    body("mentee.name", "이름"),
                                    body("mentee.profileImageUrl", "프로필 이미지 URL"),
                                    body("mentee.nationality", "국적", "KR EN CN JP VN ETC"),
                                    body("mentee.introduction", "자기 소개", "Nullable"),
                                    body("mentee.languages", "사용 가능한 언어", "KR EN CN JP VN"),
                                    body("mentee.languages.main", "메인 언어", "1개"),
                                    body("mentee.languages.sub[]", "서브 언어", "0..N개"),
                                    body("mentee.interestSchool", "관심있는 학교"),
                                    body("mentee.interestMajor", "관심있는 전공"),

                                    body("coffeeChat.id", "커피챗 ID(PK)"),
                                    body("coffeeChat.status", "커피챗 상태"),
                                    body("coffeeChat.applyReason", "신청/제안 이유"),
                                    body("coffeeChat.question", "궁금한 점", "Nullable"),
                                    body("coffeeChat.rejectReason", "거절 사유", "Nullable"),
                                    body("coffeeChat.start", "시작 날짜", "Nullable"),
                                    body("coffeeChat.end", "종료 날짜", "Nullable"),
                                    body("coffeeChat.chatType", "진행 방식", "Nullable"),
                                    body("coffeeChat.chatValue", "진행 방식에 대한 값", "Nullable")
                            )
                    ))
            );
        }

        @Test
        @DisplayName("내 일정 커피챗 상세 조회를 진행한다 (멘티 입장)")
        void mentee() {
            // given
            final CoffeeChat coffeeChat = CoffeeChatFixture.MenteeFlow.applyAndApprove(월요일_1주차_20_00_시작, mentee, mentor).apply(1L);

            applyToken(true, mentee);
            given(getCoffeeChatScheduleDetailsUseCase.invoke(any())).willReturn(new MenteeCoffeeChatScheduleDetails(
                    MentorDetails.from(mentor),
                    CoffeeChatDetails.of(coffeeChat, getEncryptor())
            ));

            // when - then
            successfulExecute(
                    getRequestWithAccessToken(new UrlWithVariables(BASE_URL, coffeeChat.getId())),
                    status().isOk(),
                    successDocsWithAccessToken("CoffeeChatApi/ScheduleDetails/Mentee", createHttpSpecSnippets(
                            pathParameters(
                                    path("coffeeChatId", "커피챗 ID(PK)", true)
                            ),
                            responseFields(
                                    body("mentor.id", "멘토 ID(PK)"),
                                    body("mentor.name", "이름"),
                                    body("mentor.profileImageUrl", "프로필 이미지 URL"),
                                    body("mentor.introduction", "자기 소개", "Nullable"),
                                    body("mentor.languages", "사용 가능한 언어", "KR EN CN JP VN"),
                                    body("mentor.languages.main", "메인 언어", "1개"),
                                    body("mentor.languages.sub[]", "서브 언어", "0..N개"),
                                    body("mentor.school", "학교"),
                                    body("mentor.major", "전공"),
                                    body("mentor.enteredIn", "학번"),

                                    body("coffeeChat.id", "커피챗 ID(PK)"),
                                    body("coffeeChat.status", "커피챗 상태"),
                                    body("coffeeChat.applyReason", "신청/제안 이유"),
                                    body("coffeeChat.question", "궁금한 점", "Nullable"),
                                    body("coffeeChat.rejectReason", "거절 사유", "Nullable"),
                                    body("coffeeChat.start", "시작 날짜", "Nullable"),
                                    body("coffeeChat.end", "종료 날짜", "Nullable"),
                                    body("coffeeChat.chatType", "진행 방식", "Nullable"),
                                    body("coffeeChat.chatValue", "진행 방식에 대한 값", "Nullable")
                            )
                    ))
            );
        }
    }
}
