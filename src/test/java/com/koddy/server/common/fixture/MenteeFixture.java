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
import com.koddy.server.member.domain.model.SocialPlatform;
import com.koddy.server.member.domain.model.mentee.Interest;
import com.koddy.server.member.domain.model.mentee.Mentee;
import io.restassured.response.ExtractableResponse;
import io.restassured.response.Response;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static com.koddy.server.auth.domain.model.AuthToken.ACCESS_TOKEN_HEADER;
import static com.koddy.server.auth.domain.model.AuthToken.REFRESH_TOKEN_HEADER;
import static com.koddy.server.auth.domain.model.oauth.OAuthProvider.GOOGLE;
import static com.koddy.server.common.utils.TokenUtils.ACCESS_TOKEN;
import static com.koddy.server.common.utils.TokenUtils.REFRESH_TOKEN;
import static com.koddy.server.member.domain.model.Nationality.CHINA;
import static com.koddy.server.member.domain.model.Nationality.ETC;
import static com.koddy.server.member.domain.model.Nationality.JAPAN;
import static com.koddy.server.member.domain.model.Nationality.USA;
import static com.koddy.server.member.domain.model.Nationality.VIETNAM;

public enum MenteeFixture {
    MENTEE_1(
            new SocialPlatform(GOOGLE, "ID-MENTEE-1", Email.from("mentee1@gmail.com")),
            "멘티1", "s3/Mentee1.png", USA, "Hello World~",
            LanguageFixture.메인_영어_서브_한국어(), new Interest("경기대학교", "컴퓨터공학부")
    ),
    MENTEE_2(
            new SocialPlatform(GOOGLE, "ID-MENTEE-2", Email.from("mentee2@gmail.com")),
            "멘티2", "s3/Mentee2.png", CHINA, "Hello World~",
            LanguageFixture.메인_영어_서브_일본어(), new Interest("서울대학교", "컴퓨터공학부")
    ),
    MENTEE_3(
            new SocialPlatform(GOOGLE, "ID-MENTEE-3", Email.from("mentee3@gmail.com")),
            "멘티3", "s3/Mentee3.png", JAPAN, "Hello World~",
            LanguageFixture.메인_영어_서브_한국어(), new Interest("연세대학교", "컴퓨터공학부")
    ),
    MENTEE_4(
            new SocialPlatform(GOOGLE, "ID-MENTEE-4", Email.from("mentee4@gmail.com")),
            "멘티4", "s3/Mentee4.png", VIETNAM, "Hello World~",
            LanguageFixture.메인_영어_서브_일본어(), new Interest("고려대학교", "컴퓨터공학부")
    ),
    MENTEE_5(
            new SocialPlatform(GOOGLE, "ID-MENTEE-5", Email.from("mentee5@gmail.com")),
            "멘티5", "s3/Mentee5.png", ETC, "Hello World~",
            LanguageFixture.메인_영어_서브_한국어(), new Interest("한양대학교", "컴퓨터공학부")
    ),
    MENTEE_6(
            new SocialPlatform(GOOGLE, "ID-MENTEE-6", Email.from("mentee6@gmail.com")),
            "멘티6", "s3/Mentee6.png", USA, "Hello World~",
            LanguageFixture.메인_영어_서브_일본어(), new Interest("경기대학교", "컴퓨터공학부")
    ),
    MENTEE_7(
            new SocialPlatform(GOOGLE, "ID-MENTEE-7", Email.from("mentee7@gmail.com")),
            "멘티7", "s3/Mentee7.png", CHINA, "Hello World~",
            LanguageFixture.메인_영어_서브_한국어(), new Interest("서울대학교", "컴퓨터공학부")
    ),
    MENTEE_8(
            new SocialPlatform(GOOGLE, "ID-MENTEE-8", Email.from("mentee8@gmail.com")),
            "멘티8", "s3/Mentee8.png", JAPAN, "Hello World~",
            LanguageFixture.메인_영어_서브_일본어(), new Interest("연세대학교", "컴퓨터공학부")
    ),
    MENTEE_9(
            new SocialPlatform(GOOGLE, "ID-MENTEE-9", Email.from("mentee9@gmail.com")),
            "멘티9", "s3/Mentee9.png", VIETNAM, "Hello World~",
            LanguageFixture.메인_영어_서브_한국어(), new Interest("고려대학교", "컴퓨터공학부")
    ),
    MENTEE_10(
            new SocialPlatform(GOOGLE, "ID-MENTEE-10", Email.from("mentee10@gmail.com")),
            "멘티10", "s3/Mentee10.png", ETC, "Hello World~",
            LanguageFixture.메인_영어_서브_일본어(), new Interest("한양대학교", "컴퓨터공학부")
    ),
    MENTEE_11(
            new SocialPlatform(GOOGLE, "ID-MENTEE-11", Email.from("mentee11@gmail.com")),
            "멘티11", "s3/Mentee11.png", USA, "Hello World~",
            LanguageFixture.메인_영어_서브_한국어(), new Interest("경기대학교", "컴퓨터공학부")
    ),
    MENTEE_12(
            new SocialPlatform(GOOGLE, "ID-MENTEE-12", Email.from("mentee12@gmail.com")),
            "멘티12", "s3/Mentee12.png", CHINA, "Hello World~",
            LanguageFixture.메인_영어_서브_일본어(), new Interest("서울대학교", "컴퓨터공학부")
    ),
    MENTEE_13(
            new SocialPlatform(GOOGLE, "ID-MENTEE-13", Email.from("mentee13@gmail.com")),
            "멘티13", "s3/Mentee13.png", JAPAN, "Hello World~",
            LanguageFixture.메인_영어_서브_한국어(), new Interest("연세대학교", "컴퓨터공학부")
    ),
    MENTEE_14(
            new SocialPlatform(GOOGLE, "ID-MENTEE-14", Email.from("mentee14@gmail.com")),
            "멘티14", "s3/Mentee14.png", VIETNAM, "Hello World~",
            LanguageFixture.메인_영어_서브_일본어(), new Interest("고려대학교", "컴퓨터공학부")
    ),
    MENTEE_15(
            new SocialPlatform(GOOGLE, "ID-MENTEE-15", Email.from("mentee15@gmail.com")),
            "멘티15", "s3/Mentee15.png", ETC, "Hello World~",
            LanguageFixture.메인_영어_서브_한국어(), new Interest("한양대학교", "컴퓨터공학부")
    ),
    MENTEE_16(
            new SocialPlatform(GOOGLE, "ID-MENTEE-16", Email.from("mentee16@gmail.com")),
            "멘티16", "s3/Mentee16.png", USA, "Hello World~",
            LanguageFixture.메인_영어_서브_일본어(), new Interest("경기대학교", "컴퓨터공학부")
    ),
    MENTEE_17(
            new SocialPlatform(GOOGLE, "ID-MENTEE-17", Email.from("mentee17@gmail.com")),
            "멘티17", "s3/Mentee17.png", CHINA, "Hello World~",
            LanguageFixture.메인_영어_서브_한국어(), new Interest("서울대학교", "컴퓨터공학부")
    ),
    MENTEE_18(
            new SocialPlatform(GOOGLE, "ID-MENTEE-18", Email.from("mentee18@gmail.com")),
            "멘티18", "s3/Mentee18.png", JAPAN, "Hello World~",
            LanguageFixture.메인_영어_서브_일본어(), new Interest("연세대학교", "컴퓨터공학부")
    ),
    MENTEE_19(
            new SocialPlatform(GOOGLE, "ID-MENTEE-19", Email.from("mentee19@gmail.com")),
            "멘티19", "s3/Mentee19.png", VIETNAM, "Hello World~",
            LanguageFixture.메인_영어_서브_한국어(), new Interest("고려대학교", "컴퓨터공학부")
    ),
    MENTEE_20(
            new SocialPlatform(GOOGLE, "ID-MENTEE-20", Email.from("mentee20@gmail.com")),
            "멘티20", "s3/Mentee20.png", ETC, "Hello World~",
            LanguageFixture.메인_영어_서브_일본어(), new Interest("한양대학교", "컴퓨터공학부")
    ),
    ;

