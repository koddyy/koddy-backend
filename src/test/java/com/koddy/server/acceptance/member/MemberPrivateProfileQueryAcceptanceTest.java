package com.koddy.server.acceptance.member;

import com.koddy.server.common.AcceptanceTest;
import com.koddy.server.common.config.DatabaseCleanerEachCallbackExtension;
import com.koddy.server.member.domain.model.Language;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import static com.koddy.server.acceptance.member.MemberAcceptanceStep.멘토_프로필을_완성시킨다;
import static com.koddy.server.acceptance.member.MemberAcceptanceStep.멘토_프로필을_조회한다;
import static com.koddy.server.acceptance.member.MemberAcceptanceStep.멘티_프로필을_완성시킨다;
import static com.koddy.server.acceptance.member.MemberAcceptanceStep.멘티_프로필을_조회한다;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_1;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static com.koddy.server.member.domain.model.Nationality.KOREA;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.http.HttpStatus.OK;

@ExtendWith(DatabaseCleanerEachCallbackExtension.class)
@DisplayName("[Acceptance Test] 사용자 마이페이지 프로필 조회")
public class MemberPrivateProfileQueryAcceptanceTest extends AcceptanceTest {
    @Nested
    @DisplayName("멘토 프로필 조회 API")
    class GetMentorProfile {
        @Test
        @DisplayName("멘토 프로필을 조회한다 (미완성 프로필)")
        void successWithUncomplete() {
            final String accessToken = MENTOR_1.회원가입_로그인_후_AccessToken을_추출한다();
            멘토_프로필을_조회한다(accessToken)
                    .statusCode(OK.value())
                    .body("id", notNullValue(Long.class))
                    .body("email", is(MENTOR_1.getEmail().getValue()))
                    .body("name", is(MENTOR_1.getName()))
                    .body("profileImageUrl", is(MENTOR_1.getProfileImageUrl()))
                    .body("nationality", is(KOREA.getKor()))
                    .body("introduction", nullValue())
                    .body("languages.mainLanguage", is(
                            MENTOR_1.getLanguages()
                                    .stream()
                                    .filter(it -> it.getType() == Language.Type.MAIN)
                                    .findFirst()
                                    .orElseThrow(RuntimeException::new)
                                    .getCategory()
                                    .getValue()
                    ))
                    .body("languages.subLanguages", containsInAnyOrder(
                            MENTOR_1.getLanguages()
                                    .stream()
                                    .filter(it -> it.getType() == Language.Type.SUB)
                                    .map(it -> it.getCategory().getValue())
                                    .toList()
                                    .toArray()
                    ))
                    .body("university.school", is(MENTOR_1.getUniversityProfile().getSchool()))
                    .body("university.major", is(MENTOR_1.getUniversityProfile().getMajor()))
                    .body("university.enteredIn", is(MENTOR_1.getUniversityProfile().getEnteredIn()))
                    .body("schedules", hasSize(0))
                    .body("role", is("mentor"))
                    .body("profileComplete", is(false));
        }

        @Test
        @DisplayName("멘토 프로필을 조회한다 (완성 프로필)")
        void successWithComplete() {
            final String accessToken = MENTOR_1.회원가입_로그인_후_AccessToken을_추출한다();
            멘토_프로필을_완성시킨다(MENTOR_1, accessToken);
            멘토_프로필을_조회한다(accessToken)
                    .statusCode(OK.value())
                    .body("id", notNullValue(Long.class))
                    .body("email", is(MENTOR_1.getEmail().getValue()))
                    .body("name", is(MENTOR_1.getName()))
                    .body("profileImageUrl", is(MENTOR_1.getProfileImageUrl()))
                    .body("nationality", is(KOREA.getKor()))
                    .body("introduction", is(MENTOR_1.getIntroduction()))
                    .body("languages.mainLanguage", is(
                            MENTOR_1.getLanguages()
                                    .stream()
                                    .filter(it -> it.getType() == Language.Type.MAIN)
                                    .findFirst()
                                    .orElseThrow(RuntimeException::new)
                                    .getCategory()
                                    .getValue()
                    ))
                    .body("languages.subLanguages", containsInAnyOrder(
                            MENTOR_1.getLanguages()
                                    .stream()
                                    .filter(it -> it.getType() == Language.Type.SUB)
                                    .map(it -> it.getCategory().getValue())
                                    .toList()
                                    .toArray()
                    ))
                    .body("university.school", is(MENTOR_1.getUniversityProfile().getSchool()))
                    .body("university.major", is(MENTOR_1.getUniversityProfile().getMajor()))
                    .body("university.enteredIn", is(MENTOR_1.getUniversityProfile().getEnteredIn()))
                    .body("schedules", hasSize(MENTOR_1.getTimelines().size()))
                    .body("role", is("mentor"))
                    .body("profileComplete", is(true));
        }
    }

