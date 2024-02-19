package com.koddy.server.auth.domain.model

object AuthenticatedHolder {
    private val holder = ThreadLocal<Authenticated>()

    fun retrieve(): Authenticated? = holder.get()

    fun store(authenticated: Authenticated): Unit = holder.set(authenticated)

    fun refresh() = holder.remove()
}
