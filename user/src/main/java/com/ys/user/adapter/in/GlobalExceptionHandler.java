package com.ys.user.adapter.in;

import com.ys.infrastructure.exception.AccessDeniedException;
import com.ys.infrastructure.exception.BadRequestException;
import com.ys.infrastructure.exception.UnauthorizedException;
import com.ys.infrastructure.utils.ApiResponseModel;
import feign.FeignException;
import jakarta.servlet.ServletException;
import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageConversionException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.NoSuchElementException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(FeignException.class)
    public ResponseEntity<ApiResponseModel<?>> handleFeignException(FeignException ex) {
        log.error("FeignException", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponseModel.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage()));
    }

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ApiResponseModel<?>> handleBadRequestException(BadRequestException ex) {
        log.error("BadRequestException", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResponseModel.error(HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
    }

    @ExceptionHandler(ServletException.class)
    public ResponseEntity<ApiResponseModel<?>> handleServletException(ServletException ex) {
        log.error("ServletException", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResponseModel.error(HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponseModel<?>> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        log.error("MethodArgumentTypeMismatchException", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResponseModel.error(HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
    }

    @ExceptionHandler(HttpMessageConversionException.class)
    public ResponseEntity<ApiResponseModel<?>> handleHttpMessageConversionException(HttpMessageConversionException ex) {
        log.error("HttpMessageConversionException", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResponseModel.error(HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
    }

    @ExceptionHandler(BindException.class)
    public ResponseEntity<ApiResponseModel<?>> handleBindException(BindException ex) {
        log.error("BindException", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResponseModel.error(HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponseModel<?>> handleUnauthorizedException(UnauthorizedException ex) {
        log.error("UnauthorizedException", ex);
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(
                ApiResponseModel.error(HttpStatus.UNAUTHORIZED.value(), ex.getMessage()));
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponseModel<?>> handleAccessDeniedException(AccessDeniedException ex) {
        log.error("AccessDeniedException", ex);
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(
                ApiResponseModel.error(HttpStatus.FORBIDDEN.value(), ex.getMessage()));
    }

    @ExceptionHandler(NoSuchElementException.class)
    public ResponseEntity<ApiResponseModel<?>> handleNoSuchElementException(NoSuchElementException ex) {
        log.error("NoSuchElementException", ex);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(
                ApiResponseModel.error(HttpStatus.NOT_FOUND.value(), ex.getMessage()));
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiResponseModel<?>> handleConstraintViolationException(ConstraintViolationException ex) {
        log.error("ConstraintViolationException", ex);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(
                ApiResponseModel.error(HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponseModel<?>> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.error("IllegalArgumentException", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponseModel.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage()));
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponseModel<?>> handleIllegalStateException(IllegalStateException ex) {
        log.error("IllegalStateException", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponseModel.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponseModel<?>> handleException(Exception ex) {
        log.error("Exception", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                ApiResponseModel.error(HttpStatus.INTERNAL_SERVER_ERROR.value(), ex.getMessage()));
    }
}
