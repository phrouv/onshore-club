package com.onshore.common_lib;

/**
 * Marker class for the common-lib module.
 *
 * <p>WHY this is NOT a @SpringBootApplication: This module is a shared LIBRARY (DTOs, enums,
 * utils), not a runnable service. Libraries should not have: - @SpringBootApplication (starts an
 * embedded web server — pointless for a library) - A main() method (nothing to "run") -
 * spring-boot-maven-plugin (repackages JAR to executable format, breaking library usage)
 *
 * <p>Other modules import this as a plain dependency: <dependency> <groupId>com.onshore</groupId>
 * <artifactId>common-lib</artifactId> </dependency>
 *
 * <p>You can delete this class entirely once you add real shared classes (DTOs, enums, exceptions,
 * etc.) to this package.
 */
public class CommonLibApplication {
  // This class exists only as a placeholder.
  // Replace it with actual shared code: DTOs, enums, utility classes.
}
