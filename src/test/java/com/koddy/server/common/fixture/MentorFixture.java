package com.koddy.server.common.fixture;

import com.koddy.server.acceptance.member.MemberAcceptanceStep;
import com.koddy.server.auth.domain.model.AuthMember;
import com.koddy.server.auth.domain.model.AuthToken;
import com.koddy.server.auth.infrastructure.social.google.response.GoogleUserResponse;
import com.koddy.server.auth.infrastructure.social.kakao.response.KakaoUserResponse;
import com.koddy.server.auth.infrastructure.social.zoom.response.ZoomUserResponse;
import com.koddy.server.member.domain.model.Email;
import com.koddy.server.member.domain.model.Language;
import com.koddy.server.member.domain.model.mentor.Mentor;
import com.koddy.server.member.domain.model.mentor.MentoringPeriod;
import com.koddy.server.member.domain.model.mentor.Timeline;
import com.koddy.server.member.domain.model.mentor.UniversityProfile;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.koddy.server.auth.utils.TokenResponseWriter.COOKIE_REFRESH_TOKEN;
import static com.koddy.server.common.utils.TokenUtils.ACCESS_TOKEN;
import static com.koddy.server.common.utils.TokenUtils.REFRESH_TOKEN;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Getter
@RequiredArgsConstructor
public enum MentorFixture {
    MENTOR_1(
            Email.from("mentor1@gmail.com"), "멘토1", "s3/Mentor1.png",
            "Hello World~", LanguageFixture.메인_한국어_서브_영어(),
            new UniversityProfile("경기대학교", "컴퓨터공학부", 18),
            MentoringPeriodFixture.FROM_01_01_TO_12_31.toDomain(), TimelineFixture.월_수_금()
    ),
    MENTOR_2(
            Email.from("mentor2@gmail.com"), "멘토2", "s3/Mentor2.png",
            "Hello World~", LanguageFixture.메인_한국어_서브_일본어_중국어(),
            new UniversityProfile("서울대학교", "컴퓨터공학부", 19),
            MentoringPeriodFixture.FROM_01_01_TO_12_31.toDomain(), TimelineFixture.화_목_토()
    ),
    MENTOR_3(
            Email.from("mentor3@gmail.com"), "멘토3", "s3/Mentor3.png",
            "Hello World~", LanguageFixture.메인_한국어_서브_영어(),
            new UniversityProfile("연세대학교", "컴퓨터공학부", 18),
            MentoringPeriodFixture.FROM_01_01_TO_12_31.toDomain(), TimelineFixture.월_화_수_목_금()
    ),
    MENTOR_4(
            Email.from("mentor4@gmail.com"), "멘토4", "s3/Mentor4.png",
            "Hello World~", LanguageFixture.메인_한국어_서브_일본어_중국어(),
            new UniversityProfile("고려대학교", "컴퓨터공학부", 19),
            MentoringPeriodFixture.FROM_01_01_TO_12_31.toDomain(), TimelineFixture.주말()
    ),
    MENTOR_5(
            Email.from("mentor5@gmail.com"), "멘토5", "s3/Mentor5.png",
            "Hello World~", LanguageFixture.메인_한국어_서브_영어(),
            new UniversityProfile("한양대학교", "컴퓨터공학부", 18),
            MentoringPeriodFixture.FROM_01_01_TO_12_31.toDomain(), TimelineFixture.allDays()
    ),

