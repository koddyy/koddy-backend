package com.koddy.server.global.utils;

import com.koddy.server.global.exception.GlobalException;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.List;

import static com.koddy.server.global.exception.GlobalExceptionCode.NOT_PROVIDED_UNIV_DOMAIN;
import static java.util.List.of;

@Getter
@RequiredArgsConstructor
public enum UniversityInfo {
    KAYA("가야대학교", of("kaya.ac.kr")), GACHON("가천대학교", of("gachon.ac.kr")), CKU("가톨릭관동대학교", of("cku.ac.kr")),
    CATHOLIC("가톨릭대학교", of("catholic.ac.kr")), KANGWON("강원대학교", of("kangwon.ac.kr")), KONKUK("건국대학교", of("konkuk.ac.kr")),
    KYONGGI("경기대학교", of("kgu.ac.kr", "kyonggi.ac.kr")), KNU("경북대학교", of("knu.ac.kr")), KHU("경희대학교", of("khu.ac.kr")),
    KOREA("고려대학교", of("korea.ac.kr")), KW("광운대학교", of("kw.ac.kr")), KOOKMIN("국민대학교", of("kookmin.ac.kr")),
    DANKOOK("단국대학교", of("dankook.ac.kr")), DAEGU("대구대학교", of("daegu.ac.kr")), DONGGUK("동국대학교", of("dongguk.edu")),
    MJU("명지대학교", of("mju.ac.kr")), PUSAN("부산대학교", of("pusan.ac.kr")), SYU("삼육대학교", of("syu.ac.kr")),
    SANGMYUNG("상명대학교", of("sangmyung.kr")), SOGANG("서강대학교", of("sogang.ac.kr")), SKUNIV("서경대학교", of("skuniv.ac.kr")),
    SEOULTECH("서울과학기술대학교", of("seoultech.ac.kr")), SNU("서울대학교", of("snu.ac.kr")), UOS("서울시립대학교", of("uos.ac.kr")),
    SKKU("성균관대학교", of("skku.edu")), SJU("세종대학교", of("sju.ac.kr")), SUWON("수원대학교", of("suwon.ac.kr")),
    SCH("순천향대학교", of("sch.ac.kr")), SOONGSIL("숭실대학교", of("soongsil.ac.kr")), AJOU("아주대학교", of("ajou.ac.kr")),
    ANYANG("안양대학교", of("anyang.ac.kr")), YONSEI("연세대학교", of("yonsei.ac.kr")), ULSAN("울산대학교", of("ulsan.ac.kr")),
    WKU("원광대학교", of("wku.ac.kr")), EULJI("을지대학교", of("eulji.ac.kr")), INHA("인하대학교", of("inha.ac.kr")),
    JNU("전남대학교", of("jnu.ac.kr")), JBNU("전북대학교", of("jbnu.ac.kr")), JJ("전주대학교", of("jj.ac.kr")),
    JEJUNU("제주대학교", of("jejunu.ac.kr")), CAU("중앙대학교", of("cau.ac.kr")), CNU("충남대학교", of("cnu.ac.kr", "cnu.kr")),
    CBNU("충북대학교", of("chungbuk.ac.kr", "cbnu.ac.kr")), POSTECH("포항공과대학교", of("postech.ac.kr")), TUKOREA("한국공학대학교", of("tukorea.ac.kr")),
    UT("한국교통대학교", of("ut.ac.kr")), HUFS("한국외국어대학교", of("hufs.ac.kr")), KAU("한국항공대학교", of("kau.ac.kr")),
    KMOU("한국항공대학교", of("g.kmou.ac.kr")), USK("한남대학교", of("gm.hannam.ac.kr", "usk.ac.kr")), HANDONG("한동대학교", of("handong.edu")),
    HALLYM("한림대학교", of("hallym.ac.kr")), HANBAT("한밭대학교", of("hanbat.ac.kr")), HANSEO("한서대학교", of("hanseo.ac.kr")),
    HANSUNG("한성대학교", of("hansung.ac.kr")), HANYANG("한양대학교", of("hanyang.ac.kr")), HONGIK("홍익대학교", of("hongik.ac.kr", "mail.hongik.ac.kr")),
    ;

    private final String schoolName;
    private final List<String> domains; // kyonggi.ac.kr

    public static void validateDomain(final String schoolMail) {
        final boolean nonExists = Arrays.stream(values())
                .noneMatch(it -> it.domains.contains(extractDomain(schoolMail)));

        if (nonExists) {
            throw new GlobalException(NOT_PROVIDED_UNIV_DOMAIN);
        }
    }

    private static String extractDomain(final String schoolMail) {
        return schoolMail.split("@")[1];
    }
}
