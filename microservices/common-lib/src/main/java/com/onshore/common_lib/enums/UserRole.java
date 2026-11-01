package com.onshore.common_lib.enums;

import com.onshore.common_lib.exception.ApiException;
import com.onshore.common_lib.exception.ClubExceptionType;
import java.util.Arrays;

public enum UserRole {
  ROLE_SYSTEM_ADMIN,
  ROLE_MEMBER,
  ROLE_PARTNER;

  public static boolean has(String header, String role) {
    return header != null && Arrays.asList(header.split("[,\\s\\[\\]]+")).contains(role);
  }

  public static boolean isAdmin(String header) {
    return has(header, ROLE_SYSTEM_ADMIN.name());
  }

  public static void requireAdmin(String header) {
    if (!isAdmin(header)) {
      throw new ApiException(ClubExceptionType.FORBIDDEN_OPERATION, "Admin role required");
    }
  }
}