    MENTOR_6(
            Email.from("mentor6@gmail.com"), "멘토6", "s3/Mentor6.png",
            "Hello World~", LanguageFixture.메인_한국어_서브_일본어_중국어(),
            new UniversityProfile("경기대학교", "컴퓨터공학부", 19),
            MentoringPeriodFixture.FROM_01_01_TO_12_31.toDomain(), TimelineFixture.월_수_금()
    ),
    MENTOR_7(
            Email.from("mentor7@gmail.com"), "멘토7", "s3/Mentor7.png",
            "Hello World~", LanguageFixture.메인_한국어_서브_영어(),
            new UniversityProfile("서울대학교", "컴퓨터공학부", 18),
            MentoringPeriodFixture.FROM_01_01_TO_12_31.toDomain(), TimelineFixture.화_목_토()
    ),
    MENTOR_8(
            Email.from("mentor8@gmail.com"), "멘토8", "s3/Mentor8.png",
            "Hello World~", LanguageFixture.메인_한국어_서브_일본어_중국어(),
            new UniversityProfile("연세대학교", "컴퓨터공학부", 19),
            MentoringPeriodFixture.FROM_01_01_TO_12_31.toDomain(), TimelineFixture.월_화_수_목_금()
    ),
    MENTOR_9(
            Email.from("mentor9@gmail.com"), "멘토9", "s3/Mentor9.png",
            "Hello World~", LanguageFixture.메인_한국어_서브_영어(),
            new UniversityProfile("고려대학교", "컴퓨터공학부", 18),
            MentoringPeriodFixture.FROM_01_01_TO_12_31.toDomain(), TimelineFixture.주말()
    ),
    MENTOR_10(
            Email.from("mentor10@gmail.com"), "멘토10", "s3/Mentor10.png",
            "Hello World~", LanguageFixture.메인_한국어_서브_일본어_중국어(),
            new UniversityProfile("한양대학교", "컴퓨터공학부", 19),
            MentoringPeriodFixture.FROM_01_01_TO_12_31.toDomain(), TimelineFixture.allDays()
    ),
    MENTOR_11(
            Email.from("mentor11@gmail.com"), "멘토11", "s3/Mentor11.png",
            "Hello World~", LanguageFixture.메인_한국어_서브_영어(),
            new UniversityProfile("경기대학교", "컴퓨터공학부", 18),
            MentoringPeriodFixture.FROM_01_01_TO_12_31.toDomain(), TimelineFixture.월_수_금()
    ),
    MENTOR_12(
            Email.from("mentor12@gmail.com"), "멘토12", "s3/Mentor12.png",
            "Hello World~", LanguageFixture.메인_한국어_서브_일본어_중국어(),
            new UniversityProfile("서울대학교", "컴퓨터공학부", 19),
            MentoringPeriodFixture.FROM_01_01_TO_12_31.toDomain(), TimelineFixture.화_목_토()
    ),
    MENTOR_13(
            Email.from("mentor13@gmail.com"), "멘토13", "s3/Mentor13.png",
            "Hello World~", LanguageFixture.메인_한국어_서브_영어(),
            new UniversityProfile("연세대학교", "컴퓨터공학부", 18),
            MentoringPeriodFixture.FROM_01_01_TO_12_31.toDomain(), TimelineFixture.월_화_수_목_금()
    ),
    MENTOR_14(
            Email.from("mentor14@gmail.com"), "멘토14", "s3/Mentor14.png",
            "Hello World~", LanguageFixture.메인_한국어_서브_일본어_중국어(),
            new UniversityProfile("고려대학교", "컴퓨터공학부", 19),
            MentoringPeriodFixture.FROM_01_01_TO_12_31.toDomain(), TimelineFixture.주말()
    ),
    MENTOR_15(
            Email.from("mentor15@gmail.com"), "멘토15", "s3/Mentor15.png",
            "Hello World~", LanguageFixture.메인_한국어_서브_영어(),
            new UniversityProfile("한양대학교", "컴퓨터공학부", 18),
            MentoringPeriodFixture.FROM_01_01_TO_12_31.toDomain(), TimelineFixture.allDays()
    ),

    MENTOR_16(
            Email.from("mentor16@gmail.com"), "멘토16", "s3/Mentor16.png",
            "Hello World~", LanguageFixture.메인_한국어_서브_일본어_중국어(),
            new UniversityProfile("경기대학교", "컴퓨터공학부", 19),
            MentoringPeriodFixture.FROM_01_01_TO_12_31.toDomain(), TimelineFixture.월_수_금()
    ),
    MENTOR_17(
            Email.from("mentor17@gmail.com"), "멘토17", "s3/Mentor17.png",
            "Hello World~", LanguageFixture.메인_한국어_서브_영어(),
            new UniversityProfile("서울대학교", "컴퓨터공학부", 18),
            MentoringPeriodFixture.FROM_01_01_TO_12_31.toDomain(), TimelineFixture.화_목_토()
    ),
    MENTOR_18(
            Email.from("mentor18@gmail.com"), "멘토18", "s3/Mentor18.png",
            "Hello World~", LanguageFixture.메인_한국어_서브_일본어_중국어(),
            new UniversityProfile("연세대학교", "컴퓨터공학부", 19),
            MentoringPeriodFixture.FROM_01_01_TO_12_31.toDomain(), TimelineFixture.월_화_수_목_금()
    ),
    MENTOR_19(
            Email.from("mentor19@gmail.com"), "멘토19", "s3/Mentor19.png",
            "Hello World~", LanguageFixture.메인_한국어_서브_영어(),
            new UniversityProfile("고려대학교", "컴퓨터공학부", 18),
            MentoringPeriodFixture.FROM_01_01_TO_12_31.toDomain(), TimelineFixture.주말()
    ),
    MENTOR_20(
            Email.from("mentor20@gmail.com"), "멘토10", "s3/Mentor20.png",
            "Hello World~", LanguageFixture.메인_한국어_서브_일본어_중국어(),
            new UniversityProfile("한양대학교", "컴퓨터공학부", 19),
            MentoringPeriodFixture.FROM_01_01_TO_12_31.toDomain(), TimelineFixture.allDays()
    ),
    ;

