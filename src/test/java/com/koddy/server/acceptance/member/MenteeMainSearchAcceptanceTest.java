package com.koddy.server.acceptance.member;

import com.koddy.server.auth.domain.model.AuthMember;
import com.koddy.server.auth.domain.model.AuthToken;
import com.koddy.server.common.AcceptanceTest;
import com.koddy.server.common.containers.callback.DatabaseCleanerAllCallbackExtension;
import com.koddy.server.common.fixture.MenteeFixture;
import com.koddy.server.common.fixture.MentorFixture;
import com.koddy.server.member.domain.model.Language;
import com.koddy.server.member.presentation.dto.request.LanguageRequest;
import com.koddy.server.member.presentation.dto.request.SignUpMenteeRequest;
import com.koddy.server.member.presentation.dto.request.SignUpMentorRequest;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import io.restassured.response.ValidatableResponse;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Arrays;
import java.util.List;

import static com.koddy.server.acceptance.coffeechat.CoffeeChatAcceptanceStep.멘토가_멘티에게_커피챗을_제안하고_ID를_추출한다;
import static com.koddy.server.acceptance.coffeechat.CoffeeChatAcceptanceStep.신청_제안한_커피챗을_취소한다;
import static com.koddy.server.acceptance.member.MemberAcceptanceStep.멘토들을_둘러본다;
import static com.koddy.server.acceptance.member.MemberAcceptanceStep.커피챗_제안한_멘토를_조회한다;
import static com.koddy.server.auth.domain.model.AuthToken.ACCESS_TOKEN_HEADER;
import static com.koddy.server.auth.domain.model.AuthToken.REFRESH_TOKEN_HEADER;
import static com.koddy.server.auth.exception.AuthExceptionCode.INVALID_PERMISSION;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_1;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_10;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_11;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_12;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_13;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_14;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_15;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_16;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_17;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_18;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_19;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_2;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_20;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_3;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_4;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_5;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_6;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_7;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_8;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_9;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.OK;

@ExtendWith(DatabaseCleanerAllCallbackExtension.class)
@DisplayName("[Acceptance Test] 멘티 메인 홈 조회 - 커피챗 제안한 멘토, 멘토 둘러보기")
public class MenteeMainSearchAcceptanceTest extends AcceptanceTest {
    private static AuthMember mentee;
    private static final AuthMember[] mentors = new AuthMember[20];

    @BeforeAll
    static void setUp() {
        mentee = createMentee(MENTEE_1);

        final List<MentorFixture> fixtures = Arrays.stream(MentorFixture.values())
                .limit(20)
                .toList();
        Arrays.setAll(mentors, it -> createMentor(fixtures.get(it)));
    }

    @Nested
    @DisplayName("커피챗 제안한 멘토 조회 API")
    class GetSuggestedMentors {
        @Test
        @DisplayName("멘티가 아니면 권한이 없다")
        void throwExceptionByInvalidPermission() {
            커피챗_제안한_멘토를_조회한다(mentors[0].token().accessToken())
                    .statusCode(FORBIDDEN.value())
                    .body("errorCode", is(INVALID_PERMISSION.getErrorCode()))
                    .body("message", is(INVALID_PERMISSION.getMessage()));
        }

        @Test
        @DisplayName("커피챗을 제안한 멘토들을 조회한다 [Limit 3]")
        void success() {
            final long coffeeChatId1 = 멘토가_멘티에게_커피챗을_제안하고_ID를_추출한다(mentee.id(), mentors[0].token().accessToken());
            final long coffeeChatId2 = 멘토가_멘티에게_커피챗을_제안하고_ID를_추출한다(mentee.id(), mentors[1].token().accessToken());
            final long coffeeChatId3 = 멘토가_멘티에게_커피챗을_제안하고_ID를_추출한다(mentee.id(), mentors[2].token().accessToken());
            final long coffeeChatId4 = 멘토가_멘티에게_커피챗을_제안하고_ID를_추출한다(mentee.id(), mentors[3].token().accessToken());
            final long coffeeChatId5 = 멘토가_멘티에게_커피챗을_제안하고_ID를_추출한다(mentee.id(), mentors[4].token().accessToken());

            /* 5명 제안 */
            final ValidatableResponse response1 = 커피챗_제안한_멘토를_조회한다(mentee.token().accessToken())
                    .statusCode(OK.value());
            assertMenteesMatch(
                    response1,
                    List.of(MENTOR_5, MENTOR_4, MENTOR_3),
                    List.of(mentors[4].id(), mentors[3].id(), mentors[2].id()),
                    5L
            );

            /* 2명 취소 */
            신청_제안한_커피챗을_취소한다(coffeeChatId4, mentors[3].token().accessToken());
            신청_제안한_커피챗을_취소한다(coffeeChatId2, mentors[1].token().accessToken());

            final ValidatableResponse response2 = 커피챗_제안한_멘토를_조회한다(mentee.token().accessToken())
                    .statusCode(OK.value());
            assertMenteesMatch(
                    response2,
                    List.of(MENTOR_5, MENTOR_3, MENTOR_1),
                    List.of(mentors[4].id(), mentors[2].id(), mentors[0].id()),
                    3L
            );
        }
    }

