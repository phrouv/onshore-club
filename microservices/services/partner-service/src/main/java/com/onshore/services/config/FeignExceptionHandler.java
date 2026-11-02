package com.onshore.services.config;

import feign.FeignException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/** Forwards the status and problem+json body of a failed downstream call. */
@RestControllerAdvice
public class FeignExceptionHandler {

  @ExceptionHandler(FeignException.class)
  public ResponseEntity<String> handle(FeignException ex) {
    int status = ex.status() > 0 ? ex.status() : 502;
    String body = ex.contentUTF8();
    return ResponseEntity.status(status)
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_PROBLEM_JSON_VALUE)
        .body(
            body == null || body.isBlank()
                ? "{\"code\":\"UPSTREAM_ERROR\",\"title\":\"Upstream service error\",\"status\":"
                    + status
                    + "}"
                : body);
  }
}
