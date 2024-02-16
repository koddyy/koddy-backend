package com.koddy.server.global.config.etc

import com.p6spy.engine.event.JdbcEventListener
import com.p6spy.engine.logging.Category.STATEMENT
import com.p6spy.engine.spy.appender.MessageFormattingStrategy
import org.hibernate.engine.jdbc.internal.FormatStyle.BASIC
import org.hibernate.engine.jdbc.internal.FormatStyle.DDL
import java.util.Locale.ROOT

class P6SpyFormatter : JdbcEventListener(), MessageFormattingStrategy {
    override fun formatMessage(
        connectionId: Int,
        now: String,
        elapsed: Long,
        category: String,
        prepared: String,
        sql: String?,
        url: String,
    ): String {
        return buildString {
            append(category)
            append(" -> ")
            append("[쿼리 수행시간 = ")
            append(elapsed).append("ms")
            append(" | DB 커넥션 정보(Connection ID) = ")
            append(connectionId)
            append("]")
            append(format(category, sql))
        }
    }

    private fun format(
        category: String,
        sql: String?,
    ): String? {
        if (sql.isNullOrBlank()) {
            return sql
        }

        if (category == STATEMENT.name) {
            val queryParts: String = sql.trim().lowercase(ROOT)
            when {
                queryParts.startsWith("create") -> DDL.formatter.format(sql)
                queryParts.startsWith("alter") -> DDL.formatter.format(sql)
                queryParts.startsWith("comment") -> DDL.formatter.format(sql)
                else -> BASIC.formatter.format(sql)
            }
        }
        return sql
    }
}
