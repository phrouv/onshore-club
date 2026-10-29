package com.onshore.common_lib.security;

import com.onshore.common_lib.exception.ApiException;
import com.onshore.common_lib.exception.ClubExceptionType;
import java.util.Arrays;

/** Role checks on the X-User-Roles header injected by the api-gateway. */
public final class Roles {

  public static final String ADMIN = "ROLE_SYSTEM_ADMIN";
  public static final String PARTNER = "ROLE_PARTNER";
  public static final String MEMBER = "ROLE_MEMBER";

  private Roles() {}

  public static boolean has(String header, String role) {
    return header != null && Arrays.asList(header.split("[,\\s\\[\\]]+")).contains(role);
  }

  public static boolean isAdmin(String header) {
    return has(header, ADMIN);
  }

  public static void requireAdmin(String header) {
    if (!isAdmin(header)) {
      throw new ApiException(ClubExceptionType.FORBIDDEN_OPERATION, "Admin role required");
    }
  }
}
