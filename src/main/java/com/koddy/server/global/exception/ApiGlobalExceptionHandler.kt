package com.koddy.server.global.exception

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.koddy.server.auth.exception.OAuthUserNotFoundException
import com.koddy.server.global.base.BusinessException
import com.koddy.server.global.base.BusinessExceptionCode
import com.koddy.server.global.exception.alert.SlackAlertManager
import com.koddy.server.global.log.RequestMetadataExtractor.getRequestUriWithQueryString
import io.github.oshai.kotlinlogging.KLogger
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.validation.BindException
import org.springframework.validation.FieldError
import org.springframework.web.HttpMediaTypeNotSupportedException
import org.springframework.web.HttpRequestMethodNotSupportedException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.UnsatisfiedServletRequestParameterException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException
import org.springframework.web.multipart.MaxUploadSizeExceededException
import org.springframework.web.servlet.NoHandlerFoundException

@RestControllerAdvice
class ApiGlobalExceptionHandler(
    private val objectMapper: ObjectMapper,
    private val slackAlertManager: SlackAlertManager,
) {
    private val logger: KLogger = KotlinLogging.logger { }

    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(exception: BusinessException): ResponseEntity<ExceptionResponse> {
        logger.info { "handleBusinessException -> code = ${exception.code} message = ${exception.message}" }
        return createExceptionResponse(exception.code)
    }

    @ExceptionHandler(OAuthUserNotFoundException::class)
    fun handleOAuthUserNotFoundException(exception: OAuthUserNotFoundException): ResponseEntity<OAuthExceptionResponse> {
        logger.info { "handleOAuthUserNotFoundException -> response = ${exception.response}" }
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(OAuthExceptionResponse(exception.response))
    }

    /**
     * HTTP Body Data Parsing에 대한 처리
     */
    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadableException(exception: HttpMessageNotReadableException): ResponseEntity<ExceptionResponse> {
        logger.warn { "handleHttpMessageNotReadableException -> message = ${exception.localizedMessage}" }
        return createExceptionResponse(GlobalExceptionCode.VALIDATION_ERROR)
    }

    @ExceptionHandler(MismatchedInputException::class)
    protected fun handleMismatchedInputException(exception: MismatchedInputException): ResponseEntity<ExceptionResponse> {
        logger.warn { "handleMismatchedInputException -> target = ${exception.targetType} ${exception.localizedMessage}" }
        return createExceptionResponse(GlobalExceptionCode.VALIDATION_ERROR)
    }

    /**
     * Required 요청 파라미터가 들어오지 않았을 경우에 대한 처리
     */
    @ExceptionHandler(UnsatisfiedServletRequestParameterException::class)
    fun handleUnsatisfiedServletRequestParameterException(exception: UnsatisfiedServletRequestParameterException): ResponseEntity<ExceptionResponse> {
        logger.warn { "handleUnsatisfiedServletRequestParameterException -> message = ${exception.localizedMessage}" }
        return createExceptionResponse(GlobalExceptionCode.VALIDATION_ERROR)
    }

    /**
     * 요청 데이터 Validation에 대한 처리 (@RequestBody)
     */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(exception: MethodArgumentNotValidException): ResponseEntity<ExceptionResponse> {
        logger.warn { "handleMethodArgumentNotValidException -> param = ${exception.parameter} message = ${exception.localizedMessage}" }
        return createExceptionResponse(exception.bindingResult.fieldErrors)
    }

    /**
     * 요청 데이터 Validation에 대한 처리 (@ModelAttribute)
     */
    @ExceptionHandler(BindException::class)
    fun handleBindException(exception: BindException): ResponseEntity<ExceptionResponse> {
        logger.warn { "handleMethodArgumentNotValidException -> message = ${exception.localizedMessage}" }
        return createExceptionResponse(exception.bindingResult.fieldErrors)
    }

    /**
     * 요청 데이터 오류에 대한 처리
     */
    @ExceptionHandler(
        MethodArgumentTypeMismatchException::class,
        MaxUploadSizeExceededException::class
    )
    fun handleRequestDataException(exception: Exception): ResponseEntity<ExceptionResponse> {
        logger.warn { "handleRequestDataException -> message = ${exception.localizedMessage}" }
        return createExceptionResponse(GlobalExceptionCode.VALIDATION_ERROR)
    }

    /**
     * HTTP Request Method 오류에 대한 처리
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun handleHttpRequestMethodNotSupportedException(exception: HttpRequestMethodNotSupportedException): ResponseEntity<ExceptionResponse> {
        logger.warn { "handleHttpRequestMethodNotSupportedException -> message = ${exception.localizedMessage}" }
        return createExceptionResponse(GlobalExceptionCode.NOT_SUPPORTED_METHOD_ERROR)
    }

    /**
     * MediaType 전용 ExceptionHandler
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException::class)
    fun handleHttpMediaTypeNotSupportedException(exception: HttpMediaTypeNotSupportedException): ResponseEntity<ExceptionResponse> {
        logger.warn { "handleHttpMediaTypeNotSupportedException -> message = ${exception.localizedMessage}" }
        return createExceptionResponse(GlobalExceptionCode.UNSUPPORTED_MEDIA_TYPE_ERROR)
    }

    /**
     * ETC 서버 내부 오류에 대한 처리
     */
    @ExceptionHandler(Exception::class)
    fun handleAnyException(
        request: HttpServletRequest,
        exception: Exception,
    ): ResponseEntity<ExceptionResponse> {
        if (exception !is NoHandlerFoundException) {
            logger.error { "handleAnyException -> method = ${request.method} request = ${getRequestUriWithQueryString(request)} ${exception.stackTrace}" }
            slackAlertManager.sendErrorLog(request, exception)
            return createExceptionResponse(GlobalExceptionCode.UNEXPECTED_SERVER_ERROR)
        }
        return createExceptionResponse(GlobalExceptionCode.NOT_SUPPORTED_URI_ERROR)
    }

    private fun createExceptionResponse(code: BusinessExceptionCode): ResponseEntity<ExceptionResponse> {
        return ResponseEntity
            .status(code.status)
            .body(ExceptionResponse(code))
    }

    private fun createExceptionResponse(fieldErrors: List<FieldError>): ResponseEntity<ExceptionResponse> {
        val code: BusinessExceptionCode = GlobalExceptionCode.VALIDATION_ERROR
        val exceptionMessage: String = extractErrorMessage(fieldErrors) ?: ""
        return ResponseEntity
            .status(code.status)
            .body(ExceptionResponse(code, exceptionMessage))
    }

    private fun extractErrorMessage(fieldErrors: List<FieldError>): String? {
        if (fieldErrors.size == 1) {
            return fieldErrors[0].defaultMessage
        }

        try {
            val errors: Map<String, String?> = collectErrorFields(fieldErrors)
            return objectMapper.writeValueAsString(errors)
        } catch (e: JsonProcessingException) {
            throw RuntimeException("JSON Parsing Error...", e)
        }
    }

    private fun collectErrorFields(fieldErrors: List<FieldError>): Map<String, String?> {
        return fieldErrors.associate { it.field to it.defaultMessage }
    }
}
