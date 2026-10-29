package com.onshore.services.model;

import com.onshore.common_lib.enums.UserRole;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.domain.Persistable;

@Entity
@Table(name = "users")
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class User implements Serializable, Persistable<UUID> {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  @Column(name = "ID", columnDefinition = "uuid", updatable = false, nullable = false)
  private UUID id;

  @NotBlank(message = "fullName is mandatory")
  private String fullName;

  private String password;

  @Column(nullable = false, unique = true)
  @NotBlank(message = "Email is mandatory")
  @Email(message = "Email should be valid")
  private String email;

  private String phone;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  @NotNull(message = "Role is mandatory")
  private UserRole role;

  @Column(nullable = false, updatable = false)
  @CreationTimestamp
  private LocalDateTime createdAt;

  @Column(nullable = false)
  @UpdateTimestamp
  private LocalDateTime updatedAt;

  @Column(nullable = false)
  private Boolean verified = false;

  private LocalDateTime lastLogin;

  @Override
  public boolean isNew() {
    return this.id == null;
  }
}
