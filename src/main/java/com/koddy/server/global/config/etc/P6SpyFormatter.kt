package com.koddy.server.global.config.etc

import com.p6spy.engine.logging.Category.STATEMENT
import com.p6spy.engine.spy.appender.MessageFormattingStrategy
import org.hibernate.engine.jdbc.internal.FormatStyle.BASIC
import org.hibernate.engine.jdbc.internal.FormatStyle.DDL
import org.hibernate.engine.jdbc.internal.FormatStyle.HIGHLIGHT
import java.util.Locale.ROOT

class P6SpyFormatter : MessageFormattingStrategy {
    override fun formatMessage(
        connectionId: Int,
        now: String,
        elapsed: Long,
        category: String,
        prepared: String,
        sql: String?,
        url: String,
    ): String {
        if (sql.isNullOrBlank()) {
            return "Command -> Execute = ${elapsed}ms || Category = $category || DB Connection ID = $connectionId || URL = $url"
        }
        if (isStatementDDL(sql, category)) {
            return "DDL Query -> Execute = ${elapsed}ms || DB Connection ID = $connectionId || URL = $url ${highlight(DDL.formatter.format(sql))}"
        }
        return "DML Query -> Execute = ${elapsed}ms || DB Connection ID = $connectionId || URL = $url ${highlight(BASIC.formatter.format(sql))}"
    }

    private fun isStatementDDL(
        sql: String?,
        category: String,
    ): Boolean {
        return isStatement(category) && !sql.isNullOrBlank() && isDDL(sql.trim().lowercase(ROOT))
    }

    private fun isStatement(category: String): Boolean {
        return STATEMENT.name == category
    }

    private fun isDDL(sql: String): Boolean {
        return setOf(CREATE, ALTER, DROP, COMMENT)
            .any { sql.startsWith(it) }
    }

    private fun highlight(sql: String?): String {
        return HIGHLIGHT.formatter.format(sql)
    }

    companion object {
        private const val CREATE: String = "create"
        private const val ALTER: String = "alter"
        private const val DROP: String = "drop"
        private const val COMMENT: String = "comment"
    }
}
