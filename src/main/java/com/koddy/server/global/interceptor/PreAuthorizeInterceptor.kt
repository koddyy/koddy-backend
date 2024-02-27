package com.koddy.server.global.interceptor

import com.koddy.server.auth.domain.model.Authenticated
import com.koddy.server.auth.domain.model.AuthenticatedHolder
import com.koddy.server.auth.domain.service.TokenProvider
import com.koddy.server.auth.utils.TokenExtractor
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.ModelAndView

class PreAuthorizeInterceptor(
    private val tokenProvider: TokenProvider,
) : HandlerInterceptor {
    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
    ): Boolean {
        val accessToken: String? = TokenExtractor.extractAccessToken(request)
        if (accessToken.isNullOrBlank()) {
            return super.preHandle(request, response, handler)
        }

        tokenProvider.validateAccessToken(accessToken)
        AuthenticatedHolder.store(
            Authenticated(
                id = tokenProvider.getId(accessToken),
                authority = tokenProvider.getAuthority(accessToken),
            ),
        )
        return super.preHandle(request, response, handler)
    }

    override fun postHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any,
        modelAndView: ModelAndView?,
    ) {
        AuthenticatedHolder.refresh()
        super.postHandle(request, response, handler, modelAndView)
    }
}
