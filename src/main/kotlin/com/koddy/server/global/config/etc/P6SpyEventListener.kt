package com.koddy.server.global.config.etc

import com.p6spy.engine.common.ConnectionInformation
import com.p6spy.engine.event.JdbcEventListener
import com.p6spy.engine.spy.P6SpyOptions
import java.sql.SQLException

class P6SpyEventListener : JdbcEventListener() {
    override fun onAfterGetConnection(
        connectionInformation: ConnectionInformation?,
        e: SQLException?,
    ) {
        P6SpyOptions.getActiveInstance().logMessageFormat = P6SpyFormatter::class.java.name
    }
}
