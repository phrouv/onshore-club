package com.onshore.common_lib.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

/** RFC 9457 problem+json enriched with a stable business code. */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorMessage(
    String type,
    String title,
    int status,
    String code,
    String detail,
    String instance,
    List<FieldError> errors) {

  public record FieldError(String field, String message) {}
}
