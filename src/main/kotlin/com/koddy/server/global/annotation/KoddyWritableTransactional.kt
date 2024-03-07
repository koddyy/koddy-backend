package com.koddy.server.global.annotation

import org.springframework.transaction.annotation.Transactional

@Target(AnnotationTarget.CLASS, AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
@Transactional
annotation class KoddyWritableTransactional
