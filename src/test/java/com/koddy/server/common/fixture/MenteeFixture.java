package com.koddy.server.common.fixture;

import com.koddy.server.acceptance.member.MemberAcceptanceStep;
import com.koddy.server.auth.domain.model.AuthMember;
import com.koddy.server.auth.domain.model.AuthToken;
import com.koddy.server.auth.infrastructure.social.google.response.GoogleUserResponse;
import com.koddy.server.auth.infrastructure.social.kakao.response.KakaoUserResponse;
import com.koddy.server.auth.infrastructure.social.zoom.response.ZoomUserResponse;
import com.koddy.server.member.domain.model.Email;
import com.koddy.server.member.domain.model.Language;
import com.koddy.server.member.domain.model.Nationality;
import com.koddy.server.member.domain.model.mentee.Interest;
import com.koddy.server.member.domain.model.mentee.Mentee;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.koddy.server.auth.utils.TokenResponseWriter.COOKIE_REFRESH_TOKEN;
import static com.koddy.server.common.utils.TokenUtils.ACCESS_TOKEN;
import static com.koddy.server.common.utils.TokenUtils.REFRESH_TOKEN;
import static com.koddy.server.member.domain.model.Nationality.CHINA;
import static com.koddy.server.member.domain.model.Nationality.JAPAN;
import static com.koddy.server.member.domain.model.Nationality.OTHERS;
import static com.koddy.server.member.domain.model.Nationality.USA;
import static com.koddy.server.member.domain.model.Nationality.VIETNAM;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Getter
@RequiredArgsConstructor
public enum MenteeFixture {
    MENTEE_1(
            Email.from("mentee1@gmail.com"), "멘티1", "s3/Mentee1.png",
            USA, "Hello World~", LanguageFixture.메인_영어_서브_한국어(),
            new Interest("경기대학교", "컴퓨터공학부")
    ),
    MENTEE_2(
            Email.from("mentee2@gmail.com"), "멘티2", "s3/Mentee2.png",
            CHINA, "Hello World~", LanguageFixture.메인_영어_서브_일본어(),
            new Interest("서울대학교", "컴퓨터공학부")
    ),
    MENTEE_3(
            Email.from("mentee3@gmail.com"), "멘티3", "s3/Mentee3.png",
            JAPAN, "Hello World~", LanguageFixture.메인_영어_서브_한국어(),
            new Interest("연세대학교", "컴퓨터공학부")
    ),
    MENTEE_4(
            Email.from("mentee4@gmail.com"), "멘티4", "s3/Mentee4.png",
            VIETNAM, "Hello World~", LanguageFixture.메인_영어_서브_일본어(),
            new Interest("고려대학교", "컴퓨터공학부")
    ),
    MENTEE_5(
            Email.from("mentee5@gmail.com"), "멘티5", "s3/Mentee5.png",
            OTHERS, "Hello World~", LanguageFixture.메인_영어_서브_한국어(),
            new Interest("한양대학교", "컴퓨터공학부")
    ),

    MENTEE_6(
            Email.from("mentee6@gmail.com"), "멘티6", "s3/Mentee6.png",
            USA, "Hello World~", LanguageFixture.메인_영어_서브_일본어(),
            new Interest("경기대학교", "컴퓨터공학부")
    ),
    MENTEE_7(
            Email.from("mentee7@gmail.com"), "멘티7", "s3/Mentee7.png",
            CHINA, "Hello World~", LanguageFixture.메인_영어_서브_한국어(),
            new Interest("서울대학교", "컴퓨터공학부")
    ),
    MENTEE_8(
            Email.from("mentee8@gmail.com"), "멘티8", "s3/Mentee8.png",
            JAPAN, "Hello World~", LanguageFixture.메인_영어_서브_일본어(),
            new Interest("연세대학교", "컴퓨터공학부")
    ),
    MENTEE_9(
            Email.from("mentee9@gmail.com"), "멘티9", "s3/Mentee9.png",
            VIETNAM, "Hello World~", LanguageFixture.메인_영어_서브_한국어(),
            new Interest("고려대학교", "컴퓨터공학부")
    ),
    MENTEE_10(
            Email.from("mentee10@gmail.com"), "멘티10", "s3/Mentee10.png",
            OTHERS, "Hello World~", LanguageFixture.메인_영어_서브_일본어(),
            new Interest("한양대학교", "컴퓨터공학부")
    ),
    ;

    private final Email email;
    private final String name;
    private final String profileImageUrl;
    private final Nationality nationality;
    private final String introduction;
    private final List<Language> languages;
    private final Interest interest;

    public Mentee toDomain() {
        final Mentee mentee = new Mentee(email, name, profileImageUrl, nationality, languages, interest);
        mentee.completeInfo(introduction);
        return mentee;
    }

    public Mentee toDomainWithLanguages(final List<Language> languages) {
        final Mentee mentee = new Mentee(email, name, profileImageUrl, nationality, languages, interest);
        mentee.completeInfo(introduction);
        return mentee;
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

    public long 회원가입_로그인_후_PK를_추출한다() {
        return MemberAcceptanceStep.멘티_회원가입_후_로그인을_진행한다(this)
                .extract()
                .jsonPath()
                .getLong("id");
    }

    public String 회원가입_로그인_후_AccessToken을_추출한다() {
        return MemberAcceptanceStep.멘티_회원가입_후_로그인을_진행한다(this)
                .extract()
                .header(AUTHORIZATION)
                .split(" ")[1];
    }

    public String 회원가입_로그인_후_프로필을_완성시킨다() {
        final String accessToken = 회원가입_로그인_후_AccessToken을_추출한다();
        MemberAcceptanceStep.멘티_프로필을_완성시킨다(this, accessToken);
        return accessToken;
    }

    public String 회원가입_로그인_후_RefreshToken을_추출한다() {
        return MemberAcceptanceStep.멘티_회원가입_후_로그인을_진행한다(this)
                .extract()
                .cookie(COOKIE_REFRESH_TOKEN);
    }
}
