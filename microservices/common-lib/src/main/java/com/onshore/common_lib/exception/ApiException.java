package com.onshore.common_lib.exception;

public class ApiException extends RuntimeException {

  private final ClubExceptionType type;

  public ApiException(ClubExceptionType type, String detail) {
    super(detail);
    this.type = type;
  }

  public ClubExceptionType getType() {
    return type;
  }
}