    @Nested
    @DisplayName("멘티 프로필 조회 API")
    class GetMenteeProfile {
        @Test
        @DisplayName("멘티 프로필을 조회한다 (미완성 프로필)")
        void successWithUncomplete() {
            final String accessToken = MENTEE_1.회원가입_로그인_후_AccessToken을_추출한다();
            멘티_프로필을_조회한다(accessToken)
                    .statusCode(OK.value())
                    .body("id", notNullValue(Long.class))
                    .body("email", is(MENTEE_1.getEmail().getValue()))
                    .body("name", is(MENTEE_1.getName()))
                    .body("profileImageUrl", is(MENTEE_1.getProfileImageUrl()))
                    .body("nationality", is(MENTEE_1.getNationality().getKor()))
                    .body("introduction", nullValue())
                    .body("languages.mainLanguage", is(
                            MENTEE_1.getLanguages()
                                    .stream()
                                    .filter(it -> it.getType() == Language.Type.MAIN)
                                    .findFirst()
                                    .orElseThrow(RuntimeException::new)
                                    .getCategory()
                                    .getValue()
                    ))
                    .body("languages.subLanguages", containsInAnyOrder(
                            MENTEE_1.getLanguages()
                                    .stream()
                                    .filter(it -> it.getType() == Language.Type.SUB)
                                    .map(it -> it.getCategory().getValue())
                                    .toList()
                                    .toArray()
                    ))
                    .body("interest.school", is(MENTEE_1.getInterest().getSchool()))
                    .body("interest.major", is(MENTEE_1.getInterest().getMajor()))
                    .body("role", is("mentee"))
                    .body("profileComplete", is(false));
        }

        @Test
        @DisplayName("멘티 프로필을 조회한다 (완성 프로필)")
        void successWithComplete() {
            final String accessToken = MENTEE_1.회원가입_로그인_후_AccessToken을_추출한다();
            멘티_프로필을_완성시킨다(MENTEE_1, accessToken);
            멘티_프로필을_조회한다(accessToken)
                    .statusCode(OK.value())
                    .body("id", notNullValue(Long.class))
                    .body("email", is(MENTEE_1.getEmail().getValue()))
                    .body("name", is(MENTEE_1.getName()))
                    .body("profileImageUrl", is(MENTEE_1.getProfileImageUrl()))
                    .body("nationality", is(MENTEE_1.getNationality().getKor()))
                    .body("introduction", is(MENTEE_1.getIntroduction()))
                    .body("languages.mainLanguage", is(
                            MENTEE_1.getLanguages()
                                    .stream()
                                    .filter(it -> it.getType() == Language.Type.MAIN)
                                    .findFirst()
                                    .orElseThrow(RuntimeException::new)
                                    .getCategory()
                                    .getValue()
                    ))
                    .body("languages.subLanguages", containsInAnyOrder(
                            MENTEE_1.getLanguages()
                                    .stream()
                                    .filter(it -> it.getType() == Language.Type.SUB)
                                    .map(it -> it.getCategory().getValue())
                                    .toList()
                                    .toArray()
                    ))
                    .body("interest.school", is(MENTEE_1.getInterest().getSchool()))
                    .body("interest.major", is(MENTEE_1.getInterest().getMajor()))
                    .body("role", is("mentee"))
                    .body("profileComplete", is(true));
        }
    }
}
