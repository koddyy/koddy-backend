package com.koddy.server.global.exception

import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import com.koddy.server.auth.exception.OAuthUserNotFoundException
import com.koddy.server.global.base.BusinessException
import com.koddy.server.global.base.BusinessExceptionCode
import com.koddy.server.global.exception.alert.SlackAlertManager
import com.koddy.server.global.log.RequestMetadataExtractor.getRequestUriWithQueryString
import com.koddy.server.global.log.logger
import jakarta.servlet.http.HttpServletRequest
import org.slf4j.Logger
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
    private val log: Logger = logger()

    @ExceptionHandler(BusinessException::class)
    fun handleBusinessException(ex: BusinessException): ResponseEntity<ExceptionResponse> {
        log.info("handleBusinessException -> code = {}, message = {}", ex.code, ex.message)
        return createExceptionResponse(ex.code)
    }

    @ExceptionHandler(OAuthUserNotFoundException::class)
    fun handleOAuthUserNotFoundException(ex: OAuthUserNotFoundException): ResponseEntity<OAuthExceptionResponse> {
        log.info("handleOAuthUserNotFoundException -> response = {}", ex.response)
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(OAuthExceptionResponse(ex.response))
    }

    /**
     * HTTP Body Data Parsing에 대한 처리
     */
    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadableException(ex: HttpMessageNotReadableException): ResponseEntity<ExceptionResponse> {
        log.warn("handleHttpMessageNotReadableException -> message = {}", ex.localizedMessage)
        return createExceptionResponse(GlobalExceptionCode.VALIDATION_ERROR)
    }

    @ExceptionHandler(MismatchedInputException::class)
    protected fun handleMismatchedInputException(ex: MismatchedInputException): ResponseEntity<ExceptionResponse> {
        log.warn("handleHttpMessageNotReadableException -> target = {}, message = {}", ex.targetType, ex.localizedMessage)
        return createExceptionResponse(GlobalExceptionCode.VALIDATION_ERROR)
    }

    /**
     * Required 요청 파라미터가 들어오지 않았을 경우에 대한 처리
     */
    @ExceptionHandler(UnsatisfiedServletRequestParameterException::class)
    fun handleUnsatisfiedServletRequestParameterException(ex: UnsatisfiedServletRequestParameterException): ResponseEntity<ExceptionResponse> {
        log.warn("handleUnsatisfiedServletRequestParameterException -> message = {}", ex.localizedMessage)
        return createExceptionResponse(GlobalExceptionCode.VALIDATION_ERROR)
    }

    /**
     * 요청 데이터 Validation에 대한 처리 (@RequestBody)
     */
    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleMethodArgumentNotValidException(ex: MethodArgumentNotValidException): ResponseEntity<ExceptionResponse> {
        log.warn("handleMethodArgumentNotValidException -> param = {}, message = {}", ex.parameter, ex.localizedMessage)
        return createExceptionResponse(ex.bindingResult.fieldErrors)
    }

    /**
     * 요청 데이터 Validation에 대한 처리 (@ModelAttribute)
     */
    @ExceptionHandler(BindException::class)
    fun handleBindException(ex: BindException): ResponseEntity<ExceptionResponse> {
        log.warn("handleMethodArgumentNotValidException -> message = {}", ex.localizedMessage)
        return createExceptionResponse(ex.bindingResult.fieldErrors)
    }

    /**
     * 요청 데이터 오류에 대한 처리
     */
    @ExceptionHandler(
        MethodArgumentTypeMismatchException::class,
        MaxUploadSizeExceededException::class,
    )
    fun handleRequestDataException(ex: Exception): ResponseEntity<ExceptionResponse> {
        log.warn("handleRequestDataException -> message = {}", ex.localizedMessage)
        return createExceptionResponse(GlobalExceptionCode.VALIDATION_ERROR)
    }

    /**
     * HTTP Request Method 오류에 대한 처리
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException::class)
    fun handleHttpRequestMethodNotSupportedException(ex: HttpRequestMethodNotSupportedException): ResponseEntity<ExceptionResponse> {
        log.warn("handleHttpRequestMethodNotSupportedException -> message = {}", ex.localizedMessage)
        return createExceptionResponse(GlobalExceptionCode.NOT_SUPPORTED_METHOD_ERROR)
    }

    /**
     * MediaType 전용 ExceptionHandler
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException::class)
    fun handleHttpMediaTypeNotSupportedException(ex: HttpMediaTypeNotSupportedException): ResponseEntity<ExceptionResponse> {
        log.warn("handleHttpMediaTypeNotSupportedException -> message = {}", ex.localizedMessage)
        return createExceptionResponse(GlobalExceptionCode.UNSUPPORTED_MEDIA_TYPE_ERROR)
    }

    /**
     * ETC 서버 내부 오류에 대한 처리
     */
    @ExceptionHandler(Exception::class)
    fun handleAnyException(
        request: HttpServletRequest,
        ex: Exception,
    ): ResponseEntity<ExceptionResponse> {
        if (ex !is NoHandlerFoundException) {
            log.error("handleAnyException -> method = {}, request = {}", request.method, getRequestUriWithQueryString(request), ex)
            slackAlertManager.sendErrorLog(request, ex)
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