    private final SocialPlatform platform;
    private final String name;
    private final String profileImageUrl;
    private final Nationality nationality;
    private final String introduction;
    private final List<Language> languages;
    private final Interest interest;

    MenteeFixture(
            final SocialPlatform platform,
            final String name,
            final String profileImageUrl,
            final Nationality nationality,
            final String introduction,
            final List<Language> languages,
            final Interest interest
    ) {
        this.platform = platform;
        this.name = name;
        this.profileImageUrl = profileImageUrl;
        this.nationality = nationality;
        this.introduction = introduction;
        this.languages = languages;
        this.interest = interest;
    }

    public Mentee toDomain() {
        final Mentee mentee = new Mentee(platform, name, nationality, languages, interest);
        mentee.completeProfile(introduction, profileImageUrl);
        return mentee;
    }

    public Mentee toDomainWithLanguages(final List<Language> languages) {
        final Mentee mentee = new Mentee(platform, name, nationality, languages, interest);
        mentee.completeProfile(introduction, profileImageUrl);
        return mentee;
    }

    public GoogleUserResponse toGoogleUserResponse() {
        return new GoogleUserResponse(
                platform.getSocialId(),
                this.name,
                this.name,
                this.name,
                this.profileImageUrl,
                this.platform.getEmail().getValue(),
                true,
                "kr"
        );
    }

