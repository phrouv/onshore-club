package com.onshore.services.model;

import com.onshore.common_lib.enums.UserRole;
import com.onshore.common_lib.model.AbstractFullAuditingEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;
import lombok.*;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(
    name = "users",
    uniqueConstraints = {
      @UniqueConstraint(
          columnNames = {"email"},
          name = "uk_users_email")
    })
public class User extends AbstractFullAuditingEntity {

  @Column(name = "full_name", length = 100, nullable = false)
  @Size(max = 100, message = "fullName must not exceed 100 characters")
  @NotNull(message = "fullName is mandatory")
  private String fullName;

  @Column(name = "password", nullable = false)
  @NotBlank(message = "Password is mandatory")
  private String password;

  @Column(name = "email", length = 100, nullable = false)
  @NotNull(message = "Email is mandatory")
  @Size(max = 100, message = "Email must not exceed 100 characters")
  @Email(message = "Email should be valid")
  private String email;

  @Column(name = "phone", length = 20)
  @Size(max = 20, message = "Phone must not exceed 20 characters")
  private String phone;

  @Enumerated(EnumType.STRING)
  @Column(name = "role", nullable = false, length = 20)
  @NotNull(message = "Role is mandatory")
  private UserRole role;

  @Builder.Default
  @Column(name = "verified", nullable = false)
  private Boolean verified = false;

  @Column(name = "last_login")
  private LocalDateTime lastLogin;
}
