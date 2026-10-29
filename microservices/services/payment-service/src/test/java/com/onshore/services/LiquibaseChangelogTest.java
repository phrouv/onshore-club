package com.onshore.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.onshore.services.model.Payment;
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
  void paymentEnumsUseStringsAndMatchChangelogLengths() throws Exception {
    for (String name : new String[] {"provider", "status"}) {
      var field = Payment.class.getDeclaredField(name);
      assertEquals(EnumType.STRING, field.getAnnotation(Enumerated.class).value());
      assertEquals(50, field.getAnnotation(Column.class).length());
    }
  }

  @Test
  void testPaymentChangelogIsValid() throws Exception {
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