    @Nested
    @DisplayName("멘토 둘러보기 API")
    class GetMentorsByCondition {
        @Test
        @DisplayName("멘토 둘러보기를 진행한다")
        void success() {
            /* 최신 가입순 */
            final String url1 = UriComponentsBuilder
                    .fromUriString("/api/mentors?page=1")
                    .build()
                    .toUriString();
            final ValidatableResponse response1 = 멘토들을_둘러본다(url1).statusCode(OK.value());
            assertMenteesMatch(
                    response1,
                    List.of(
                            MENTOR_20, MENTOR_19, MENTOR_18, MENTOR_17, MENTOR_16,
                            MENTOR_15, MENTOR_14, MENTOR_13, MENTOR_12, MENTOR_11
                    ),
                    List.of(
                            mentors[19].id(), mentors[18].id(), mentors[17].id(), mentors[16].id(), mentors[15].id(),
                            mentors[14].id(), mentors[13].id(), mentors[12].id(), mentors[11].id(), mentors[10].id()
                    ),
                    true
            );

            final String url2 = UriComponentsBuilder
                    .fromUriString("/api/mentors?page=2")
                    .build()
                    .toUriString();
            final ValidatableResponse response2 = 멘토들을_둘러본다(url2).statusCode(OK.value());
            assertMenteesMatch(
                    response2,
                    List.of(
                            MENTOR_10, MENTOR_9, MENTOR_8, MENTOR_7, MENTOR_6,
                            MENTOR_5, MENTOR_4, MENTOR_3, MENTOR_2, MENTOR_1
                    ),
                    List.of(
                            mentors[9].id(), mentors[8].id(), mentors[7].id(), mentors[6].id(), mentors[5].id(),
                            mentors[4].id(), mentors[3].id(), mentors[2].id(), mentors[1].id(), mentors[0].id()
                    ),
                    false
            );

            /* 최신 가입순 + 언어 */
            final String url3 = UriComponentsBuilder
                    .fromUriString("/api/mentors?page=1&languages=KR,EN")
                    .build()
                    .toUriString();
            final ValidatableResponse response3 = 멘토들을_둘러본다(url3).statusCode(OK.value());
            assertMenteesMatch(
                    response3,
                    List.of(
                            MENTOR_19, MENTOR_17, MENTOR_15, MENTOR_13, MENTOR_11,
                            MENTOR_9, MENTOR_7, MENTOR_5, MENTOR_3, MENTOR_1
                    ),
                    List.of(
                            mentors[18].id(), mentors[16].id(), mentors[14].id(), mentors[12].id(), mentors[10].id(),
                            mentors[8].id(), mentors[6].id(), mentors[4].id(), mentors[2].id(), mentors[0].id()
                    ),
                    false
            );

            final String url4 = UriComponentsBuilder
                    .fromUriString("/api/mentors?page=2&languages=KR,EN")
                    .build()
                    .toUriString();
            final ValidatableResponse response4 = 멘토들을_둘러본다(url4).statusCode(OK.value());
            assertMenteesMatch(
                    response4,
                    List.of(),
                    List.of(),
                    false
            );

            final String url5 = UriComponentsBuilder
                    .fromUriString("/api/mentors?page=1&languages=KR,JP")
                    .build()
                    .toUriString();
            final ValidatableResponse response5 = 멘토들을_둘러본다(url5).statusCode(OK.value());
            assertMenteesMatch(
                    response5,
                    List.of(
                            MENTOR_20, MENTOR_18, MENTOR_16, MENTOR_14, MENTOR_12,
                            MENTOR_10, MENTOR_8, MENTOR_6, MENTOR_4, MENTOR_2
                    ),
                    List.of(
                            mentors[19].id(), mentors[17].id(), mentors[15].id(), mentors[13].id(), mentors[11].id(),
                            mentors[9].id(), mentors[7].id(), mentors[5].id(), mentors[3].id(), mentors[1].id()
                    ),
                    false
            );

            final String url6 = UriComponentsBuilder
                    .fromUriString("/api/mentors?page=2&languages=KR,JP")
                    .build()
                    .toUriString();
            final ValidatableResponse response6 = 멘토들을_둘러본다(url6).statusCode(OK.value());
            assertMenteesMatch(
                    response6,
                    List.of(),
                    List.of(),
                    false
            );
        }
    }

