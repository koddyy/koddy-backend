package com.koddy.server.global.aop

import java.util.concurrent.TimeUnit

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class DailyMailAuthLimit(
    val maxTry: Int = 3,
    val banTime: Long = 10,
    val banTimeUnit: TimeUnit = TimeUnit.MINUTES,
)
