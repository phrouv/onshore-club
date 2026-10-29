package com.onshore.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.onshore.services.model.User;
import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import liquibase.changelog.ChangeLogParameters;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.parser.ChangeLogParser;
import liquibase.parser.ChangeLogParserFactory;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.junit.jupiter.api.Test;

public class LiquibaseChangelogTest {

  @Test
  void userRoleIsStoredAsString() throws Exception {
    var role = User.class.getDeclaredField("role");
    assertEquals(EnumType.STRING, role.getAnnotation(Enumerated.class).value());
    assertEquals(20, role.getAnnotation(Column.class).length());
  }

  @Test
  void testUserChangelogIsValid() throws Exception {
    ClassLoaderResourceAccessor resourceAccessor =
        new ClassLoaderResourceAccessor(getClass().getClassLoader());
    ChangeLogParser parser =
        ChangeLogParserFactory.getInstance()
            .getParser("db/changelog/db.changelog-master.yaml", resourceAccessor);
    DatabaseChangeLog changeLog =
        parser.parse(
            "db/changelog/db.changelog-master.yaml", new ChangeLogParameters(), resourceAccessor);

    assertNotNull(changeLog);
    assertFalse(changeLog.getChangeSets().isEmpty());
  }
}
