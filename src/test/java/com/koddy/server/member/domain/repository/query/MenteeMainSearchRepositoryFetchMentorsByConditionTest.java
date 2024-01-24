package com.koddy.server.member.domain.repository.query;

import com.koddy.server.common.RepositoryTest;
import com.koddy.server.common.fixture.MentorFixture;
import com.koddy.server.global.PageCreator;
import com.koddy.server.member.domain.model.mentor.Mentor;
import com.koddy.server.member.domain.repository.MemberRepository;
import com.koddy.server.member.domain.repository.query.spec.SearchMentorCondition;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.Arrays;
import java.util.List;

import static com.koddy.server.member.domain.model.Language.Category.CN;
import static com.koddy.server.member.domain.model.Language.Category.EN;
import static com.koddy.server.member.domain.model.Language.Category.JP;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Import(MenteeMainSearchRepositoryImpl.class)
@DisplayName("Member -> MenteeMainSearchRepository [fetchMentorsByCondition] 테스트")
class MenteeMainSearchRepositoryFetchMentorsByConditionTest extends RepositoryTest {
    @Autowired
    private MenteeMainSearchRepositoryImpl sut;

    @Autowired
    private MemberRepository memberRepository;

    private final Mentor[] mentors = new Mentor[20];
    private final Pageable pageable1 = PageCreator.create(1);
    private final Pageable pageable2 = PageCreator.create(2);

    @BeforeEach
    void setUp() {
        final List<MentorFixture> fixtures = Arrays.stream(MentorFixture.values())
                .limit(20)
                .toList();
        Arrays.setAll(mentors, it -> memberRepository.save(fixtures.get(it).toDomain()));
    }

    @Test
    @DisplayName("최신 가입순으로 멘토를 둘러본다")
    void withLatest() {
        // given
        final SearchMentorCondition condition = SearchMentorCondition.basic();

        /* 페이지 1 */
        final Slice<Mentor> result1 = sut.fetchMentorsByCondition(condition, pageable1);

        assertAll(
                () -> assertThat(result1.hasNext()).isTrue(),
                () -> assertThat(result1.getContent()).containsExactly(
                        mentors[19], mentors[18], mentors[17], mentors[16], mentors[15],
                        mentors[14], mentors[13], mentors[12], mentors[11], mentors[10]
                )
        );

        /* 페이지 2 */
        final Slice<Mentor> result2 = sut.fetchMentorsByCondition(condition, pageable2);

        assertAll(
                () -> assertThat(result2.hasNext()).isFalse(),
                () -> assertThat(result2.getContent()).containsExactly(
                        mentors[9], mentors[8], mentors[7], mentors[6], mentors[5],
                        mentors[4], mentors[3], mentors[2], mentors[1], mentors[0]
                )
        );
    }

    @Test
    @DisplayName("사용 가능한 언어 기준으로 멘토를 둘러본다")
    void withLanguage() {
        // given
        final SearchMentorCondition condition1 = SearchMentorCondition.of(List.of(EN));
        final SearchMentorCondition condition2 = SearchMentorCondition.of(List.of(JP, CN));
        final SearchMentorCondition condition3 = SearchMentorCondition.of(List.of(EN, JP, CN));

        /* 페이지 1 */
        final Slice<Mentor> result1 = sut.fetchMentorsByCondition(condition1, pageable1);
        final Slice<Mentor> result2 = sut.fetchMentorsByCondition(condition2, pageable1);
        final Slice<Mentor> result3 = sut.fetchMentorsByCondition(condition3, pageable1);

        assertAll(
                () -> assertThat(result1.hasNext()).isFalse(),
                () -> assertThat(result1.getContent()).containsExactly(
                        mentors[18], mentors[16], mentors[14], mentors[12], mentors[10],
                        mentors[8], mentors[6], mentors[4], mentors[2], mentors[0]
                ),
                () -> assertThat(result2.hasNext()).isFalse(),
                () -> assertThat(result2.getContent()).containsExactly(
                        mentors[19], mentors[17], mentors[15], mentors[13], mentors[11],
                        mentors[9], mentors[7], mentors[5], mentors[3], mentors[1]
                ),
                () -> assertThat(result3.hasNext()).isTrue(),
                () -> assertThat(result3.getContent()).containsExactly(
                        mentors[19], mentors[18], mentors[17], mentors[16], mentors[15],
                        mentors[14], mentors[13], mentors[12], mentors[11], mentors[10]
                )
        );

        /* 페이지 2 */
        final Slice<Mentor> result4 = sut.fetchMentorsByCondition(condition1, pageable2);
        final Slice<Mentor> result5 = sut.fetchMentorsByCondition(condition2, pageable2);
        final Slice<Mentor> result6 = sut.fetchMentorsByCondition(condition3, pageable2);

        assertAll(
                () -> assertThat(result4.hasNext()).isFalse(),
                () -> assertThat(result4.getContent()).isEmpty(),
                () -> assertThat(result5.hasNext()).isFalse(),
                () -> assertThat(result5.getContent()).isEmpty(),
                () -> assertThat(result6.hasNext()).isFalse(),
                () -> assertThat(result6.getContent()).containsExactly(
                        mentors[9], mentors[8], mentors[7], mentors[6], mentors[5],
                        mentors[4], mentors[3], mentors[2], mentors[1], mentors[0]
                )
        );
    }
}
