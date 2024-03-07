package com.koddy.server.global.utils

import com.koddy.server.auth.domain.model.Authenticated
import com.koddy.server.global.exception.GlobalException
import com.koddy.server.global.exception.GlobalExceptionCode.NOT_PROVIDED_UNIV_DOMAIN
import com.koddy.server.global.log.logger
import org.slf4j.Logger

enum class UniversityInfo(
    private val schoolName: String,
    private val domains: List<String>,
) {
    KAYA("가야대학교", listOf("kaya.ac.kr")),
    GACHON("가천대학교", listOf("gachon.ac.kr")),
    CKU("가톨릭관동대학교", listOf("cku.ac.kr")),
    CATHOLIC("가톨릭대학교", listOf("catholic.ac.kr")),
    KANGWON("강원대학교", listOf("kangwon.ac.kr")),
    KONKUK("건국대학교", listOf("konkuk.ac.kr")),
    KYONGGI("경기대학교", listOf("kgu.ac.kr", "kyonggi.ac.kr")),
    KNU("경북대학교", listOf("knu.ac.kr")),
    KHU("경희대학교", listOf("khu.ac.kr")),
    KOREA("고려대학교", listOf("korea.ac.kr")),
    KW("광운대학교", listOf("kw.ac.kr")),
    KOOKMIN("국민대학교", listOf("kookmin.ac.kr")),
    DANKOOK("단국대학교", listOf("dankook.ac.kr")),
    DAEGU("대구대학교", listOf("daegu.ac.kr")),
    DONGGUK("동국대학교", listOf("dongguk.edu")),
    MJU("명지대학교", listOf("mju.ac.kr")),
    PUSAN("부산대학교", listOf("pusan.ac.kr")),
    SYU("삼육대학교", listOf("syu.ac.kr")),
    SANGMYUNG("상명대학교", listOf("sangmyung.kr")),
    SOGANG("서강대학교", listOf("sogang.ac.kr")),
    SKUNIV("서경대학교", listOf("skuniv.ac.kr")),
    SEOULTECH("서울과학기술대학교", listOf("seoultech.ac.kr")),
    SNU("서울대학교", listOf("snu.ac.kr")),
    UOS("서울시립대학교", listOf("uos.ac.kr")),
    SKKU("성균관대학교", listOf("skku.edu")),
    SJU("세종대학교", listOf("sju.ac.kr")),
    SUWON("수원대학교", listOf("suwon.ac.kr")),
    SCH("순천향대학교", listOf("sch.ac.kr")),
    SOONGSIL("숭실대학교", listOf("soongsil.ac.kr")),
    AJOU("아주대학교", listOf("ajou.ac.kr")),
    ANYANG("안양대학교", listOf("anyang.ac.kr")),
    YONSEI("연세대학교", listOf("yonsei.ac.kr")),
    ULSAN("울산대학교", listOf("ulsan.ac.kr")),
    WKU("원광대학교", listOf("wku.ac.kr")),
    EULJI("을지대학교", listOf("eulji.ac.kr")),
    INHA("인하대학교", listOf("inha.ac.kr")),
    JNU("전남대학교", listOf("jnu.ac.kr")),
    JBNU("전북대학교", listOf("jbnu.ac.kr")),
    JJ("전주대학교", listOf("jj.ac.kr")),
    JEJUNU("제주대학교", listOf("jejunu.ac.kr")),
    CAU("중앙대학교", listOf("cau.ac.kr")),
    CNU("충남대학교", listOf("cnu.ac.kr", "cnu.kr")),
    CBNU("충북대학교", listOf("chungbuk.ac.kr", "cbnu.ac.kr")),
    POSTECH("포항공과대학교", listOf("postech.ac.kr")),
    TUKOREA("한국공학대학교", listOf("tukorea.ac.kr")),
    UT("한국교통대학교", listOf("ut.ac.kr")),
    HUFS("한국외국어대학교", listOf("hufs.ac.kr")),
    KAU("한국항공대학교", listOf("kau.ac.kr")),
    KMOU("한국항공대학교", listOf("g.kmou.ac.kr")),
    USK("한남대학교", listOf("gm.hannam.ac.kr", "usk.ac.kr")),
    HANDONG("한동대학교", listOf("handong.edu")),
    HALLYM("한림대학교", listOf("hallym.ac.kr")),
    HANBAT("한밭대학교", listOf("hanbat.ac.kr")),
    HANSEO("한서대학교", listOf("hanseo.ac.kr")),
    HANSUNG("한성대학교", listOf("hansung.ac.kr")),
    HANYANG("한양대학교", listOf("hanyang.ac.kr")),
    HONGIK("홍익대학교", listOf("hongik.ac.kr", "mail.hongik.ac.kr")),
    ;

    companion object {
        private val log: Logger = logger()

        fun validateDomain(
            authenticated: Authenticated,
            schoolMail: String,
        ) {
            val domain: String = extractDomain(schoolMail)
            if (isAnonymousDomain(domain)) {
                log.warn("School Domain Not Match -> auth = {}, mail = {}", authenticated, schoolMail)
                throw GlobalException(NOT_PROVIDED_UNIV_DOMAIN)
            }
        }

        private fun isAnonymousDomain(domain: String): Boolean = entries.none { it.domains.contains(domain) }

        private fun extractDomain(schoolMail: String): String = schoolMail.split("@".toRegex()).last()
    }
}