    private final Email email;
    private final String name;
    private final String profileImageUrl;
    private final String introduction;
    private final List<Language> languages;
    private final UniversityProfile universityProfile;
    private final MentoringPeriod mentoringPeriod;
    private final List<Timeline> timelines;

    public Mentor toDomain() {
        final Mentor mentor = new Mentor(email, name, profileImageUrl, languages, universityProfile);
        mentor.completeInfo(introduction, mentoringPeriod, timelines);
        return mentor;
    }

    public Mentor toDomainWithLanguages(final List<Language> languages) {
        final Mentor mentor = new Mentor(email, name, profileImageUrl, languages, universityProfile);
        mentor.completeInfo(introduction, mentoringPeriod, timelines);
        return mentor;
    }

    public Mentor toDomainWithMentoringInfo(final MentoringPeriod mentoringPeriod, final List<Timeline> timelines) {
        final Mentor mentor = new Mentor(email, name, profileImageUrl, languages, universityProfile);
        mentor.completeInfo(introduction, mentoringPeriod, timelines);
        return mentor;
    }

    public Mentor toDomainWithLanguagesAndMentoringInfo(
            final List<Language> languages,
            final MentoringPeriod mentoringPeriod,
            final List<Timeline> timelines
    ) {
        final Mentor mentor = new Mentor(email, name, profileImageUrl, languages, universityProfile);
        mentor.completeInfo(introduction, mentoringPeriod, timelines);
        return mentor;
    }

    public GoogleUserResponse toGoogleUserResponse() {
        return new GoogleUserResponse(
                UUID.randomUUID().toString(),
                this.name,
                this.name,
                this.name,
                this.profileImageUrl,
                this.email.getValue(),
                true,
                "kr"
        );
    }

    public KakaoUserResponse toKakaoUserResponse() {
        return new KakaoUserResponse(
                UUID.randomUUID().toString(),
                LocalDateTime.now(),
                null
        );
    }

    public ZoomUserResponse toZoomUserResponse() {
        return new ZoomUserResponse(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                this.name,
                this.name,
                this.name,
                this.email.getValue(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                "Asia/Seoul",
                UUID.randomUUID().toString()
        );
    }

    public AuthMember toAuthMember() {
        return new AuthMember(
                this.toDomain().apply(1L),
                new AuthToken(ACCESS_TOKEN, REFRESH_TOKEN)
        );
    }

    public AuthMember 회원가입과_로그인을_진행한다() {
        final ExtractableResponse<Response> result = MemberAcceptanceStep.멘토_회원가입_후_로그인을_진행한다(this).extract();
        final long memberId = result.jsonPath().getLong("id");
        final String accessToken = result.header(AUTHORIZATION).split(" ")[1];
        final String refreshToken = result.cookie(COOKIE_REFRESH_TOKEN);

        return new AuthMember(
                memberId,
                this.name,
                this.profileImageUrl,
                new AuthToken(accessToken, refreshToken)
        );
    }

    public AuthMember 회원가입과_로그인을_하고_프로필을_완성시킨다() {
        final ExtractableResponse<Response> result = MemberAcceptanceStep.멘토_회원가입_후_로그인을_진행한다(this).extract();
        final long memberId = result.jsonPath().getLong("id");
        final String accessToken = result.header(AUTHORIZATION).split(" ")[1];
        final String refreshToken = result.cookie(COOKIE_REFRESH_TOKEN);

        MemberAcceptanceStep.멘토_프로필을_완성시킨다(this, accessToken);

        return new AuthMember(
                memberId,
                this.name,
                this.profileImageUrl,
                new AuthToken(accessToken, refreshToken)
        );
    }
}
