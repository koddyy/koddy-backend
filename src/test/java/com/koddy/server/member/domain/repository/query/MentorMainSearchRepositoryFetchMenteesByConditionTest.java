//package com.koddy.server.member.domain.repository.query;
//
//import com.koddy.server.common.RepositoryTest;
//import com.koddy.server.common.fixture.MenteeFixtureStore.menteeFixture;
//import com.koddy.server.global.query.PageCreator;
//import com.koddy.server.member.domain.model.mentee.Mentee;
//import com.koddy.server.member.domain.repository.MemberRepository;
//import com.koddy.server.member.domain.repository.query.spec.SearchMenteeCondition;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.DisplayName;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.context.annotation.Import;
//import org.springframework.data.domain.Pageable;
//import org.springframework.data.domain.Slice;
//
//import java.util.Arrays;
//import java.util.List;
//
//import static com.koddy.server.member.domain.model.Language.Category.EN;
//import static com.koddy.server.member.domain.model.Language.Category.JP;
//import static com.koddy.server.member.domain.model.Language.Category.KR;
//import static com.koddy.server.member.domain.model.Nationality.CHINA;
//import static com.koddy.server.member.domain.model.Nationality.JAPAN;
//import static com.koddy.server.member.domain.model.Nationality.USA;
//import static org.assertj.core.api.Assertions.assertThat;
//import static org.junit.jupiter.api.Assertions.assertAll;
//
//@Import(MentorMainSearchRepositoryImpl.class)
//@DisplayName("Member -> MentorMainSearchRepository [fetchMenteesByCondition] 테스트")
//class MentorMainSearchRepositoryFetchMenteesByConditionTest extends RepositoryTest {
//    @Autowired
//    private MentorMainSearchRepositoryImpl sut;
//
//    @Autowired
//    private MemberRepository memberRepository;
//
//    private final Mentee[] mentees = new Mentee[20];
//    private final Pageable pageable1 = PageCreator.create(1);
//    private final Pageable pageable2 = PageCreator.create(2);
//
//    @BeforeEach
//    void setUp() {
//        final List<MenteeFixture> fixtures = Arrays.stream(MenteeFixture.values())
//                .limit(20)
//                .toList();
//        Arrays.setAll(mentees, it -> memberRepository.save(fixtures.get(it).toDomain()));
//    }
//
//    @Test
//    @DisplayName("최신 가입순으로 멘티를 둘러본다")
//    void withLatest() {
//        // given
//        final SearchMenteeCondition condition = SearchMenteeCondition.basic();
//
//        /* 페이지 1 */
//        final Slice<Mentee> result1 = sut.fetchMenteesByCondition(condition, pageable1);
//
//        assertAll(
//                () -> assertThat(result1.hasNext()).isTrue(),
//                () -> assertThat(result1.getContent()).containsExactly(
//                        mentees[19], mentees[18], mentees[17], mentees[16], mentees[15],
//                        mentees[14], mentees[13], mentees[12], mentees[11], mentees[10]
//                )
//        );
//
//        /* 페이지 2 */
//        final Slice<Mentee> result2 = sut.fetchMenteesByCondition(condition, pageable2);
//
//        assertAll(
//                () -> assertThat(result2.hasNext()).isFalse(),
//                () -> assertThat(result2.getContent()).containsExactly(
//                        mentees[9], mentees[8], mentees[7], mentees[6], mentees[5],
//                        mentees[4], mentees[3], mentees[2], mentees[1], mentees[0]
//                )
//        );
//    }
//
//    @Test
//    @DisplayName("국적 기준으로 멘티를 둘러본다")
//    void withNationality() {
//        // given
//        final SearchMenteeCondition condition = SearchMenteeCondition.of(List.of(USA, CHINA, JAPAN), List.of());
//
//        /* 페이지 1 */
//        final Slice<Mentee> result1 = sut.fetchMenteesByCondition(condition, pageable1);
//
//        assertAll(
//                () -> assertThat(result1.hasNext()).isTrue(),
//                () -> assertThat(result1.getContent()).containsExactly(
//                        mentees[17], mentees[16], mentees[15], mentees[12], mentees[11],
//                        mentees[10], mentees[7], mentees[6], mentees[5], mentees[2]
//                )
//        );
//
//        /* 페이지 2 */
//        final Slice<Mentee> result2 = sut.fetchMenteesByCondition(condition, pageable2);
//
//        assertAll(
//                () -> assertThat(result2.hasNext()).isFalse(),
//                () -> assertThat(result2.getContent()).containsExactly(mentees[1], mentees[0])
//        );
//    }
//
//    @Test
//    @DisplayName("사용 가능한 언어 기준으로 멘티를 둘러본다")
//    void withLanguage() {
//        // given
//        final SearchMenteeCondition condition1 = SearchMenteeCondition.of(List.of(), List.of(EN));
//        final SearchMenteeCondition condition2 = SearchMenteeCondition.of(List.of(), List.of(EN, KR));
//        final SearchMenteeCondition condition3 = SearchMenteeCondition.of(List.of(), List.of(EN, JP));
//        final SearchMenteeCondition condition4 = SearchMenteeCondition.of(List.of(), List.of(EN, KR, JP));
//
//        /* 페이지 1 */
//        final Slice<Mentee> result1 = sut.fetchMenteesByCondition(condition1, pageable1);
//        final Slice<Mentee> result2 = sut.fetchMenteesByCondition(condition2, pageable1);
//        final Slice<Mentee> result3 = sut.fetchMenteesByCondition(condition3, pageable1);
//        final Slice<Mentee> result4 = sut.fetchMenteesByCondition(condition4, pageable1);
//
//        assertAll(
//                () -> assertThat(result1.hasNext()).isTrue(),
//                () -> assertThat(result1.getContent()).containsExactly(
//                        mentees[19], mentees[18], mentees[17], mentees[16], mentees[15],
//                        mentees[14], mentees[13], mentees[12], mentees[11], mentees[10]
//                ),
//                () -> assertThat(result2.hasNext()).isFalse(),
//                () -> assertThat(result2.getContent()).containsExactly(
//                        mentees[18], mentees[16], mentees[14], mentees[12], mentees[10],
//                        mentees[8], mentees[6], mentees[4], mentees[2], mentees[0]
//                ),
//                () -> assertThat(result3.hasNext()).isFalse(),
//                () -> assertThat(result3.getContent()).containsExactly(
//                        mentees[19], mentees[17], mentees[15], mentees[13], mentees[11],
//                        mentees[9], mentees[7], mentees[5], mentees[3], mentees[1]
//                ),
//                () -> assertThat(result4.hasNext()).isFalse(),
//                () -> assertThat(result4.getContent()).isEmpty()
//        );
//
//        /* 페이지 2 */
//        final Slice<Mentee> result5 = sut.fetchMenteesByCondition(condition1, pageable2);
//        final Slice<Mentee> result6 = sut.fetchMenteesByCondition(condition2, pageable2);
//        final Slice<Mentee> result7 = sut.fetchMenteesByCondition(condition3, pageable2);
//
//        assertAll(
//                () -> assertThat(result5.hasNext()).isFalse(),
//                () -> assertThat(result5.getContent()).containsExactly(
//                        mentees[9], mentees[8], mentees[7], mentees[6], mentees[5],
//                        mentees[4], mentees[3], mentees[2], mentees[1], mentees[0]
//                ),
//                () -> assertThat(result6.hasNext()).isFalse(),
//                () -> assertThat(result6.getContent()).isEmpty(),
//                () -> assertThat(result7.hasNext()).isFalse(),
//                () -> assertThat(result7.getContent()).isEmpty()
//        );
//    }
//
//    @Test
//    @DisplayName("국적 + 사용 가능한 언어 기준으로 멘티를 둘러본다")
//    void withNationalityAndLanguage() {
//        // given
//        final SearchMenteeCondition condition1 = SearchMenteeCondition.of(List.of(USA, CHINA, JAPAN), List.of(EN));
//        final SearchMenteeCondition condition2 = SearchMenteeCondition.of(List.of(USA, CHINA, JAPAN), List.of(EN, KR));
//        final SearchMenteeCondition condition3 = SearchMenteeCondition.of(List.of(USA, CHINA, JAPAN), List.of(EN, JP));
//
//        /* 페이지 1 */
//        final Slice<Mentee> result1 = sut.fetchMenteesByCondition(condition1, pageable1);
//        final Slice<Mentee> result2 = sut.fetchMenteesByCondition(condition2, pageable1);
//        final Slice<Mentee> result3 = sut.fetchMenteesByCondition(condition3, pageable1);
//
//        assertAll(
//                () -> assertThat(result1.hasNext()).isTrue(),
//                () -> assertThat(result1.getContent()).containsExactly(
//                        mentees[17], mentees[16], mentees[15], mentees[12], mentees[11],
//                        mentees[10], mentees[7], mentees[6], mentees[5], mentees[2]
//                ),
//                () -> assertThat(result2.hasNext()).isFalse(),
//                () -> assertThat(result2.getContent()).containsExactly(
//                        mentees[16], mentees[12], mentees[10],
//                        mentees[6], mentees[2], mentees[0]
//                ),
//                () -> assertThat(result3.hasNext()).isFalse(),
//                () -> assertThat(result3.getContent()).containsExactly(
//                        mentees[17], mentees[15], mentees[11],
//                        mentees[7], mentees[5], mentees[1]
//                )
//        );
//
//        /* 페이지 2 */
//        final Slice<Mentee> result4 = sut.fetchMenteesByCondition(condition1, pageable2);
//        final Slice<Mentee> result5 = sut.fetchMenteesByCondition(condition2, pageable2);
//        final Slice<Mentee> result6 = sut.fetchMenteesByCondition(condition3, pageable2);
//
//        assertAll(
//                () -> assertThat(result4.hasNext()).isFalse(),
//                () -> assertThat(result4.getContent()).containsExactly(mentees[1], mentees[0]),
//                () -> assertThat(result5.hasNext()).isFalse(),
//                () -> assertThat(result5.getContent()).isEmpty(),
//                () -> assertThat(result6.hasNext()).isFalse(),
//                () -> assertThat(result6.getContent()).isEmpty()
//        );
//    }
//}
