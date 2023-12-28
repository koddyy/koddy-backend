package com.koddy.server.global.exception;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.koddy.server.global.base.KoddyException;
import com.koddy.server.global.base.KoddyExceptionCode;
import com.koddy.server.global.exception.alert.SlackAlertManager;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.UnsatisfiedServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.koddy.server.global.log.RequestMetadataExtractor.getRequestUriWithQueryString;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class ApiGlobalExceptionHandler {
    private final ObjectMapper objectMapper;
    private final SlackAlertManager slackAlertManager;

    @ExceptionHandler(KoddyException.class)
    public ResponseEntity<ExceptionResponse> handleKoddyException(final KoddyException exception) {
        return createExceptionResponse(exception.getCode());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionResponse> handleIllegalArgumentException(
            final IllegalArgumentException exception
    ) {
        log.warn("handleIllegalArgumentException: ", exception);
        return createExceptionResponse(GlobalExceptionCode.VALIDATION_ERROR);
    }

    /**
     * HTTP Body Data Parsing에 대한 처리
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ExceptionResponse> handleHttpMessageNotReadableException(
            final HttpMessageNotReadableException exception
    ) {
        log.warn("handleHttpMessageNotReadableException: ", exception);
        return createExceptionResponse(GlobalExceptionCode.VALIDATION_ERROR);
    }

    /**
     * Required 요청 파라미터가 들어오지 않았을 경우에 대한 처리
     */
    @ExceptionHandler(UnsatisfiedServletRequestParameterException.class)
    public ResponseEntity<ExceptionResponse> handleUnsatisfiedServletRequestParameterException(
            final UnsatisfiedServletRequestParameterException exception
    ) {
        log.warn("handleUnsatisfiedServletRequestParameterException: ", exception);
        return createExceptionResponse(GlobalExceptionCode.VALIDATION_ERROR);
    }

    /**
     * 요청 데이터 Validation에 대한 처리 (@RequestBody)
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionResponse> handleMethodArgumentNotValidException(
            final MethodArgumentNotValidException exception
    ) {
        log.warn("handleMethodArgumentNotValidException: ", exception);
        return createExceptionResponse(exception.getBindingResult().getFieldErrors());
    }

    /**
     * 요청 데이터 Validation에 대한 처리 (@ModelAttribute)
     */
    @ExceptionHandler(BindException.class)
    public ResponseEntity<ExceptionResponse> handleBindException(final BindException exception) {
        log.warn("handleBindException: ", exception);
        return createExceptionResponse(exception.getBindingResult().getFieldErrors());
    }

    /**
     * 요청 데이터 오류에 대한 처리
     */
    @ExceptionHandler({
            MethodArgumentTypeMismatchException.class,
            MaxUploadSizeExceededException.class
    })
    public ResponseEntity<ExceptionResponse> handleRequestDataException(
            final Exception exception
    ) {
        log.warn("handleRequestDataException: ", exception);
        return createExceptionResponse(GlobalExceptionCode.VALIDATION_ERROR);
    }

    /**
     * 존재하지 않는 Endpoint에 대한 처리
     */
    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ExceptionResponse> handleNoHandlerFoundException(final NoHandlerFoundException exception) {
        log.warn("handleNoHandlerFoundException: ", exception);
        return createExceptionResponse(GlobalExceptionCode.NOT_SUPPORTED_URI_ERROR);
    }

    /**
     * HTTP Request Method 오류에 대한 처리
     */
    @ExceptionHandler(HttpRequestMethodNotSupportedException.class)
    public ResponseEntity<ExceptionResponse> handleHttpRequestMethodNotSupportedException(
            final HttpRequestMethodNotSupportedException exception
    ) {
        log.warn("HttpRequestMethodNotSupportedException: ", exception);
        return createExceptionResponse(GlobalExceptionCode.NOT_SUPPORTED_METHOD_ERROR);
    }

    /**
     * MediaType 전용 ExceptionHandler
     */
    @ExceptionHandler(HttpMediaTypeNotSupportedException.class)
    public ResponseEntity<ExceptionResponse> handleHttpMediaTypeNotSupportedException(
            final HttpMediaTypeNotSupportedException exception
    ) {
        log.warn("HttpMediaTypeNotSupportedException: ", exception);
        return createExceptionResponse(GlobalExceptionCode.UNSUPPORTED_MEDIA_TYPE_ERROR);
    }

    /**
     * ETC 서버 내부 오류에 대한 처리
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleAnyException(
            final HttpServletRequest request,
            final Exception exception
    ) {
        log.error(
                "handleAnyException: {} {}",
                request.getMethod(),
                getRequestUriWithQueryString(request),
                exception
        );
        slackAlertManager.sendErrorLog(request, exception);
        return createExceptionResponse(GlobalExceptionCode.UNEXPECTED_SERVER_ERROR);
    }

    private ResponseEntity<ExceptionResponse> createExceptionResponse(final KoddyExceptionCode code) {
        return ResponseEntity
                .status(code.getStatus())
                .body(new ExceptionResponse(code));
    }

    private ResponseEntity<ExceptionResponse> createExceptionResponse(final List<FieldError> fieldErrors) {
        final KoddyExceptionCode code = GlobalExceptionCode.VALIDATION_ERROR;
        final String exceptionMessage = extractErrorMessage(fieldErrors);
        return ResponseEntity
                .status(code.getStatus())
                .body(new ExceptionResponse(code, exceptionMessage));
    }

    private String extractErrorMessage(final List<FieldError> fieldErrors) {
        if (fieldErrors.size() == 1) {
            return fieldErrors.get(0).getDefaultMessage();
        }

        final Map<String, String> errors = new HashMap<>();
        for (final FieldError error : fieldErrors) {
            errors.put(error.getField(), error.getDefaultMessage());
        }

        try {
            return objectMapper.writeValueAsString(errors);
        } catch (final JsonProcessingException e) {
            throw new RuntimeException("JSON Parsing Error...", e);
        }
    }
}