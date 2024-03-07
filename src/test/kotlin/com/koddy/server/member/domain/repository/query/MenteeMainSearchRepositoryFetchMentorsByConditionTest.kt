package com.koddy.server.member.domain.repository.query

import com.koddy.server.common.RepositoryTestKt
import com.koddy.server.common.fixture.MentorFixtureStore.mentorFixture
import com.koddy.server.global.query.PageCreator
import com.koddy.server.member.domain.model.Language
import com.koddy.server.member.domain.model.mentor.Mentor
import com.koddy.server.member.domain.repository.MemberRepository
import com.koddy.server.member.domain.repository.query.spec.SearchMentorCondition
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
@Import(MenteeMainSearchRepositoryImpl::class)
@DisplayName("Member -> MenteeMainSearchRepository [fetchMentorsByCondition] 테스트")
internal class MenteeMainSearchRepositoryFetchMentorsByConditionTest(
    private val sut: MenteeMainSearchRepositoryImpl,
    private val memberRepository: MemberRepository,
) {
    companion object {
        private val pageable1: Pageable = PageCreator.create(1)
        private val pageable2: Pageable = PageCreator.create(2)
    }

    private lateinit var mentors: List<Mentor>

    @BeforeEach
    fun setUp() {
        mentors = mutableListOf<Mentor>().apply {
            (1..20).forEach { add(memberRepository.save(mentorFixture(sequence = it).toDomain())) }
        }
    }

    @Test
    fun `최신 가입순으로 멘토를 둘러본다`() {
        // given
        val condition: SearchMentorCondition = SearchMentorCondition.basic()

        /* 페이지 1 */
        val result1: Slice<Mentor> = sut.fetchMentorsByCondition(condition, pageable1)
        assertSoftly(result1) {
            hasNext() shouldBe true
            content shouldContainExactly listOf(
                mentors[19], mentors[18], mentors[17], mentors[16], mentors[15],
                mentors[14], mentors[13], mentors[12], mentors[11], mentors[10],
            )
        }

        /* 페이지 2 */
        val result2: Slice<Mentor> = sut.fetchMentorsByCondition(condition, pageable2)
        assertSoftly(result2) {
            hasNext() shouldBe false
            content shouldContainExactly listOf(
                mentors[9], mentors[8], mentors[7], mentors[6], mentors[5],
                mentors[4], mentors[3], mentors[2], mentors[1], mentors[0],
            )
        }
    }

    @Test
    fun `사용 가능한 언어 기준으로 멘토를 둘러본다`() {
        // given
        val condition1: SearchMentorCondition = SearchMentorCondition.of(listOf(Language.Category.KR))
        val condition2: SearchMentorCondition = SearchMentorCondition.of(listOf(Language.Category.KR, Language.Category.EN))
        val condition3: SearchMentorCondition = SearchMentorCondition.of(listOf(Language.Category.KR, Language.Category.JP))
        val condition4: SearchMentorCondition = SearchMentorCondition.of(listOf(Language.Category.KR, Language.Category.EN, Language.Category.JP))

        /* 페이지 1 */
        val result1: Slice<Mentor> = sut.fetchMentorsByCondition(condition1, pageable1)
        val result2: Slice<Mentor> = sut.fetchMentorsByCondition(condition2, pageable1)
        val result3: Slice<Mentor> = sut.fetchMentorsByCondition(condition3, pageable1)
        val result4: Slice<Mentor> = sut.fetchMentorsByCondition(condition4, pageable1)

        assertSoftly {
            result1.hasNext() shouldBe true
            result1.content shouldContainExactly listOf(
                mentors[19], mentors[18], mentors[17], mentors[16], mentors[15],
                mentors[14], mentors[13], mentors[12], mentors[11], mentors[10],
            )
            result2.hasNext() shouldBe false
            result2.content shouldContainExactly listOf(
                mentors[18], mentors[16], mentors[14], mentors[12], mentors[10],
                mentors[8], mentors[6], mentors[4], mentors[2], mentors[0],
            )
            result3.hasNext() shouldBe false
            result3.content shouldContainExactly listOf(
                mentors[19], mentors[17], mentors[15], mentors[13], mentors[11],
                mentors[9], mentors[7], mentors[5], mentors[3], mentors[1],
            )
            result4.hasNext() shouldBe false
            result4.content shouldContainExactly emptyList()
        }

        /* 페이지 2 */
        val result5: Slice<Mentor> = sut.fetchMentorsByCondition(condition1, pageable2)
        val result6: Slice<Mentor> = sut.fetchMentorsByCondition(condition2, pageable2)
        val result7: Slice<Mentor> = sut.fetchMentorsByCondition(condition3, pageable2)

        assertSoftly {
            result5.hasNext() shouldBe false
            result5.content shouldContainExactly listOf(
                mentors[9], mentors[8], mentors[7], mentors[6], mentors[5],
                mentors[4], mentors[3], mentors[2], mentors[1], mentors[0],
            )
            result6.hasNext() shouldBe false
            result6.content shouldContainExactly emptyList()
            result7.hasNext() shouldBe false
            result7.content shouldContainExactly emptyList()
        }
    }
}
