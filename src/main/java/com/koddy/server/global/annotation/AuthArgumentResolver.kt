package com.koddy.server.global.annotation

import com.koddy.server.auth.domain.model.Authenticated
import com.koddy.server.auth.domain.service.TokenProvider
import com.koddy.server.auth.exception.AuthException
import com.koddy.server.auth.exception.AuthExceptionCode.AUTH_REQUIRED
import com.koddy.server.auth.utils.TokenExtractor
import jakarta.servlet.http.HttpServletRequest
import org.springframework.core.MethodParameter
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

class AuthArgumentResolver(
    private val tokenProvider: TokenProvider,
) : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        val hasRequiredAnnotation: Boolean = parameter.getParameterAnnotation(Auth::class.java) != null
        val hasRequiredType: Boolean = parameter.parameterType == Authenticated::class.java
        return hasRequiredAnnotation && hasRequiredType
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?,
    ): Authenticated {
        val request: HttpServletRequest? = webRequest.getNativeRequest(HttpServletRequest::class.java)
        val accessToken: String = getAccessToken(request!!)
        return Authenticated(
            tokenProvider.getId(accessToken),
            tokenProvider.getAuthority(accessToken),
        )
    }

    private fun getAccessToken(request: HttpServletRequest): String {
        val accessToken: String = TokenExtractor.extractAccessToken(request) ?: throw AuthException(AUTH_REQUIRED)
        tokenProvider.validateToken(accessToken)
        return accessToken
    }
}
