package com.onshore.common_lib.exception;

/** Stable business error codes shared by the club microservices. */
public enum ClubExceptionType {
  INVALID_PAYLOAD(400, "Structural or semantic validation failure"),
  CATEGORY_CODE_IMMUTABLE(400, "Category code cannot be changed"),
  FORBIDDEN_OPERATION(403, "Operation not permitted"),
  UNKNOWN_EXCEPTION(500, "Unexpected error"),
  UPSTREAM_ERROR(502, "Upstream service error"),

  PARTNER_NOT_FOUND(404, "Partner not found"),
  ACTIVITY_NOT_FOUND(404, "Activity not found"),
  CLASS_NOT_FOUND(404, "Class not found"),
  BOOKING_NOT_FOUND(404, "Booking not found"),
  PLAN_NOT_FOUND(404, "Plan not found"),
  SUBSCRIPTION_NOT_FOUND(404, "Subscription not found"),
  PAYOUT_NOT_FOUND(404, "Payout not found"),
  USAGE_RULE_NOT_FOUND(404, "Usage rule not found"),
  CREDIT_SETTING_NOT_FOUND(404, "Credit setting not found"),
  CATEGORY_NOT_FOUND(404, "Category not found"),

  PARTNER_ALREADY_EXISTS(409, "Partner already exists"),
  CATEGORY_ALREADY_EXISTS(409, "Category already exists"),
  PARTNER_NOT_ACTIVE(409, "Partner not active"),
  CLASS_CLOSED(409, "Class closed"),
  CLASS_PAST(409, "Class already past"),
  CLASS_FULL(409, "Class full"),
  CLASS_HAS_BOOKINGS(409, "Class has bookings"),
  BOOKING_WINDOW_EXCEEDED(409, "Booking window exceeded"),
  ALREADY_BOOKED(409, "Already booked"),
  BOOKING_NOT_CONFIRMED(409, "Booking not confirmed"),
  USAGE_LIMIT_REACHED(409, "Usage limit reached"),
  INSUFFICIENT_CREDITS(409, "Insufficient credits"),
  CHECKIN_ALREADY_DONE(409, "Check-in already done"),
  CHECKIN_WINDOW_NOT_OPEN(409, "Check-in window not open"),
  CHECKIN_WINDOW_CLOSED(409, "Check-in window closed"),
  WRONG_PARTNER_CODE(422, "Wrong partner code");

  private final int status;
  private final String title;

  ClubExceptionType(int status, String title) {
    this.status = status;
    this.title = title;
  }

  public int getStatus() {
    return status;
  }

  public String getTitle() {
    return title;
  }

  public String getCode() {
    return name();
  }
}
