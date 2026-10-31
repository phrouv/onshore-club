package com.onshore.common_lib.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import java.io.Serializable;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

/** Provides a base for all entities. */
@MappedSuperclass
@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public abstract class AbstractAuditingEntity implements Serializable, Persistable<UUID> {

  /** The unique identifier for the entity, generated automatically as a UUID. */
  @Getter(onMethod_ = {@Override})
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "ID", columnDefinition = "uuid", updatable = false, nullable = false)
  private UUID id;

  @JsonIgnore
  public boolean isNew() {
    return this.id == null;
  }
}
