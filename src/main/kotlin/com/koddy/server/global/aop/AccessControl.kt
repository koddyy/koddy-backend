package com.koddy.server.global.aop

import com.koddy.server.member.domain.model.Role

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class AccessControl(val role: Role)
