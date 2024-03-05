package com.koddy.server.member.domain.repository.query

import com.koddy.server.coffeechat.domain.model.CoffeeChat
import com.koddy.server.coffeechat.domain.model.CoffeeChatStatus.MENTEE_APPLY
import com.koddy.server.global.annotation.KoddyReadOnlyTransactional
import com.koddy.server.member.domain.model.AvailableLanguage
import com.koddy.server.member.domain.model.Language
import com.koddy.server.member.domain.model.Member
import com.koddy.server.member.domain.model.mentee.Mentee
import com.koddy.server.member.domain.repository.query.response.AppliedCoffeeChatsByMentee
import com.koddy.server.member.domain.repository.query.spec.SearchMenteeCondition
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
class MentorMainSearchRepositoryImpl(
    private val entityManager: EntityManager,
    private val context: JpqlRenderContext,
) : MentorMainSearchRepository {
    override fun fetchAppliedMentees(
        mentorId: Long,
        limit: Int,
    ): Page<AppliedCoffeeChatsByMentee> {
        val targetQuery: SelectQuery<AppliedCoffeeChatsByMentee> = jpql {
            selectNew<AppliedCoffeeChatsByMentee>(
                path(CoffeeChat::id),
                path(Mentee::id),
                path(Mentee::name),
                path(Mentee::profileImageUrl),
                path(Mentee::nationality),
                path(Mentee::interest),
            ).from(
                entity(CoffeeChat::class),
                innerJoin(Mentee::class).on(path(Mentee::id).equal(path(CoffeeChat::menteeId))),
            ).whereAnd(
                path(CoffeeChat::mentorId).equal(mentorId),
                path(CoffeeChat::status).equal(MENTEE_APPLY),
            ).orderBy(
                path(CoffeeChat::id).desc(),
            )
        }
        val countQuery: SelectQuery<Long> = jpql {
            selectNew<Long>(
                count(CoffeeChat::id),
            ).from(
                entity(CoffeeChat::class),
                innerJoin(Mentee::class).on(path(Mentee::id).equal(path(CoffeeChat::menteeId))),
            ).whereAnd(
                path(CoffeeChat::mentorId).equal(mentorId),
                path(CoffeeChat::status).equal(MENTEE_APPLY),
            )
        }

        val targetResult: List<AppliedCoffeeChatsByMentee> = entityManager.createQuery(targetQuery, context)
            .setFirstResult(0)
            .setMaxResults(limit)
            .resultList
        val countResult: Long = entityManager.createQuery(countQuery, context).singleResult

        return PageableExecutionUtils.getPage(
            targetResult,
            PageRequest.of(0, limit),
        ) { countResult }
    }

    override fun fetchMenteesByCondition(
        condition: SearchMenteeCondition,
        pageable: Pageable,
    ): Slice<Mentee> {
        val filteringMenteeIds: List<Long> = filteringByCondition(condition)
        val targetQuery: SelectQuery<Mentee> = jpql {
            select(
                entity(Mentee::class),
            ).from(
                entity(Mentee::class),
            ).where(
                path(Mentee::id).`in`(filteringMenteeIds),
            ).orderBy(
                path(Mentee::id).desc(),
            )
        }

        val targetResult: List<Mentee> = when (filteringMenteeIds.isEmpty()) {
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

    private fun filteringByCondition(condition: SearchMenteeCondition): List<Long> {
        val containsNationalityMenteeIds: List<Long> = filteringNationality(condition.nationality)
        val containsLanguageMenteeIds: List<Long> = filteringLanguage(condition.language)
        return mergeMenteeIds(containsNationalityMenteeIds, containsLanguageMenteeIds)
    }

    private fun filteringNationality(nationality: SearchMenteeCondition.NationalityCondition): List<Long> {
        val query: SelectQuery<Long> = jpql {
            select<Long>(
                path(Mentee::id),
            ).from(
                entity(Mentee::class),
            ).where(
                when {
                    nationality.exists -> or(*nationality.values.map { path(Mentee::nationality).equal(it) }.toTypedArray())
                    else -> null
                },
            ).orderBy(
                path(Mentee::id).desc(),
            )
        }

        return entityManager.createQuery(query, context).resultList
    }

    private fun filteringLanguage(language: SearchMenteeCondition.LanguageCondition): List<Long> {
        val query: SelectQuery<Long> = when (language.exists) {
            true -> jpql {
                selectDistinctNew<Long>(
                    path(AvailableLanguage::member)(Member<*>::id),
                ).from(
                    entity(AvailableLanguage::class),
                ).where(
                    path(AvailableLanguage::language)(Language::category).`in`(language.values),
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

    private fun mergeMenteeIds(
        containsNationalityMenteeIds: List<Long>,
        containsLanguageMenteeIds: List<Long>,
    ): List<Long> {
        return containsNationalityMenteeIds.toMutableList().apply {
            retainAll(containsLanguageMenteeIds)
        }
    }
}
