package com.onshore.common_lib.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/**
 * Base abstract class for entities which will hold definitions for created, last modified, created
 * by, last modified by attributes.
 */
@MappedSuperclass
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
@JsonIgnoreProperties(
    value = {"createdDate", "lastModifiedDate"},
    allowGetters = true)
public abstract class AbstractFullAuditingEntity implements Serializable, Persistable<UUID> {

  @Getter(onMethod_ = {@Override})
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "ID", columnDefinition = "uuid", updatable = false, nullable = false)
  private UUID id;

  @CreatedDate
  @Column(
      name = "CREATED_DATE",
      columnDefinition = "timestamp without time zone",
      nullable = false,
      updatable = false)
  private Instant createdDate = Instant.now();

  @LastModifiedDate
  @Column(name = "LAST_MODIFIED_DATE", columnDefinition = "timestamp without time zone")
  private Instant lastModifiedDate = Instant.now();

  @JsonIgnore
  public boolean isNew() {
    return this.id == null;
  }
}
