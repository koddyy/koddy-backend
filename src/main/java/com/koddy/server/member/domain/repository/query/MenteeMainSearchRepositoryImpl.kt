package com.koddy.server.member.domain.repository.query

import com.koddy.server.coffeechat.domain.model.CoffeeChat
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTOR_SUGGEST
import com.koddy.server.global.annotation.KoddyReadOnlyTransactional
import com.koddy.server.global.query.CoffeeChatDsl
import com.koddy.server.global.query.MemberDsl
import com.koddy.server.member.domain.model.AvailableLanguage
import com.koddy.server.member.domain.model.Language
import com.koddy.server.member.domain.model.Member
import com.koddy.server.member.domain.model.mentor.Mentor
import com.koddy.server.member.domain.repository.query.response.SuggestedCoffeeChatsByMentor
import com.koddy.server.member.domain.repository.query.spec.SearchMentorCondition
import com.linecorp.kotlinjdsl.dsl.jpql.jpql
import com.linecorp.kotlinjdsl.querymodel.jpql.select.SelectQuery
import com.linecorp.kotlinjdsl.render.jpql.JpqlRenderContext
import com.linecorp.kotlinjdsl.support.spring.data.jpa.extension.createQuery
import jakarta.persistence.EntityManager
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Slice
import org.springframework.data.domain.SliceImpl
import org.springframework.data.support.PageableExecutionUtils
import org.springframework.stereotype.Repository

@Repository
@KoddyReadOnlyTransactional
class MenteeMainSearchRepositoryImpl(
    private val entityManager: EntityManager,
    private val context: JpqlRenderContext,
) : MenteeMainSearchRepository {
    override fun fetchSuggestedMentors(
        menteeId: Long,
        limit: Int,
    ): Page<SuggestedCoffeeChatsByMentor> {
        val targetQuery: SelectQuery<SuggestedCoffeeChatsByMentor> = jpql(CoffeeChatDsl) {
            selectNew<SuggestedCoffeeChatsByMentor>(
                path(CoffeeChat::id),
                path(Mentor::id),
                path(Mentor::name),
                path(Mentor::profileImageUrl),
                path(Mentor::universityProfile),
            ).from(
                entity(CoffeeChat::class),
                innerJoin(Mentor::class).on(path(Mentor::id).equal(path(CoffeeChat::mentorId))),
            ).whereAnd(
                entity(CoffeeChat::class).menteeIdEq(menteeId),
                entity(CoffeeChat::class).statusEq(MENTOR_SUGGEST),
            ).orderBy(
                path(CoffeeChat::id).desc(),
            )
        }
        val countQuery: SelectQuery<Long> = jpql(CoffeeChatDsl) {
            selectNew<Long>(
                count(CoffeeChat::id),
            ).from(
                entity(CoffeeChat::class),
                innerJoin(Mentor::class).on(path(Mentor::id).equal(path(CoffeeChat::mentorId))),
            ).whereAnd(
                entity(CoffeeChat::class).menteeIdEq(menteeId),
                entity(CoffeeChat::class).statusEq(MENTOR_SUGGEST),
            )
        }

        val targetResult: List<SuggestedCoffeeChatsByMentor> = entityManager.createQuery(targetQuery, context)
            .setFirstResult(0)
            .setMaxResults(limit)
            .resultList
        val countResult: Long = entityManager.createQuery(countQuery, context).singleResult

        return PageableExecutionUtils.getPage(
            targetResult,
            PageRequest.of(0, limit),
        ) { countResult }
    }

    override fun fetchMentorsByCondition(
        condition: SearchMentorCondition,
        pageable: Pageable,
    ): Slice<Mentor> {
        val filteringMentorIds: List<Long> = filteringByCondition(condition)
        val targetQuery: SelectQuery<Mentor> = jpql(MemberDsl) {
            select(
                entity(Mentor::class),
            ).from(
                entity(Mentor::class),
            ).where(
                entity(Mentor::class).mentorIdIn(filteringMentorIds),
            ).orderBy(
                path(Mentor::id).desc(),
            )
        }

        val targetResult: List<Mentor> = when (filteringMentorIds.isEmpty()) {
            true -> emptyList()
            false -> entityManager.createQuery(targetQuery, context)
                .setFirstResult(pageable.offset.toInt())
                .setMaxResults(pageable.pageSize + 1)
                .resultList
        }
        val hasNext: Boolean = targetResult.size > pageable.pageSize

        return SliceImpl(
            takeIf { hasNext }?.let { targetResult.dropLast(1) } ?: targetResult,
            pageable,
            targetResult.size > pageable.pageSize,
        )
    }

    private fun filteringByCondition(condition: SearchMentorCondition): List<Long> {
        return filteringLanguage(condition.language)
    }

    private fun filteringLanguage(language: SearchMentorCondition.LanguageCondition): List<Long> {
        val query: SelectQuery<Long> = when (language.exists) {
            true -> jpql(MemberDsl) {
                selectDistinctNew<Long>(
                    path(AvailableLanguage::member)(Member<*>::id),
                ).from(
                    entity(AvailableLanguage::class),
                ).where(
                    entity(AvailableLanguage::class).languageCategoryIn(language.values),
                ).groupBy(
                    path(AvailableLanguage::member)(Member<*>::id),
                ).having(
                    count(path(AvailableLanguage::language)(Language::category))
                        .greaterThanOrEqualTo(language.values.size.toLong()),
                ).orderBy(
                    path(AvailableLanguage::member)(Member<*>::id).desc(),
                )
            }

            false -> jpql {
                selectDistinctNew<Long>(
                    path(AvailableLanguage::member)(Member<*>::id),
                ).from(
                    entity(AvailableLanguage::class),
                ).orderBy(
                    path(AvailableLanguage::member)(Member<*>::id).desc(),
                )
            }
        }

        return entityManager.createQuery(query, context).resultList
    }
}
