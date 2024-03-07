package com.koddy.server.member.domain.repository.query

import com.koddy.server.common.RepositoryTestKt
import com.koddy.server.common.fixture.MenteeFixtureStore.menteeFixture
import com.koddy.server.global.query.PageCreator
import com.koddy.server.member.domain.model.Language
import com.koddy.server.member.domain.model.Nationality
import com.koddy.server.member.domain.model.mentee.Mentee
import com.koddy.server.member.domain.repository.MemberRepository
import com.koddy.server.member.domain.repository.query.spec.SearchMenteeCondition
import io.kotest.assertions.assertSoftly
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.context.annotation.Import
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice

@RepositoryTestKt
@Import(MentorMainSearchRepositoryImpl::class)
@DisplayName("Member -> MentorMainSearchRepository [fetchMenteesByCondition] 테스트")
internal class MentorMainSearchRepositoryFetchMenteesByConditionTest(
    private val sut: MentorMainSearchRepositoryImpl,
    private val memberRepository: MemberRepository,
) {
    companion object {
        private val pageable1: Pageable = PageCreator.create(1)
        private val pageable2: Pageable = PageCreator.create(2)
    }

    private lateinit var mentees: List<Mentee>

    @BeforeEach
    fun setUp() {
        mentees = mutableListOf<Mentee>().apply {
            (1..20).forEach { add(memberRepository.save(menteeFixture(sequence = it).toDomain())) }
        }
    }

    @Test
    fun `최신 가입순으로 멘티를 둘러본다`() {
        // given
        val condition: SearchMenteeCondition = SearchMenteeCondition.basic()

        /* 페이지 1 */
        val result1: Slice<Mentee> = sut.fetchMenteesByCondition(condition, pageable1)
        assertSoftly(result1) {
            hasNext() shouldBe true
            content shouldContainExactly listOf(
                mentees[19], mentees[18], mentees[17], mentees[16], mentees[15],
                mentees[14], mentees[13], mentees[12], mentees[11], mentees[10],
            )
        }

        /* 페이지 2 */
        val result2: Slice<Mentee> = sut.fetchMenteesByCondition(condition, pageable2)
        assertSoftly(result2) {
            hasNext() shouldBe false
            content shouldContainExactly listOf(
                mentees[9], mentees[8], mentees[7], mentees[6], mentees[5],
                mentees[4], mentees[3], mentees[2], mentees[1], mentees[0],
            )
        }
    }

    @Test
    fun `국적 기준으로 멘티를 둘러본다`() {
        // given
        val condition: SearchMenteeCondition = SearchMenteeCondition.of(
            nationalities = listOf(Nationality.USA, Nationality.CHINA, Nationality.JAPAN),
            languages = listOf(),
        )

        /* 페이지 1 */
        val result1: Slice<Mentee> = sut.fetchMenteesByCondition(condition, pageable1)
        assertSoftly(result1) {
            hasNext() shouldBe true
            content shouldContainExactly listOf(
                mentees[17], mentees[16], mentees[15], mentees[12], mentees[11],
                mentees[10], mentees[7], mentees[6], mentees[5], mentees[2],
            )
        }

        /* 페이지 2 */
        val result2: Slice<Mentee> = sut.fetchMenteesByCondition(condition, pageable2)
        assertSoftly(result2) {
            hasNext() shouldBe false
            content shouldContainExactly listOf(mentees[1], mentees[0])
        }
    }

    @Test
    fun `사용 가능한 언어 기준으로 멘티를 둘러본다`() {
        // given
        val condition1: SearchMenteeCondition = SearchMenteeCondition.of(
            nationalities = listOf(),
            languages = listOf(Language.Category.EN),
        )
        val condition2: SearchMenteeCondition = SearchMenteeCondition.of(
            nationalities = listOf(),
            languages = listOf(Language.Category.EN, Language.Category.KR),
        )
        val condition3: SearchMenteeCondition = SearchMenteeCondition.of(
            nationalities = listOf(),
            languages = listOf(Language.Category.EN, Language.Category.JP),
        )
        val condition4: SearchMenteeCondition = SearchMenteeCondition.of(
            nationalities = listOf(),
            languages = listOf(Language.Category.EN, Language.Category.KR, Language.Category.JP),
        )

        /* 페이지 1 */
        val result1: Slice<Mentee> = sut.fetchMenteesByCondition(condition1, pageable1)
        val result2: Slice<Mentee> = sut.fetchMenteesByCondition(condition2, pageable1)
        val result3: Slice<Mentee> = sut.fetchMenteesByCondition(condition3, pageable1)
        val result4: Slice<Mentee> = sut.fetchMenteesByCondition(condition4, pageable1)

        assertSoftly {
            result1.hasNext() shouldBe true
            result1.content shouldContainExactly listOf(
                mentees[19], mentees[18], mentees[17], mentees[16], mentees[15],
                mentees[14], mentees[13], mentees[12], mentees[11], mentees[10],
            )
            result2.hasNext() shouldBe false
            result2.content shouldContainExactly listOf(
                mentees[18], mentees[16], mentees[14], mentees[12], mentees[10],
                mentees[8], mentees[6], mentees[4], mentees[2], mentees[0],
            )
            result3.hasNext() shouldBe false
            result3.content shouldContainExactly listOf(
                mentees[19], mentees[17], mentees[15], mentees[13], mentees[11],
                mentees[9], mentees[7], mentees[5], mentees[3], mentees[1],
            )
            result4.hasNext() shouldBe false
            result4.content shouldContainExactly emptyList()
        }

        /* 페이지 2 */
        val result5: Slice<Mentee> = sut.fetchMenteesByCondition(condition1, pageable2)
        val result6: Slice<Mentee> = sut.fetchMenteesByCondition(condition2, pageable2)
        val result7: Slice<Mentee> = sut.fetchMenteesByCondition(condition3, pageable2)

        assertSoftly {
            result5.hasNext() shouldBe false
            result5.content shouldContainExactly listOf(
                mentees[9], mentees[8], mentees[7], mentees[6], mentees[5],
                mentees[4], mentees[3], mentees[2], mentees[1], mentees[0],
            )
            result6.hasNext() shouldBe false
            result6.content shouldContainExactly emptyList()
            result7.hasNext() shouldBe false
            result7.content shouldContainExactly emptyList()
        }
    }

    @Test
    fun `국적 + 사용 가능한 언어 기준으로 멘티를 둘러본다`() {
        // given
        val condition1: SearchMenteeCondition = SearchMenteeCondition.of(
            nationalities = listOf(Nationality.USA, Nationality.CHINA, Nationality.JAPAN),
            languages = listOf(Language.Category.EN),
        )
        val condition2: SearchMenteeCondition = SearchMenteeCondition.of(
            nationalities = listOf(Nationality.USA, Nationality.CHINA, Nationality.JAPAN),
            languages = listOf(Language.Category.EN, Language.Category.KR),
        )
        val condition3: SearchMenteeCondition = SearchMenteeCondition.of(
            nationalities = listOf(Nationality.USA, Nationality.CHINA, Nationality.JAPAN),
            languages = listOf(Language.Category.EN, Language.Category.JP),
        )

        /* 페이지 1 */
        val result1: Slice<Mentee> = sut.fetchMenteesByCondition(condition1, pageable1)
        val result2: Slice<Mentee> = sut.fetchMenteesByCondition(condition2, pageable1)
        val result3: Slice<Mentee> = sut.fetchMenteesByCondition(condition3, pageable1)

        assertSoftly {
            result1.hasNext() shouldBe true
            result1.content shouldContainExactly listOf(
                mentees[17], mentees[16], mentees[15], mentees[12], mentees[11],
                mentees[10], mentees[7], mentees[6], mentees[5], mentees[2],
            )
            result2.hasNext() shouldBe false
            result2.content shouldContainExactly listOf(
                mentees[16], mentees[12], mentees[10],
                mentees[6], mentees[2], mentees[0],
            )
            result3.hasNext() shouldBe false
            result3.content shouldContainExactly listOf(
                mentees[17], mentees[15], mentees[11],
                mentees[7], mentees[5], mentees[1],
            )
        }

        /* 페이지 2 */
        val result4: Slice<Mentee> = sut.fetchMenteesByCondition(condition1, pageable2)
        val result5: Slice<Mentee> = sut.fetchMenteesByCondition(condition2, pageable2)
        val result6: Slice<Mentee> = sut.fetchMenteesByCondition(condition3, pageable2)

        assertSoftly {
            result4.hasNext() shouldBe false
            result4.content shouldContainExactly listOf(mentees[1], mentees[0])
            result5.hasNext() shouldBe false
            result5.content shouldContainExactly emptyList()
            result6.hasNext() shouldBe false
            result6.content shouldContainExactly emptyList()
        }
    }
}
