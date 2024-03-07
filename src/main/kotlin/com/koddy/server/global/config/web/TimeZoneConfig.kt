package com.koddy.server.global.config.web

import com.koddy.server.global.base.DEFAULT_ZONE_ID
import jakarta.annotation.PostConstruct
import org.springframework.context.annotation.Configuration
import java.util.TimeZone

@Configuration
class TimeZoneConfig {
    @PostConstruct
    fun initTimeZone() = TimeZone.setDefault(TimeZone.getTimeZone(DEFAULT_ZONE_ID))
}