    public KakaoUserResponse toKakaoUserResponse() {
        return new KakaoUserResponse(
                platform.getSocialId(),
                LocalDateTime.now(),
                null
        );
    }

    public ZoomUserResponse toZoomUserResponse() {
        return new ZoomUserResponse(
                platform.getSocialId(),
                platform.getSocialId(),
                UUID.randomUUID().toString(),
                this.name,
                this.name,
                this.name,
                this.platform.getEmail().getValue(),
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
        final ExtractableResponse<Response> result = MemberAcceptanceStep.멘티_회원가입_후_로그인을_진행한다(this).extract();
        final long memberId = result.jsonPath().getLong("id");
        final String accessToken = result.header(ACCESS_TOKEN_HEADER).split(" ")[1];
        final String refreshToken = result.cookie(REFRESH_TOKEN_HEADER);

        return new AuthMember(
                memberId,
                this.name,
                new AuthToken(accessToken, refreshToken)
        );
    }

    public AuthMember 회원가입과_로그인을_하고_프로필을_완성시킨다() {
        final ExtractableResponse<Response> result = MemberAcceptanceStep.멘티_회원가입_후_로그인을_진행한다(this).extract();
        final long memberId = result.jsonPath().getLong("id");
        final String accessToken = result.header(ACCESS_TOKEN_HEADER).split(" ")[1];
        final String refreshToken = result.cookie(REFRESH_TOKEN_HEADER);

        MemberAcceptanceStep.멘티_프로필을_완성시킨다(this, accessToken);
        return new AuthMember(
                memberId,
                this.name,
                new AuthToken(accessToken, refreshToken)
        );
    }

    public SocialPlatform getPlatform() {
        return platform;
    }

    public String getName() {
        return name;
    }

    public String getProfileImageUrl() {
        return profileImageUrl;
    }

    public Nationality getNationality() {
        return nationality;
    }

    public String getIntroduction() {
        return introduction;
    }

    public List<Language> getLanguages() {
        return languages;
    }

    public Interest getInterest() {
        return interest;
    }
}
