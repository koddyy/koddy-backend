package com.koddy.server.global.annotation

import com.koddy.server.auth.domain.model.TokenType
import com.koddy.server.auth.domain.service.TokenProvider
import com.koddy.server.auth.exception.AuthException
import com.koddy.server.auth.exception.AuthExceptionCode.INVALID_PERMISSION
import com.koddy.server.auth.utils.TokenExtractor
import jakarta.servlet.http.HttpServletRequest
import org.springframework.core.MethodParameter
import org.springframework.web.bind.support.WebDataBinderFactory
import org.springframework.web.context.request.NativeWebRequest
import org.springframework.web.method.support.HandlerMethodArgumentResolver
import org.springframework.web.method.support.ModelAndViewContainer

class ExtractTokenArgumentResolver(
    private val tokenProvider: TokenProvider,
) : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        val hasRequiredAnnotation: Boolean = parameter.getParameterAnnotation(ExtractToken::class.java) != null
        val hasRequiredType: Boolean = parameter.parameterType == String::class.java
        return hasRequiredAnnotation && hasRequiredType
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        mavContainer: ModelAndViewContainer?,
        webRequest: NativeWebRequest,
        binderFactory: WebDataBinderFactory?,
    ): String {
        val request: HttpServletRequest? = webRequest.getNativeRequest(HttpServletRequest::class.java)
        val extractToken: ExtractToken? = parameter.getParameterAnnotation(ExtractToken::class.java)
        return getToken(request!!, extractToken!!.tokenType)
    }

    private fun getToken(
        request: HttpServletRequest,
        type: TokenType,
    ): String {
        val token: String = when (type) {
            TokenType.ACCESS -> TokenExtractor.extractAccessToken(request) ?: throw AuthException(INVALID_PERMISSION)
            TokenType.REFRESH -> TokenExtractor.extractRefreshToken(request) ?: throw AuthException(INVALID_PERMISSION)
        }
        tokenProvider.validateToken(token)
        return token
    }
}