    private void assertMenteesMatch(
            final ValidatableResponse response,
            final List<MentorFixture> mentees,
            final List<Long> ids,
            final Long totalCount
    ) {
        final int totalSize = mentees.size();
        response
                .body("result", hasSize(totalSize))
                .body("totalCount", is(totalCount.intValue()));

        for (int i = 0; i < totalSize; i++) {
            final String index = String.format("result[%d]", i);
            final MentorFixture mentor = mentees.get(i);
            final Long id = ids.get(i);

            response
                    .body(index + ".id", is(id.intValue()))
                    .body(index + ".name", is(mentor.getName()))
                    .body(index + ".profileImageUrl", is(mentor.getProfileImageUrl()))
                    .body(index + ".school", is(mentor.getUniversityProfile().getSchool()))
                    .body(index + ".major", is(mentor.getUniversityProfile().getMajor()))
                    .body(index + ".enteredIn", is(mentor.getUniversityProfile().getEnteredIn()));
        }
    }

    private void assertMenteesMatch(
            final ValidatableResponse response,
            final List<MentorFixture> mentees,
            final List<Long> ids,
            final boolean hasNext
    ) {
        final int totalSize = mentees.size();
        response
                .body("result", hasSize(totalSize))
                .body("hasNext", is(hasNext));

        for (int i = 0; i < totalSize; i++) {
            final String index = String.format("result[%d]", i);
            final MentorFixture mentor = mentees.get(i);
            final Long id = ids.get(i);

            response
                    .body(index + ".id", is(id.intValue()))
                    .body(index + ".name", is(mentor.getName()))
                    .body(index + ".profileImageUrl", is(mentor.getProfileImageUrl()))
                    .body(index + ".school", is(mentor.getUniversityProfile().getSchool()))
                    .body(index + ".major", is(mentor.getUniversityProfile().getMajor()))
                    .body(index + ".enteredIn", is(mentor.getUniversityProfile().getEnteredIn()));
        }
    }

    private static AuthMember createMentor(final MentorFixture fixture) {
        final SignUpMentorRequest request = new SignUpMentorRequest(
                fixture.getPlatform().getProvider().getValue(),
                fixture.getPlatform().getSocialId(),
                fixture.getPlatform().getEmail().getValue(),
                fixture.getName(),
                fixture.getProfileImageUrl(),
                new LanguageRequest(
                        fixture.getLanguages()
                                .stream()
                                .filter(it -> it.getType() == Language.Type.MAIN)
                                .toList()
                                .get(0)
                                .getCategory()
                                .getCode(),
                        fixture.getLanguages()
                                .stream()
                                .filter(it -> it.getType() == Language.Type.SUB)
                                .map(it -> it.getCategory().getCode())
                                .toList()
                ),
                fixture.getUniversityProfile().getSchool(),
                fixture.getUniversityProfile().getMajor(),
                fixture.getUniversityProfile().getEnteredIn()
        );

        final ExtractableResponse<Response> result = MemberAcceptanceStep.멘토_회원가입_후_로그인을_진행한다(request).extract();
        final long memberId = result.jsonPath().getLong("id");
        final String accessToken = result.header(ACCESS_TOKEN_HEADER).split(" ")[1];
        final String refreshToken = result.cookie(REFRESH_TOKEN_HEADER);

        MemberAcceptanceStep.멘토_프로필을_완성시킨다(fixture, accessToken);
        return new AuthMember(
                memberId,
                fixture.getName(),
                fixture.getProfileImageUrl(),
                new AuthToken(accessToken, refreshToken)
        );
    }

    private static AuthMember createMentee(final MenteeFixture fixture) {
        final SignUpMenteeRequest request = new SignUpMenteeRequest(
                fixture.getPlatform().getProvider().getValue(),
                fixture.getPlatform().getSocialId(),
                fixture.getPlatform().getEmail().getValue(),
                fixture.getName(),
                fixture.getProfileImageUrl(),
                fixture.getNationality().getCode(),
                new LanguageRequest(
                        fixture.getLanguages()
                                .stream()
                                .filter(it -> it.getType() == Language.Type.MAIN)
                                .toList()
                                .get(0)
                                .getCategory()
                                .getCode(),
                        fixture.getLanguages()
                                .stream()
                                .filter(it -> it.getType() == Language.Type.SUB)
                                .map(it -> it.getCategory().getCode())
                                .toList()
                ),
                fixture.getInterest().getSchool(),
                fixture.getInterest().getMajor()
        );

        final ExtractableResponse<Response> result = MemberAcceptanceStep.멘티_회원가입_후_로그인을_진행한다(request).extract();
        final long memberId = result.jsonPath().getLong("id");
        final String accessToken = result.header(ACCESS_TOKEN_HEADER).split(" ")[1];
        final String refreshToken = result.cookie(REFRESH_TOKEN_HEADER);

        MemberAcceptanceStep.멘티_프로필을_완성시킨다(fixture, accessToken);
        return new AuthMember(
                memberId,
                fixture.getName(),
                fixture.getProfileImageUrl(),
                new AuthToken(accessToken, refreshToken)
        );
    }
}
