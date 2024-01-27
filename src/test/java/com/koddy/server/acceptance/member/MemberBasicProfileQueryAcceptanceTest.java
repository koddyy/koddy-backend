package com.koddy.server.acceptance.member;

import com.koddy.server.auth.domain.model.AuthMember;
import com.koddy.server.common.AcceptanceTest;
import com.koddy.server.common.containers.callback.DatabaseCleanerEachCallbackExtension;
import com.koddy.server.member.domain.model.Language;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import java.util.List;

import static com.koddy.server.acceptance.member.MemberAcceptanceStep.멘토_기본_프로필을_조회한다;
import static com.koddy.server.acceptance.member.MemberAcceptanceStep.멘티_기본_프로필을_조회한다;
import static com.koddy.server.common.fixture.MenteeFixture.MENTEE_1;
import static com.koddy.server.common.fixture.MentorFixture.MENTOR_1;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.springframework.http.HttpStatus.OK;

@ExtendWith(DatabaseCleanerEachCallbackExtension.class)
@DisplayName("[Acceptance Test] 사용자 기본(Public) 프로필 조회")
public class MemberBasicProfileQueryAcceptanceTest extends AcceptanceTest {
    @Nested
    @DisplayName("멘토 기본 프로필 조회 API")
    class GetMentorProfile {
        @Test
        @DisplayName("멘토 기본 프로필을 조회한다 (미완성 프로필)")
        void successWithUncomplete() {
            final AuthMember mentor = MENTOR_1.회원가입과_로그인을_진행한다();
            멘토_기본_프로필을_조회한다(mentor.id())
                    .statusCode(OK.value())
                    .body("id", notNullValue(Long.class))
                    .body("name", is(MENTOR_1.getName()))
                    .body("profileImageUrl", is(MENTOR_1.getProfileImageUrl()))
                    .body("introduction", nullValue())
                    .body("languages.main", is(Language.Category.KR.getCode()))
                    .body("languages.sub", containsInAnyOrder(
                            List.of(
                                    Language.Category.EN.getCode(),
                                    Language.Category.JP.getCode(),
                                    Language.Category.CN.getCode()
                            ).toArray()
                    ))
                    .body("school", is(MENTOR_1.getUniversityProfile().getSchool()))
                    .body("major", is(MENTOR_1.getUniversityProfile().getMajor()))
                    .body("enteredIn", is(MENTOR_1.getUniversityProfile().getEnteredIn()));
        }

        @Test
        @DisplayName("멘토 기본 프로필을 조회한다 (완성 프로필)")
        void successWithComplete() {
            final AuthMember mentor = MENTOR_1.회원가입과_로그인을_하고_프로필을_완성시킨다();
            멘토_기본_프로필을_조회한다(mentor.id())
                    .statusCode(OK.value())
                    .body("id", notNullValue(Long.class))
                    .body("name", is(MENTOR_1.getName()))
                    .body("profileImageUrl", is(MENTOR_1.getProfileImageUrl()))
                    .body("introduction", is(MENTOR_1.getIntroduction()))
                    .body("languages.main", is(Language.Category.KR.getCode()))
                    .body("languages.sub", containsInAnyOrder(
                            List.of(
                                    Language.Category.EN.getCode(),
                                    Language.Category.JP.getCode(),
                                    Language.Category.CN.getCode()
                            ).toArray()
                    ))
                    .body("school", is(MENTOR_1.getUniversityProfile().getSchool()))
                    .body("major", is(MENTOR_1.getUniversityProfile().getMajor()))
                    .body("enteredIn", is(MENTOR_1.getUniversityProfile().getEnteredIn()));
        }
    }

    @Nested
    @DisplayName("멘티 기본 프로필 조회 API")
    class GetMenteeProfile {
        @Test
        @DisplayName("멘티 기본 프로필을 조회한다 (미완성 프로필)")
        void successWithUncomplete() {
            final AuthMember mentee = MENTEE_1.회원가입과_로그인을_진행한다();
            멘티_기본_프로필을_조회한다(mentee.id())
                    .statusCode(OK.value())
                    .body("id", notNullValue(Long.class))
                    .body("name", is(MENTEE_1.getName()))
                    .body("profileImageUrl", is(MENTEE_1.getProfileImageUrl()))
                    .body("nationality", is(MENTEE_1.getNationality().getValue()))
                    .body("introduction", nullValue())
                    .body("languages.main", is(Language.Category.KR.getCode()))
                    .body("languages.subs", nullValue())
                    .body("interestSchool", is(MENTEE_1.getInterest().getSchool()))
                    .body("interestMajor", is(MENTEE_1.getInterest().getMajor()));
        }

        @Test
        @DisplayName("멘티 기본 프로필을 조회한다 (완성 프로필)")
        void successWithComplete() {
            final AuthMember mentee = MENTEE_1.회원가입과_로그인을_하고_프로필을_완성시킨다();
            멘티_기본_프로필을_조회한다(mentee.id())
                    .statusCode(OK.value())
                    .body("id", notNullValue(Long.class))
                    .body("name", is(MENTEE_1.getName()))
                    .body("profileImageUrl", is(MENTEE_1.getProfileImageUrl()))
                    .body("nationality", is(MENTEE_1.getNationality().getValue()))
                    .body("introduction", is(MENTEE_1.getIntroduction()))
                    .body("languages.main", is(Language.Category.KR.getCode()))
                    .body("languages.subs", nullValue())
                    .body("interestSchool", is(MENTEE_1.getInterest().getSchool()))
                    .body("interestMajor", is(MENTEE_1.getInterest().getMajor()));
        }
    }
}
