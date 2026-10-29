package com.onshore.common_lib.exception;

import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Single centralized mapping of exceptions to {@link ErrorMessage}; imported by each service. */
@RestControllerAdvice
public class GlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  private final String baseUrl;

  public GlobalExceptionHandler(
      @Value("${onshore.exception.base-url:https://onshore.errors}") String baseUrl) {
    this.baseUrl = baseUrl;
  }

  @ExceptionHandler(ApiException.class)
  public ResponseEntity<ErrorMessage> handleApi(ApiException ex, HttpServletRequest request) {
    return build(ex.getType(), ex.getMessage(), request, null);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorMessage> handleValidation(
      MethodArgumentNotValidException ex, HttpServletRequest request) {
    List<ErrorMessage.FieldError> errors =
        ex.getBindingResult().getFieldErrors().stream()
            .map(e -> new ErrorMessage.FieldError(e.getField(), e.getDefaultMessage()))
            .toList();
    return build(ClubExceptionType.INVALID_PAYLOAD, null, request, errors);
  }

  @ExceptionHandler(MissingRequestHeaderException.class)
  public ResponseEntity<ErrorMessage> handleMissingHeader(
      MissingRequestHeaderException ex, HttpServletRequest request) {
    return build(
        ClubExceptionType.FORBIDDEN_OPERATION,
        "Missing header " + ex.getHeaderName(),
        request,
        null);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorMessage> handleUnknown(Exception ex, HttpServletRequest request) {
    log.error("Unexpected error on {}", request.getRequestURI(), ex);
    return build(ClubExceptionType.UNKNOWN_EXCEPTION, null, request, null);
  }

  private ResponseEntity<ErrorMessage> build(
      ClubExceptionType type,
      String detail,
      HttpServletRequest request,
      List<ErrorMessage.FieldError> errors) {
    ErrorMessage body =
        new ErrorMessage(
            baseUrl + "/" + type.getCode(),
            type.getTitle(),
            type.getStatus(),
            type.getCode(),
            detail,
            request.getRequestURI(),
            errors);
    return ResponseEntity.status(type.getStatus())
        .contentType(MediaType.APPLICATION_PROBLEM_JSON)
        .body(body);
  }
}
