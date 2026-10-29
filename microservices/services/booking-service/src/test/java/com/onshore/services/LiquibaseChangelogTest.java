package com.onshore.services;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import liquibase.changelog.ChangeLogParameters;
import liquibase.changelog.DatabaseChangeLog;
import liquibase.parser.ChangeLogParser;
import liquibase.parser.ChangeLogParserFactory;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.junit.jupiter.api.Test;

public class LiquibaseChangelogTest {

  @Test
  void testBookingServiceChangelogIsValid() throws Exception {
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
